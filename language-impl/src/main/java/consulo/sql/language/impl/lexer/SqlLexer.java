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

package consulo.sql.language.impl.lexer;

import consulo.language.ast.IElementType;
import consulo.language.lexer.Lexer;
import consulo.language.lexer.LookAheadLexer;
import consulo.sql.language.impl.BaseSqlLanguageVersion;
import consulo.sql.language.impl.psi.SqlKeywordRegistry;
import consulo.sql.language.impl.psi.SqlTokenType;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22/10/2021
 */
public class SqlLexer extends LookAheadLexer {
    @Nonnull
    private final SqlKeywordRegistry myKeywordRegistry;

    public SqlLexer(@Nonnull BaseSqlLanguageVersion baseSqlLanguageVersion) {
        this(baseSqlLanguageVersion, new _SqlLexer());
    }

    public SqlLexer(@Nonnull BaseSqlLanguageVersion baseSqlLanguageVersion, @Nonnull Lexer baseLexer) {
        super(baseLexer);
        myKeywordRegistry = baseSqlLanguageVersion.getKeywordRegistry();
    }

    @Override
    protected void lookAhead(Lexer baseLexer) {
        IElementType tokenType = baseLexer.getTokenType();
        if (tokenType == SqlTokenType.IDENTIFIER) {
            IElementType keywordElement = myKeywordRegistry.toKeyword(baseLexer.getTokenSequence());
            if (keywordElement != null) {
                advanceAs(baseLexer, keywordElement);
            }
            else {
                super.lookAhead(baseLexer);
            }
        }
        else {
            super.lookAhead(baseLexer);
        }
    }
}
