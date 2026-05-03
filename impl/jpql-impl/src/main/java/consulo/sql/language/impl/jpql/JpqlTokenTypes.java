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

package consulo.sql.language.impl.jpql;

import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * Reserved keywords of the Jakarta Persistence Query Language.
 * <p>
 * This is a self-contained list per the Jakarta Persistence specification.
 * JPQL is intentionally <em>not</em> a superset of SQL-92 &mdash; it has no
 * DDL ({@code CREATE/DROP/ALTER}), no cursor/PSM ({@code LOOP}, {@code HANDLER},
 * {@code DECLARE}, &hellip;), no transaction ({@code COMMIT}, {@code ROLLBACK})
 * and no catalog/schema keywords. So {@link
 * consulo.sql.language.impl.jpql.JpqlLanguageVersion} registers <em>only</em>
 * this set, not {@code Sql92TokenTypes.RESERVED_KEYWORDS}.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface JpqlTokenTypes {
    SqlKeywordElementType[] RESERVED_KEYWORDS = {
        // statement / clause openers
        SqlKeywordTokenTypes.SELECT_KEYWORD,
        SqlKeywordTokenTypes.FROM_KEYWORD,
        SqlKeywordTokenTypes.WHERE_KEYWORD,
        SqlKeywordTokenTypes.GROUP_KEYWORD,
        SqlKeywordTokenTypes.BY_KEYWORD,
        SqlKeywordTokenTypes.HAVING_KEYWORD,
        SqlKeywordTokenTypes.ORDER_KEYWORD,
        SqlKeywordTokenTypes.UPDATE_KEYWORD,
        SqlKeywordTokenTypes.DELETE_KEYWORD,
        SqlKeywordTokenTypes.SET_KEYWORD,
        SqlKeywordTokenTypes.INSERT_KEYWORD,
        SqlKeywordTokenTypes.INTO_KEYWORD,
        SqlKeywordTokenTypes.VALUES_KEYWORD,

        // sorting
        SqlKeywordTokenTypes.ASC_KEYWORD,
        SqlKeywordTokenTypes.DESC_KEYWORD,

        // joins / aliasing
        SqlKeywordTokenTypes.JOIN_KEYWORD,
        SqlKeywordTokenTypes.INNER_KEYWORD,
        SqlKeywordTokenTypes.LEFT_KEYWORD,
        SqlKeywordTokenTypes.OUTER_KEYWORD,
        SqlKeywordTokenTypes.ON_KEYWORD,
        SqlKeywordTokenTypes.AS_KEYWORD,
        SqlKeywordTokenTypes.DISTINCT_KEYWORD,
        SqlKeywordTokenTypes.FETCH_KEYWORD,

        // entity / construction
        SqlKeywordTokenTypes.NEW_KEYWORD,
        SqlKeywordTokenTypes.OBJECT_KEYWORD,
        SqlKeywordTokenTypes.TYPE_KEYWORD,
        SqlKeywordTokenTypes.TREAT_KEYWORD,

        // collection navigation
        SqlKeywordTokenTypes.EMPTY_KEYWORD,
        SqlKeywordTokenTypes.MEMBER_KEYWORD,
        SqlKeywordTokenTypes.OF_KEYWORD,
        SqlKeywordTokenTypes.KEY_KEYWORD,
        SqlKeywordTokenTypes.VALUE_KEYWORD,
        SqlKeywordTokenTypes.ENTRY_KEYWORD,
        SqlKeywordTokenTypes.INDEX_KEYWORD,

        // boolean / null
        SqlKeywordTokenTypes.IS_KEYWORD,
        SqlKeywordTokenTypes.NOT_KEYWORD,
        SqlKeywordTokenTypes.NULL_KEYWORD,
        SqlKeywordTokenTypes.TRUE_KEYWORD,
        SqlKeywordTokenTypes.FALSE_KEYWORD,
        SqlKeywordTokenTypes.AND_KEYWORD,
        SqlKeywordTokenTypes.OR_KEYWORD,

        // predicates
        SqlKeywordTokenTypes.LIKE_KEYWORD,
        SqlKeywordTokenTypes.ESCAPE_KEYWORD,
        SqlKeywordTokenTypes.BETWEEN_KEYWORD,
        SqlKeywordTokenTypes.IN_KEYWORD,
        SqlKeywordTokenTypes.EXISTS_KEYWORD,
        SqlKeywordTokenTypes.ALL_KEYWORD,
        SqlKeywordTokenTypes.ANY_KEYWORD,
        SqlKeywordTokenTypes.SOME_KEYWORD,

        // set operators
        SqlKeywordTokenTypes.UNION_KEYWORD,
        SqlKeywordTokenTypes.INTERSECT_KEYWORD,
        SqlKeywordTokenTypes.EXCEPT_KEYWORD,

        // conditional
        SqlKeywordTokenTypes.CASE_KEYWORD,
        SqlKeywordTokenTypes.WHEN_KEYWORD,
        SqlKeywordTokenTypes.THEN_KEYWORD,
        SqlKeywordTokenTypes.ELSE_KEYWORD,
        SqlKeywordTokenTypes.END_KEYWORD,
        SqlKeywordTokenTypes.COALESCE_KEYWORD,
        SqlKeywordTokenTypes.NULLIF_KEYWORD,

        // standard functions
        SqlKeywordTokenTypes.FUNCTION_KEYWORD,
        SqlKeywordTokenTypes.CURRENT_DATE_KEYWORD,
        SqlKeywordTokenTypes.CURRENT_TIME_KEYWORD,
        SqlKeywordTokenTypes.CURRENT_TIMESTAMP_KEYWORD,
        SqlKeywordTokenTypes.TRIM_KEYWORD,
        SqlKeywordTokenTypes.LEADING_KEYWORD,
        SqlKeywordTokenTypes.TRAILING_KEYWORD,
        SqlKeywordTokenTypes.BOTH_KEYWORD,
        SqlKeywordTokenTypes.ABS_KEYWORD,
        SqlKeywordTokenTypes.MOD_KEYWORD,
        SqlKeywordTokenTypes.SQRT_KEYWORD,
        SqlKeywordTokenTypes.LENGTH_KEYWORD,
        SqlKeywordTokenTypes.LOCATE_KEYWORD,
        SqlKeywordTokenTypes.LOWER_KEYWORD,
        SqlKeywordTokenTypes.UPPER_KEYWORD,
        SqlKeywordTokenTypes.CONCAT_KEYWORD,
        SqlKeywordTokenTypes.SIZE_KEYWORD,
    };
}
