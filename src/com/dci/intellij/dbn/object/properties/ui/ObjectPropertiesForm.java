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

package com.dci.intellij.dbn.object.properties.ui;

import com.dci.intellij.dbn.browser.DatabaseBrowserManager;
import com.dci.intellij.dbn.browser.model.BrowserTreeNode;
import com.dci.intellij.dbn.browser.ui.BrowserSelectionChangeListener;
import com.dci.intellij.dbn.browser.ui.DatabaseBrowserTree;
import com.dci.intellij.dbn.common.event.EventManager;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.common.ui.DBNForm;
import com.dci.intellij.dbn.common.ui.DBNFormImpl;
import com.dci.intellij.dbn.common.ui.table.DBNTable;
import com.dci.intellij.dbn.common.util.NamingUtil;
import com.dci.intellij.dbn.object.common.DBObject;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class ObjectPropertiesForm extends DBNFormImpl implements DBNForm, BrowserSelectionChangeListener {
    private JPanel mainPanel;
    private JLabel objectLabel;
    private JLabel objectTypeLabel;
    private JTable objectPropertiesTable;
    private JBScrollPane objectPropertiesScrollPane;
    private JPanel closeActionPanel;
    private DBObject object;
    private Project project;

    public ObjectPropertiesForm(Project project) {
        this.project = project;
        //ActionToolbar objectPropertiesActionToolbar = ActionUtil.createActionToolbar("", true, "DBNavigator.ActionGroup.Browser.ObjectProperties");
        //closeActionPanel.add(objectPropertiesActionToolbar.getComponent(), BorderLayout.CENTER);
        objectPropertiesTable.setRowHeight(objectPropertiesTable.getRowHeight() + 2);
        objectPropertiesTable.setRowSelectionAllowed(false);
        objectPropertiesTable.setCellSelectionEnabled(true);
        objectPropertiesScrollPane.getViewport().setBackground(objectPropertiesTable.getBackground());
        objectTypeLabel.setText("Object properties:");
        objectLabel.setText("(no object selected)");

        EventManager.subscribe(project, BrowserSelectionChangeListener.TOPIC, this);
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    public void browserSelectionChanged() {
        DatabaseBrowserManager browserManager = DatabaseBrowserManager.getInstance(project);
        if (browserManager.getShowObjectProperties().value()) {
            DatabaseBrowserTree activeBrowserTree = browserManager.getActiveBrowserTree();
            if (activeBrowserTree != null) {
                BrowserTreeNode treeNode = activeBrowserTree.getSelectedNode();
                if (treeNode instanceof DBObject) {
                    DBObject object = (DBObject) treeNode;
                    setObject(object);
                }
            }
        }
    }

    public DBObject getObject() {
        return object;
    }

    public void setObject(final DBObject object) {
        if (!object.equals(this.object)) {
            this.object = object;

            new BackgroundTask(object.getProject(), "Rendering object properties", true) {
                @Override
                public void execute(@NotNull ProgressIndicator progressIndicator) {
                    initProgressIndicator(progressIndicator, true);
                    final ObjectPropertiesTableModel tableModel = new ObjectPropertiesTableModel(object.getPresentableProperties());

                    new SimpleLaterInvocator() {
                        public void run() {
                            objectLabel.setText(object.getName());
                            objectLabel.setIcon(object.getIcon());
                            objectTypeLabel.setText(NamingUtil.capitalize(object.getTypeName()) + ":");


                            objectPropertiesTable.setModel(tableModel);
                            ((DBNTable) objectPropertiesTable).accommodateColumnsSize();
                            mainPanel.repaint();
                        }
                    }.start();
                }
            }.start();
        }
    }

    public void dispose() {
        EventManager.unsubscribe(this);
        super.dispose();
        object = null;
        project = null;
    }

    private void createUIComponents() {
        objectPropertiesTable = new ObjectPropertiesTable(null, new ObjectPropertiesTableModel());
        objectPropertiesTable.getTableHeader().setReorderingAllowed(false);
    }
}
