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

import com.dci.intellij.dbn.browser.model.BrowserTreeNode;
import com.dci.intellij.dbn.connection.ConnectionBundle;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBObjectBundle;
import com.intellij.openapi.Disposable;
import com.intellij.ui.SpeedSearchBase;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBrowserTreeSpeedSearch extends SpeedSearchBase<JTree> implements Disposable {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private DatabaseBrowserTree tree;
    private Object[] elements = null;

    public DatabaseBrowserTreeSpeedSearch(DatabaseBrowserTree tree) {
        super(tree);
        this.tree = tree;
        this.tree.getModel().addTreeModelListener(treeModelListener);
    }

    protected int getSelectedIndex() {
        Object[] elements = getAllElements();
        BrowserTreeNode treeNode = getSelectedTreeElement();
        if (treeNode != null) {
            for (int i=0; i<elements.length; i++) {
                if (treeNode == elements[i]) {
                    return i;
                }
            }
        }
        return -1;
    }

    private BrowserTreeNode getSelectedTreeElement() {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath != null) {
            return (BrowserTreeNode) selectionPath.getLastPathComponent();
        }
        return null;
    }

    protected Object[] getAllElements() {
        if (elements == null) {
            List<BrowserTreeNode> nodes = new ArrayList<BrowserTreeNode>();
            BrowserTreeNode root = tree.getModel().getRoot();
            loadElements(nodes, root);
            this.elements = nodes.toArray();
        }
        return elements;
    }

    private void loadElements(List<BrowserTreeNode> nodes, BrowserTreeNode browserTreeNode) {
        if (browserTreeNode.isTreeStructureLoaded()) {
            if (browserTreeNode instanceof ConnectionBundle) {
                ConnectionBundle connectionBundle = (ConnectionBundle) browserTreeNode;
                for (ConnectionHandler connectionHandler : connectionBundle.getConnectionHandlers()){
                    DBObjectBundle objectBundle = connectionHandler.getObjectBundle();
                    loadElements(nodes, objectBundle);
                }
            }
            else {
                for (BrowserTreeNode treeNode : browserTreeNode.getTreeChildren()) {
                    if (treeNode instanceof DBObject) {
                        nodes.add(treeNode);
                    }
                    loadElements(nodes, treeNode);
                }
            }
        }
    }

    protected String getElementText(Object o) {
        BrowserTreeNode treeNode = (BrowserTreeNode) o;
        return treeNode.getPresentableText();
    }

    protected void selectElement(Object o, String s) {
        BrowserTreeNode treeNode = (BrowserTreeNode) o;
        tree.selectElement(treeNode, false);

/*
        TreePath treePath = DatabaseBrowserUtils.createTreePath(treeNode);
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
*/
    }

    TreeModelListener treeModelListener = new TreeModelListener() {

        public void treeNodesChanged(TreeModelEvent e) {
            elements = null;
        }

        public void treeNodesInserted(TreeModelEvent e) {
            elements = null;
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            elements = null;
        }

        public void treeStructureChanged(TreeModelEvent e) {
            elements = null;
        }
    };

    @Override
    public void dispose() {
        tree.getModel().removeTreeModelListener(treeModelListener);
        elements = EMPTY_ARRAY;
        tree = null;
    }
}
