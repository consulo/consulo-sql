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

package consulo.sql.language.impl.jpql;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.lexer.Lexer;
import consulo.language.parser.PsiParser;
import consulo.sql.language.impl.BaseSqlLanguageVersion;
import consulo.sql.language.impl.jpql.lexer._JpqlLexer;
import consulo.sql.language.impl.jpql.parser.JpqlParser;
import consulo.sql.language.impl.lexer.SqlLexer;
import jakarta.annotation.Nonnull;

/**
 * Jakarta Persistence Query Language &mdash; the standard JPA query dialect.
 * <p>
 * Registers only {@link JpqlTokenTypes#RESERVED_KEYWORDS}. JPQL is intentionally
 * <em>not</em> a superset of SQL-92, so the SQL-92 reserved set is deliberately
 * <em>not</em> registered here.
 *
 * @author VISTALL
 * @since 2026-05-03
 */
@ExtensionImpl
public class JpqlLanguageVersion extends BaseSqlLanguageVersion {
    public JpqlLanguageVersion() {
        super("JPQL", "JPQL");

        registerKeywords(JpqlTokenTypes.RESERVED_KEYWORDS);
    }

    @Nonnull
    @Override
    public Lexer createLexer() {
        return new SqlLexer(this, new _JpqlLexer());
    }

    @Nonnull
    @Override
    public PsiParser createParser() {
        return new JpqlParser();
    }
}
