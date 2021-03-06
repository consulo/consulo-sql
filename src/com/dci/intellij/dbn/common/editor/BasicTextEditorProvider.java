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

package com.dci.intellij.dbn.common.editor;

import javax.swing.Icon;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.common.util.DocumentUtil;
import com.dci.intellij.dbn.common.util.EditorUtil;
import com.dci.intellij.dbn.vfs.DatabaseEditableObjectFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class BasicTextEditorProvider implements FileEditorProvider, DumbAware
{
	@Override
	@NotNull
	public FileEditorState readState(@NotNull Element sourceElement,
			@NotNull final Project project,
			@NotNull final VirtualFile virtualFile)
	{
		BasicTextEditorState editorState = new BasicTextEditorState();
		Document document = ApplicationManager.getApplication().runReadAction(new Computable<Document>()
		{
			@Override
			public Document compute()
			{
				return DocumentUtil.getDocument(virtualFile);
			}
		});
		editorState.readState(sourceElement, project, document);
		return editorState;
	}

	@Override
	public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement)
	{
		if(state instanceof BasicTextEditorState)
		{
			BasicTextEditorState editorState = (BasicTextEditorState) state;
			editorState.writeState(targetElement, project);
		}
	}

	protected void updateTabIcon(final DatabaseEditableObjectFile databaseFile,
			final BasicTextEditor textEditor,
			final Icon icon)
	{
		new SimpleLaterInvocator()
		{
			@Override
			public void run()
			{
				EditorUtil.setEditorIcon(databaseFile, textEditor, icon);
			}
		}.start();
	}
}
