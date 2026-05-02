/*
 * Copyright 2013-2021 consulo.io
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

package consulo.sql.language.impl.psi;

import consulo.language.Language;
import consulo.language.ast.IElementType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Locale;

/**
 * @author VISTALL
 * @since 22/10/2021
 */
public class SqlKeywordElementType extends IElementType {
    @Nonnull
    private final String myKeyword;

    public SqlKeywordElementType(@Nonnull String keyword, @Nullable Language language) {
        super(keyword.toUpperCase(Locale.ROOT) + "_KEYWORD", language);
        myKeyword = keyword;
    }

    @Nonnull
    public String getKeyword() {
        return myKeyword;
    }
}
