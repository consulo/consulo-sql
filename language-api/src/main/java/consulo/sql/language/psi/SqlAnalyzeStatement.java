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

package consulo.sql.language.psi;

import consulo.language.psi.PsiElement;
import jakarta.annotation.Nonnull;

import java.util.List;

/**
 * Vendor SQL maintenance statement that asks the engine to (re)collect
 * statistics on one or more tables, so the optimizer can plan better.
 * <p>
 * Common shapes:
 * <ul>
 *     <li>MySQL / MariaDB &mdash; {@code ANALYZE TABLE t1, t2}</li>
 *     <li>Oracle &mdash; {@code ANALYZE TABLE t COMPUTE STATISTICS}</li>
 *     <li>PostgreSQL &mdash; {@code ANALYZE [VERBOSE] [t1 [, t2]]}
 *         (no {@code TABLE} keyword)</li>
 *     <li>SQLite &mdash; {@code ANALYZE [t]}</li>
 * </ul>
 * <p>
 * This base PSI captures the common structure: an {@code ANALYZE} keyword,
 * optional {@code TABLE} keyword, and a list of qualified table references.
 * Vendor-specific options (e.g. {@code COMPUTE STATISTICS}, {@code VERBOSE},
 * column lists) are not modelled at this level.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface SqlAnalyzeStatement extends PsiElement {
    /**
     * @return the table reference children, in source order. Empty for the
     *         bare PostgreSQL {@code ANALYZE} form.
     */
    @Nonnull
    List<SqlTableExpression> getTables();
}
