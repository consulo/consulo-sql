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
import consulo.sql.language.psi.SqlNamedFunctionCall;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2026-05-03
 */
public class SqlNamedFunctionCallImpl extends ASTWrapperPsiElement implements SqlNamedFunctionCall {
    public SqlNamedFunctionCallImpl(@Nonnull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public String getFunctionName() {
        ASTNode literal = getNode().findChildByType(SqlTokenType.SINGLE_QUOTED_LITERAL);
        if (literal == null) {
            return null;
        }
        String raw = literal.getText();
        if (raw.length() < 2 || raw.charAt(0) != '\'' || raw.charAt(raw.length() - 1) != '\'') {
            return raw;
        }
        return raw.substring(1, raw.length() - 1).replace("''", "'");
    }
}
