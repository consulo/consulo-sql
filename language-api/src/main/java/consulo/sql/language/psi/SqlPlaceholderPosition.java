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
 * Position-number portion of a {@link SqlPositionPlaceholderExpression}, e.g.
 * the {@code 1} in {@code ?1}.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface SqlPlaceholderPosition extends PsiElement {
    /**
     * @return the numeric value of this position, or {@code null} on numeric
     *         overflow of malformed input.
     */
    @Nullable
    Integer getValue();
}
