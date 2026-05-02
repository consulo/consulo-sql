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

package consulo.sql.language.impl.highlight;

import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.editor.highlight.LanguageVersionableSyntaxHighlighter;
import consulo.language.lexer.Lexer;
import consulo.language.version.LanguageVersion;
import consulo.sql.language.impl.BaseSqlLanguageVersion;
import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlTokenType;
import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 22/10/2021
 */
public class SqlSyntaxHighlighter extends LanguageVersionableSyntaxHighlighter {
    private static Map<IElementType, TextAttributesKey> ourTextAttributes = new HashMap<>();

    static {
        safeMap(ourTextAttributes, SqlTokenType.LPAR, DefaultLanguageHighlighterColors.PARENTHESES);
        safeMap(ourTextAttributes, SqlTokenType.RPAR, DefaultLanguageHighlighterColors.PARENTHESES);
        safeMap(ourTextAttributes, SqlTokenType.COMMA, DefaultLanguageHighlighterColors.COMMA);
        safeMap(ourTextAttributes, SqlTokenType.DOT, DefaultLanguageHighlighterColors.DOT);
        safeMap(ourTextAttributes, SqlTokenType.SEMICOLON, DefaultLanguageHighlighterColors.SEMICOLON);
        safeMap(ourTextAttributes, SqlTokenType.IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER);
        safeMap(ourTextAttributes, SqlTokenType.BACKTICK_QUOTED_IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER);
        safeMap(ourTextAttributes, SqlTokenType.DOUBLE_QUOTED_IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER);
        safeMap(ourTextAttributes, SqlTokenType.BRACKET_QUOTED_IDENTIFIER, DefaultLanguageHighlighterColors.IDENTIFIER);
        safeMap(ourTextAttributes, SqlTokenType.SINGLE_QUOTED_LITERAL, DefaultLanguageHighlighterColors.STRING);
        safeMap(ourTextAttributes, SqlTokenType.NUMBER, DefaultLanguageHighlighterColors.NUMBER);
        safeMap(ourTextAttributes, SqlTokenType.ASTERISK, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.EQ, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.NE, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.LT, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.GT, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.LE, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.GE, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.PLUS, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.MINUS, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.SLASH, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.CONCAT, DefaultLanguageHighlighterColors.OPERATION_SIGN);
        safeMap(ourTextAttributes, SqlTokenType.C_STYLE_COMMENT, DefaultLanguageHighlighterColors.BLOCK_COMMENT);
        safeMap(ourTextAttributes, SqlTokenType.END_OF_LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT);
    }

    public SqlSyntaxHighlighter(LanguageVersion languageVersion) {
        super(languageVersion);
    }

    @Override
    public Lexer getHighlightingLexer(LanguageVersion languageVersion) {
        BaseSqlLanguageVersion sqlLanguageVersion = (BaseSqlLanguageVersion) languageVersion;
        return sqlLanguageVersion.createLexer();
    }

    @Nonnull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType instanceof SqlKeywordElementType) {
            return pack(DefaultLanguageHighlighterColors.KEYWORD);
        }

        return pack(ourTextAttributes.get(tokenType));
    }
}
