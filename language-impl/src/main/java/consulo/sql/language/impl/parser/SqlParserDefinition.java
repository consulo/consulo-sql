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

package consulo.sql.language.impl.parser;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersionableParserDefinition;
import consulo.sql.language.SqlLanguage;
import consulo.sql.language.impl.psi.*;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22/10/2021
 */
@ExtensionImpl
public class SqlParserDefinition extends LanguageVersionableParserDefinition {
    private static final IFileElementType FILE_ELEMENT_TYPE = new IFileElementType("SQL_FILE", SqlLanguage.INSTANCE);

    static {
        SqlKeywordTokenTypes.init();
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return SqlLanguage.INSTANCE;
    }

    @Nonnull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE_ELEMENT_TYPE;
    }

    @Nonnull
    @Override
    public PsiFile createFile(@Nonnull FileViewProvider fileViewProvider) {
        return new SqlFileImpl(fileViewProvider);
    }

    @Nonnull
    @Override
    public PsiElement createElement(@Nonnull ASTNode node) {
        IElementType type = node.getElementType();

        if (type == SqlCompositeElementTypes.SELECT_STATEMENT) {
            return new SqlSelectStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.INSERT_STATEMENT) {
            return new SqlInsertStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.UPDATE_STATEMENT) {
            return new SqlUpdateStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.DELETE_STATEMENT) {
            return new SqlDeleteStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.CREATE_TABLE_STATEMENT) {
            return new SqlCreateTableStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.DROP_TABLE_STATEMENT) {
            return new SqlDropTableStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.ALTER_TABLE_STATEMENT) {
            return new SqlAlterTableStatementImpl(node);
        }
        if (type == SqlCompositeElementTypes.QUERY_EXPRESSION) {
            return new SqlQueryExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.SELECT_CLAUSE) {
            return new SqlSelectClauseImpl(node);
        }
        if (type == SqlCompositeElementTypes.FROM_CLAUSE) {
            return new SqlFromClauseImpl(node);
        }
        if (type == SqlCompositeElementTypes.WHERE_CLAUSE) {
            return new SqlWhereClauseImpl(node);
        }
        if (type == SqlCompositeElementTypes.BINARY_EXPRESSION) {
            return new SqlBinaryExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.FUNCTION_CALL_EXPRESSION) {
            return new SqlFunctionCallExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.LITERAL_EXPRESSION) {
            return new SqlLiteralExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.REFERENCE_EXPRESSION) {
            return new SqlReferenceExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.TABLE_EXPRESSION) {
            return new SqlTableExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.COLUMN_DEFINITION) {
            return new SqlColumnDefinitionImpl(node);
        }
        if (type == SqlCompositeElementTypes.NAMED_PLACEHOLDER_EXPRESSION) {
            return new SqlNamedPlaceholderExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.NAMED_PLACEHOLDER_REFERENCE) {
            return new SqlNamedPlaceholderReferenceImpl(node);
        }
        if (type == SqlCompositeElementTypes.POSITION_PLACEHOLDER_EXPRESSION) {
            return new SqlPositionPlaceholderExpressionImpl(node);
        }
        if (type == SqlCompositeElementTypes.PLACEHOLDER_POSITION) {
            return new SqlPlaceholderPositionImpl(node);
        }
        if (type == SqlCompositeElementTypes.ANONYMOUS_PLACEHOLDER_EXPRESSION) {
            return new SqlAnonymousPlaceholderExpressionImpl(node);
        }

        return new SqlCompositeElementImpl(node);
    }
}
