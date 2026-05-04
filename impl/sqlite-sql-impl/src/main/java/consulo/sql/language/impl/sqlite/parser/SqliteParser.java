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

package consulo.sql.language.impl.sqlite.parser;

import consulo.language.parser.PsiBuilder;
import consulo.sql.language.impl.parser.SqlParser;
import consulo.sql.language.impl.psi.SqlCompositeElementTypes;
import consulo.sql.language.impl.psi.SqlKeywordTokenTypes;
import consulo.sql.language.localize.SqlLocalize;

/**
 * @author VISTALL
 * @since 2026-05-04
 */
public class SqliteParser extends SqlParser {
    /**
     * SQLite form: {@code ANALYZE [schemaOrTable]}. There is no {@code TABLE}
     * keyword and at most one (optional) target.
     */
    @Override
    protected void parseAnalyzeStatement(PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        expectKeyword(builder, SqlKeywordTokenTypes.ANALYZE_KEYWORD, SqlLocalize.parserStatementExpected());

        if (isIdentifier(builder)) {
            parseAnalyzeTableRef(builder);
        }

        mark.done(SqlCompositeElementTypes.ANALYZE_STATEMENT);
    }
}
