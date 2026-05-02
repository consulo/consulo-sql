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

package consulo.sql.language.impl.mariadb;

import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * MariaDB-specific reserved keywords (beyond SQL-92).
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public interface MariaDbTokenTypes {
    SqlKeywordElementType[] RESERVED_KEYWORDS = {
        SqlKeywordTokenTypes.AUTO_INCREMENT_KEYWORD,
        SqlKeywordTokenTypes.BIGINT_KEYWORD,
        SqlKeywordTokenTypes.BINARY_KEYWORD,
        SqlKeywordTokenTypes.BLOB_KEYWORD,
        SqlKeywordTokenTypes.BOOLEAN_KEYWORD,
        SqlKeywordTokenTypes.CHANGE_KEYWORD,
        SqlKeywordTokenTypes.DATABASE_KEYWORD,
        SqlKeywordTokenTypes.DATABASES_KEYWORD,
        SqlKeywordTokenTypes.DELIMITER_KEYWORD,
        SqlKeywordTokenTypes.ENCLOSED_KEYWORD,
        SqlKeywordTokenTypes.ENGINE_KEYWORD,
        SqlKeywordTokenTypes.ENUM_KEYWORD,
        SqlKeywordTokenTypes.EXPLAIN_KEYWORD,
        SqlKeywordTokenTypes.FORCE_KEYWORD,
        SqlKeywordTokenTypes.FULLTEXT_KEYWORD,
        SqlKeywordTokenTypes.IGNORE_KEYWORD,
        SqlKeywordTokenTypes.INDEX_KEYWORD,
        SqlKeywordTokenTypes.INFILE_KEYWORD,
        SqlKeywordTokenTypes.KEYS_KEYWORD,
        SqlKeywordTokenTypes.KILL_KEYWORD,
        SqlKeywordTokenTypes.LIMIT_KEYWORD,
        SqlKeywordTokenTypes.LINES_KEYWORD,
        SqlKeywordTokenTypes.LOAD_KEYWORD,
        SqlKeywordTokenTypes.LOCK_KEYWORD,
        SqlKeywordTokenTypes.LONGBLOB_KEYWORD,
        SqlKeywordTokenTypes.LONGTEXT_KEYWORD,
        SqlKeywordTokenTypes.LOW_PRIORITY_KEYWORD,
        SqlKeywordTokenTypes.MEDIUMBLOB_KEYWORD,
        SqlKeywordTokenTypes.MEDIUMINT_KEYWORD,
        SqlKeywordTokenTypes.MEDIUMTEXT_KEYWORD,
        SqlKeywordTokenTypes.MODIFY_KEYWORD,
        SqlKeywordTokenTypes.OFFSET_KEYWORD,
        SqlKeywordTokenTypes.OPTIMIZE_KEYWORD,
        SqlKeywordTokenTypes.OUTFILE_KEYWORD,
        SqlKeywordTokenTypes.PURGE_KEYWORD,
        SqlKeywordTokenTypes.REGEXP_KEYWORD,
        SqlKeywordTokenTypes.RENAME_KEYWORD,
        SqlKeywordTokenTypes.REPLACE_KEYWORD,
        SqlKeywordTokenTypes.REQUIRE_KEYWORD,
        SqlKeywordTokenTypes.RLIKE_KEYWORD,
        SqlKeywordTokenTypes.SCHEMAS_KEYWORD,
        SqlKeywordTokenTypes.SEPARATOR_KEYWORD,
        SqlKeywordTokenTypes.SHOW_KEYWORD,
        SqlKeywordTokenTypes.SPATIAL_KEYWORD,
        SqlKeywordTokenTypes.SSL_KEYWORD,
        SqlKeywordTokenTypes.STARTING_KEYWORD,
        SqlKeywordTokenTypes.STRAIGHT_JOIN_KEYWORD,
        SqlKeywordTokenTypes.TERMINATED_KEYWORD,
        SqlKeywordTokenTypes.TEXT_KEYWORD,
        SqlKeywordTokenTypes.TINYBLOB_KEYWORD,
        SqlKeywordTokenTypes.TINYINT_KEYWORD,
        SqlKeywordTokenTypes.TINYTEXT_KEYWORD,
        SqlKeywordTokenTypes.TRIGGER_KEYWORD,
        SqlKeywordTokenTypes.TRUNCATE_KEYWORD,
        SqlKeywordTokenTypes.UNLOCK_KEYWORD,
        SqlKeywordTokenTypes.UNSIGNED_KEYWORD,
        SqlKeywordTokenTypes.USE_KEYWORD,
        SqlKeywordTokenTypes.VARBINARY_KEYWORD,
        SqlKeywordTokenTypes.ZEROFILL_KEYWORD,
    };
}
