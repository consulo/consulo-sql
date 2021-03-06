/*
 * Copyright 2012-2014 Dan Cioca
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

package com.dci.intellij.dbn.language.common;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DBLanguageBraceMatcher implements PairedBraceMatcher {
    private final BracePair[] bracePairs;
    private SqlLikeLanguage language;

    public DBLanguageBraceMatcher(SqlLikeLanguage language) {
        this.language = language;
        SharedTokenTypeBundle tt = language.getSharedTokenTypes();
        bracePairs = new BracePair[]{
            new BracePair(tt.getLeftParenthesis(), tt.getRightParenthesis(), false),
            new BracePair(tt.getTokenType("CHR_LEFT_BRACKET"), tt.getTokenType("CHR_RIGHT_BRACKET"), false)};
    }

    public BracePair[] getPairs() {
        return bracePairs;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
        if (iElementType1 instanceof SimpleTokenType) {
            SimpleTokenType simpleTokenType = (SimpleTokenType) iElementType1;
            SharedTokenTypeBundle tt = language.getSharedTokenTypes();
            return simpleTokenType == tt.getWhiteSpace() ||
                    simpleTokenType == tt.getTokenType("CHR_DOT") ||
                    simpleTokenType == tt.getTokenType("CHR_COMMA") ||
                    simpleTokenType == tt.getTokenType("CHR_COLON") ||
                    simpleTokenType == tt.getTokenType("CHR_SEMICOLON");

        }
        return iElementType1 == null;
    }

    public int getCodeConstructStart(PsiFile psiFile, int i) {
        return i;
    }
}
