package com.dci.intellij.dbn.language.sql.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.dci.intellij.dbn.language.common.psi.LeafPsiElement;
import com.dci.intellij.dbn.language.common.psi.PsiUtil;
import com.dci.intellij.dbn.language.sql.SQLLanguage;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiFile;

public class SQLTemplateContextType extends TemplateContextType {
    protected SQLTemplateContextType() {
        super("SQL", "SQL (DBN)");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        Language language = file.getLanguage();
        if (language instanceof SQLLanguage) {
            LeafPsiElement leafPsiElement = PsiUtil.lookupLeafBeforeOffset(file, offset);
            return leafPsiElement == null || leafPsiElement.getLanguage() instanceof SQLLanguage;
        }
        return false;
    }

    @Nullable
    @Override
    public SyntaxHighlighter createHighlighter() {
        return SQLLanguage.INSTANCE.getFirstVersion().getSyntaxHighlighter();
    }
}
