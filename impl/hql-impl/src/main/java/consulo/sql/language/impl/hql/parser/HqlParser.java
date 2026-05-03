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

package consulo.sql.language.impl.hql.parser;

import consulo.language.ast.IElementType;
import consulo.sql.language.impl.jpql.parser.JpqlParser;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;

/**
 * Hibernate Query Language parser. Inherits the full JPQL grammar and adds
 * the HQL-only path functions {@code ELEMENTS(c)} and {@code INDICES(c)}.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
public class HqlParser extends JpqlParser {
    @Override
    protected boolean isPathFunctionKeyword(IElementType token) {
        return super.isPathFunctionKeyword(token)
            || token == SqlKeywordTokenTypes.ELEMENTS_KEYWORD
            || token == SqlKeywordTokenTypes.INDICES_KEYWORD;
    }
}
