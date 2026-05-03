/*
 * Copyright 2013-2021 consulo.io
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

package consulo.sql.language.impl.psi;

import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.ast.TokenType;
import consulo.sql.language.SqlLanguage;

/**
 * @author VISTALL
 * @since 22/10/2021
 */
public interface SqlTokenType extends TokenType {
    IElementType IDENTIFIER = new IElementType("SQL_IDENTIFIER", SqlLanguage.INSTANCE);

    IElementType COMMA = new IElementType("SQL_COMMA", SqlLanguage.INSTANCE);

    IElementType LPAR = new IElementType("SQL_LPAR", SqlLanguage.INSTANCE);

    IElementType RPAR = new IElementType("SQL_RPAR", SqlLanguage.INSTANCE);

    IElementType NUMBER = new IElementType("SQL_NUMBER", SqlLanguage.INSTANCE);

    IElementType SINGLE_QUOTED_LITERAL = new IElementType("SQL_SINGLE_QUOTED_LITERAL", SqlLanguage.INSTANCE);

    IElementType BACKTICK_QUOTED_IDENTIFIER = new IElementType("SQL_BACKTICK_QUOTED_IDENTIFIER", SqlLanguage.INSTANCE);

    IElementType DOUBLE_QUOTED_IDENTIFIER = new IElementType("SQL_DOUBLE_QUOTED_IDENTIFIER", SqlLanguage.INSTANCE);

    IElementType BRACKET_QUOTED_IDENTIFIER = new IElementType("SQL_BRACKET_QUOTED_IDENTIFIER", SqlLanguage.INSTANCE);

    IElementType DOLLAR_QUOTED_STRING = new IElementType("SQL_DOLLAR_QUOTED_STRING", SqlLanguage.INSTANCE);

    IElementType C_STYLE_COMMENT = new IElementType("SQL_C_STYLE_COMMENT", SqlLanguage.INSTANCE);

    IElementType END_OF_LINE_COMMENT = new IElementType("SQL_END_OF_LINE_COMMENT", SqlLanguage.INSTANCE);

    IElementType DOT = new IElementType("SQL_DOT", SqlLanguage.INSTANCE);

    IElementType SEMICOLON = new IElementType("SQL_SEMICOLON", SqlLanguage.INSTANCE);

    IElementType ASTERISK = new IElementType("SQL_ASTERISK", SqlLanguage.INSTANCE);

    IElementType EQ = new IElementType("SQL_EQ", SqlLanguage.INSTANCE);

    IElementType LT = new IElementType("SQL_LT", SqlLanguage.INSTANCE);

    IElementType GT = new IElementType("SQL_GT", SqlLanguage.INSTANCE);

    IElementType LE = new IElementType("SQL_LE", SqlLanguage.INSTANCE);

    IElementType GE = new IElementType("SQL_GE", SqlLanguage.INSTANCE);

    IElementType NE = new IElementType("SQL_NE", SqlLanguage.INSTANCE);

    IElementType PLUS = new IElementType("SQL_PLUS", SqlLanguage.INSTANCE);

    IElementType MINUS = new IElementType("SQL_MINUS", SqlLanguage.INSTANCE);

    IElementType SLASH = new IElementType("SQL_SLASH", SqlLanguage.INSTANCE);

    IElementType CONCAT = new IElementType("SQL_CONCAT", SqlLanguage.INSTANCE);

    /**
     * Question mark, used as positional placeholder marker: {@code ?} or {@code ?N}.
     */
    IElementType QUESTION = new IElementType("SQL_QUESTION", SqlLanguage.INSTANCE);

    /**
     * Colon, used as named placeholder marker: {@code :name}.
     */
    IElementType COLON = new IElementType("SQL_COLON", SqlLanguage.INSTANCE);

    TokenSet IDENTIFIERS = TokenSet.create(IDENTIFIER, BACKTICK_QUOTED_IDENTIFIER, DOUBLE_QUOTED_IDENTIFIER, BRACKET_QUOTED_IDENTIFIER);

    TokenSet COMMENTS = TokenSet.create(C_STYLE_COMMENT, END_OF_LINE_COMMENT);

    TokenSet STRINGS = TokenSet.create(SINGLE_QUOTED_LITERAL);
}
