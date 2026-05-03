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
import jakarta.annotation.Nullable;

/**
 * Common supertype for SQL statement parameter placeholders.
 * <p>
 * Three concrete forms exist:
 * <ul>
 *     <li>{@link SqlAnonymousPlaceholderExpression} &mdash; bare {@code ?}</li>
 *     <li>{@link SqlPositionPlaceholderExpression} &mdash; numbered {@code ?N},
 *         with a dedicated {@link SqlPlaceholderPosition} child carrying the
 *         index</li>
 *     <li>{@link SqlNamedPlaceholderExpression} &mdash; named {@code :name},
 *         with a dedicated {@link SqlNamedPlaceholderReference} child carrying
 *         the name</li>
 * </ul>
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface SqlPlaceholderExpression extends SqlExpression {
    /**
     * @return the discriminator child element &mdash; the
     *         {@link SqlNamedPlaceholderReference} for named placeholders,
     *         the {@link SqlPlaceholderPosition} for {@code ?N}, or
     *         {@code null} for the anonymous {@code ?} form.
     */
    @Nullable
    PsiElement getNameOrPositionElement();
}
