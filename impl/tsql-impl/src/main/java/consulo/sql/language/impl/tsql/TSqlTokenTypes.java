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

package consulo.sql.language.impl.tsql;

import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * T-SQL (SQL Server) specific reserved keywords (beyond SQL-92).
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public interface TSqlTokenTypes {
    SqlKeywordElementType[] RESERVED_KEYWORDS = {
        SqlKeywordTokenTypes.BACKUP_KEYWORD,
        SqlKeywordTokenTypes.BIGINT_KEYWORD,
        SqlKeywordTokenTypes.BINARY_KEYWORD,
        SqlKeywordTokenTypes.BREAK_KEYWORD,
        SqlKeywordTokenTypes.BROWSE_KEYWORD,
        SqlKeywordTokenTypes.BULK_KEYWORD,
        SqlKeywordTokenTypes.CATCH_KEYWORD,
        SqlKeywordTokenTypes.COMPUTE_KEYWORD,
        SqlKeywordTokenTypes.CONTAINSTABLE_KEYWORD,
        SqlKeywordTokenTypes.DATABASE_KEYWORD,
        SqlKeywordTokenTypes.DBCC_KEYWORD,
        SqlKeywordTokenTypes.DENY_KEYWORD,
        SqlKeywordTokenTypes.DISK_KEYWORD,
        SqlKeywordTokenTypes.DUMP_KEYWORD,
        SqlKeywordTokenTypes.ERRLVL_KEYWORD,
        SqlKeywordTokenTypes.FILLFACTOR_KEYWORD,
        SqlKeywordTokenTypes.FREETEXT_KEYWORD,
        SqlKeywordTokenTypes.FREETEXTTABLE_KEYWORD,
        SqlKeywordTokenTypes.HOLDLOCK_KEYWORD,
        SqlKeywordTokenTypes.IDENTITY_INSERT_KEYWORD,
        SqlKeywordTokenTypes.IDENTITYCOL_KEYWORD,
        SqlKeywordTokenTypes.INDEX_KEYWORD,
        SqlKeywordTokenTypes.LINENO_KEYWORD,
        SqlKeywordTokenTypes.NOCHECK_KEYWORD,
        SqlKeywordTokenTypes.NONCLUSTERED_KEYWORD,
        SqlKeywordTokenTypes.OFF_KEYWORD,
        SqlKeywordTokenTypes.OFFSETS_KEYWORD,
        SqlKeywordTokenTypes.OPENDATASOURCE_KEYWORD,
        SqlKeywordTokenTypes.OPENQUERY_KEYWORD,
        SqlKeywordTokenTypes.OPENROWSET_KEYWORD,
        SqlKeywordTokenTypes.PERCENT_KEYWORD,
        SqlKeywordTokenTypes.PIVOT_KEYWORD,
        SqlKeywordTokenTypes.PLAN_KEYWORD,
        SqlKeywordTokenTypes.PRINT_KEYWORD,
        SqlKeywordTokenTypes.PROC_KEYWORD,
        SqlKeywordTokenTypes.RAISERROR_KEYWORD,
        SqlKeywordTokenTypes.READTEXT_KEYWORD,
        SqlKeywordTokenTypes.RECONFIGURE_KEYWORD,
        SqlKeywordTokenTypes.REPLICATION_KEYWORD,
        SqlKeywordTokenTypes.RESTORE_KEYWORD,
        SqlKeywordTokenTypes.REVERT_KEYWORD,
        SqlKeywordTokenTypes.ROWCOUNT_KEYWORD,
        SqlKeywordTokenTypes.ROWGUIDCOL_KEYWORD,
        SqlKeywordTokenTypes.SAVE_KEYWORD,
        SqlKeywordTokenTypes.SECURITYAUDIT_KEYWORD,
        SqlKeywordTokenTypes.SETUSER_KEYWORD,
        SqlKeywordTokenTypes.SHOW_KEYWORD,
        SqlKeywordTokenTypes.SHUTDOWN_KEYWORD,
        SqlKeywordTokenTypes.TEXT_KEYWORD,
        SqlKeywordTokenTypes.THROW_KEYWORD,
        SqlKeywordTokenTypes.TOP_KEYWORD,
        SqlKeywordTokenTypes.TRAN_KEYWORD,
        SqlKeywordTokenTypes.TRIGGER_KEYWORD,
        SqlKeywordTokenTypes.TRUNCATE_KEYWORD,
        SqlKeywordTokenTypes.TRY_KEYWORD,
        SqlKeywordTokenTypes.UNPIVOT_KEYWORD,
        SqlKeywordTokenTypes.UPDATETEXT_KEYWORD,
        SqlKeywordTokenTypes.VARBINARY_KEYWORD,
        SqlKeywordTokenTypes.WAITFOR_KEYWORD,
        SqlKeywordTokenTypes.WRITETEXT_KEYWORD,
    };
}
