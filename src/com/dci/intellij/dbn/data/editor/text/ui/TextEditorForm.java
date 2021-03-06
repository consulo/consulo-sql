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

package com.dci.intellij.dbn.data.editor.text.ui;

import com.dci.intellij.dbn.common.ui.DBNForm;
import com.dci.intellij.dbn.common.ui.DBNFormImpl;
import com.dci.intellij.dbn.common.util.ActionUtil;
import com.dci.intellij.dbn.common.util.StringUtil;
import com.dci.intellij.dbn.data.editor.text.TextContentType;
import com.dci.intellij.dbn.data.editor.text.TextEditorAdapter;
import com.dci.intellij.dbn.data.editor.text.actions.TextContentTypeComboBoxAction;
import com.dci.intellij.dbn.data.editor.ui.UserValueHolder;
import com.dci.intellij.dbn.data.value.LazyLoadedValue;
import com.dci.intellij.dbn.editor.data.options.DataEditorQualifiedEditorSettings;
import com.dci.intellij.dbn.editor.data.options.DataEditorSettings;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.sql.SQLException;

public class TextEditorForm extends DBNFormImpl implements DBNForm {
    private JPanel mainPanel;
    private JPanel editorPanel;
    private JPanel actionsPanel;

    private EditorEx editor;
    private UserValueHolder userValueHolder;
    private String error;

    private TextEditorAdapter textEditorAdapter;


    public JComponent getComponent() {
        return mainPanel;
    }

    public TextEditorForm(DocumentListener documentListener, UserValueHolder userValueHolder, TextEditorAdapter textEditorAdapter) throws SQLException {
        this.userValueHolder = userValueHolder;
        this.textEditorAdapter = textEditorAdapter;
        Project project = userValueHolder.getProject();

        if (userValueHolder.getContentType() == null) {
            userValueHolder.setContentType(getPlainTextContentType());
        }

        ActionToolbar actionToolbar = ActionUtil.createActionToolbar(
                "DBNavigator.Place.DataEditor.LonContentTypeEditor", true,
                new TextContentTypeComboBoxAction(this));
        actionsPanel.add(actionToolbar.getComponent(), BorderLayout.WEST);

        String text = readUserValue();
        Document document = EditorFactory.getInstance().createDocument(text == null ? "" : StringUtil.removeCharacter(text, '\r'));
        document.addDocumentListener(documentListener);

        editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, userValueHolder.getContentType().getFileType(), false);
        editor.setEmbeddedIntoDialogWrapper(true);
        editor.getContentComponent().setFocusTraversalKeysEnabled(false);

        editorPanel.add(editor.getComponent(), BorderLayout.CENTER);

    }

    private TextContentType getPlainTextContentType() {
        Project project = userValueHolder.getProject();
        DataEditorQualifiedEditorSettings qualifiedEditorSettings = DataEditorSettings.getInstance(project).getQualifiedEditorSettings();
        return qualifiedEditorSettings.getPlainTextContentType();

    }

    public String readUserValue() throws SQLException {
        Object userValue = userValueHolder.getUserValue();
        if (userValue instanceof String) {
            return (String) userValue;
        } else if (userValue instanceof LazyLoadedValue) {
            LazyLoadedValue lazyLoadedValue = (LazyLoadedValue) userValue;
            return lazyLoadedValue.loadValue();
        }
        return null;
    }

    public void writeUserValue() throws SQLException {
        String text = editor.getDocument().getText();
        userValueHolder.updateUserValue(text, false);
        textEditorAdapter.afterUpdate();
    }

    public TextContentType getContentType() {
        return userValueHolder.getContentType();
    }

    public void setContentType(TextContentType contentType) {
        SyntaxHighlighter syntaxHighlighter = SyntaxHighlighter.PROVIDER.create(contentType.getFileType(), userValueHolder.getProject(), null);
        EditorColorsScheme colorsScheme = editor.getColorsScheme();
        editor.setHighlighter(HighlighterFactory.createHighlighter(syntaxHighlighter, colorsScheme));
        userValueHolder.setContentType(contentType);

    }

    public void dispose() {
        super.dispose();
        EditorFactory.getInstance().releaseEditor(editor);
        //editor = null;

    }

    public JComponent getEditorComponent() {
        return editor.getContentComponent();
    }
}
