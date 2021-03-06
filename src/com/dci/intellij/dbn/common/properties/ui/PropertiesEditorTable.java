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

package com.dci.intellij.dbn.common.properties.ui;

import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.intellij.ui.TableUtil;
import com.intellij.util.ui.UIUtil;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.Map;

public class PropertiesEditorTable extends JTable {

    public PropertiesEditorTable(Map<String, String> properties) {
        super(new PropertiesTableModel(properties));
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        getSelectionModel().addListSelectionListener(selectionListener);
        setSelectionBackground(UIUtil.getTableBackground());
        setSelectionForeground(UIUtil.getTableForeground());
        setCellSelectionEnabled(true);
    }

    ListSelectionListener selectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                startCellEditing();
            }
        }
    };
    
    public void setProperties(Map<String, String> properties) {
        setModel(new PropertiesTableModel(properties));
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        super.columnSelectionChanged(e);
        JTableHeader tableHeader = getTableHeader();
        if (tableHeader != null && tableHeader.getDraggedColumn() == null) {
            if (!e.getValueIsAdjusting()) {
                startCellEditing();
            }
        }
    }

    private void startCellEditing() {
        if (getModel().getRowCount() > 0) {
            int[] selectedRows = getSelectedRows();
            int selectedRow = selectedRows.length > 0 ? selectedRows[0] : 0;

            int[] selectedColumns = getSelectedColumns();
            int selectedColumn = selectedColumns.length > 0 ? selectedColumns[0] : 0;

            editCellAt(selectedRow, selectedColumn);
        }
    }

    private void stopCellEditing() {
        if (isEditing()) getCellEditor().stopCellEditing();
    }

    public Component prepareEditor(TableCellEditor editor, int rowIndex, int columnIndex) {
        final JTextField textField = (JTextField) super.prepareEditor(editor, rowIndex, columnIndex);
        textField.setBorder(new EmptyBorder(0,0,0,0));

        new SimpleLaterInvocator() {
            public void run() {
                textField.selectAll();
                textField.grabFocus();
            }
        }.start();
        selectCell(rowIndex, columnIndex);
        return textField;
    }

    @Override
    public PropertiesTableModel getModel() {
        return (PropertiesTableModel) super.getModel();
    }

    public void insertRow() {
        stopCellEditing();
        int rowIndex = getSelectedRow();
        rowIndex = getModel().getRowCount() == 0 ? 0 : rowIndex + 1;
        getModel().insertRow(rowIndex);
        getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        updateUI();
    }


    public void removeRow() {
        stopCellEditing();
        int selectedRow = getSelectedRow();
        getModel().removeRow(selectedRow);
        updateUI();
        if (getModel().getRowCount() == selectedRow && selectedRow > 0) {
            getSelectionModel().setSelectionInterval(selectedRow -1, selectedRow -1);
        }
    }

    public void moveRowUp() {
        int selectedRow = getSelectedRow();
        int selectedColumn = getSelectedColumn();

        TableUtil.moveSelectedItemsUp(this);
        selectCell(selectedRow -1, selectedColumn);
    }

    public void moveRowDown() {
        int selectedRow = getSelectedRow();
        int selectedColumn = getSelectedColumn();

        TableUtil.moveSelectedItemsDown(this);
        selectCell(selectedRow + 1, selectedColumn);
    }

    public void selectCell(int rowIndex, int columnIndex) {
        scrollRectToVisible(getCellRect(rowIndex, columnIndex, true));
        setRowSelectionInterval(rowIndex, rowIndex);
        setColumnSelectionInterval(columnIndex, columnIndex);
    }
}
