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

package consulo.sql.language.impl.mariadb;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.lexer.Lexer;
import consulo.sql.language.impl.BaseSqlLanguageVersion;
import consulo.sql.language.impl.lexer.SqlLexer;
import consulo.sql.language.impl.mariadb.lexer._MariaDbLexer;
import consulo.sql.language.impl.version.sql92.Sql92TokenTypes;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2026-03-17
 */
@ExtensionImpl
public class MariaDbLanguageVersion extends BaseSqlLanguageVersion {
    public MariaDbLanguageVersion() {
        super("MariaDB", "MariaDB");

        registerKeywords(Sql92TokenTypes.RESERVED_KEYWORDS);
        registerKeywords(MariaDbTokenTypes.RESERVED_KEYWORDS);
    }

    @Nonnull
    @Override
    public Lexer createLexer() {
        return new SqlLexer(this, new _MariaDbLexer());
    }
}
