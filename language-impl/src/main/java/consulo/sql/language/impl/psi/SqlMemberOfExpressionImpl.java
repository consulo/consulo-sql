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
import consulo.sql.language.psi.SqlMemberOfExpression;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
public class SqlMemberOfExpressionImpl extends ASTWrapperPsiElement implements SqlMemberOfExpression {
    public SqlMemberOfExpressionImpl(@Nonnull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isNegated() {
        return getNode().findChildByType(SqlKeywordTokenTypes.NOT_KEYWORD) != null;
    }
}
