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

package consulo.sql.language.impl.postgresql;

import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * PostgreSQL-specific reserved keywords (beyond SQL-92).
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public interface PostgreSqlTokenTypes {
    // PostgreSQL-unique keyword definitions
    SqlKeywordElementType ANALYSE_KEYWORD = new SqlKeywordElementType("ANALYSE", SqlLanguage.INSTANCE);
    SqlKeywordElementType ARRAY_KEYWORD = new SqlKeywordElementType("ARRAY", SqlLanguage.INSTANCE);
    SqlKeywordElementType BYTEA_KEYWORD = new SqlKeywordElementType("BYTEA", SqlLanguage.INSTANCE);
    SqlKeywordElementType CHECKPOINT_KEYWORD = new SqlKeywordElementType("CHECKPOINT", SqlLanguage.INSTANCE);
    SqlKeywordElementType CLUSTER_KEYWORD = new SqlKeywordElementType("CLUSTER", SqlLanguage.INSTANCE);
    SqlKeywordElementType COMMENT_KEYWORD = new SqlKeywordElementType("COMMENT", SqlLanguage.INSTANCE);
    SqlKeywordElementType CONCURRENTLY_KEYWORD = new SqlKeywordElementType("CONCURRENTLY", SqlLanguage.INSTANCE);
    SqlKeywordElementType COPY_KEYWORD = new SqlKeywordElementType("COPY", SqlLanguage.INSTANCE);
    SqlKeywordElementType DISABLE_KEYWORD = new SqlKeywordElementType("DISABLE", SqlLanguage.INSTANCE);
    SqlKeywordElementType ENABLE_KEYWORD = new SqlKeywordElementType("ENABLE", SqlLanguage.INSTANCE);
    SqlKeywordElementType ENCODING_KEYWORD = new SqlKeywordElementType("ENCODING", SqlLanguage.INSTANCE);
    SqlKeywordElementType EXCLUDE_KEYWORD = new SqlKeywordElementType("EXCLUDE", SqlLanguage.INSTANCE);
    SqlKeywordElementType EXTENSION_KEYWORD = new SqlKeywordElementType("EXTENSION", SqlLanguage.INSTANCE);
    SqlKeywordElementType FREEZE_KEYWORD = new SqlKeywordElementType("FREEZE", SqlLanguage.INSTANCE);
    SqlKeywordElementType GREATEST_KEYWORD = new SqlKeywordElementType("GREATEST", SqlLanguage.INSTANCE);
    SqlKeywordElementType ILIKE_KEYWORD = new SqlKeywordElementType("ILIKE", SqlLanguage.INSTANCE);
    SqlKeywordElementType IMMUTABLE_KEYWORD = new SqlKeywordElementType("IMMUTABLE", SqlLanguage.INSTANCE);
    SqlKeywordElementType INHERIT_KEYWORD = new SqlKeywordElementType("INHERIT", SqlLanguage.INSTANCE);
    SqlKeywordElementType INHERITS_KEYWORD = new SqlKeywordElementType("INHERITS", SqlLanguage.INSTANCE);
    SqlKeywordElementType LATERAL_KEYWORD = new SqlKeywordElementType("LATERAL", SqlLanguage.INSTANCE);
    SqlKeywordElementType LEAST_KEYWORD = new SqlKeywordElementType("LEAST", SqlLanguage.INSTANCE);
    SqlKeywordElementType LISTEN_KEYWORD = new SqlKeywordElementType("LISTEN", SqlLanguage.INSTANCE);
    SqlKeywordElementType MOVE_KEYWORD = new SqlKeywordElementType("MOVE", SqlLanguage.INSTANCE);
    SqlKeywordElementType NOTHING_KEYWORD = new SqlKeywordElementType("NOTHING", SqlLanguage.INSTANCE);
    SqlKeywordElementType NOTIFY_KEYWORD = new SqlKeywordElementType("NOTIFY", SqlLanguage.INSTANCE);
    SqlKeywordElementType NOWAIT_KEYWORD = new SqlKeywordElementType("NOWAIT", SqlLanguage.INSTANCE);
    SqlKeywordElementType OPERATOR_KEYWORD = new SqlKeywordElementType("OPERATOR", SqlLanguage.INSTANCE);
    SqlKeywordElementType OWNED_KEYWORD = new SqlKeywordElementType("OWNED", SqlLanguage.INSTANCE);
    SqlKeywordElementType OWNER_KEYWORD = new SqlKeywordElementType("OWNER", SqlLanguage.INSTANCE);
    SqlKeywordElementType PARALLEL_KEYWORD = new SqlKeywordElementType("PARALLEL", SqlLanguage.INSTANCE);
    SqlKeywordElementType PLACING_KEYWORD = new SqlKeywordElementType("PLACING", SqlLanguage.INSTANCE);
    SqlKeywordElementType POLICY_KEYWORD = new SqlKeywordElementType("POLICY", SqlLanguage.INSTANCE);
    SqlKeywordElementType REASSIGN_KEYWORD = new SqlKeywordElementType("REASSIGN", SqlLanguage.INSTANCE);
    SqlKeywordElementType REPLICA_KEYWORD = new SqlKeywordElementType("REPLICA", SqlLanguage.INSTANCE);
    SqlKeywordElementType RESET_KEYWORD = new SqlKeywordElementType("RESET", SqlLanguage.INSTANCE);
    SqlKeywordElementType RETURNING_KEYWORD = new SqlKeywordElementType("RETURNING", SqlLanguage.INSTANCE);
    SqlKeywordElementType RULE_KEYWORD = new SqlKeywordElementType("RULE", SqlLanguage.INSTANCE);
    SqlKeywordElementType SERIAL_KEYWORD = new SqlKeywordElementType("SERIAL", SqlLanguage.INSTANCE);
    SqlKeywordElementType SHARE_KEYWORD = new SqlKeywordElementType("SHARE", SqlLanguage.INSTANCE);
    SqlKeywordElementType SNAPSHOT_KEYWORD = new SqlKeywordElementType("SNAPSHOT", SqlLanguage.INSTANCE);
    SqlKeywordElementType STABLE_KEYWORD = new SqlKeywordElementType("STABLE", SqlLanguage.INSTANCE);
    SqlKeywordElementType STATISTICS_KEYWORD = new SqlKeywordElementType("STATISTICS", SqlLanguage.INSTANCE);
    SqlKeywordElementType STORAGE_KEYWORD = new SqlKeywordElementType("STORAGE", SqlLanguage.INSTANCE);
    SqlKeywordElementType STRICT_KEYWORD = new SqlKeywordElementType("STRICT", SqlLanguage.INSTANCE);
    SqlKeywordElementType TEMP_KEYWORD = new SqlKeywordElementType("TEMP", SqlLanguage.INSTANCE);
    SqlKeywordElementType UNLOGGED_KEYWORD = new SqlKeywordElementType("UNLOGGED", SqlLanguage.INSTANCE);
    SqlKeywordElementType VALIDATE_KEYWORD = new SqlKeywordElementType("VALIDATE", SqlLanguage.INSTANCE);
    SqlKeywordElementType VARIADIC_KEYWORD = new SqlKeywordElementType("VARIADIC", SqlLanguage.INSTANCE);
    SqlKeywordElementType VERBOSE_KEYWORD = new SqlKeywordElementType("VERBOSE", SqlLanguage.INSTANCE);
    SqlKeywordElementType VOLATILE_KEYWORD = new SqlKeywordElementType("VOLATILE", SqlLanguage.INSTANCE);
    SqlKeywordElementType WINDOW_KEYWORD = new SqlKeywordElementType("WINDOW", SqlLanguage.INSTANCE);

    // PostgreSQL procedural keywords
    SqlKeywordElementType PERFORM_KEYWORD = new SqlKeywordElementType("PERFORM", SqlLanguage.INSTANCE);
    SqlKeywordElementType RAISE_KEYWORD = new SqlKeywordElementType("RAISE", SqlLanguage.INSTANCE);
    SqlKeywordElementType NOTICE_KEYWORD = new SqlKeywordElementType("NOTICE", SqlLanguage.INSTANCE);
    SqlKeywordElementType RECORD_KEYWORD = new SqlKeywordElementType("RECORD", SqlLanguage.INSTANCE);
    SqlKeywordElementType SETOF_KEYWORD = new SqlKeywordElementType("SETOF", SqlLanguage.INSTANCE);
    SqlKeywordElementType ELSIF_KEYWORD = new SqlKeywordElementType("ELSIF", SqlLanguage.INSTANCE);

    SqlKeywordElementType[] RESERVED_KEYWORDS = {
        // Shared keywords (from SqlKeywordTokenTypes)
        SqlKeywordTokenTypes.ABORT_KEYWORD,
        SqlKeywordTokenTypes.ANALYZE_KEYWORD,
        SqlKeywordTokenTypes.BIGINT_KEYWORD,
        SqlKeywordTokenTypes.BOOLEAN_KEYWORD,
        SqlKeywordTokenTypes.CACHE_KEYWORD,
        SqlKeywordTokenTypes.DATABASE_KEYWORD,
        SqlKeywordTokenTypes.ENUM_KEYWORD,
        SqlKeywordTokenTypes.EXPLAIN_KEYWORD,
        SqlKeywordTokenTypes.INCREMENT_KEYWORD,
        SqlKeywordTokenTypes.INDEX_KEYWORD,
        SqlKeywordTokenTypes.ISNULL_KEYWORD,
        SqlKeywordTokenTypes.LIMIT_KEYWORD,
        SqlKeywordTokenTypes.LOCK_KEYWORD,
        SqlKeywordTokenTypes.MATERIALIZED_KEYWORD,
        SqlKeywordTokenTypes.NOTNULL_KEYWORD,
        SqlKeywordTokenTypes.OFFSET_KEYWORD,
        SqlKeywordTokenTypes.PARTITION_KEYWORD,
        SqlKeywordTokenTypes.REINDEX_KEYWORD,
        SqlKeywordTokenTypes.RENAME_KEYWORD,
        SqlKeywordTokenTypes.REPLACE_KEYWORD,
        SqlKeywordTokenTypes.SEQUENCE_KEYWORD,
        SqlKeywordTokenTypes.SHOW_KEYWORD,
        SqlKeywordTokenTypes.TABLESPACE_KEYWORD,
        SqlKeywordTokenTypes.TEXT_KEYWORD,
        SqlKeywordTokenTypes.TRIGGER_KEYWORD,
        SqlKeywordTokenTypes.TRUNCATE_KEYWORD,
        SqlKeywordTokenTypes.VACUUM_KEYWORD,
        // PostgreSQL-unique keywords
        ANALYSE_KEYWORD,
        ARRAY_KEYWORD,
        BYTEA_KEYWORD,
        CHECKPOINT_KEYWORD,
        CLUSTER_KEYWORD,
        COMMENT_KEYWORD,
        CONCURRENTLY_KEYWORD,
        COPY_KEYWORD,
        DISABLE_KEYWORD,
        ENABLE_KEYWORD,
        ENCODING_KEYWORD,
        EXCLUDE_KEYWORD,
        EXTENSION_KEYWORD,
        FREEZE_KEYWORD,
        GREATEST_KEYWORD,
        ILIKE_KEYWORD,
        IMMUTABLE_KEYWORD,
        INHERIT_KEYWORD,
        INHERITS_KEYWORD,
        LATERAL_KEYWORD,
        LEAST_KEYWORD,
        LISTEN_KEYWORD,
        MOVE_KEYWORD,
        NOTHING_KEYWORD,
        NOTIFY_KEYWORD,
        NOWAIT_KEYWORD,
        OPERATOR_KEYWORD,
        OWNED_KEYWORD,
        OWNER_KEYWORD,
        PARALLEL_KEYWORD,
        PLACING_KEYWORD,
        POLICY_KEYWORD,
        REASSIGN_KEYWORD,
        REPLICA_KEYWORD,
        RESET_KEYWORD,
        RETURNING_KEYWORD,
        RULE_KEYWORD,
        SERIAL_KEYWORD,
        SHARE_KEYWORD,
        SNAPSHOT_KEYWORD,
        STABLE_KEYWORD,
        STATISTICS_KEYWORD,
        STORAGE_KEYWORD,
        STRICT_KEYWORD,
        TEMP_KEYWORD,
        UNLOGGED_KEYWORD,
        VALIDATE_KEYWORD,
        VARIADIC_KEYWORD,
        VERBOSE_KEYWORD,
        VOLATILE_KEYWORD,
        WINDOW_KEYWORD,
        // Procedural keywords
        PERFORM_KEYWORD,
        RAISE_KEYWORD,
        NOTICE_KEYWORD,
        RECORD_KEYWORD,
        SETOF_KEYWORD,
        ELSIF_KEYWORD,
    };
}
