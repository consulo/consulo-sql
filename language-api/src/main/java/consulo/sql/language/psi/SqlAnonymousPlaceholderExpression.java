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

/**
 * Anonymous placeholder of the form {@code ?} &mdash; no explicit position number.
 * <p>
 * This is the JDBC-style {@code PreparedStatement} parameter. Use
 * {@link SqlPositionPlaceholderExpression} for the {@code ?N} variant where an
 * explicit 1-based index is supplied.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface SqlAnonymousPlaceholderExpression extends SqlPlaceholderExpression {
}
