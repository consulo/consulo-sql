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

package com.dci.intellij.dbn.data.ui.table.basic;

import com.dci.intellij.dbn.data.editor.color.DataGridTextAttributes;
import com.dci.intellij.dbn.data.find.DataSearchResult;
import com.dci.intellij.dbn.data.find.DataSearchResultMatch;
import com.dci.intellij.dbn.data.model.DataModel;
import com.dci.intellij.dbn.data.model.DataModelCell;
import com.dci.intellij.dbn.data.model.basic.BasicDataModel;
import com.dci.intellij.dbn.data.ui.table.sortable.SortableTable;
import com.dci.intellij.dbn.data.value.LazyLoadedValue;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.JTable;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.util.Iterator;


public class BasicTableCellRenderer extends ColoredTableCellRenderer {
    public BasicTableCellRenderer(Project project) {
    }

    protected void customizeCellRenderer(JTable table, Object value, boolean selected, boolean hasFocus, int rowIndex, int columnIndex) {
        DataGridTextAttributes configTextAttributes = ((BasicTable) table).getConfigTextAttributes();

        SortableTable sortableTable = (SortableTable) table;
        boolean isLoading = sortableTable.isLoading();

        DataModelCell cell = (DataModelCell) value;
        if (cell != null && cell.getUserValue() != null) {
            boolean isLazyValue = cell.getUserValue() instanceof LazyLoadedValue;
            //append(cell.getFormattedUserValue(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            SimpleTextAttributes textAttributes =
                    isLoading ? configTextAttributes.getLoadingData() :
                    isLazyValue ? configTextAttributes.getReadonlyData() : configTextAttributes.getPlainData();
            writeUserValue(cell, textAttributes, configTextAttributes);
        }

        //updateBorder(cell, sortableTable);
        if (!selected) {
            boolean isCaretRow = table.getCellSelectionEnabled() && table.getSelectedRow() == rowIndex && table.getSelectedRowCount() == 1;
            if (isCaretRow) {
                setBackground(configTextAttributes.getCaretRowBgColor());
            } else if (isLoading) {
                setBackground(configTextAttributes.getLoadingData().getBgColor());
            } else {
                setBackground(configTextAttributes.getPlainData().getBgColor());
            }
        }
    }

    protected void updateBorder(DataModelCell cell, BasicTable table) {
        BasicDataModel model = table.getModel();
        if (model.hasSearchResult()) {
            DataSearchResult searchResult = model.getSearchResult();
            if (searchResult.getSelectedMatch() != null && searchResult.getSelectedMatch().getCell() == cell) {
                setBorder(new LineBorder(Color.BLACK));
            }
        } else {
            setBorder(null);
        }
    }

    protected void writeUserValue(DataModelCell cell, SimpleTextAttributes textAttributes, DataGridTextAttributes configTextAttributes) {
         String formattedUserValue;
         if (cell.getUserValue() instanceof String) {
             formattedUserValue = (String) cell.getUserValue();
             if (formattedUserValue.indexOf('\n') > -1) {
                 formattedUserValue = formattedUserValue.replace('\n', ' ');
             }

         } else {
             formattedUserValue = cell.getFormattedUserValue();
         }

         if (cell.isDisposed()) return;
         DataModel model = cell.getRow().getModel();
         if (model.hasSearchResult()) {
             DataSearchResult searchResult = model.getSearchResult();

             Iterator<DataSearchResultMatch> matches = searchResult.getMatches(cell);
             if (matches != null) {
                 int lastEndOffset = 0;
                 SimpleTextAttributes searchResultAttributes = configTextAttributes.getSearchResult();
                 DataSearchResultMatch selectedMatch = searchResult.getSelectedMatch();
                 if (selectedMatch != null && selectedMatch.getCell() == cell) {
                    searchResultAttributes = configTextAttributes.getSelection();
                 }
                 
                 while (matches.hasNext()) {
                     DataSearchResultMatch match = matches.next();
                     if (match.getStartOffset() > lastEndOffset) {
                         append(formattedUserValue.substring(lastEndOffset, match.getStartOffset()), textAttributes);
                     }
                     append(formattedUserValue.substring(match.getStartOffset(), match.getEndOffset()), searchResultAttributes);
                     lastEndOffset = match.getEndOffset();

                 }
                 if (lastEndOffset < formattedUserValue.length()) {
                     append(formattedUserValue.substring(lastEndOffset), textAttributes);
                 }
             } else {
                 append(formattedUserValue, textAttributes);
             }

         } else {
             append(formattedUserValue, textAttributes);
         }
     }

    protected boolean match(int[] indexes, int index) {
        for (int idx : indexes) {
            if (idx == index) return true;
        }
        return false;
    }
}
