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

package consulo.sql.language.impl.parser;

import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersion;
import consulo.localize.LocalizeValue;
import consulo.sql.language.impl.psi.SqlCompositeElementTypes;
import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;
import consulo.sql.language.impl.psi.SqlTokenType;
import consulo.sql.language.localize.SqlLocalize;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2026-02-28
 */
public class SqlParser implements PsiParser {
    private static final TokenSet ADDITIVE_OPS = TokenSet.create(SqlTokenType.PLUS, SqlTokenType.MINUS, SqlTokenType.CONCAT);

    private static final TokenSet MULTIPLICATIVE_OPS = TokenSet.create(SqlTokenType.ASTERISK, SqlTokenType.SLASH);

    private static final TokenSet COMPARISON_OPS = TokenSet.create(
        SqlTokenType.EQ, SqlTokenType.NE, SqlTokenType.LT, SqlTokenType.GT, SqlTokenType.LE, SqlTokenType.GE
    );

    @Nonnull
    @Override
    public ASTNode parse(@Nonnull IElementType root, @Nonnull PsiBuilder builder, @Nonnull LanguageVersion languageVersion) {
        PsiBuilder.Marker rootMark = builder.mark();
        while (!builder.eof()) {
            parseStatement(builder);
        }
        rootMark.done(root);
        return builder.getTreeBuilt();
    }

    protected void parseStatement(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        if (token == null) {
            return;
        }

        if (token == SqlTokenType.SEMICOLON) {
            builder.advanceLexer();
            return;
        }

        if (token == SqlKeywordTokenTypes.SELECT_KEYWORD) {
            parseSelectStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.INSERT_KEYWORD) {
            parseInsertStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.UPDATE_KEYWORD) {
            parseUpdateStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.DELETE_KEYWORD) {
            parseDeleteStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.CREATE_KEYWORD) {
            parseCreateStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.DROP_KEYWORD) {
            parseDropStatement(builder);
        }
        else if (token == SqlKeywordTokenTypes.ALTER_KEYWORD) {
            parseAlterTableStatement(builder);
        }
        else {
            builder.error(SqlLocalize.parserStatementExpected());
            builder.advanceLexer();
        }
    }

    // =================== SELECT ===================

    private void parseSelectStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        parseQueryExpression(builder);
        mark.done(SqlCompositeElementTypes.SELECT_STATEMENT);
    }

    protected void parseQueryExpression(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        parseSelectClause(builder);

        if (isToken(builder, SqlKeywordTokenTypes.FROM_KEYWORD)) {
            parseFromClause(builder);
        }

        if (isToken(builder, SqlKeywordTokenTypes.WHERE_KEYWORD)) {
            parseWhereClause(builder);
        }

        if (isToken(builder, SqlKeywordTokenTypes.GROUP_KEYWORD)) {
            parseGroupByClause(builder);
        }

        if (isToken(builder, SqlKeywordTokenTypes.HAVING_KEYWORD)) {
            parseHavingClause(builder);
        }

        // UNION / INTERSECT / EXCEPT
        while (isToken(builder, SqlKeywordTokenTypes.UNION_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.INTERSECT_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.EXCEPT_KEYWORD)) {
            builder.advanceLexer(); // UNION / INTERSECT / EXCEPT

            if (isToken(builder, SqlKeywordTokenTypes.ALL_KEYWORD)) {
                builder.advanceLexer();
            }

            parseSelectClause(builder);

            if (isToken(builder, SqlKeywordTokenTypes.FROM_KEYWORD)) {
                parseFromClause(builder);
            }

            if (isToken(builder, SqlKeywordTokenTypes.WHERE_KEYWORD)) {
                parseWhereClause(builder);
            }

            if (isToken(builder, SqlKeywordTokenTypes.GROUP_KEYWORD)) {
                parseGroupByClause(builder);
            }

            if (isToken(builder, SqlKeywordTokenTypes.HAVING_KEYWORD)) {
                parseHavingClause(builder);
            }
        }

        if (isToken(builder, SqlKeywordTokenTypes.ORDER_KEYWORD)) {
            parseOrderByClause(builder);
        }

        mark.done(SqlCompositeElementTypes.QUERY_EXPRESSION);
    }

    private void parseSelectClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectKeyword(builder, SqlKeywordTokenTypes.SELECT_KEYWORD, SqlLocalize.parserSelectExpected());

        if (isToken(builder, SqlKeywordTokenTypes.DISTINCT_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.ALL_KEYWORD)) {
            builder.advanceLexer();
        }

        parseSelectItemList(builder);

        mark.done(SqlCompositeElementTypes.SELECT_CLAUSE);
    }

    private void parseSelectItemList(PsiBuilder builder) {
        parseSelectItem(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseSelectItem(builder);
        }
    }

    private void parseSelectItem(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        if (isToken(builder, SqlTokenType.ASTERISK)) {
            PsiBuilder.Marker starMark = builder.mark();
            builder.advanceLexer();
            starMark.done(SqlCompositeElementTypes.STAR_EXPRESSION);
        }
        else {
            parseExpression(builder);

            if (isToken(builder, SqlKeywordTokenTypes.AS_KEYWORD)) {
                PsiBuilder.Marker aliasMark = builder.mark();
                builder.advanceLexer();
                expectIdentifier(builder, SqlLocalize.parserAliasNameExpected());
                aliasMark.done(SqlCompositeElementTypes.ALIAS);
            }
            else if (isIdentifier(builder) && !isStatementBoundary(builder)) {
                PsiBuilder.Marker aliasMark = builder.mark();
                builder.advanceLexer();
                aliasMark.done(SqlCompositeElementTypes.ALIAS);
            }
        }

        mark.done(SqlCompositeElementTypes.COLUMN_EXPRESSION);
    }

    // =================== FROM ===================

    private void parseFromClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectKeyword(builder, SqlKeywordTokenTypes.FROM_KEYWORD, SqlLocalize.parserFromExpected());

        parseTableReferenceList(builder);

        mark.done(SqlCompositeElementTypes.FROM_CLAUSE);
    }

    private void parseTableReferenceList(PsiBuilder builder) {
        parseTableReference(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseTableReference(builder);
        }
    }

    private void parseTableReference(PsiBuilder builder) {
        parseTablePrimary(builder);

        while (isJoinKeyword(builder)) {
            parseJoin(builder);
        }
    }

    private void parseTablePrimary(PsiBuilder builder) {
        if (isToken(builder, SqlTokenType.LPAR)) {
            if (lookAheadIs(builder, SqlKeywordTokenTypes.SELECT_KEYWORD)) {
                PsiBuilder.Marker mark = builder.mark();
                builder.advanceLexer(); // (
                parseQueryExpression(builder);
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
                parseOptionalAlias(builder);
                mark.done(SqlCompositeElementTypes.SUBQUERY_EXPRESSION);
            }
            else {
                PsiBuilder.Marker mark = builder.mark();
                builder.advanceLexer(); // (
                parseTableReferenceList(builder);
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
                mark.done(SqlCompositeElementTypes.PARENTHESIZED_EXPRESSION);
            }
        }
        else {
            PsiBuilder.Marker mark = builder.mark();
            parseQualifiedName(builder);
            parseOptionalAlias(builder);
            mark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);
        }
    }

    private void parseOptionalAlias(PsiBuilder builder) {
        if (isToken(builder, SqlKeywordTokenTypes.AS_KEYWORD)) {
            PsiBuilder.Marker aliasMark = builder.mark();
            builder.advanceLexer();
            expectIdentifier(builder, SqlLocalize.parserAliasNameExpected());
            aliasMark.done(SqlCompositeElementTypes.ALIAS);
        }
        else if (isIdentifier(builder) && !isStatementBoundary(builder)) {
            PsiBuilder.Marker aliasMark = builder.mark();
            builder.advanceLexer();
            aliasMark.done(SqlCompositeElementTypes.ALIAS);
        }
    }

    private boolean isJoinKeyword(PsiBuilder builder) {
        IElementType t = builder.getTokenType();
        return t == SqlKeywordTokenTypes.JOIN_KEYWORD
            || t == SqlKeywordTokenTypes.INNER_KEYWORD
            || t == SqlKeywordTokenTypes.LEFT_KEYWORD
            || t == SqlKeywordTokenTypes.RIGHT_KEYWORD
            || t == SqlKeywordTokenTypes.FULL_KEYWORD
            || t == SqlKeywordTokenTypes.CROSS_KEYWORD
            || t == SqlKeywordTokenTypes.NATURAL_KEYWORD;
    }

    private void parseJoin(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        // optional: NATURAL
        if (isToken(builder, SqlKeywordTokenTypes.NATURAL_KEYWORD)) {
            builder.advanceLexer();
        }

        // optional: INNER / LEFT [OUTER] / RIGHT [OUTER] / FULL [OUTER] / CROSS
        if (isToken(builder, SqlKeywordTokenTypes.INNER_KEYWORD)) {
            builder.advanceLexer();
        }
        else if (isToken(builder, SqlKeywordTokenTypes.LEFT_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.RIGHT_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.FULL_KEYWORD)) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.OUTER_KEYWORD)) {
                builder.advanceLexer();
            }
        }
        else if (isToken(builder, SqlKeywordTokenTypes.CROSS_KEYWORD)) {
            builder.advanceLexer();
        }

        expectKeyword(builder, SqlKeywordTokenTypes.JOIN_KEYWORD, SqlLocalize.parserJoinExpected());

        parseTablePrimary(builder);

        if (isToken(builder, SqlKeywordTokenTypes.ON_KEYWORD)) {
            PsiBuilder.Marker condMark = builder.mark();
            builder.advanceLexer();
            parseExpression(builder);
            condMark.done(SqlCompositeElementTypes.JOIN_CONDITION);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.USING_KEYWORD)) {
            PsiBuilder.Marker condMark = builder.mark();
            builder.advanceLexer();
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseIdentifierList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            condMark.done(SqlCompositeElementTypes.JOIN_CONDITION);
        }

        mark.done(SqlCompositeElementTypes.JOIN_EXPRESSION);
    }

    // =================== WHERE ===================

    private void parseWhereClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.WHERE_KEYWORD, SqlLocalize.parserWhereExpected());
        parseExpression(builder);
        mark.done(SqlCompositeElementTypes.WHERE_CLAUSE);
    }

    // =================== GROUP BY ===================

    private void parseGroupByClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.GROUP_KEYWORD, SqlLocalize.parserGroupExpected());
        expectKeyword(builder, SqlKeywordTokenTypes.BY_KEYWORD, SqlLocalize.parserByExpected());
        parseExpressionList(builder);
        mark.done(SqlCompositeElementTypes.GROUP_BY_CLAUSE);
    }

    // =================== HAVING ===================

    private void parseHavingClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.HAVING_KEYWORD, SqlLocalize.parserHavingExpected());
        parseExpression(builder);
        mark.done(SqlCompositeElementTypes.HAVING_CLAUSE);
    }

    // =================== ORDER BY ===================

    private void parseOrderByClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.ORDER_KEYWORD, SqlLocalize.parserOrderExpected());
        expectKeyword(builder, SqlKeywordTokenTypes.BY_KEYWORD, SqlLocalize.parserByExpected());

        parseOrderItem(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseOrderItem(builder);
        }

        mark.done(SqlCompositeElementTypes.ORDER_BY_CLAUSE);
    }

    private void parseOrderItem(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        parseExpression(builder);

        if (isToken(builder, SqlKeywordTokenTypes.ASC_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.DESC_KEYWORD)) {
            builder.advanceLexer();
        }

        if (isToken(builder, SqlKeywordTokenTypes.NULL_KEYWORD)) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.FIRST_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.LAST_KEYWORD)) {
                builder.advanceLexer();
            }
        }

        mark.done(SqlCompositeElementTypes.ORDER_ITEM);
    }

    // =================== INSERT ===================

    private void parseInsertStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectKeyword(builder, SqlKeywordTokenTypes.INSERT_KEYWORD, SqlLocalize.parserInsertExpected());
        expectKeyword(builder, SqlKeywordTokenTypes.INTO_KEYWORD, SqlLocalize.parserIntoExpected());

        PsiBuilder.Marker tableMark = builder.mark();
        parseQualifiedName(builder);
        tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

        if (isToken(builder, SqlTokenType.LPAR)) {
            PsiBuilder.Marker colListMark = builder.mark();
            builder.advanceLexer();
            parseIdentifierList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            colListMark.done(SqlCompositeElementTypes.COLUMN_LIST);
        }

        if (isToken(builder, SqlKeywordTokenTypes.VALUES_KEYWORD)) {
            parseValuesClause(builder);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.SELECT_KEYWORD)) {
            parseQueryExpression(builder);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.DEFAULT_KEYWORD)) {
            builder.advanceLexer();
            expectKeyword(builder, SqlKeywordTokenTypes.VALUES_KEYWORD, SqlLocalize.parserValuesExpected());
        }
        else {
            builder.error(SqlLocalize.parserValuesOrSelectExpected());
        }

        mark.done(SqlCompositeElementTypes.INSERT_STATEMENT);
    }

    private void parseValuesClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.VALUES_KEYWORD, SqlLocalize.parserValuesExpected());

        parseValueRow(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseValueRow(builder);
        }

        mark.done(SqlCompositeElementTypes.VALUES_CLAUSE);
    }

    private void parseValueRow(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
        parseExpressionList(builder);
        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        mark.done(SqlCompositeElementTypes.VALUE_LIST);
    }

    // =================== UPDATE ===================

    private void parseUpdateStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectKeyword(builder, SqlKeywordTokenTypes.UPDATE_KEYWORD, SqlLocalize.parserUpdateExpected());

        PsiBuilder.Marker tableMark = builder.mark();
        parseQualifiedName(builder);
        parseOptionalAlias(builder);
        tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

        parseSetClause(builder);

        if (isToken(builder, SqlKeywordTokenTypes.WHERE_KEYWORD)) {
            parseWhereClause(builder);
        }

        mark.done(SqlCompositeElementTypes.UPDATE_STATEMENT);
    }

    private void parseSetClause(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.SET_KEYWORD, SqlLocalize.parserSetExpected());

        parseAssignment(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseAssignment(builder);
        }

        mark.done(SqlCompositeElementTypes.SET_CLAUSE);
    }

    private void parseAssignment(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        parseQualifiedName(builder);
        expectToken(builder, SqlTokenType.EQ, SqlLocalize.parserEqExpected());
        parseExpression(builder);
        mark.done(SqlCompositeElementTypes.ASSIGNMENT_EXPRESSION);
    }

    // =================== DELETE ===================

    private void parseDeleteStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectKeyword(builder, SqlKeywordTokenTypes.DELETE_KEYWORD, SqlLocalize.parserDeleteExpected());
        expectKeyword(builder, SqlKeywordTokenTypes.FROM_KEYWORD, SqlLocalize.parserFromExpected());

        PsiBuilder.Marker tableMark = builder.mark();
        parseQualifiedName(builder);
        parseOptionalAlias(builder);
        tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

        if (isToken(builder, SqlKeywordTokenTypes.WHERE_KEYWORD)) {
            parseWhereClause(builder);
        }

        mark.done(SqlCompositeElementTypes.DELETE_STATEMENT);
    }

    // =================== CREATE ===================

    protected void parseCreateStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.CREATE_KEYWORD, SqlLocalize.parserCreateExpected());

        if (isToken(builder, SqlKeywordTokenTypes.TABLE_KEYWORD)) {
            builder.advanceLexer();

            PsiBuilder.Marker tableMark = builder.mark();
            parseQualifiedName(builder);
            tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseTableElementList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());

            mark.done(SqlCompositeElementTypes.CREATE_TABLE_STATEMENT);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.VIEW_KEYWORD)) {
            builder.advanceLexer();

            PsiBuilder.Marker viewMark = builder.mark();
            parseQualifiedName(builder);
            viewMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

            expectKeyword(builder, SqlKeywordTokenTypes.AS_KEYWORD, SqlLocalize.parserAsExpected());

            parseQueryExpression(builder);

            mark.done(SqlCompositeElementTypes.CREATE_VIEW_STATEMENT);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.UNIQUE_KEYWORD) || isTokenIdentifierText(builder, "INDEX")) {
            if (isToken(builder, SqlKeywordTokenTypes.UNIQUE_KEYWORD)) {
                builder.advanceLexer();
            }

            // INDEX is not a reserved keyword, consume it as identifier
            if (isTokenIdentifierText(builder, "INDEX")) {
                builder.advanceLexer();
            }
            else {
                builder.error(SqlLocalize.parserIndexExpected());
            }

            PsiBuilder.Marker nameMark = builder.mark();
            parseQualifiedName(builder);
            nameMark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);

            expectKeyword(builder, SqlKeywordTokenTypes.ON_KEYWORD, SqlLocalize.parserOnExpected());

            PsiBuilder.Marker tableMark = builder.mark();
            parseQualifiedName(builder);
            tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseIdentifierList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());

            mark.done(SqlCompositeElementTypes.CREATE_INDEX_STATEMENT);
        }
        else {
            builder.error(SqlLocalize.parserTableViewOrIndexExpected());
            mark.done(SqlCompositeElementTypes.CREATE_TABLE_STATEMENT);
        }
    }

    private void parseTableElementList(PsiBuilder builder) {
        parseTableElement(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseTableElement(builder);
        }
    }

    private void parseTableElement(PsiBuilder builder) {
        if (isToken(builder, SqlKeywordTokenTypes.CONSTRAINT_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.PRIMARY_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.UNIQUE_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.FOREIGN_KEYWORD)
            || isToken(builder, SqlKeywordTokenTypes.CHECK_KEYWORD)) {
            parseTableConstraint(builder);
        }
        else {
            parseColumnDefinition(builder);
        }
    }

    private void parseColumnDefinition(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectIdentifier(builder, SqlLocalize.parserColumnNameExpected());

        parseDataType(builder);

        while (isColumnConstraintStart(builder)) {
            parseColumnConstraint(builder);
        }

        mark.done(SqlCompositeElementTypes.COLUMN_DEFINITION);
    }

    protected void parseDataType(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        IElementType token = builder.getTokenType();
        if (token == null) {
            builder.error(SqlLocalize.parserDataTypeExpected());
            mark.drop();
            return;
        }

        boolean consumed = false;
        if (token instanceof SqlKeywordElementType) {
            builder.advanceLexer();
            consumed = true;

            // DOUBLE PRECISION, CHARACTER VARYING, etc.
            IElementType next = builder.getTokenType();
            if (next != null && next instanceof SqlKeywordElementType) {
                if (next == SqlKeywordTokenTypes.PRECISION_KEYWORD
                    || next == SqlKeywordTokenTypes.VARYING_KEYWORD) {
                    builder.advanceLexer();
                }
            }
        }
        else if (SqlTokenType.IDENTIFIERS.contains(token)) {
            builder.advanceLexer();
            consumed = true;
        }

        if (!consumed) {
            builder.error(SqlLocalize.parserDataTypeExpected());
            mark.drop();
            return;
        }

        // optional (precision, scale)
        if (isToken(builder, SqlTokenType.LPAR)) {
            builder.advanceLexer();
            expectToken(builder, SqlTokenType.NUMBER, SqlLocalize.parserPrecisionExpected());
            if (isToken(builder, SqlTokenType.COMMA)) {
                builder.advanceLexer();
                expectToken(builder, SqlTokenType.NUMBER, SqlLocalize.parserScaleExpected());
            }
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        }

        mark.done(SqlCompositeElementTypes.TYPE_EXPRESSION);
    }

    private boolean isColumnConstraintStart(PsiBuilder builder) {
        IElementType t = builder.getTokenType();
        return t == SqlKeywordTokenTypes.NOT_KEYWORD
            || t == SqlKeywordTokenTypes.NULL_KEYWORD
            || t == SqlKeywordTokenTypes.PRIMARY_KEYWORD
            || t == SqlKeywordTokenTypes.UNIQUE_KEYWORD
            || t == SqlKeywordTokenTypes.DEFAULT_KEYWORD
            || t == SqlKeywordTokenTypes.CHECK_KEYWORD
            || t == SqlKeywordTokenTypes.REFERENCES_KEYWORD
            || t == SqlKeywordTokenTypes.CONSTRAINT_KEYWORD;
    }

    private void parseColumnConstraint(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        if (isToken(builder, SqlKeywordTokenTypes.CONSTRAINT_KEYWORD)) {
            builder.advanceLexer();
            expectIdentifier(builder, SqlLocalize.parserConstraintNameExpected());
        }

        if (isToken(builder, SqlKeywordTokenTypes.NOT_KEYWORD)) {
            builder.advanceLexer();
            expectKeyword(builder, SqlKeywordTokenTypes.NULL_KEYWORD, SqlLocalize.parserNullExpected());
        }
        else if (isToken(builder, SqlKeywordTokenTypes.NULL_KEYWORD)) {
            builder.advanceLexer();
        }
        else if (isToken(builder, SqlKeywordTokenTypes.PRIMARY_KEYWORD)) {
            builder.advanceLexer();
            expectKeyword(builder, SqlKeywordTokenTypes.KEY_KEYWORD, SqlLocalize.parserKeyExpected());
        }
        else if (isToken(builder, SqlKeywordTokenTypes.UNIQUE_KEYWORD)) {
            builder.advanceLexer();
        }
        else if (isToken(builder, SqlKeywordTokenTypes.DEFAULT_KEYWORD)) {
            builder.advanceLexer();
            parseExpression(builder);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.CHECK_KEYWORD)) {
            builder.advanceLexer();
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseExpression(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        }
        else if (isToken(builder, SqlKeywordTokenTypes.REFERENCES_KEYWORD)) {
            builder.advanceLexer();
            parseQualifiedName(builder);
            if (isToken(builder, SqlTokenType.LPAR)) {
                builder.advanceLexer();
                parseIdentifierList(builder);
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            }
            parseReferentialActions(builder);
        }

        mark.done(SqlCompositeElementTypes.COLUMN_CONSTRAINT);
    }

    private void parseTableConstraint(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        if (isToken(builder, SqlKeywordTokenTypes.CONSTRAINT_KEYWORD)) {
            builder.advanceLexer();
            expectIdentifier(builder, SqlLocalize.parserConstraintNameExpected());
        }

        if (isToken(builder, SqlKeywordTokenTypes.PRIMARY_KEYWORD)) {
            builder.advanceLexer();
            expectKeyword(builder, SqlKeywordTokenTypes.KEY_KEYWORD, SqlLocalize.parserKeyExpected());
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseIdentifierList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        }
        else if (isToken(builder, SqlKeywordTokenTypes.UNIQUE_KEYWORD)) {
            builder.advanceLexer();
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseIdentifierList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        }
        else if (isToken(builder, SqlKeywordTokenTypes.FOREIGN_KEYWORD)) {
            builder.advanceLexer();
            expectKeyword(builder, SqlKeywordTokenTypes.KEY_KEYWORD, SqlLocalize.parserKeyExpected());
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseIdentifierList(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            expectKeyword(builder, SqlKeywordTokenTypes.REFERENCES_KEYWORD, SqlLocalize.parserReferencesExpected());
            parseQualifiedName(builder);
            if (isToken(builder, SqlTokenType.LPAR)) {
                builder.advanceLexer();
                parseIdentifierList(builder);
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            }
            parseReferentialActions(builder);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.CHECK_KEYWORD)) {
            builder.advanceLexer();
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseExpression(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        }

        mark.done(SqlCompositeElementTypes.TABLE_CONSTRAINT);
    }

    private void parseReferentialActions(PsiBuilder builder) {
        // ON DELETE / ON UPDATE action
        while (isToken(builder, SqlKeywordTokenTypes.ON_KEYWORD)) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.DELETE_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.UPDATE_KEYWORD)) {
                builder.advanceLexer();
            }
            else {
                builder.error(SqlLocalize.parserDeleteOrUpdateExpected());
                break;
            }

            if (isToken(builder, SqlKeywordTokenTypes.CASCADE_KEYWORD)) {
                builder.advanceLexer();
            }
            else if (isToken(builder, SqlKeywordTokenTypes.SET_KEYWORD)) {
                builder.advanceLexer();
                if (isToken(builder, SqlKeywordTokenTypes.NULL_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.DEFAULT_KEYWORD)) {
                    builder.advanceLexer();
                }
            }
            else if (isToken(builder, SqlKeywordTokenTypes.RESTRICT_KEYWORD)) {
                builder.advanceLexer();
            }
            else if (isToken(builder, SqlKeywordTokenTypes.NO_KEYWORD)) {
                builder.advanceLexer();
                expectKeyword(builder, SqlKeywordTokenTypes.ACTION_KEYWORD, SqlLocalize.parserActionExpected());
            }
        }
    }

    // =================== DROP ===================

    protected void parseDropStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.DROP_KEYWORD, SqlLocalize.parserDropExpected());

        if (isToken(builder, SqlKeywordTokenTypes.TABLE_KEYWORD)) {
            builder.advanceLexer();

            if (isToken(builder, SqlKeywordTokenTypes.IF_KEYWORD)) {
                builder.advanceLexer();
                expectKeyword(builder, SqlKeywordTokenTypes.EXISTS_KEYWORD, SqlLocalize.parserExistsExpected());
            }

            PsiBuilder.Marker tableMark = builder.mark();
            parseQualifiedName(builder);
            tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

            if (isToken(builder, SqlKeywordTokenTypes.CASCADE_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.RESTRICT_KEYWORD)) {
                builder.advanceLexer();
            }

            mark.done(SqlCompositeElementTypes.DROP_TABLE_STATEMENT);
        }
        else if (isToken(builder, SqlKeywordTokenTypes.VIEW_KEYWORD)) {
            builder.advanceLexer();

            PsiBuilder.Marker viewMark = builder.mark();
            parseQualifiedName(builder);
            viewMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

            mark.done(SqlCompositeElementTypes.DROP_VIEW_STATEMENT);
        }
        else if (isTokenIdentifierText(builder, "INDEX")) {
            builder.advanceLexer();

            PsiBuilder.Marker indexMark = builder.mark();
            parseQualifiedName(builder);
            indexMark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);

            mark.done(SqlCompositeElementTypes.DROP_INDEX_STATEMENT);
        }
        else {
            builder.error(SqlLocalize.parserTableViewOrIndexExpected());
            mark.done(SqlCompositeElementTypes.DROP_TABLE_STATEMENT);
        }
    }

    // =================== ALTER TABLE ===================

    private void parseAlterTableStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        expectKeyword(builder, SqlKeywordTokenTypes.ALTER_KEYWORD, SqlLocalize.parserAlterExpected());
        expectKeyword(builder, SqlKeywordTokenTypes.TABLE_KEYWORD, SqlLocalize.parserTableExpected());

        PsiBuilder.Marker tableMark = builder.mark();
        parseQualifiedName(builder);
        tableMark.done(SqlCompositeElementTypes.TABLE_EXPRESSION);

        // parse alter action(s)
        if (isToken(builder, SqlKeywordTokenTypes.ADD_KEYWORD)) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.COLUMN_KEYWORD)) {
                builder.advanceLexer();
            }

            if (isToken(builder, SqlKeywordTokenTypes.CONSTRAINT_KEYWORD)
                || isToken(builder, SqlKeywordTokenTypes.PRIMARY_KEYWORD)
                || isToken(builder, SqlKeywordTokenTypes.UNIQUE_KEYWORD)
                || isToken(builder, SqlKeywordTokenTypes.FOREIGN_KEYWORD)
                || isToken(builder, SqlKeywordTokenTypes.CHECK_KEYWORD)) {
                parseTableConstraint(builder);
            }
            else {
                parseColumnDefinition(builder);
            }
        }
        else if (isToken(builder, SqlKeywordTokenTypes.DROP_KEYWORD)) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.COLUMN_KEYWORD)) {
                builder.advanceLexer();
            }
            else if (isToken(builder, SqlKeywordTokenTypes.CONSTRAINT_KEYWORD)) {
                builder.advanceLexer();
            }
            expectIdentifier(builder, SqlLocalize.parserNameExpected());

            if (isToken(builder, SqlKeywordTokenTypes.CASCADE_KEYWORD) || isToken(builder, SqlKeywordTokenTypes.RESTRICT_KEYWORD)) {
                builder.advanceLexer();
            }
        }
        else if (isToken(builder, SqlKeywordTokenTypes.ALTER_KEYWORD)) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.COLUMN_KEYWORD)) {
                builder.advanceLexer();
            }
            expectIdentifier(builder, SqlLocalize.parserColumnNameExpected());

            if (isToken(builder, SqlKeywordTokenTypes.SET_KEYWORD)) {
                builder.advanceLexer();
                if (isToken(builder, SqlKeywordTokenTypes.DEFAULT_KEYWORD)) {
                    builder.advanceLexer();
                    parseExpression(builder);
                }
                else if (isToken(builder, SqlKeywordTokenTypes.NOT_KEYWORD)) {
                    builder.advanceLexer();
                    expectKeyword(builder, SqlKeywordTokenTypes.NULL_KEYWORD, SqlLocalize.parserNullExpected());
                }
            }
            else if (isToken(builder, SqlKeywordTokenTypes.DROP_KEYWORD)) {
                builder.advanceLexer();
                if (isToken(builder, SqlKeywordTokenTypes.DEFAULT_KEYWORD)) {
                    builder.advanceLexer();
                }
                else if (isToken(builder, SqlKeywordTokenTypes.NOT_KEYWORD)) {
                    builder.advanceLexer();
                    expectKeyword(builder, SqlKeywordTokenTypes.NULL_KEYWORD, SqlLocalize.parserNullExpected());
                }
            }
        }

        mark.done(SqlCompositeElementTypes.ALTER_TABLE_STATEMENT);
    }

    // =================== EXPRESSIONS ===================

    protected void parseExpression(PsiBuilder builder) {
        parseOrExpression(builder);
    }

    private void parseOrExpression(PsiBuilder builder) {
        PsiBuilder.Marker left = builder.mark();
        parseAndExpression(builder);

        while (isToken(builder, SqlKeywordTokenTypes.OR_KEYWORD)) {
            builder.advanceLexer();
            parseAndExpression(builder);
            left.done(SqlCompositeElementTypes.BINARY_EXPRESSION);
            left = left.precede();
        }

        left.drop();
    }

    private void parseAndExpression(PsiBuilder builder) {
        PsiBuilder.Marker left = builder.mark();
        parseNotExpression(builder);

        while (isToken(builder, SqlKeywordTokenTypes.AND_KEYWORD)) {
            builder.advanceLexer();
            parseNotExpression(builder);
            left.done(SqlCompositeElementTypes.BINARY_EXPRESSION);
            left = left.precede();
        }

        left.drop();
    }

    private void parseNotExpression(PsiBuilder builder) {
        if (isToken(builder, SqlKeywordTokenTypes.NOT_KEYWORD)) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            parseNotExpression(builder);
            mark.done(SqlCompositeElementTypes.PREFIX_EXPRESSION);
        }
        else {
            parseComparisonExpression(builder);
        }
    }

    private void parseComparisonExpression(PsiBuilder builder) {
        PsiBuilder.Marker left = builder.mark();
        parseAdditiveExpression(builder);

        IElementType token = builder.getTokenType();

        if (token != null && COMPARISON_OPS.contains(token)) {
            builder.advanceLexer();
            parseAdditiveExpression(builder);
            left.done(SqlCompositeElementTypes.BINARY_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.IS_KEYWORD) {
            builder.advanceLexer();
            if (isToken(builder, SqlKeywordTokenTypes.NOT_KEYWORD)) {
                builder.advanceLexer();
            }
            if (isToken(builder, SqlKeywordTokenTypes.NULL_KEYWORD)) {
                builder.advanceLexer();
            }
            else {
                builder.error(SqlLocalize.parserNullExpected());
            }
            left.done(SqlCompositeElementTypes.IS_NULL_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.IN_KEYWORD || (token == SqlKeywordTokenTypes.NOT_KEYWORD && lookAheadTokenIs(builder, SqlKeywordTokenTypes.IN_KEYWORD))) {
            if (token == SqlKeywordTokenTypes.NOT_KEYWORD) {
                builder.advanceLexer();
            }
            builder.advanceLexer(); // IN

            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            if (isToken(builder, SqlKeywordTokenTypes.SELECT_KEYWORD)) {
                parseQueryExpression(builder);
            }
            else {
                parseExpressionList(builder);
            }
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            left.done(SqlCompositeElementTypes.IN_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.BETWEEN_KEYWORD || (token == SqlKeywordTokenTypes.NOT_KEYWORD && lookAheadTokenIs(builder, SqlKeywordTokenTypes.BETWEEN_KEYWORD))) {
            if (token == SqlKeywordTokenTypes.NOT_KEYWORD) {
                builder.advanceLexer();
            }
            builder.advanceLexer(); // BETWEEN
            parseAdditiveExpression(builder);
            expectKeyword(builder, SqlKeywordTokenTypes.AND_KEYWORD, SqlLocalize.parserAndExpected());
            parseAdditiveExpression(builder);
            left.done(SqlCompositeElementTypes.BETWEEN_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.LIKE_KEYWORD || (token == SqlKeywordTokenTypes.NOT_KEYWORD && lookAheadTokenIs(builder, SqlKeywordTokenTypes.LIKE_KEYWORD))) {
            if (token == SqlKeywordTokenTypes.NOT_KEYWORD) {
                builder.advanceLexer();
            }
            builder.advanceLexer(); // LIKE
            parseAdditiveExpression(builder);
            if (isToken(builder, SqlKeywordTokenTypes.ESCAPE_KEYWORD)) {
                builder.advanceLexer();
                parseAdditiveExpression(builder);
            }
            left.done(SqlCompositeElementTypes.LIKE_EXPRESSION);
        }
        else {
            left.drop();
        }
    }

    private void parseAdditiveExpression(PsiBuilder builder) {
        PsiBuilder.Marker left = builder.mark();
        parseMultiplicativeExpression(builder);

        while (builder.getTokenType() != null && ADDITIVE_OPS.contains(builder.getTokenType())) {
            builder.advanceLexer();
            parseMultiplicativeExpression(builder);
            left.done(SqlCompositeElementTypes.BINARY_EXPRESSION);
            left = left.precede();
        }

        left.drop();
    }

    private void parseMultiplicativeExpression(PsiBuilder builder) {
        PsiBuilder.Marker left = builder.mark();
        parseUnaryExpression(builder);

        while (builder.getTokenType() != null && MULTIPLICATIVE_OPS.contains(builder.getTokenType())) {
            builder.advanceLexer();
            parseUnaryExpression(builder);
            left.done(SqlCompositeElementTypes.BINARY_EXPRESSION);
            left = left.precede();
        }

        left.drop();
    }

    private void parseUnaryExpression(PsiBuilder builder) {
        if (isToken(builder, SqlTokenType.MINUS) || isToken(builder, SqlTokenType.PLUS)) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            parsePrimaryExpression(builder);
            mark.done(SqlCompositeElementTypes.PREFIX_EXPRESSION);
        }
        else {
            parsePrimaryExpression(builder);
        }
    }

    private void parsePrimaryExpression(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        if (token == null) {
            builder.error(SqlLocalize.parserExpressionExpected());
            return;
        }

        if (token == SqlTokenType.NUMBER) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.LITERAL_EXPRESSION);
        }
        else if (token == SqlTokenType.SINGLE_QUOTED_LITERAL) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.LITERAL_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.NULL_KEYWORD) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.LITERAL_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.TRUE_KEYWORD || token == SqlKeywordTokenTypes.FALSE_KEYWORD) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.LITERAL_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.CURRENT_DATE_KEYWORD
            || token == SqlKeywordTokenTypes.CURRENT_TIME_KEYWORD
            || token == SqlKeywordTokenTypes.CURRENT_TIMESTAMP_KEYWORD
            || token == SqlKeywordTokenTypes.CURRENT_USER_KEYWORD
            || token == SqlKeywordTokenTypes.SESSION_USER_KEYWORD
            || token == SqlKeywordTokenTypes.SYSTEM_USER_KEYWORD) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.LITERAL_EXPRESSION);
        }
        else if (token == SqlTokenType.ASTERISK) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.STAR_EXPRESSION);
        }
        else if (token == SqlTokenType.LPAR) {
            if (lookAheadIs(builder, SqlKeywordTokenTypes.SELECT_KEYWORD)) {
                PsiBuilder.Marker mark = builder.mark();
                builder.advanceLexer(); // (
                parseQueryExpression(builder);
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
                mark.done(SqlCompositeElementTypes.SUBQUERY_EXPRESSION);
            }
            else {
                PsiBuilder.Marker mark = builder.mark();
                builder.advanceLexer(); // (
                parseExpression(builder);
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
                mark.done(SqlCompositeElementTypes.PARENTHESIZED_EXPRESSION);
            }
        }
        else if (token == SqlKeywordTokenTypes.EXISTS_KEYWORD) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
            parseQueryExpression(builder);
            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            mark.done(SqlCompositeElementTypes.EXISTS_EXPRESSION);
        }
        else if (token == SqlKeywordTokenTypes.CASE_KEYWORD) {
            parseCaseExpression(builder);
        }
        else if (token == SqlKeywordTokenTypes.CAST_KEYWORD) {
            parseCastExpression(builder);
        }
        else if (isAggregateFunction(token)) {
            parseFunctionCall(builder);
        }
        else if (SqlTokenType.IDENTIFIERS.contains(token)) {
            parseIdentifierOrFunctionCall(builder);
        }
        else if (token instanceof SqlKeywordElementType) {
            // Some keywords can be used as identifiers in expression context
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            mark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);
        }
        else {
            builder.error(SqlLocalize.parserExpressionExpected());
            builder.advanceLexer();
        }
    }

    private boolean isAggregateFunction(IElementType token) {
        return token == SqlKeywordTokenTypes.COUNT_KEYWORD
            || token == SqlKeywordTokenTypes.SUM_KEYWORD
            || token == SqlKeywordTokenTypes.AVG_KEYWORD
            || token == SqlKeywordTokenTypes.MIN_KEYWORD
            || token == SqlKeywordTokenTypes.MAX_KEYWORD
            || token == SqlKeywordTokenTypes.COALESCE_KEYWORD
            || token == SqlKeywordTokenTypes.NULLIF_KEYWORD
            || token == SqlKeywordTokenTypes.UPPER_KEYWORD
            || token == SqlKeywordTokenTypes.LOWER_KEYWORD
            || token == SqlKeywordTokenTypes.TRIM_KEYWORD
            || token == SqlKeywordTokenTypes.SUBSTRING_KEYWORD
            || token == SqlKeywordTokenTypes.POSITION_KEYWORD
            || token == SqlKeywordTokenTypes.EXTRACT_KEYWORD
            || token == SqlKeywordTokenTypes.CONVERT_KEYWORD
            || token == SqlKeywordTokenTypes.TRANSLATE_KEYWORD
            || token == SqlKeywordTokenTypes.CHAR_LENGTH_KEYWORD
            || token == SqlKeywordTokenTypes.CHARACTER_LENGTH_KEYWORD
            || token == SqlKeywordTokenTypes.OCTET_LENGTH_KEYWORD
            || token == SqlKeywordTokenTypes.BIT_LENGTH_KEYWORD;
    }

    private void parseFunctionCall(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer(); // function name keyword

        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());

        if (isToken(builder, SqlKeywordTokenTypes.DISTINCT_KEYWORD)) {
            builder.advanceLexer();
        }

        if (!isToken(builder, SqlTokenType.RPAR)) {
            if (isToken(builder, SqlTokenType.ASTERISK)) {
                PsiBuilder.Marker starMark = builder.mark();
                builder.advanceLexer();
                starMark.done(SqlCompositeElementTypes.STAR_EXPRESSION);
            }
            else {
                parseExpressionList(builder);
            }
        }

        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        mark.done(SqlCompositeElementTypes.FUNCTION_CALL_EXPRESSION);
    }

    private void parseIdentifierOrFunctionCall(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer(); // identifier

        if (isToken(builder, SqlTokenType.LPAR)) {
            // function call
            builder.advanceLexer(); // (

            if (isToken(builder, SqlKeywordTokenTypes.DISTINCT_KEYWORD)) {
                builder.advanceLexer();
            }

            if (!isToken(builder, SqlTokenType.RPAR)) {
                if (isToken(builder, SqlTokenType.ASTERISK)) {
                    PsiBuilder.Marker starMark = builder.mark();
                    builder.advanceLexer();
                    starMark.done(SqlCompositeElementTypes.STAR_EXPRESSION);
                }
                else {
                    parseExpressionList(builder);
                }
            }

            expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
            mark.done(SqlCompositeElementTypes.FUNCTION_CALL_EXPRESSION);
        }
        else if (isToken(builder, SqlTokenType.DOT)) {
            // qualified reference: schema.table.column or table.column
            while (isToken(builder, SqlTokenType.DOT)) {
                builder.advanceLexer(); // .

                if (isToken(builder, SqlTokenType.ASTERISK)) {
                    builder.advanceLexer();
                    break;
                }
                else if (isIdentifier(builder)) {
                    builder.advanceLexer();
                }
                else {
                    builder.error(SqlLocalize.parserIdentifierExpected());
                    break;
                }
            }

            // Check if this qualified name is actually a function call
            if (isToken(builder, SqlTokenType.LPAR)) {
                builder.advanceLexer(); // (
                if (!isToken(builder, SqlTokenType.RPAR)) {
                    parseExpressionList(builder);
                }
                expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
                mark.done(SqlCompositeElementTypes.FUNCTION_CALL_EXPRESSION);
            }
            else {
                mark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);
            }
        }
        else {
            mark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);
        }
    }

    private void parseCaseExpression(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.CASE_KEYWORD, SqlLocalize.parserCaseExpected());

        // simple CASE: CASE expr WHEN ...
        // searched CASE: CASE WHEN ...
        if (!isToken(builder, SqlKeywordTokenTypes.WHEN_KEYWORD)) {
            parseExpression(builder);
        }

        while (isToken(builder, SqlKeywordTokenTypes.WHEN_KEYWORD)) {
            PsiBuilder.Marker whenMark = builder.mark();
            builder.advanceLexer(); // WHEN
            parseExpression(builder);
            expectKeyword(builder, SqlKeywordTokenTypes.THEN_KEYWORD, SqlLocalize.parserThenExpected());
            parseExpression(builder);
            whenMark.done(SqlCompositeElementTypes.CASE_WHEN_CLAUSE);
        }

        if (isToken(builder, SqlKeywordTokenTypes.ELSE_KEYWORD)) {
            builder.advanceLexer();
            parseExpression(builder);
        }

        expectKeyword(builder, SqlKeywordTokenTypes.END_KEYWORD, SqlLocalize.parserEndExpected());
        mark.done(SqlCompositeElementTypes.CASE_EXPRESSION);
    }

    private void parseCastExpression(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.CAST_KEYWORD, SqlLocalize.parserCastExpected());
        expectToken(builder, SqlTokenType.LPAR, SqlLocalize.parserLparExpected());
        parseExpression(builder);
        expectKeyword(builder, SqlKeywordTokenTypes.AS_KEYWORD, SqlLocalize.parserAsExpected());
        parseDataType(builder);
        expectToken(builder, SqlTokenType.RPAR, SqlLocalize.parserRparExpected());
        mark.done(SqlCompositeElementTypes.CAST_EXPRESSION);
    }

    // =================== HELPERS ===================

    protected void parseQualifiedName(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        if (isIdentifier(builder)) {
            builder.advanceLexer();
        }
        else {
            builder.error(SqlLocalize.parserIdentifierExpected());
            mark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);
            return;
        }

        while (isToken(builder, SqlTokenType.DOT)) {
            builder.advanceLexer();
            if (isIdentifier(builder)) {
                builder.advanceLexer();
            }
            else {
                builder.error(SqlLocalize.parserIdentifierExpected());
                break;
            }
        }

        mark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);
    }

    private void parseIdentifierList(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectIdentifier(builder, SqlLocalize.parserIdentifierExpected());
        mark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);

        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            PsiBuilder.Marker idMark = builder.mark();
            expectIdentifier(builder, SqlLocalize.parserIdentifierExpected());
            idMark.done(SqlCompositeElementTypes.REFERENCE_EXPRESSION);
        }
    }

    protected void parseExpressionList(PsiBuilder builder) {
        parseExpression(builder);
        while (isToken(builder, SqlTokenType.COMMA)) {
            builder.advanceLexer();
            parseExpression(builder);
        }
    }

    protected boolean isToken(PsiBuilder builder, IElementType type) {
        return builder.getTokenType() == type;
    }

    protected boolean isIdentifier(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        return SqlTokenType.IDENTIFIERS.contains(token) || token instanceof SqlKeywordElementType;
    }

    protected void expectIdentifier(PsiBuilder builder, LocalizeValue errorMessage) {
        IElementType token = builder.getTokenType();
        if (SqlTokenType.IDENTIFIERS.contains(token) || token instanceof SqlKeywordElementType) {
            builder.advanceLexer();
        }
        else {
            builder.error(errorMessage);
        }
    }

    protected boolean isTokenIdentifierText(PsiBuilder builder, String text) {
        return SqlTokenType.IDENTIFIERS.contains(builder.getTokenType())
            && text.equalsIgnoreCase(builder.getTokenText());
    }

    protected void expectToken(PsiBuilder builder, IElementType type, LocalizeValue errorMessage) {
        if (builder.getTokenType() == type) {
            builder.advanceLexer();
        }
        else {
            builder.error(errorMessage);
        }
    }

    protected void expectKeyword(PsiBuilder builder, IElementType keyword, LocalizeValue errorMessage) {
        if (builder.getTokenType() == keyword) {
            builder.advanceLexer();
        }
        else {
            builder.error(errorMessage);
        }
    }

    protected boolean isStatementBoundary(PsiBuilder builder) {
        IElementType t = builder.getTokenType();
        return t == SqlKeywordTokenTypes.FROM_KEYWORD
            || t == SqlKeywordTokenTypes.WHERE_KEYWORD
            || t == SqlKeywordTokenTypes.GROUP_KEYWORD
            || t == SqlKeywordTokenTypes.HAVING_KEYWORD
            || t == SqlKeywordTokenTypes.ORDER_KEYWORD
            || t == SqlKeywordTokenTypes.UNION_KEYWORD
            || t == SqlKeywordTokenTypes.INTERSECT_KEYWORD
            || t == SqlKeywordTokenTypes.EXCEPT_KEYWORD
            || t == SqlKeywordTokenTypes.ON_KEYWORD
            || t == SqlKeywordTokenTypes.JOIN_KEYWORD
            || t == SqlKeywordTokenTypes.INNER_KEYWORD
            || t == SqlKeywordTokenTypes.LEFT_KEYWORD
            || t == SqlKeywordTokenTypes.RIGHT_KEYWORD
            || t == SqlKeywordTokenTypes.FULL_KEYWORD
            || t == SqlKeywordTokenTypes.CROSS_KEYWORD
            || t == SqlKeywordTokenTypes.NATURAL_KEYWORD
            || t == SqlKeywordTokenTypes.USING_KEYWORD
            || t == SqlKeywordTokenTypes.SET_KEYWORD
            || t == SqlKeywordTokenTypes.VALUES_KEYWORD
            || t == SqlKeywordTokenTypes.INTO_KEYWORD
            || t == SqlKeywordTokenTypes.LIMIT_KEYWORD
            || t == SqlTokenType.SEMICOLON
            || t == SqlTokenType.RPAR
            || t == SqlTokenType.COMMA;
    }

    private boolean lookAheadIs(PsiBuilder builder, IElementType expected) {
        // look past '(' to see if next meaningful token matches
        return builder.lookAhead(1) == expected;
    }

    protected boolean lookAheadTokenIs(PsiBuilder builder, IElementType expected) {
        return builder.lookAhead(1) == expected;
    }
}
