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

import jakarta.annotation.Nullable;

/**
 * Positional placeholder of the form {@code ?N} where {@code N} is a 1-based
 * parameter index.
 * <p>
 * The bare {@code ?} variant is represented by
 * {@link SqlAnonymousPlaceholderExpression}.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface SqlPositionPlaceholderExpression extends SqlPlaceholderExpression {
    /**
     * @return the dedicated child element wrapping the {@code N} part of
     *         {@code ?N}. By construction the parser always emits this child
     *         for a {@link SqlPositionPlaceholderExpression}, so it is non-null in
     *         well-formed PSI.
     */
    @Nullable
    SqlPlaceholderPosition getPositionElement();
}
