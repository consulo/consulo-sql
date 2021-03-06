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

package com.dci.intellij.dbn.browser.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jetbrains.annotations.Nullable;
import com.dci.intellij.dbn.browser.DatabaseBrowserManager;
import com.dci.intellij.dbn.browser.model.BrowserTreeNode;
import com.dci.intellij.dbn.common.ui.DBNForm;
import com.dci.intellij.dbn.common.ui.DBNFormImpl;
import com.dci.intellij.dbn.common.ui.GUIUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.properties.ui.ObjectPropertiesForm;
import com.intellij.openapi.project.Project;
import com.intellij.ui.GuiUtils;

public class BrowserToolWindowForm extends DBNFormImpl implements DBNForm
{
    private JPanel mainPanel;
    private JPanel browserPanel;
    private JPanel closeActionPanel;
    private JPanel objectPropertiesPanel;
    private DatabaseBrowserForm browserForm;

    private Project project;
    private ObjectPropertiesForm objectPropertiesForm;

    public BrowserToolWindowForm(Project project)
    {
        this.project = project;
        //toolWindow.setIcon(dbBrowser.getIcon(0));
        DatabaseBrowserManager browserManager = DatabaseBrowserManager.getInstance(project);

        initBrowserForm();


        /*ActionToolbar objectPropertiesActionToolbar = ActionUtil.createActionToolbar("", false, "DBNavigator.ActionGroup.Browser.ObjectProperties");
        closeActionPanel.add(objectPropertiesActionToolbar.getComponent(), BorderLayout.CENTER);*/

        objectPropertiesPanel.setVisible(browserManager.getShowObjectProperties().value());
        objectPropertiesForm = new ObjectPropertiesForm(project);
        objectPropertiesPanel.add(objectPropertiesForm.getComponent());
        GuiUtils.replaceJSplitPaneWithIDEASplitter(mainPanel);
        GUIUtil.updateSplitterProportion(mainPanel, (float) 0.7);
    }

    private void initBrowserForm()
    {
        browserForm = new SimpleBrowserForm(project);

        browserPanel.removeAll();
        browserPanel.add(browserForm.getComponent(), BorderLayout.CENTER);
    }

    public DatabaseBrowserTree getBrowserTree(ConnectionHandler connectionHandler)
    {

        if(browserForm instanceof SimpleBrowserForm)
        {
            return browserForm.getBrowserTree();
        }

        return null;
    }


    public void showObjectProperties()
    {
        DatabaseBrowserManager browserManager = DatabaseBrowserManager.getInstance(project);
        DatabaseBrowserTree activeBrowserTree = browserManager.getActiveBrowserTree();
        BrowserTreeNode treeNode = activeBrowserTree == null ? null : activeBrowserTree.getSelectedNode();
        if(treeNode instanceof DBObject)
        {
            DBObject object = (DBObject) treeNode;
            objectPropertiesForm.setObject(object);
        }

        objectPropertiesPanel.setVisible(true);
    }

    public void hideObjectProperties()
    {
        objectPropertiesPanel.setVisible(false);
    }

    @Nullable
    public DatabaseBrowserTree getActiveBrowserTree()
    {
        return browserForm.getBrowserTree();
    }

    public DatabaseBrowserForm getBrowserForm()
    {
        return browserForm;
    }

    public JPanel getComponent()
    {
        return mainPanel;
    }

    public void dispose()
    {
        super.dispose();
        objectPropertiesForm.dispose();
        objectPropertiesForm = null;
        browserForm.dispose();
        browserForm = null;
        project = null;
    }
}

