/*
 * Copyright 2013-2026 consulo.io
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

package consulo.sql.language.impl.jpql.parser;

import consulo.language.ast.IElementType;
import consulo.language.parser.PsiBuilder;
import consulo.sql.language.impl.parser.SqlParser;
import consulo.sql.language.impl.psi.SqlCompositeElementTypes;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;
import consulo.sql.language.impl.psi.SqlTokenType;
import consulo.sql.language.localize.SqlLocalize;

/**
 * Parser for the Jakarta Persistence Query Language.
 * <p>
 * Adds, on top of the base SQL-92 parser:
 * <ul>
 *     <li>{@code expr IS [NOT] EMPTY}</li>
 *     <li>{@code expr [NOT] MEMBER [OF] collection}</li>
 *     <li>{@code TREAT(expr AS Subtype)}</li>
 *     <li>Single-arg path functions {@code KEY VALUE ENTRY INDEX SIZE TYPE}</li>
 *     <li>{@code FUNCTION('name', arg1, &hellip;)}</li>
 *     <li>{@code JOIN FETCH path}</li>
 * </ul>
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public class JpqlParser extends SqlParser {
    @Override
    protected void parseIsTail(PsiBuilder builder, PsiBuilder.Marker left) {
        builder.advanceLexer(); // IS

        if (isToken(builder, SqlKeywordTokenTypes.NOT_KEYWORD)) {
            builder.advanceLexer();
        }

        if (isToken(builder, SqlKeywordTokenTypes.NULL_KEYWORD)) {
            builder.advanceLexer();
            left.done(SqlCompositeElementTypes.IS_NULL_EXPRESSION);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.EMPTY_KEYWORD)) {
            builder.advanceLexer();
            left.done(SqlCompositeElementTypes.IS_EMPTY_EXPRESSION);
        }
        else {
            builder.error(SqlLocalize.parserNullExpected());
            left.done(SqlCompositeElementTypes.IS_NULL_EXPRESSION);
        }
    }

    @Override
    protected boolean parseExtendedComparison(PsiBuilder builder, PsiBuilder.Marker left, IElementType token) {
        // expr [NOT] MEMBER [OF] expr
        boolean negated = false;
        IElementType mainToken = token;
        if (token == SqlKeywordTokenTypes.NOT_KEYWORD
            && lookAheadTokenIs(builder, SqlKeywordTokenTypes.MEMBER_KEYWORD)) {
            negated = true;
        }

        if (negated || mainToken == SqlKeywordTokenTypes.MEMBER_KEYWORD) {
            if (negated) {
                builder.advanceLexer(); // NOT
            }
            builder.advanceLexer(); // MEMBER
            if (isToken(builder, SqlKeywordTokenTypes.OF_KEYWORD)) {
                builder.advanceLexer();
            }
            parseExpression(builder);
            left.done(SqlCompositeElementTypes.MEMBER_OF_EXPRESSION);
            return true;
        }

        return false;
    }

    @Override
    protected void parseJoinModifiers(PsiBuilder builder) {
        if (isToken(builder, SqlKeywordTokenTypes.FETCH_KEYWORD)) {
            builder.advanceLexer();
        }
    }

    @Override
    protected boolean parseSpecialFunctionCall(PsiBuilder builder, IElementType token) {
        // TREAT(expr AS Subtype)
        if (token == SqlKeywordTokenTypes.TREAT_KEYWORD
            && lookAheadTokenIs(builder, SqlTokenType.LPAR)) {
            parseTreatExpression(builder);
            return true;
        }

        // FUNCTION('name', args...)
        if (token == SqlKeywordTokenTypes.FUNCTION_KEYWORD
            && lookAheadTokenIs(builder, SqlTokenType.LPAR)) {
            parseNamedFunctionCall(builder);
            return true;
        }

        // KEY/VALUE/ENTRY/INDEX/SIZE/TYPE(expr) — JPQL path functions
        if (isPathFunctionKeyword(token)
            && lookAheadTokenIs(builder, SqlTokenType.LPAR)) {
            parsePathFunctionExpression(builder);
            return true;
        }

        // JPQL-reserved scalar functions: CONCAT/LENGTH/ABS/MOD/SQRT/LOCATE
        // These are reserved keyword tokens in JPQL/HQL (so they don't reach
        // the IDENTIFIERS branch the way they would in plain SQL dialects),
        // but syntactically they're ordinary n-ary function calls.
        if (isJpqlScalarFunction(token)
            && lookAheadTokenIs(builder, SqlTokenType.LPAR)) {
            parseFunctionCall(builder);
            return true;
        }

        return false;
    }

    /**
     * Standard JPQL scalar functions that are <em>reserved keywords</em> per
     * the spec but otherwise behave like ordinary function calls. Subclasses
     * (HQL) may extend with their own dialect-specific entries.
     */
    protected boolean isJpqlScalarFunction(IElementType token) {
        return token == SqlKeywordTokenTypes.CONCAT_KEYWORD
            || token == SqlKeywordTokenTypes.LENGTH_KEYWORD
            || token == SqlKeywordTokenTypes.ABS_KEYWORD
            || token == SqlKeywordTokenTypes.MOD_KEYWORD
            || token == SqlKeywordTokenTypes.SQRT_KEYWORD
            || token == SqlKeywordTokenTypes.LOCATE_KEYWORD;
    }

    /**
     * Recognises the keywords that introduce a single-argument path function.
     * Subclasses (HQL) override to add their own keywords.
     */
    protected boolean isPathFunctionKeyword(IElementType token) {
        return token == SqlKeywordTokenTypes.KEY_KEYWORD
            || token == SqlKeywordTokenTypes.VALUE_KEYWORD
            || token == SqlKeywordTokenTypes.ENTRY_KEYWORD
            || token == SqlKeywordTokenTypes.INDEX_KEYWORD
            || token == SqlKeywordTokenTypes.SIZE_KEYWORD
            || token == SqlKeywordTokenTypes.TYPE_KEYWORD;
    }

    private void parseTreatExpression(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer(); // TREAT
        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
        parseExpression(builder);
        expectKeyword(builder, SqlKeywordTokenTypes.AS_KEYWORD, SqlLocalize.parserAsExpected());
        parseQualifiedName(builder);
        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        mark.done(SqlCompositeElementTypes.TREAT_EXPRESSION);
    }

    private void parseNamedFunctionCall(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer(); // FUNCTION
        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
        if (isToken(builder, SqlTokenType.SINGLE_QUOTED_LITERAL)) {
            builder.advanceLexer();
        }
        else {
            builder.error(SqlLocalize.parserExpressionExpected());
        }
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseExpression(builder);
        }
        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        mark.done(SqlCompositeElementTypes.NAMED_FUNCTION_CALL_EXPRESSION);
    }

    private void parsePathFunctionExpression(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer(); // KEY/VALUE/...
        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
        parseExpression(builder);
        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        mark.done(SqlCompositeElementTypes.PATH_FUNCTION_EXPRESSION);
    }
}
