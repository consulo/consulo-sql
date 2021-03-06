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

package com.dci.intellij.dbn.editor.data.ui.table.cell;

import com.dci.intellij.dbn.common.ui.Borders;
import com.dci.intellij.dbn.data.editor.ui.TextFieldPopupProviderForm;
import com.dci.intellij.dbn.data.editor.ui.TextFieldWithPopup;
import com.dci.intellij.dbn.editor.data.model.DatasetEditorModelCell;
import com.dci.intellij.dbn.editor.data.options.DataEditorPopupSettings;
import com.dci.intellij.dbn.editor.data.ui.table.DatasetEditorTable;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.event.KeyEvent;

public class DatasetTableCellEditorWithPopup extends DatasetTableCellEditor {

    public DatasetTableCellEditorWithPopup(DatasetEditorTable table) {
        super(table, new CustomTextFieldWithPopup(table.getProject()));
    }
                                                                                                        
    public TextFieldWithPopup getEditorComponent() {
        return (TextFieldWithPopup) super.getEditorComponent();
    }

    @Override
    public void prepareEditor(final DatasetEditorModelCell cell) {
        getEditorComponent().setUserValueHolder(cell);
        super.prepareEditor(cell);

        // show automatic popup
        final TextFieldPopupProviderForm app = getEditorComponent().getAutoPopupProvider();
        if (app != null && showAutoPopup()) {
            Thread popupThread = new Thread() {
                   public void run() {
                       try {
                           sleep(settings.getPopupSettings().getDelay());
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                       if (!cell.isDisposed() && cell.isEditing()) {
                           app.showPopup();
                       }
                   }
               };
               popupThread.start();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        getEditorComponent().setEditable(editable);
    }


    private boolean showAutoPopup() {
        DataEditorPopupSettings settings = this.settings.getPopupSettings();
        long dataLength = getCell().getColumnInfo().getDataType().getLength();
        if (!isEditable()) return true;
        if (settings.isActive() && (settings.getDataLengthThreshold() < dataLength || dataLength == 0)) {
            if (settings.isActiveIfEmpty() || getTextField().getText().length() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void fireEditingCanceled() {
        getEditorComponent().disposeActivePopup();
        super.fireEditingCanceled();
    }

    @Override
    protected void fireEditingStopped() {
        getEditorComponent().disposeActivePopup();
        super.fireEditingStopped();
    }

    /********************************************************
     *                      KeyListener                     *
     ********************************************************/
    public void keyPressed(KeyEvent keyEvent) {
        if (!keyEvent.isConsumed()) {
            TextFieldPopupProviderForm popupProviderForm = getEditorComponent().getActivePopupProvider();
            if (popupProviderForm != null) {
                popupProviderForm.handleKeyPressedEvent(keyEvent);

            } else {
                popupProviderForm = getEditorComponent().getPopupProvider(keyEvent);
                if (popupProviderForm != null) {
                    getEditorComponent().disposeActivePopup();
                    popupProviderForm.showPopup();
                } else {
                    super.keyPressed(keyEvent);
                }
            }
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        TextFieldPopupProviderForm popupProviderForm = getEditorComponent().getActivePopupProvider();
        if (popupProviderForm != null) {
            popupProviderForm.handleKeyReleasedEvent(keyEvent);

        }
    }

    /********************************************************
     *                  TextFieldWithPopup                  *
     ********************************************************/
    private static class CustomTextFieldWithPopup extends TextFieldWithPopup {
        private CustomTextFieldWithPopup(Project project) {
            super(project);
            setBackground(UIUtil.getTableBackground());
        }

        @Override
        public void customizeTextField(JTextField textField) {
            textField.setBorder(Borders.EMPTY_BORDER);
            //textField.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(new Insets(1, 1, 1, 1))));
        }

        @Override
        public void customizeButton(JButton button) {
            button.setMargin(Borders.EMPTY_INSETS);
            button.setBackground(UIUtil.getTableBackground());
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void setEditable(boolean editable) {
            super.setEditable(editable);
            setBackground(getTextField().getBackground());
            getButton().setBackground(getTextField().getBackground());
        }
    }
}
