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

package consulo.sql.language.impl.oracle;

import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * Oracle-specific reserved keywords (beyond SQL-92).
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public interface OracleTokenTypes
{
    SqlKeywordElementType[] RESERVED_KEYWORDS = {
            SqlKeywordTokenTypes.ACCESS_KEYWORD,
            SqlKeywordTokenTypes.ANALYZE_KEYWORD,
            SqlKeywordTokenTypes.AUDIT_KEYWORD,
            SqlKeywordTokenTypes.BODY_KEYWORD,
            SqlKeywordTokenTypes.BOOLEAN_KEYWORD,
            SqlKeywordTokenTypes.CACHE_KEYWORD,
            SqlKeywordTokenTypes.COMPRESS_KEYWORD,
            SqlKeywordTokenTypes.DATABASE_KEYWORD,
            SqlKeywordTokenTypes.EXCLUSIVE_KEYWORD,
            SqlKeywordTokenTypes.EXPLAIN_KEYWORD,
            SqlKeywordTokenTypes.FILE_KEYWORD,
            SqlKeywordTokenTypes.INCREMENT_KEYWORD,
            SqlKeywordTokenTypes.INCREMENT_BY_KEYWORD,
            SqlKeywordTokenTypes.INDEX_KEYWORD,
            SqlKeywordTokenTypes.INITIAL_KEYWORD,
            SqlKeywordTokenTypes.LOCK_KEYWORD,
            SqlKeywordTokenTypes.MATERIALIZED_KEYWORD,
            SqlKeywordTokenTypes.MAXEXTENTS_KEYWORD,
            SqlKeywordTokenTypes.MAXVALUE_KEYWORD,
            SqlKeywordTokenTypes.MINEXTENTS_KEYWORD,
            SqlKeywordTokenTypes.MINUS_KEYWORD,
            SqlKeywordTokenTypes.MINVALUE_KEYWORD,
            SqlKeywordTokenTypes.NOAUDIT_KEYWORD,
            SqlKeywordTokenTypes.NOCACHE_KEYWORD,
            SqlKeywordTokenTypes.NOCOMPRESS_KEYWORD,
            SqlKeywordTokenTypes.NOCYCLE_KEYWORD,
            SqlKeywordTokenTypes.NOMAXVALUE_KEYWORD,
            SqlKeywordTokenTypes.NOMINVALUE_KEYWORD,
            SqlKeywordTokenTypes.NOORDER_KEYWORD,
            SqlKeywordTokenTypes.NUMBER_KEYWORD,
            SqlKeywordTokenTypes.OFFLINE_KEYWORD,
            SqlKeywordTokenTypes.ONLINE_KEYWORD,
            SqlKeywordTokenTypes.PACKAGE_KEYWORD,
            SqlKeywordTokenTypes.PARTITION_KEYWORD,
            SqlKeywordTokenTypes.PCTFREE_KEYWORD,
            SqlKeywordTokenTypes.PRIOR_KEYWORD,
            SqlKeywordTokenTypes.RAW_KEYWORD,
            SqlKeywordTokenTypes.RENAME_KEYWORD,
            SqlKeywordTokenTypes.REPLACE_KEYWORD,
            SqlKeywordTokenTypes.ROWID_KEYWORD,
            SqlKeywordTokenTypes.ROWNUM_KEYWORD,
            SqlKeywordTokenTypes.SEQUENCE_KEYWORD,
            SqlKeywordTokenTypes.SHOW_KEYWORD,
            SqlKeywordTokenTypes.START_KEYWORD,
            SqlKeywordTokenTypes.SUCCESSFUL_KEYWORD,
            SqlKeywordTokenTypes.SYNONYM_KEYWORD,
            SqlKeywordTokenTypes.SYSDATE_KEYWORD,
            SqlKeywordTokenTypes.TABLESPACE_KEYWORD,
            SqlKeywordTokenTypes.TRIGGER_KEYWORD,
            SqlKeywordTokenTypes.TRUNCATE_KEYWORD,
            SqlKeywordTokenTypes.UID_KEYWORD,
            SqlKeywordTokenTypes.VARCHAR2_KEYWORD,
    };
}
