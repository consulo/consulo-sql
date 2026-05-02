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

package consulo.sql.language.impl.psi;

import consulo.language.ast.IElementType;
import consulo.util.collection.CharSequenceHashingStrategy;
import consulo.util.collection.Maps;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Map;

/**
 * Per-version keyword registry. Each SQL dialect version creates its own
 * registry with the keywords it supports.
 *
 * @author VISTALL
 * @since 2026-03-17
 */
public class SqlKeywordRegistry {
    private final Map<CharSequence, SqlKeywordElementType> myKeywords =
        Maps.newHashMap(CharSequenceHashingStrategy.CASE_INSENSITIVE);

    public void registerKeyword(@Nonnull SqlKeywordElementType keyword) {
        myKeywords.put(keyword.getKeyword(), keyword);
    }

    public void registerKeywords(@Nonnull SqlKeywordElementType... keywords) {
        for (SqlKeywordElementType keyword : keywords) {
            myKeywords.put(keyword.getKeyword(), keyword);
        }
    }

    @Nullable
    public IElementType toKeyword(@Nonnull CharSequence text) {
        return myKeywords.get(text);
    }
}
