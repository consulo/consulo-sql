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

package consulo.sql.language.impl.hql;

import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * Hibernate Query Language extensions on top of {@link
 * consulo.sql.language.impl.jpql.JpqlTokenTypes}.
 * <p>
 * HQL is a strict superset of JPQL, so {@link
 * consulo.sql.language.impl.hql.HqlLanguageVersion} registers the JPQL keyword
 * set first and then adds the HQL-only set declared here.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public interface HqlTokenTypes {
    SqlKeywordElementType[] RESERVED_KEYWORDS = {
        SqlKeywordTokenTypes.ELEMENTS_KEYWORD,
        SqlKeywordTokenTypes.INDICES_KEYWORD,
        SqlKeywordTokenTypes.CLASS_KEYWORD,
        SqlKeywordTokenTypes.VERSIONED_KEYWORD,
    };
}
