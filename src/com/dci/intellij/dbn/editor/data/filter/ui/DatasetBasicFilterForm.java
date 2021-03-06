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

package com.dci.intellij.dbn.editor.data.filter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.compatibility.CompatibilityUtil;
import com.dci.intellij.dbn.common.options.ui.ConfigurationEditorForm;
import com.dci.intellij.dbn.common.thread.WriteActionRunner;
import com.dci.intellij.dbn.common.util.ActionUtil;
import com.dci.intellij.dbn.common.util.DocumentUtil;
import com.dci.intellij.dbn.editor.data.filter.DatasetBasicFilter;
import com.dci.intellij.dbn.editor.data.filter.DatasetBasicFilterCondition;
import com.dci.intellij.dbn.editor.data.filter.action.CreateBasicFilterConditionAction;
import com.dci.intellij.dbn.language.sql.SQLFile;
import com.dci.intellij.dbn.language.sql.SQLLanguage;
import com.dci.intellij.dbn.object.DBDataset;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.ui.UIUtil;

public class DatasetBasicFilterForm extends ConfigurationEditorForm<DatasetBasicFilter> {
    private JRadioButton joinAndRadioButton;
    private JRadioButton joinOrRadioButton;
    private JPanel conditionsPanel;
    private JPanel mainPanel;
    private JPanel actionsPanel;
    private JTextField nameTextField;
    private JLabel errorLabel;
    private JPanel previewPanel;

    private DBDataset dataset;
    private List<DatasetBasicFilterConditionForm> conditionForms = new ArrayList<DatasetBasicFilterConditionForm>();
    private Document previewDocument;
    private boolean isCustomNamed;
    private EditorEx viewer;


    public DatasetBasicFilterForm(DBDataset dataset, DatasetBasicFilter filter) {
        super(filter);
        conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));
        this.dataset = dataset;
        nameTextField.setText(filter.getDisplayName());

        ActionToolbar actionToolbar = ActionUtil.createActionToolbar(
                "DBNavigator.DataEditor.SimpleFilter.Add", true,
                new CreateBasicFilterConditionAction(this));
        actionsPanel.add(actionToolbar.getComponent(), BorderLayout.WEST);

        for (DatasetBasicFilterCondition condition : filter.getConditions()) {
            addConditionPanel(condition);
        }

        joinAndRadioButton.setSelected(filter.getJoinType() == DatasetBasicFilter.JOIN_TYPE_AND);
        joinOrRadioButton.setSelected(filter.getJoinType() == DatasetBasicFilter.JOIN_TYPE_OR);

        nameTextField.addKeyListener(createKeyListener());
        registerComponent(joinAndRadioButton);
        registerComponent(joinOrRadioButton);

        if (filter.getError() == null) {
            errorLabel.setText("");
        } else {
            errorLabel.setText(filter.getError());
            errorLabel.setIcon(Icons.EXEC_MESSAGES_ERROR);
        }
        updateNameAndPreview();
        isCustomNamed = filter.isCustomNamed();
    }

    public void focus() {
        if (conditionForms.size() > 0) {
            conditionForms.get(0).focus();
        }
    }

    protected ActionListener createActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateNameAndPreview();
            }
        };
    }

    private KeyListener createKeyListener() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                isCustomNamed = true;
                nameTextField.setForeground(UIUtil.getTextFieldForeground());
            }
        };
    }


    public void updateGeneratedName() {
        if (!isDisposed() && (!isCustomNamed || nameTextField.getText().trim().length() == 0)) {
            getConfiguration().setCustomNamed(false);
            boolean addSeparator = false;
            StringBuilder buffer = new StringBuilder();
            for (DatasetBasicFilterConditionForm conditionForm : conditionForms) {
                if (conditionForm.isActive()) {
                    if (addSeparator) buffer.append(joinAndRadioButton.isSelected() ? " & " : " | ");
                    addSeparator = true;
                    buffer.append(conditionForm.getValue());
                    if (buffer.length() > 40) {
                        buffer.setLength(40);
                        buffer.append("...");
                        break;
                    }
                }
            }

            String name = buffer.length() > 0 ? buffer.toString() : getConfiguration().getFilterGroup().createFilterName("Filter");
            nameTextField.setText(name);
            nameTextField.setForeground(Color.GRAY);
        }
    }

    public void updateNameAndPreview() {
        updateGeneratedName();
        final StringBuilder selectStatement = new StringBuilder("select * from ");
        selectStatement.append(dataset.getSchema().getQuotedName(false)).append('.');
        selectStatement.append(dataset.getQuotedName(false));
        selectStatement.append(" where\n    ");

        boolean addJoin = false;
        for (DatasetBasicFilterConditionForm conditionForm : conditionForms) {
            DatasetBasicFilterCondition condition = conditionForm.getCondition();
            if (conditionForm.isActive()) {
                if (addJoin) {
                    selectStatement.append(joinAndRadioButton.isSelected() ? " and\n    " : " or\n    ");
                }
                addJoin = true;
                condition.appendConditionString(selectStatement, dataset);
            }
        }

        if (previewDocument == null) {
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(dataset.getProject());

            SQLFile selectStatementFile = (SQLFile)
                psiFileFactory.createFileFromText(
                    "filter.sql",
						SQLLanguage.INSTANCE,
						dataset.getLanguageDialect(SQLLanguage.INSTANCE),
                    selectStatement.toString());

            selectStatementFile.setActiveConnection(dataset.getConnectionHandler());
            selectStatementFile.setCurrentSchema(dataset.getSchema());
            previewDocument = DocumentUtil.getDocument(selectStatementFile);

            viewer = (EditorEx) EditorFactory.getInstance().createViewer(previewDocument, dataset.getProject());
            viewer.setEmbeddedIntoDialogWrapper(true);
            JScrollPane viewerScrollPane = viewer.getScrollPane();
            SyntaxHighlighter syntaxHighlighter = dataset.getLanguageDialect(SQLLanguage.INSTANCE).getSyntaxHighlighter();
            EditorColorsScheme colorsScheme = viewer.getColorsScheme();
            viewer.setHighlighter(HighlighterFactory.createHighlighter(syntaxHighlighter, colorsScheme));
            viewer.setBackgroundColor(colorsScheme.getColor(ColorKey.find("CARET_ROW_COLOR")));
            viewerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            viewerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            //viewerScrollPane.setBorder(null);
            viewerScrollPane.setViewportBorder(new LineBorder(CompatibilityUtil.getEditorBackgroundColor(viewer), 4, false));

            EditorSettings settings = viewer.getSettings();
            settings.setFoldingOutlineShown(false);
            settings.setLineMarkerAreaShown(false);
            settings.setLineNumbersShown(false);
            settings.setVirtualSpace(false);
            settings.setDndEnabled(false);
            settings.setAdditionalLinesCount(2);
            settings.setRightMarginShown(false);
            previewPanel.add(viewer.getComponent(), BorderLayout.CENTER);

        } else {
            new WriteActionRunner() {
                public void run() {
                    previewDocument.setText(selectStatement);
                }
            }.start();
        }
    }

    public String getFilterName() {
        return nameTextField.getText();
    }

    public DBDataset getDataset() {
        return dataset;
    }

    public void addConditionPanel(DatasetBasicFilterCondition condition) {
        condition.createComponent();
        DatasetBasicFilterConditionForm conditionForm = condition.getSettingsEditor();
        conditionForm.setBasicFilterPanel(this);
        conditionForms.add(conditionForm);
        conditionsPanel.add(conditionForm.getComponent());
        conditionsPanel.updateUI();
        conditionForm.focus();
    }

    public void addConditionPanel() {
        DatasetBasicFilter filter = getConfiguration();
        DatasetBasicFilterCondition condition = new DatasetBasicFilterCondition(filter);
        addConditionPanel(condition);
        updateNameAndPreview();
    }

    public void removeConditionPanel(DatasetBasicFilterConditionForm conditionForm) {
        conditionForm.setBasicFilterPanel(null);
        conditionForms.remove(conditionForm);
        conditionsPanel.remove(conditionForm.getComponent());
        conditionsPanel.updateUI();
        updateNameAndPreview();
    }


    /*************************************************
     *                  SettingsEditor               *
     *************************************************/
    public JPanel getComponent() {
        return mainPanel;
    }

    public void applyChanges() throws ConfigurationException {
        updateGeneratedName();
        DatasetBasicFilter filter = getConfiguration();
        filter.setJoinType(joinAndRadioButton.isSelected() ?
                DatasetBasicFilter.JOIN_TYPE_AND :
                DatasetBasicFilter.JOIN_TYPE_OR);
        filter.setCustomNamed(isCustomNamed);
        filter.getConditions().clear();
        for (DatasetBasicFilterConditionForm conditionForm : conditionForms) {
            conditionForm.applyChanges();
            filter.addCondition(conditionForm.getConfiguration());
        }
        filter.setName(nameTextField.getText());
    }

    public void resetChanges() {

    }

    @Override
    public void dispose() {
        super.dispose();
        EditorFactory.getInstance().releaseEditor(viewer);
        viewer = null;
        previewDocument = null;
        for (DatasetBasicFilterConditionForm conditionForm : conditionForms) {
            conditionForm.dispose();
        }
        conditionForms.clear();
    }
}
