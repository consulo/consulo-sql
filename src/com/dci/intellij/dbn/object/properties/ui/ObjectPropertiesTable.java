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

import com.dci.intellij.dbn.common.ui.MouseUtil;
import com.dci.intellij.dbn.common.ui.table.DBNTable;
import com.dci.intellij.dbn.object.properties.PresentableProperty;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ObjectPropertiesTable extends DBNTable {

    public ObjectPropertiesTable(Project project, TableModel tableModel) {
        super(project, tableModel, false);
        setDefaultRenderer(String.class, cellRenderer);
        setDefaultRenderer(PresentableProperty.class, cellRenderer);

        addMouseListener(mouseListener);
        addKeyListener(keyListener);
    }

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() > 1) {
                navigateInBrowser();
                event.consume();
            }


            if (MouseUtil.isNavigationEvent(event)) {
                navigateInBrowser();
                event.consume();
            }
        }
    };


    private KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == 10) {  // ENTER
                navigateInBrowser();
            }
        }
    };


    private void navigateInBrowser() {
        int rowIndex = getSelectedRow();
        int columnIndex = getSelectedColumn();
        if (columnIndex == 1) {
            PresentableProperty presentableProperty = (PresentableProperty) getModel().getValueAt(rowIndex, 1);
            Navigatable navigatable = presentableProperty.getNavigatable();
            if (navigatable != null) navigatable.navigate(true);
        }
    }


    protected void processMouseMotionEvent(MouseEvent e) {
        if (e.isControlDown() && e.getID() != MouseEvent.MOUSE_DRAGGED && isNavigableCellAtMousePosition()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            super.processMouseMotionEvent(e);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private boolean isNavigableCellAtMousePosition() {
        Object value = getValueAtMouseLocation();
        if (value instanceof PresentableProperty) {
            PresentableProperty property = (PresentableProperty) value;
            return property.getNavigatable() != null;
        }
        return false;
    }

    TableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            PresentableProperty property = (PresentableProperty) value;
            if (property != null) {
                if (column == 0) {
                    setIcon(null);
                    setText(property.getName());
                    //setFont(GUIUtil.BOLD_FONT);
                } else if (column == 1) {
                    setText(property.getValue());
                    setIcon(property.getIcon());
                    //setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    //setFont(property.getIcon() == null ? GUIUtil.BOLD_FONT : GUIUtil.REGULAR_FONT);
                }
            }

            Dimension dimension = getSize();
            dimension.setSize(dimension.getWidth(), 30);
            setSize(dimension);

            return component;
        }

    };
}
