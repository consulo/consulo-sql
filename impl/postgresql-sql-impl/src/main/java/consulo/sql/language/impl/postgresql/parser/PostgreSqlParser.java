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

package consulo.sql.language.impl.postgresql.parser;

import consulo.language.ast.IElementType;
import consulo.language.parser.PsiBuilder;
import consulo.sql.language.impl.parser.SqlParser;
import consulo.sql.language.impl.postgresql.PostgreSqlTokenTypes;
import consulo.sql.language.impl.psi.SqlCompositeElementTypes;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;
import consulo.sql.language.impl.psi.SqlTokenType;
import consulo.sql.language.localize.SqlLocalize;

/**
 * PostgreSQL parser with CREATE FUNCTION/PROCEDURE and PL/pgSQL block support.
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public class PostgreSqlParser extends SqlParser {
    /**
     * PostgreSQL form: {@code ANALYZE [VERBOSE] [tbl [, tbl]…]}. There is no
     * {@code TABLE} keyword; the table list is optional.
     */
    @Override
    protected void parseAnalyzeStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.ANALYZE_KEYWORD, SqlLocalize.parserStatementExpected());

        if (isToken(builder, PostgreSqlTokenTypes.VERBOSE_KEYWORD)) {
            builder.advanceLexer();
        }

        if (isIdentifier(builder)) {
            parseAnalyzeTableRef(builder);
            while (isToken(builder, SqlTokenType.COMMA)) {
                builder.advanceLexer();
                parseAnalyzeTableRef(builder);
            }
        }

        mark.done(SqlCompositeElementTypes.ANALYZE_STATEMENT);
    }

    @Override
    protected void parseStatement(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        if (token == null) {
            return;
        }

        if (token == SqlKeywordTokenTypes.CREATE_KEYWORD) {
            parseCreateStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.DROP_KEYWORD) {
            parseDropStatement(builder);
        }
        else {
            super.parseStatement(builder);
        }
    }

    @Override
    protected void parseCreateStatement(PsiBuilder builder) {
        IElementType next = builder.lookAhead(1);

        if (next == SqlKeywordTokenTypes.FUNCTION_KEYWORD || next == SqlKeywordTokenTypes.PROCEDURE_KEYWORD) {
            parseCreateFunctionOrProcedure(builder, false);
            return;
        }

        if (next == SqlKeywordTokenTypes.OR_KEYWORD) {
            // CREATE OR REPLACE FUNCTION/PROCEDURE
            IElementType afterReplace = builder.lookAhead(3);
            if (afterReplace == SqlKeywordTokenTypes.FUNCTION_KEYWORD || afterReplace == SqlKeywordTokenTypes.PROCEDURE_KEYWORD) {
                parseCreateFunctionOrProcedure(builder, true);
                return;
            }
        }

        super.parseCreateStatement(builder);
    }

    @Override
    protected void parseDropStatement(PsiBuilder builder) {
        IElementType next = builder.lookAhead(1);

        if (next == SqlKeywordTokenTypes.FUNCTION_KEYWORD) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer(); // DROP
            builder.advanceLexer(); // FUNCTION

            if (isToken(builder, SqlKeywordTokenTypes.IF_KEYWORD)) {
                builder.advanceLexer();
                expectKeyword(builder, SqlKeywordTokenTypes.EXISTS_KEYWORD, SqlLocalize.parserExistsExpected());
            }

            parseQualifiedName(builder);

            if (isToken(builder, SqlTokenType.LPAR)) {
                parseFunctionParameters(builder);
            }

            if (isToken(builder, SqlKeywordTokenTypes.CASCADE_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.RESTRICT_KEYWORD)) {
                builder.advanceLexer();
            }

            mark.done(SqlCompositeElementTypes.DROP_FUNCTION_STATEMENT);
            return;
        }

        if (next == SqlKeywordTokenTypes.PROCEDURE_KEYWORD) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer(); // DROP
            builder.advanceLexer(); // PROCEDURE

            if (isToken(builder, SqlKeywordTokenTypes.IF_KEYWORD)) {
                builder.advanceLexer();
                expectKeyword(builder, SqlKeywordTokenTypes.EXISTS_KEYWORD, SqlLocalize.parserExistsExpected());
            }

            parseQualifiedName(builder);

            if (isToken(builder, SqlTokenType.LPAR)) {
                parseFunctionParameters(builder);
            }

            if (isToken(builder, SqlKeywordTokenTypes.CASCADE_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.RESTRICT_KEYWORD)) {
                builder.advanceLexer();
            }

            mark.done(SqlCompositeElementTypes.DROP_PROCEDURE_STATEMENT);
            return;
        }

        super.parseDropStatement(builder);
    }

    private void parseCreateFunctionOrProcedure(PsiBuilder builder, boolean orReplace) {
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer(); // CREATE

        if (orReplace) {
            builder.advanceLexer(); // OR
            expectKeyword(builder, SqlKeywordTokenTypes.REPLACE_KEYWORD, SqlLocalize.parserOrReplaceExpected());
        }

        boolean isFunction = isToken(builder, SqlKeywordTokenTypes.FUNCTION_KEYWORD);
        builder.advanceLexer(); // FUNCTION or PROCEDURE

        parseQualifiedName(builder);

        parseFunctionParameters(builder);

        // RETURNS clause (function only)
        if (isToken(builder, SqlKeywordTokenTypes.RETURNS_KEYWORD)) {
            builder.advanceLexer(); // RETURNS

            if (isToken(builder, PostgreSqlTokenTypes.SETOF_KEYWORD)) {
                builder.advanceLexer(); // SETOF
                parseDataType(builder);
            }
            else if (isToken(builder, SqlKeywordTokenTypes.TABLE_KEYWORD)) {
                builder.advanceLexer(); // TABLE
                parseFunctionParameters(builder);
            }
            else if (isTokenIdentifierText(builder, "VOID")) {
                builder.advanceLexer();
            }
            else {
                parseDataType(builder);
            }
        }

        // Function attributes: LANGUAGE, IMMUTABLE, STABLE, VOLATILE, STRICT, etc.
        parseFunctionAttributes(builder);

        // AS $$ ... $$ body
        if (isToken(builder, SqlKeywordTokenTypes.AS_KEYWORD)) {
            builder.advanceLexer(); // AS

            if (isToken(builder, SqlTokenType.DOLLAR_QUOTED_STRING)) {
                builder.advanceLexer();
            }
            else if (isToken(builder, SqlTokenType.SINGLE_QUOTED_LITERAL)) {
                builder.advanceLexer();
            }
            else {
                builder.error(SqlLocalize.parserDollarQuoteExpected());
            }
        }

        // trailing attributes after body
        parseFunctionAttributes(builder);

        mark.done(isFunction ? SqlCompositeElementTypes.CREATE_FUNCTION_STATEMENT : SqlCompositeElementTypes.CREATE_PROCEDURE_STATEMENT);
    }

    private void parseFunctionParameters(PsiBuilder builder) {
        PsiBuilder.Marker listMark = builder.mark();
        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());

        if (!isToken(builder, SqlTokenType.RPAR)) {
            parseSingleParameter(builder);
            while (isToken(builder, SqlTokenType.COMMA)) {
                builder.advanceLexer();
                parseSingleParameter(builder);
            }
        }

        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        listMark.done(SqlCompositeElementTypes.PARAMETER_LIST);
    }

    private void parseSingleParameter(PsiBuilder builder) {
        PsiBuilder.Marker paramMark = builder.mark();

        // optional mode: IN, OUT, INOUT, VARIADIC
        if (isToken(builder, SqlKeywordTokenTypes.IN_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.OUT_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.INOUT_KEYWORD)
            || isToken(builder, PostgreSqlTokenTypes.VARIADIC_KEYWORD)) {
            builder.advanceLexer();
        }

        // parameter name (optional - could just be a type)
        // heuristic: if next token after identifier is also an identifier/keyword type, then current is param name
        if (isIdentifier(builder) && isLikelyParamName(builder)) {
            builder.advanceLexer(); // param name
        }

        // data type
        parseDataType(builder);

        // optional DEFAULT
        if (isToken(builder, SqlKeywordTokenTypes.DEFAULT_KEYWORD)) {
            builder.advanceLexer();
            parseExpression(builder);
        }

        paramMark.done(SqlCompositeElementTypes.PARAMETER_DEFINITION);
    }

    private boolean isLikelyParamName(PsiBuilder builder) {
        // Look ahead: if the next token is also an identifier or a type keyword,
        // then current token is the parameter name
        IElementType next = builder.lookAhead(1);
        if (next == null) {
            return false;
        }
        return SqlTokenType.IDENTIFIERS.contains(next)
            || next == SqlKeywordTokenTypes.INT_KEYWORD
            || next == SqlKeywordTokenTypes.INTEGER_KEYWORD
            || next == SqlKeywordTokenTypes.VARCHAR_KEYWORD
            || next == SqlKeywordTokenTypes.CHAR_KEYWORD
            || next == SqlKeywordTokenTypes.BOOLEAN_KEYWORD
            || next == SqlKeywordTokenTypes.NUMERIC_KEYWORD
            || next == SqlKeywordTokenTypes.REAL_KEYWORD
            || next == SqlKeywordTokenTypes.FLOAT_KEYWORD
            || next == SqlKeywordTokenTypes.DOUBLE_KEYWORD
            || next == SqlKeywordTokenTypes.SMALLINT_KEYWORD
            || next == SqlKeywordTokenTypes.BIGINT_KEYWORD
            || next == SqlKeywordTokenTypes.DATE_KEYWORD
            || next == SqlKeywordTokenTypes.TIME_KEYWORD
            || next == SqlKeywordTokenTypes.TIMESTAMP_KEYWORD
            || next == SqlKeywordTokenTypes.INTERVAL_KEYWORD
            || next == SqlKeywordTokenTypes.DECIMAL_KEYWORD
            || next == PostgreSqlTokenTypes.BYTEA_KEYWORD
            || next == PostgreSqlTokenTypes.SERIAL_KEYWORD
            || next == PostgreSqlTokenTypes.RECORD_KEYWORD
            || next == SqlKeywordTokenTypes.TEXT_KEYWORD;
    }

    private void parseFunctionAttributes(PsiBuilder builder) {
        while (true) {
            if (isToken(builder, SqlKeywordTokenTypes.LANGUAGE_KEYWORD)) {
                builder.advanceLexer(); // LANGUAGE
                if (isIdentifier(builder)) {
                    builder.advanceLexer(); // language name (plpgsql, sql, etc.)
                }
                else {
                    builder.error(SqlLocalize.parserIdentifierExpected());
                }
            }
            else if (isToken(builder, PostgreSqlTokenTypes.IMMUTABLE_KEYWORD)
                || isToken(builder, PostgreSqlTokenTypes.STABLE_KEYWORD)
                || isToken(builder, PostgreSqlTokenTypes.VOLATILE_KEYWORD)
                || isToken(builder, PostgreSqlTokenTypes.STRICT_KEYWORD)) {
                builder.advanceLexer();
            }
            else if (isToken(builder, PostgreSqlTokenTypes.PARALLEL_KEYWORD)) {
                builder.advanceLexer(); // PARALLEL
                // SAFE/UNSAFE/RESTRICTED
                if (isTokenIdentifierText(builder, "SAFE")
                    || isTokenIdentifierText(builder, "UNSAFE")
                    || isTokenIdentifierText(builder, "RESTRICTED")) {
                    builder.advanceLexer();
                }
            }
            else if (isTokenIdentifierText(builder, "SECURITY")) {
                builder.advanceLexer(); // SECURITY
                if (isTokenIdentifierText(builder, "DEFINER") || isTokenIdentifierText(builder, "INVOKER")) {
                    builder.advanceLexer();
                }
            }
            else if (isTokenIdentifierText(builder, "COST")) {
                builder.advanceLexer(); // COST
                if (isToken(builder, SqlTokenType.NUMBER)) {
                    builder.advanceLexer();
                }
            }
            else if (isTokenIdentifierText(builder, "ROWS")) {
                builder.advanceLexer(); // ROWS
                if (isToken(builder, SqlTokenType.NUMBER)) {
                    builder.advanceLexer();
                }
            }
            else {
                break;
            }
        }
    }
}
