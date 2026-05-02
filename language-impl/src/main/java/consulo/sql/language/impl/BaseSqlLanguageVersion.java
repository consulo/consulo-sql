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

package consulo.sql.language.impl;

import consulo.language.ast.TokenSet;
import consulo.language.lexer.Lexer;
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersionWithParsing;
import consulo.sql.language.SqlLanguageVersion;
import consulo.sql.language.impl.lexer.SqlLexer;
import consulo.sql.language.impl.parser.SqlParser;
import consulo.sql.language.impl.psi.SqlKeywordElementType;
import consulo.sql.language.impl.psi.SqlKeywordRegistry;
import consulo.sql.language.impl.psi.SqlTokenType;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22/10/2021
 */
public abstract class BaseSqlLanguageVersion extends SqlLanguageVersion implements LanguageVersionWithParsing {
    private final SqlKeywordRegistry myKeywordRegistry = new SqlKeywordRegistry();

    public BaseSqlLanguageVersion(@Nonnull String id, @Nonnull String name) {
        super(id, name);
    }

    protected void registerKeywords(@Nonnull SqlKeywordElementType... keywords) {
        myKeywordRegistry.registerKeywords(keywords);
    }

    @Nonnull
    public SqlKeywordRegistry getKeywordRegistry() {
        return myKeywordRegistry;
    }

    @Nonnull
    @Override
    public Lexer createLexer() {
        return new SqlLexer(this);
    }

    @Nonnull
    @Override
    public PsiParser createParser() {
        return new SqlParser();
    }

    @Nonnull
    @Override
    public TokenSet getWhitespaceTokens() {
        return TokenSet.WHITE_SPACE;
    }

    @Nonnull
    @Override
    public TokenSet getCommentTokens() {
        return SqlTokenType.COMMENTS;
    }

    @Nonnull
    @Override
    public TokenSet getStringLiteralElements() {
        return SqlTokenType.STRINGS;
    }
}
