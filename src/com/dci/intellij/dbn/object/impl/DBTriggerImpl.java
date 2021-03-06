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

package com.dci.intellij.dbn.object.impl;

import com.dci.intellij.dbn.browser.model.BrowserTreeNode;
import com.dci.intellij.dbn.browser.ui.HtmlToolTipBuilder;
import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.database.DatabaseDDLInterface;
import com.dci.intellij.dbn.database.DatabaseMetadataInterface;
import com.dci.intellij.dbn.ddl.DDLFileManager;
import com.dci.intellij.dbn.ddl.DDLFileType;
import com.dci.intellij.dbn.ddl.DDLFileTypeId;
import com.dci.intellij.dbn.editor.DBContentType;
import com.dci.intellij.dbn.object.DBDataset;
import com.dci.intellij.dbn.object.DBTrigger;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.dci.intellij.dbn.object.common.DBSchemaObjectImpl;
import com.dci.intellij.dbn.object.common.loader.DBObjectTimestampLoader;
import com.dci.intellij.dbn.object.common.loader.DBSourceCodeLoader;
import com.dci.intellij.dbn.object.common.operation.DBOperationExecutor;
import com.dci.intellij.dbn.object.common.operation.DBOperationNotSupportedException;
import com.dci.intellij.dbn.object.common.operation.DBOperationType;
import com.dci.intellij.dbn.object.common.property.DBObjectProperties;
import com.dci.intellij.dbn.object.common.property.DBObjectProperty;
import com.dci.intellij.dbn.object.common.status.DBObjectStatus;
import com.dci.intellij.dbn.object.common.status.DBObjectStatusHolder;
import com.dci.intellij.dbn.object.properties.PresentableProperty;
import com.dci.intellij.dbn.object.properties.SimplePresentableProperty;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBTriggerImpl extends DBSchemaObjectImpl implements DBTrigger {
    private boolean isForEachRow;
    private TriggerType triggerType;
    private TriggeringEvent[] triggeringEvents;

    public DBTriggerImpl(DBDataset dataset, ResultSet resultSet) throws SQLException {
        super(dataset, DBContentType.CODE, resultSet);
    }

    @Override
    protected void initObject(ResultSet resultSet) throws SQLException {
        name = resultSet.getString("TRIGGER_NAME");
        isForEachRow = resultSet.getString("IS_FOR_EACH_ROW").equals("Y");

        String triggerTypeString = resultSet.getString("TRIGGER_TYPE");
        triggerType =
                triggerTypeString.contains("BEFORE") ? TRIGGER_TYPE_BEFORE :
                        triggerTypeString.contains("AFTER") ? TRIGGER_TYPE_AFTER :
                                triggerTypeString.contains("INSTEAD OF") ? TRIGGER_TYPE_INSTEAD_OF :
                                        TRIGGER_TYPE_UNKNOWN;


        String triggeringEventString = resultSet.getString("TRIGGERING_EVENT");
        List<TriggeringEvent> triggeringEventList = new ArrayList<TriggeringEvent>();
        if (triggeringEventString.contains("INSERT")) triggeringEventList.add(TRIGGERING_EVENT_INSERT);
        if (triggeringEventString.contains("UPDATE")) triggeringEventList.add(TRIGGERING_EVENT_UPDATE);
        if (triggeringEventString.contains("DELETE")) triggeringEventList.add(TRIGGERING_EVENT_DELETE);
        if (triggeringEventString.contains("TRUNCATE")) triggeringEventList.add(TRIGGERING_EVENT_TRUNCATE);
        if (triggeringEventString.contains("DROP")) triggeringEventList.add(TRIGGERING_EVENT_DROP);
        if (triggeringEventList.size() == 0) triggeringEventList.add(TRIGGERING_EVENT_UNKNOWN);

        triggeringEvents = triggeringEventList.toArray(new TriggeringEvent[triggeringEventList.size()]);    }

    public void initStatus(ResultSet resultSet) throws SQLException {
        boolean isEnabled = resultSet.getString("IS_ENABLED").equals("Y");
        boolean isValid = resultSet.getString("IS_VALID").equals("Y");
        boolean isDebug = resultSet.getString("IS_DEBUG").equals("Y");
        DBObjectStatusHolder objectStatus = getStatus();
        objectStatus.set(DBObjectStatus.ENABLED, isEnabled);
        objectStatus.set(DBObjectStatus.VALID, isValid);
        objectStatus.set(DBObjectStatus.DEBUG, isDebug);
    }

    @Override
    public void initProperties() {
        DBObjectProperties properties = getProperties();
        properties.set(DBObjectProperty.EDITABLE);
        properties.set(DBObjectProperty.DISABLEABLE);
        properties.set(DBObjectProperty.COMPILABLE);
        properties.set(DBObjectProperty.SCHEMA_OBJECT);
    }

    public boolean isForEachRow() {
        return isForEachRow;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public TriggeringEvent[] getTriggeringEvents() {
        return triggeringEvents;
    }

    @Override
    public Icon getIcon() {
        DBObjectStatusHolder status = getStatus();
        if (status.is(DBObjectStatus.VALID)) {
            if (status.is(DBObjectStatus.ENABLED)) {
                if (status.is(DBObjectStatus.DEBUG)) {
                    return Icons.DBO_TRIGGER_DEBUG;
                } else {
                    return Icons.DBO_TRIGGER;
                }
            } else {
                if (status.is(DBObjectStatus.DEBUG)) {
                    return Icons.DBO_TRIGGER_DISABLED_DEBUG;
                } else {
                    return Icons.DBO_TRIGGER_DISABLED;
                }
            }
        } else {
            if (status.is(DBObjectStatus.ENABLED)) {
                return Icons.DBO_TRIGGER_ERR;
            } else {
                return Icons.DBO_TRIGGER_ERR_DISABLED;
            }

        }
    }

    @Override
    public DBOperationExecutor getOperationExecutor() {
        return new DBOperationExecutor() {
            public void executeOperation(DBOperationType operationType) throws SQLException, DBOperationNotSupportedException {
                Connection connection = getConnectionHandler().getStandaloneConnection(getSchema());
                DatabaseMetadataInterface metadataInterface = getConnectionHandler().getInterfaceProvider().getMetadataInterface();
                if (operationType == DBOperationType.ENABLE) {
                    metadataInterface.enableTrigger(getSchema().getName(), getName(), connection);
                    getStatus().set(DBObjectStatus.ENABLED, true);
                } else if (operationType == DBOperationType.DISABLE) {
                    metadataInterface.disableTrigger(getSchema().getName(), getName(), connection);
                    getStatus().set(DBObjectStatus.ENABLED, false);
                } else {
                    throw new DBOperationNotSupportedException(operationType, getObjectType());
                }
            }
        };
    }

    public DBObjectType getObjectType() {
        return DBObjectType.TRIGGER;
    }

    public DBDataset getDataset() {
        return (DBDataset) getParentObject();
    }

    public void buildToolTip(HtmlToolTipBuilder ttb) {
        ttb.append(true, getObjectType().getName(), true);
        StringBuilder triggerDesc = new StringBuilder();
        triggerDesc.append(" - ");
        triggerDesc.append(triggerType.getName().toLowerCase());
        triggerDesc.append(" ") ;

        for (TriggeringEvent triggeringEvent : triggeringEvents) {
            if (triggeringEvent != triggeringEvents[0]) triggerDesc.append(" or ");
            triggerDesc.append(triggeringEvent.getName());
        }
        triggerDesc.append(" on ");
        triggerDesc.append(getDataset().getName());

        ttb.append(false, triggerDesc.toString(), false);

        ttb.createEmptyRow();
        super.buildToolTip(ttb);
    }

    @Override
    public List<PresentableProperty> getPresentableProperties() {
        List<PresentableProperty> properties = super.getPresentableProperties();
        StringBuilder events = new StringBuilder(triggerType.getName().toLowerCase());
        events.append(" ");
        for (TriggeringEvent triggeringEvent : triggeringEvents) {
            if (triggeringEvent != triggeringEvents[0]) events.append(" or ");
            events.append(triggeringEvent.getName().toUpperCase());
        }

        properties.add(0, new SimplePresentableProperty("Trigger event", events.toString()));
        return properties;
    }

    /*********************************************************
     *                     TreeElement                       *
     *********************************************************/

    public boolean isLeafTreeElement() {
        return true;
    }

    @NotNull
    public List<BrowserTreeNode> buildAllPossibleTreeChildren() {
        return BrowserTreeNode.EMPTY_LIST;
    }

    /*********************************************************
     *                         Loaders                       *
     *********************************************************/

    private class SourceCodeLoader extends DBSourceCodeLoader {
        protected SourceCodeLoader(DBObject object) {
            super(object, false);
        }

        public ResultSet loadSourceCode(Connection connection) throws SQLException {
            DatabaseMetadataInterface metadataInterface = getConnectionHandler().getInterfaceProvider().getMetadataInterface();
            return metadataInterface.loadTriggerSourceCode(getSchema().getName(), getName(), connection);
        }
    }

    private static DBObjectTimestampLoader TIMESTAMP_LOADER = new DBObjectTimestampLoader("TRIGGER");

    /*********************************************************
     *                   DBEditableObject                    *
     ********************************************************/

    public String loadCodeFromDatabase(DBContentType contentType) throws SQLException {
        DatabaseDDLInterface ddlInterface = getConnectionHandler().getInterfaceProvider().getDDLInterface();
        String header = ddlInterface.createTriggerEditorHeader(this);
        String body = new SourceCodeLoader(this).load();
        return header + body;
    }

    public String getCodeParseRootId(DBContentType contentType) {
        return "trigger_declaration";
    }

    public DDLFileType getDDLFileType(DBContentType contentType) {
        return DDLFileManager.getInstance(getProject()).getDDLFileType(DDLFileTypeId.TRIGGER);
    }

    public DDLFileType[] getDDLFileTypes() {
        return new DDLFileType[]{getDDLFileType(null)};
    }

    public DBObjectTimestampLoader getTimestampLoader(DBContentType contentType) {
        return TIMESTAMP_LOADER;
    }

}
