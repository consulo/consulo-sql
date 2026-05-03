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

import consulo.annotation.component.ExtensionImpl;
import consulo.language.lexer.Lexer;
import consulo.language.parser.PsiParser;
import consulo.sql.language.impl.BaseSqlLanguageVersion;
import consulo.sql.language.impl.hql.parser.HqlParser;
import consulo.sql.language.impl.jpql.JpqlTokenTypes;
import consulo.sql.language.impl.jpql.lexer._JpqlLexer;
import consulo.sql.language.impl.lexer.SqlLexer;
import jakarta.annotation.Nonnull;

/**
 * Hibernate Query Language &mdash; a strict superset of JPQL.
 * <p>
 * Reuses the JPQL lexer (HQL has no extra literal forms over JPQL) and extends
 * the JPQL keyword set with the HQL-only keywords. Like JPQL, it does
 * <em>not</em> register the SQL-92 reserved set.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
@ExtensionImpl
public class HqlLanguageVersion extends BaseSqlLanguageVersion {
    public HqlLanguageVersion() {
        super("HQL", "HQL");

        registerKeywords(JpqlTokenTypes.RESERVED_KEYWORDS);
        registerKeywords(HqlTokenTypes.RESERVED_KEYWORDS);
    }

    @Nonnull
    @Override
    public Lexer createLexer() {
        return new SqlLexer(this, new _JpqlLexer());
    }

    @Nonnull
    @Override
    public PsiParser createParser() {
        return new HqlParser();
    }
}
