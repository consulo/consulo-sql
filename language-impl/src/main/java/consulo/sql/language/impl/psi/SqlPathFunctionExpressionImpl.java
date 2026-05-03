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

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.sql.language.psi.SqlPathFunctionExpression;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
public class SqlPathFunctionExpressionImpl extends ASTWrapperPsiElement implements SqlPathFunctionExpression {
    public SqlPathFunctionExpressionImpl(@Nonnull ASTNode node) {
        super(node);
    }

    @Nonnull
    @Override
    public Kind getKind() {
        ASTNode kw = getNode().getFirstChildNode();
        while (kw != null) {
            if (kw.getElementType() == SqlKeywordTokenTypes.KEY_KEYWORD) {
                return Kind.KEY;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.VALUE_KEYWORD) {
                return Kind.VALUE;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.ENTRY_KEYWORD) {
                return Kind.ENTRY;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.INDEX_KEYWORD) {
                return Kind.INDEX;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.SIZE_KEYWORD) {
                return Kind.SIZE;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.TYPE_KEYWORD) {
                return Kind.TYPE;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.ELEMENTS_KEYWORD) {
                return Kind.ELEMENTS;
            }
            if (kw.getElementType() == SqlKeywordTokenTypes.INDICES_KEYWORD) {
                return Kind.INDICES;
            }
            kw = kw.getTreeNext();
        }
        throw new IllegalStateException("path-function keyword child is missing on " + getNode().getText());
    }
}
