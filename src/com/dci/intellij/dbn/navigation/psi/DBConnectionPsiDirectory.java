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

package com.dci.intellij.dbn.navigation.psi;

import com.dci.intellij.dbn.common.dispose.DisposeUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.language.common.psi.EmptySearchScope;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.list.DBObjectList;
import com.dci.intellij.dbn.vfs.DatabaseConnectionFile;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageVersion;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.LanguageVersionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DBConnectionPsiDirectory extends UserDataHolderBase implements PsiDirectory, Disposable {
    private DatabaseConnectionFile virtualFile;

    public DBConnectionPsiDirectory(ConnectionHandler connectionHandler) {
        this.virtualFile = new DatabaseConnectionFile(connectionHandler);
    }

    @NotNull
    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    @NotNull
    public String getName() {
        return virtualFile.getConnectionHandler().getName();
    }

    public ItemPresentation getPresentation() {
        return virtualFile.getConnectionHandler().getObjectBundle();
    }

    public FileStatus getFileStatus() {
        return FileStatus.NOT_CHANGED;
    }

    @Override
    public void dispose() {
        DisposeUtil.dispose(virtualFile);
        virtualFile = null;
    }

    public boolean processChildren(PsiElementProcessor<PsiFileSystemItem> processor) {
        return false;
    }

    @NotNull
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Operation not supported");
    }

    public PsiDirectory getParentDirectory() {
        return null;
    }

    public boolean isDirectory() {
        return false;
    }

    @NotNull
    public Project getProject() throws PsiInvalidElementAccessException {
        return virtualFile.getProject();
    }

    @NotNull
    public Language getLanguage() {
        return Language.ANY;
    }

	@NotNull
	@Override
	public LanguageVersion getLanguageVersion()
	{
		return LanguageVersionUtil.findDefaultVersion(getLanguage());
	}

	public PsiManager getManager() {
        return PsiManager.getInstance(getProject());
    }

    @NotNull
    public PsiElement[] getChildren() {
        List<PsiElement> children = new ArrayList<PsiElement>();
        Collection<DBObjectList<DBObject>> objectLists = virtualFile.getConnectionHandler().getObjectBundle().getObjectListContainer().getObjectLists();
        if (objectLists != null) {
            for (DBObjectList objectList : objectLists) {
                children.add(NavigationPsiCache.getPsiDirectory(objectList));
            }
            return children.toArray(new PsiElement[children.size()]);
        }

        return new PsiElement[0];        
    }

    public PsiDirectory getParent() {
        return null;
    }

    public PsiElement getFirstChild() {
        return null;
    }

    public PsiElement getLastChild() {
        return null;
    }

    public PsiElement getNextSibling() {
        return null;
    }

    public PsiElement getPrevSibling() {
        return null;
    }

    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return null;
    }

    public TextRange getTextRange() {
        return null;  
    }

    public int getStartOffsetInParent() {
        return 0;  
    }

    public int getTextLength() {
        return 0;  
    }

    public PsiElement findElementAt(int offset) {
        return null;  
    }

    public PsiReference findReferenceAt(int offset) {
        return null;  
    }

    public int getTextOffset() {
        return 0;  
    }

    public String getText() {
        return null;  
    }

    @NotNull
    public char[] textToCharArray() {
        return new char[0];  
    }

    public PsiElement getNavigationElement() {
        return this;
    }

    public PsiElement getOriginalElement() {
        return this;
    }

    public boolean textMatches(@NotNull CharSequence text) {
        return false;  
    }

    public boolean textMatches(@NotNull PsiElement element) {
        return false;  
    }

    public boolean textContains(char c) {
        return false;  
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        
    }

    public void acceptChildren(@NotNull PsiElementVisitor visitor) {
        
    }

    public PsiElement copy() {
        return null;  
    }

    public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;  
    }

    public PsiElement addBefore(@NotNull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        return null;  
    }

    public PsiElement addAfter(@NotNull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        return null;  
    }

    public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {
        
    }

    public PsiElement addRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        return null;  
    }

    public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return null;  
    }

    public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return null;  
    }

    public void delete() throws IncorrectOperationException {
        
    }

    public void checkDelete() throws IncorrectOperationException {
        
    }

    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        
    }

    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return null;  
    }

    public boolean isValid() {
        return true;
    }

    public boolean isWritable() {
        return false;  
    }

    public PsiReference getReference() {
        return null;  
    }

    @NotNull
    public PsiReference[] getReferences() {
        return new PsiReference[0];  
    }

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, @Nullable PsiElement lastParent, @NotNull PsiElement place) {
        return false;  
    }

    public PsiElement getContext() {
        return null;  
    }

    public boolean isPhysical() {
        return true;
    }

    @NotNull
    public GlobalSearchScope getResolveScope() {
        return EmptySearchScope.INSTANCE;
    }

    @NotNull
    public SearchScope getUseScope() {
        return EmptySearchScope.INSTANCE;
    }

    public ASTNode getNode() {
        return null;  
    }

    public boolean isEquivalentTo(PsiElement another) {
        return false;  
    }

    @NotNull
    public PsiDirectory[] getSubdirectories() {
        return new PsiDirectory[0];  
    }

    @NotNull
    public PsiFile[] getFiles() {
        return new PsiFile[0];  
    }

    public PsiDirectory findSubdirectory(@NotNull String name) {
        return null;  
    }

    public PsiFile findFile(@NotNull String name) {
        return null;  
    }

    @NotNull
    public PsiDirectory createSubdirectory(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Operation not supported");
    }

    public void checkCreateSubdirectory(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Operation not supported");
    }

    @NotNull
    public PsiFile createFile(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Operation not supported");
    }

    @NotNull
    public PsiFile copyFileFrom(@NotNull String newName, @NotNull PsiFile originalFile) throws IncorrectOperationException {
        throw new IncorrectOperationException("Operation not supported");
    }

    public void checkCreateFile(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Operation not supported");
    }

    public void navigate(boolean requestFocus) {
        virtualFile.getConnectionHandler().getObjectBundle().navigate(requestFocus);
    }

    public boolean canNavigate() {
        return true;
    }

    public boolean canNavigateToSource() {
        return false;  
    }

    public void checkSetName(String name) throws IncorrectOperationException {
        
    }
}
