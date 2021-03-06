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

package com.dci.intellij.dbn.object.action;

import com.dci.intellij.dbn.browser.DatabaseBrowserManager;
import com.dci.intellij.dbn.browser.ui.DatabaseBrowserTree;
import com.dci.intellij.dbn.common.Colors;
import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.object.common.DBObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Point;
import java.util.List;

public abstract class ObjectListShowAction extends AnAction {
    protected DBObject sourceObject;
    protected RelativePoint popupLocation;

    public ObjectListShowAction(String text, DBObject sourceObject) {
        super(text);
        this.sourceObject = sourceObject;
    }

    public void setPopupLocation(RelativePoint popupLocation) {
        this.popupLocation = popupLocation;
    }

    public abstract List<DBObject> getObjectList();
    public abstract String getTitle();
    public abstract String getEmptyListMessage();
    public abstract String getListName();

    public final void actionPerformed(final AnActionEvent e) {
        new BackgroundTask(sourceObject.getProject(), "Loading " + getListName(), false, true) {

            @Override
            public void execute(@NotNull ProgressIndicator progressIndicator) {
                initProgressIndicator(progressIndicator, true);
                List<DBObject> objects = getObjectList();
                if (!progressIndicator.isCanceled()) {
                    if (objects.size() > 0) {
                        final ObjectListActionGroup actionGroup = new ObjectListActionGroup(ObjectListShowAction.this, objects);
                        new SimpleLaterInvocator() {
                            public void run() {
                                JBPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
                                        ObjectListShowAction.this.getTitle(),
                                        actionGroup,
                                        e.getDataContext(),
                                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                                        true, null, 10);

                                popup.getContent().setBackground(Colors.LIGHT_BLUE);
                                showPopup(popup);
                            }
                        }.start();

                    }
                    else {
                        new SimpleLaterInvocator() {
                            public void run() {
                                JLabel label = new JLabel(getEmptyListMessage(), Icons.EXEC_MESSAGES_INFO, SwingConstants.LEFT);
                                label.setBorder(new EmptyBorder(3, 3, 3, 3));
                                JPanel panel = new JPanel(new BorderLayout());
                                panel.add(label);
                                panel.setBackground(Colors.LIGHT_BLUE);
                                ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, null);
                                JBPopup popup = popupBuilder.createPopup();
                                showPopup(popup);
                            }
                        }.start();
                    }
                }
            }
        }.start();
    }

    private void showPopup(JBPopup popup) {
        if (popupLocation == null) {
            DatabaseBrowserManager browserManager = DatabaseBrowserManager.getInstance(sourceObject.getProject());
            DatabaseBrowserTree activeBrowserTree = browserManager.getActiveBrowserTree();
            if (activeBrowserTree != null) {
                popupLocation = TreeUtil.getPointForSelection(activeBrowserTree);
                Point point = popupLocation.getPoint();
                point.setLocation(point.getX() + 20, point.getY() + 4);
            }
        }
        if (popupLocation != null) {
            popup.show(popupLocation);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }

    protected abstract AnAction createObjectAction(DBObject object);
}
