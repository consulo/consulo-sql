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

package consulo.sql.language.impl.sqlite;

import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * SQLite-specific reserved keywords (beyond SQL-92).
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public interface SqliteTokenTypes {
    SqlKeywordElementType[] RESERVED_KEYWORDS = {
        SqlKeywordTokenTypes.ABORT_KEYWORD,
        SqlKeywordTokenTypes.ANALYZE_KEYWORD,
        SqlKeywordTokenTypes.ATTACH_KEYWORD,
        SqlKeywordTokenTypes.AUTOINCREMENT_KEYWORD,
        SqlKeywordTokenTypes.CONFLICT_KEYWORD,
        SqlKeywordTokenTypes.DATABASE_KEYWORD,
        SqlKeywordTokenTypes.DETACH_KEYWORD,
        SqlKeywordTokenTypes.EXPLAIN_KEYWORD,
        SqlKeywordTokenTypes.FAIL_KEYWORD,
        SqlKeywordTokenTypes.GLOB_KEYWORD,
        SqlKeywordTokenTypes.IGNORE_KEYWORD,
        SqlKeywordTokenTypes.INDEX_KEYWORD,
        SqlKeywordTokenTypes.INDEXED_KEYWORD,
        SqlKeywordTokenTypes.INSTEAD_KEYWORD,
        SqlKeywordTokenTypes.ISNULL_KEYWORD,
        SqlKeywordTokenTypes.LIMIT_KEYWORD,
        SqlKeywordTokenTypes.NOTNULL_KEYWORD,
        SqlKeywordTokenTypes.OFFSET_KEYWORD,
        SqlKeywordTokenTypes.PRAGMA_KEYWORD,
        SqlKeywordTokenTypes.REINDEX_KEYWORD,
        SqlKeywordTokenTypes.RENAME_KEYWORD,
        SqlKeywordTokenTypes.REPLACE_KEYWORD,
        SqlKeywordTokenTypes.TRIGGER_KEYWORD,
        SqlKeywordTokenTypes.VACUUM_KEYWORD,
    };
}
