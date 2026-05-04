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

package consulo.sql.language.impl.psi;

import consulo.language.ast.IElementType;
import consulo.sql.language.SqlLanguage;

/**
 * @author VISTALL
 * @since 2026-02-28
 */
public interface SqlCompositeElementTypes {
    // Statements
    IElementType SELECT_STATEMENT = new IElementType("SQL_SELECT_STATEMENT", SqlLanguage.INSTANCE);
    IElementType INSERT_STATEMENT = new IElementType("SQL_INSERT_STATEMENT", SqlLanguage.INSTANCE);
    IElementType UPDATE_STATEMENT = new IElementType("SQL_UPDATE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType DELETE_STATEMENT = new IElementType("SQL_DELETE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType CREATE_TABLE_STATEMENT = new IElementType("SQL_CREATE_TABLE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType DROP_TABLE_STATEMENT = new IElementType("SQL_DROP_TABLE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType ALTER_TABLE_STATEMENT = new IElementType("SQL_ALTER_TABLE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType ANALYZE_STATEMENT = new IElementType("SQL_ANALYZE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType CREATE_INDEX_STATEMENT = new IElementType("SQL_CREATE_INDEX_STATEMENT", SqlLanguage.INSTANCE);
    IElementType DROP_INDEX_STATEMENT = new IElementType("SQL_DROP_INDEX_STATEMENT", SqlLanguage.INSTANCE);
    IElementType CREATE_VIEW_STATEMENT = new IElementType("SQL_CREATE_VIEW_STATEMENT", SqlLanguage.INSTANCE);
    IElementType DROP_VIEW_STATEMENT = new IElementType("SQL_DROP_VIEW_STATEMENT", SqlLanguage.INSTANCE);

    // Clauses
    IElementType SELECT_CLAUSE = new IElementType("SQL_SELECT_CLAUSE", SqlLanguage.INSTANCE);
    IElementType FROM_CLAUSE = new IElementType("SQL_FROM_CLAUSE", SqlLanguage.INSTANCE);
    IElementType WHERE_CLAUSE = new IElementType("SQL_WHERE_CLAUSE", SqlLanguage.INSTANCE);
    IElementType GROUP_BY_CLAUSE = new IElementType("SQL_GROUP_BY_CLAUSE", SqlLanguage.INSTANCE);
    IElementType HAVING_CLAUSE = new IElementType("SQL_HAVING_CLAUSE", SqlLanguage.INSTANCE);
    IElementType ORDER_BY_CLAUSE = new IElementType("SQL_ORDER_BY_CLAUSE", SqlLanguage.INSTANCE);
    IElementType SET_CLAUSE = new IElementType("SQL_SET_CLAUSE", SqlLanguage.INSTANCE);
    IElementType VALUES_CLAUSE = new IElementType("SQL_VALUES_CLAUSE", SqlLanguage.INSTANCE);

    // Query
    IElementType QUERY_EXPRESSION = new IElementType("SQL_QUERY_EXPRESSION", SqlLanguage.INSTANCE);

    // Expressions
    IElementType REFERENCE_EXPRESSION = new IElementType("SQL_REFERENCE_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType BINARY_EXPRESSION = new IElementType("SQL_BINARY_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType PREFIX_EXPRESSION = new IElementType("SQL_PREFIX_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType LITERAL_EXPRESSION = new IElementType("SQL_LITERAL_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType PARENTHESIZED_EXPRESSION = new IElementType("SQL_PARENTHESIZED_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType FUNCTION_CALL_EXPRESSION = new IElementType("SQL_FUNCTION_CALL_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType SUBQUERY_EXPRESSION = new IElementType("SQL_SUBQUERY_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType IN_EXPRESSION = new IElementType("SQL_IN_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType BETWEEN_EXPRESSION = new IElementType("SQL_BETWEEN_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType LIKE_EXPRESSION = new IElementType("SQL_LIKE_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType IS_NULL_EXPRESSION = new IElementType("SQL_IS_NULL_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType EXISTS_EXPRESSION = new IElementType("SQL_EXISTS_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType CASE_EXPRESSION = new IElementType("SQL_CASE_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType CASE_WHEN_CLAUSE = new IElementType("SQL_CASE_WHEN_CLAUSE", SqlLanguage.INSTANCE);
    IElementType CAST_EXPRESSION = new IElementType("SQL_CAST_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType STAR_EXPRESSION = new IElementType("SQL_STAR_EXPRESSION", SqlLanguage.INSTANCE);

    // JPQL / HQL extensions
    IElementType IS_EMPTY_EXPRESSION = new IElementType("SQL_IS_EMPTY_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType MEMBER_OF_EXPRESSION = new IElementType("SQL_MEMBER_OF_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType TREAT_EXPRESSION = new IElementType("SQL_TREAT_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType PATH_FUNCTION_EXPRESSION = new IElementType("SQL_PATH_FUNCTION_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType NAMED_FUNCTION_CALL_EXPRESSION = new IElementType("SQL_NAMED_FUNCTION_CALL_EXPRESSION", SqlLanguage.INSTANCE);

    // Placeholders
    IElementType ANONYMOUS_PLACEHOLDER_EXPRESSION = new IElementType("SQL_ANONYMOUS_PLACEHOLDER_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType POSITION_PLACEHOLDER_EXPRESSION = new IElementType("SQL_POSITION_PLACEHOLDER_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType PLACEHOLDER_POSITION = new IElementType("SQL_PLACEHOLDER_POSITION", SqlLanguage.INSTANCE);
    IElementType NAMED_PLACEHOLDER_EXPRESSION = new IElementType("SQL_NAMED_PLACEHOLDER_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType NAMED_PLACEHOLDER_REFERENCE = new IElementType("SQL_NAMED_PLACEHOLDER_REFERENCE", SqlLanguage.INSTANCE);

    // Table references
    IElementType TABLE_EXPRESSION = new IElementType("SQL_TABLE_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType JOIN_EXPRESSION = new IElementType("SQL_JOIN_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType JOIN_CONDITION = new IElementType("SQL_JOIN_CONDITION", SqlLanguage.INSTANCE);

    // Column/select item
    IElementType COLUMN_EXPRESSION = new IElementType("SQL_COLUMN_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType ALIAS = new IElementType("SQL_ALIAS", SqlLanguage.INSTANCE);

    // CREATE TABLE elements
    IElementType COLUMN_DEFINITION = new IElementType("SQL_COLUMN_DEFINITION", SqlLanguage.INSTANCE);
    IElementType TYPE_EXPRESSION = new IElementType("SQL_TYPE_EXPRESSION", SqlLanguage.INSTANCE);
    IElementType COLUMN_CONSTRAINT = new IElementType("SQL_COLUMN_CONSTRAINT", SqlLanguage.INSTANCE);
    IElementType TABLE_CONSTRAINT = new IElementType("SQL_TABLE_CONSTRAINT", SqlLanguage.INSTANCE);

    // UPDATE SET assignment
    IElementType ASSIGNMENT_EXPRESSION = new IElementType("SQL_ASSIGNMENT_EXPRESSION", SqlLanguage.INSTANCE);

    // INSERT column list
    IElementType COLUMN_LIST = new IElementType("SQL_COLUMN_LIST", SqlLanguage.INSTANCE);
    IElementType VALUE_LIST = new IElementType("SQL_VALUE_LIST", SqlLanguage.INSTANCE);

    // ORDER BY item
    IElementType ORDER_ITEM = new IElementType("SQL_ORDER_ITEM", SqlLanguage.INSTANCE);

    // Function/Procedure statements
    IElementType CREATE_FUNCTION_STATEMENT = new IElementType("SQL_CREATE_FUNCTION_STATEMENT", SqlLanguage.INSTANCE);
    IElementType CREATE_PROCEDURE_STATEMENT = new IElementType("SQL_CREATE_PROCEDURE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType DROP_FUNCTION_STATEMENT = new IElementType("SQL_DROP_FUNCTION_STATEMENT", SqlLanguage.INSTANCE);
    IElementType DROP_PROCEDURE_STATEMENT = new IElementType("SQL_DROP_PROCEDURE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PARAMETER_DEFINITION = new IElementType("SQL_PARAMETER_DEFINITION", SqlLanguage.INSTANCE);
    IElementType PARAMETER_LIST = new IElementType("SQL_PARAMETER_LIST", SqlLanguage.INSTANCE);

    // PL/pgSQL block elements
    IElementType PLPGSQL_BLOCK = new IElementType("SQL_PLPGSQL_BLOCK", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_DECLARE_SECTION = new IElementType("SQL_PLPGSQL_DECLARE_SECTION", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_VARIABLE_DECLARATION = new IElementType("SQL_PLPGSQL_VARIABLE_DECLARATION", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_IF_STATEMENT = new IElementType("SQL_PLPGSQL_IF_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_LOOP_STATEMENT = new IElementType("SQL_PLPGSQL_LOOP_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_WHILE_STATEMENT = new IElementType("SQL_PLPGSQL_WHILE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_FOR_STATEMENT = new IElementType("SQL_PLPGSQL_FOR_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_RETURN_STATEMENT = new IElementType("SQL_PLPGSQL_RETURN_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_RAISE_STATEMENT = new IElementType("SQL_PLPGSQL_RAISE_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_PERFORM_STATEMENT = new IElementType("SQL_PLPGSQL_PERFORM_STATEMENT", SqlLanguage.INSTANCE);
    IElementType PLPGSQL_ASSIGNMENT_STATEMENT = new IElementType("SQL_PLPGSQL_ASSIGNMENT_STATEMENT", SqlLanguage.INSTANCE);
}
