/*
 * Copyright 2012-2014 Dan Cioca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dci.intellij.dbn.language.common;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import com.intellij.lang.LanguageVersion;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public abstract class DBLanguageTokenTypeBundle {
    protected final Logger log = Logger.getInstance(getClass().getName());
	protected final SqlLikeLanguage language;
	protected final LanguageVersion<? extends SqlLikeLanguage> languageVersion;
    private SimpleTokenType[] keywords;
    private SimpleTokenType[] functions;
    private SimpleTokenType[] parameters;
    private SimpleTokenType[] dataTypes;
    private SimpleTokenType[] exceptions;
    private SimpleTokenType[] characters;
    private SimpleTokenType[] operators;
    private Map<String, SimpleTokenType> keywordsMap;
    private Map<String, SimpleTokenType> functionsMap;
    private Map<String, SimpleTokenType> parametersMap;
    private Map<String, SimpleTokenType> dataTypesMap;
    private Map<String, SimpleTokenType> exceptionsMap;
    private Map<String, SimpleTokenType> charactersMap;
    private Map<String, SimpleTokenType> operatorsMap;

    private Map<String, SimpleTokenType> tokenTypes = new THashMap<String, SimpleTokenType>();
    private Map<String, TokenSet> tokenSets = new THashMap<String, TokenSet>();

    public Map<String, SimpleTokenType> getTokenTypes() {
        return tokenTypes;
    }

    public DBLanguageTokenTypeBundle(SqlLikeLanguage language, LanguageVersion<? extends SqlLikeLanguage> languageVersion, Document document) {
        this.language = language;
		this.languageVersion = languageVersion;
        loadDefinition(document);
    }

    public SqlLikeLanguage getLanguage() {
        return language;
    }

	public SqlLikeLanguageVersion<? extends SqlLikeLanguage> getLanguageVersion()
	{
		assert languageVersion instanceof SqlLikeLanguageVersion;
		return (SqlLikeLanguageVersion<? extends SqlLikeLanguage>) languageVersion;
	}

    protected void loadDefinition(Document document) {
        try {
            Element root = document.getRootElement();
            parseTokens(root.getChild("tokens"));
            parseTokenSets(root.getChild("token-sets"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseTokens(Element tokenDefs) {
        List<SimpleTokenType> keywordList = new ArrayList<SimpleTokenType>();
        List<SimpleTokenType> functionList = new ArrayList<SimpleTokenType>();
        List<SimpleTokenType> parameterList = new ArrayList<SimpleTokenType>();
        List<SimpleTokenType> dataTypeList = new ArrayList<SimpleTokenType>();
        List<SimpleTokenType> exceptionList = new ArrayList<SimpleTokenType>();
        List<SimpleTokenType> characterList = new ArrayList<SimpleTokenType>();
        List<SimpleTokenType> operatorList = new ArrayList<SimpleTokenType>();
        for (Object o : tokenDefs.getChildren()) {
            SimpleTokenType tokenType = new SimpleTokenType((Element) o, language, languageVersion);
            log.debug("Creating token type '" + tokenType.getId() + "'");
            tokenTypes.put(tokenType.getId(), tokenType);
            switch(tokenType.getTokenTypeIdentifier()) {
                case KEYWORD: keywordList.add(tokenType); break;
                case FUNCTION: functionList.add(tokenType); break;
                case PARAMETER: parameterList.add(tokenType); break;
                case DATATYPE: dataTypeList.add(tokenType); break;
                case EXCEPTION: exceptionList.add(tokenType); break;
                case CHARACTER: characterList.add(tokenType); break;
                case OPERATOR: operatorList.add(tokenType); break;
            }
        }
        keywords = new SimpleTokenType[keywordList.size()];
        keywordsMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType keyword : keywordList) {
            keywords[keyword.getIdx()] = keyword;
            keywordsMap.put(keyword.getValue(), keyword);
        }

        functions = new SimpleTokenType[functionList.size()];
        functionsMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType function : functionList) {
            functions[function.getIdx()] = function;
            functionsMap.put(function.getValue(), function);
        }

        parameters = new SimpleTokenType[parameterList.size()];
        parametersMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType parameter : parameterList) {
            parameters[parameter.getIdx()] = parameter;
            parametersMap.put(parameter.getValue(), parameter);
        }

        dataTypes = new SimpleTokenType[dataTypeList.size()];
        dataTypesMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType dataType : dataTypeList) {
            dataTypes[dataType.getIdx()] = dataType;
            dataTypesMap.put(dataType.getValue(), dataType);
        }

        exceptions = new SimpleTokenType[exceptionList.size()];
        exceptionsMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType exception : exceptionList) {
            exceptions[exception.getIdx()] = exception;
            exceptionsMap.put(exception.getValue(), exception);
        }

        characters = new SimpleTokenType[characterList.size()];
        charactersMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType character : characterList) {
            characters[character.getIdx()] = character;
            charactersMap.put(character.getValue(), character);
        }

        operators = new SimpleTokenType[characterList.size()];
        operatorsMap = new THashMap<String, SimpleTokenType>();
        for (SimpleTokenType operator : operatorList) {
            operators[operator.getIdx()] = operator;
            operatorsMap.put(operator.getValue(), operator);
        }
    }

    public SimpleTokenType getKeywordTokenType(int index) {
        return keywords[index];
    }

    public SimpleTokenType getFunctionTokenType(int index) {
        return functions[index];
    }

    public SimpleTokenType getParameterTokenType(int index) {
        return parameters[index];
    }

    public SimpleTokenType getDataTypeTokenType(int index) {
        return dataTypes[index];
    }

    public SimpleTokenType getExceptionTokenType(int index) {
        return exceptions[index];
    }

    public SimpleTokenType getCharacterTokenType(int index) {
        return characters[index];
    }

    public SimpleTokenType getOperatorTokenType(int index) {
        return operators[index];
    }

    private void parseTokenSets(Element tokenSetDefs) {
        for (Object o : tokenSetDefs.getChildren()) {
            Element element = (Element) o;
            String id = element.getAttributeValue("id");

            List<SimpleTokenType> tokenSetList = new ArrayList<SimpleTokenType>();
            StringTokenizer tokenizer = new StringTokenizer(element.getText(), ",");
            while (tokenizer.hasMoreTokens()) {
                String tokenId = tokenizer.nextToken().trim();
                SimpleTokenType tokenType = tokenTypes.get(tokenId);
                if (tokenType == null) {
                    System.out.println("DEBUG - [" + getLanguage().getID() + "] undefined token type: " + tokenId);
                } else {
                    tokenSetList.add(tokenType);
                }
            }
            IElementType[] tokenSetArray = tokenSetList.toArray(new IElementType[tokenSetList.size()]);
            TokenSet tokenSet = TokenSet.create(tokenSetArray);
            tokenSets.put(id, tokenSet);
        }
    }


    public SimpleTokenType getTokenType(String id) {
        return tokenTypes.get(id);
    }

    public TokenSet getTokenSet(String id) {
        return tokenSets.get(id);
    }

	public TokenSet getRequiredTokenSet(String id)
	{
		TokenSet tokenSet = tokenSets.get(id);
		return tokenSet == null ? TokenSet.EMPTY : tokenSet;
	}

    public boolean isReservedWord(String text) {
        return
            isKeyword(text) ||
            isFunction(text) ||
            isParameter(text) ||
            isDataType(text) || 
            isException(text);
    }

    public boolean isKeyword(String text) {
        return isTokenType(text, keywordsMap);
    }

    public boolean isFunction(String text) {
        return isTokenType(text, functionsMap);
    }

    public boolean isParameter(String text) {
        return isTokenType(text, parametersMap);
    }

    public boolean isDataType(String text) {
        return isTokenType(text, dataTypesMap);
    }

    public boolean isException(String text) {
        return isTokenType(text, exceptionsMap);
    }


    private boolean isTokenType(String text, Map<String, SimpleTokenType> tokenTypesMap) {
        return tokenTypesMap.containsKey(text.toLowerCase());
    }
}

