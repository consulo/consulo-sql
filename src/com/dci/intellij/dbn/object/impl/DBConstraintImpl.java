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
import com.dci.intellij.dbn.common.content.loader.DynamicContentLoader;
import com.dci.intellij.dbn.database.DatabaseMetadataInterface;
import com.dci.intellij.dbn.editor.DBContentType;
import com.dci.intellij.dbn.object.DBColumn;
import com.dci.intellij.dbn.object.DBConstraint;
import com.dci.intellij.dbn.object.DBDataset;
import com.dci.intellij.dbn.object.common.DBObjectRelationType;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.dci.intellij.dbn.object.common.DBSchemaObjectImpl;
import com.dci.intellij.dbn.object.common.list.DBObjectList;
import com.dci.intellij.dbn.object.common.list.DBObjectListContainer;
import com.dci.intellij.dbn.object.common.list.DBObjectNavigationList;
import com.dci.intellij.dbn.object.common.list.DBObjectNavigationListImpl;
import com.dci.intellij.dbn.object.common.list.DBObjectRelationList;
import com.dci.intellij.dbn.object.common.list.DBObjectRelationListContainer;
import com.dci.intellij.dbn.object.common.list.loader.DBObjectListFromRelationListLoader;
import com.dci.intellij.dbn.object.common.operation.DBOperationExecutor;
import com.dci.intellij.dbn.object.common.operation.DBOperationNotSupportedException;
import com.dci.intellij.dbn.object.common.operation.DBOperationType;
import com.dci.intellij.dbn.object.common.property.DBObjectProperties;
import com.dci.intellij.dbn.object.common.property.DBObjectProperty;
import com.dci.intellij.dbn.object.common.status.DBObjectStatus;
import com.dci.intellij.dbn.object.identifier.DBObjectIdentifier;
import com.dci.intellij.dbn.object.properties.DBObjectPresentableProperty;
import com.dci.intellij.dbn.object.properties.PresentableProperty;
import com.dci.intellij.dbn.object.properties.SimplePresentableProperty;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConstraintImpl extends DBSchemaObjectImpl implements DBConstraint {
    private int constraintType;
    private DBObjectIdentifier foreignKeyConstraint;

    private String checkCondition;
    private DBObjectList<DBColumn> columns;

    public DBConstraintImpl(DBDataset dataset, ResultSet resultSet) throws SQLException {
        super(dataset, DBContentType.NONE, resultSet);
    }

    @Override
    protected void initObject(ResultSet resultSet) throws SQLException {
        name = resultSet.getString("CONSTRAINT_NAME");
        checkCondition = resultSet.getString("CHECK_CONDITION");

        String typeString = resultSet.getString("CONSTRAINT_TYPE");
        constraintType =
            typeString == null ? -1 :
            typeString.equals("CHECK")? DBConstraint.CHECK :
            typeString.equals("UNIQUE") ? DBConstraint.UNIQUE_KEY :
            typeString.equals("PRIMARY KEY") ? DBConstraint.PRIMARY_KEY :
            typeString.equals("FOREIGN KEY") ? DBConstraint.FOREIGN_KEY :
            typeString.equals("VIEW CHECK") ? DBConstraint.VIEW_CHECK :
            typeString.equals("VIEW READONLY") ? DBConstraint.VIEW_READONLY : -1;

        if (isForeignKey()) {
            String fkOwner = resultSet.getString("FK_CONSTRAINT_OWNER");
            String fkName = resultSet.getString("FK_CONSTRAINT_NAME");
            foreignKeyConstraint = new DBObjectIdentifier(getConnectionHandler()).
                    add(DBObjectType.SCHEMA, fkOwner).
                    add(DBObjectType.CONSTRAINT, fkName);
        }
    }

    @Override
    protected void initLists() {
        super.initLists();
        DBObjectListContainer childObjects = initChildObjects();
        columns = childObjects.createSubcontentObjectList(
                DBObjectType.COLUMN, this,
                COLUMNS_LOADER, getDataset(),
                DBObjectRelationType.CONSTRAINT_COLUMN, true);
    }

    @Override
    public void initStatus(ResultSet resultSet) throws SQLException {
        boolean enabled = resultSet.getString("IS_ENABLED").equals("Y");
        getStatus().set(DBObjectStatus.ENABLED, enabled);
    }

    @Override
    protected void initProperties() {
        DBObjectProperties properties = getProperties();
        properties.set(DBObjectProperty.SCHEMA_OBJECT);
        properties.set(DBObjectProperty.DISABLEABLE);
    }

    @Override
    public Icon getIcon() {
        boolean enabled = getStatus().is(DBObjectStatus.ENABLED);
        return enabled ? Icons.DBO_CONSTRAINT : Icons.DBO_CONSTRAINT_DISABLED;
    }

    public DBObjectType getObjectType() {
        return DBObjectType.CONSTRAINT;
    }

    public int getConstraintType() {
        return constraintType;
    }

    public boolean isPrimaryKey() {
        return constraintType == PRIMARY_KEY;
    }

    public boolean isForeignKey() {
        return constraintType == FOREIGN_KEY;
    }
    
    public boolean isUniqueKey() {
        return constraintType == UNIQUE_KEY;
    }


    public String getCheckCondition() {
        return checkCondition;
    }

    public DBDataset getDataset() {
        return (DBDataset) getParentObject();
    }

    public List<DBColumn> getColumns() {
        return columns.getObjects();
    }

    public int getColumnPosition(DBColumn column) {
        DBObjectRelationListContainer childObjectRelations = getDataset().getChildObjectRelations();
        if (childObjectRelations != null) {
            DBObjectRelationList<DBConstraintColumnRelation> relations = childObjectRelations.getObjectRelationList(DBObjectRelationType.CONSTRAINT_COLUMN);
            for (DBConstraintColumnRelation relation : relations.getObjectRelations()) {
                if (relation.getConstraint().equals(this) && relation.getColumn().equals(column)) {
                    return relation.getPosition();
                }
            }
        }
        return 0;
    }

    public DBColumn getColumnForPosition(int position) {
        DBObjectRelationListContainer childObjectRelations = getDataset().getChildObjectRelations();
        if (childObjectRelations != null) {
            DBObjectRelationList<DBConstraintColumnRelation> relations = childObjectRelations.getObjectRelationList(DBObjectRelationType.CONSTRAINT_COLUMN);
            for (DBConstraintColumnRelation relation : relations.getObjectRelations()) {
                if (relation.getConstraint().equals(this) && relation.getPosition() == position)
                    return relation.getColumn();
            }
        }
        return null;
    }

    public DBConstraint getForeignKeyConstraint() {
        return foreignKeyConstraint == null ? null : (DBConstraint) foreignKeyConstraint.lookupObject();
    }

    public void buildToolTip(HtmlToolTipBuilder ttb) {
        switch (constraintType) {
            case CHECK: ttb.append(true, "check constraint - " + (
                    checkCondition.length() > 120 ?
                            checkCondition.substring(0, 120) + "..." :
                            checkCondition), true); break;
            case PRIMARY_KEY: ttb.append(true, "primary key constraint", true); break;
            case FOREIGN_KEY: ttb.append(true, "foreign key constraint", true); break;
            case UNIQUE_KEY: ttb.append(true, "unique constraint", true); break;
        }

        ttb.createEmptyRow();
        super.buildToolTip(ttb);
    }

    @Override
    public List<PresentableProperty> getPresentableProperties() {
        List<PresentableProperty> properties = super.getPresentableProperties();
        switch (constraintType) {
            case CHECK:
                properties.add(0, new SimplePresentableProperty("Check condition", checkCondition));
                properties.add(0, new SimplePresentableProperty("Constraint type", "Check"));
                break;
            case PRIMARY_KEY: properties.add(0, new SimplePresentableProperty("Constraint type", "Primary Key")); break;
            case FOREIGN_KEY:
                DBConstraint foreignKeyConstraint = getForeignKeyConstraint();
                properties.add(0, new DBObjectPresentableProperty(foreignKeyConstraint));
                properties.add(0, new SimplePresentableProperty("Constraint type", "Foreign Key"));
                break;
            case UNIQUE_KEY: properties.add(0, new SimplePresentableProperty("Constraint type", "Unique")); break;
        }

        return properties;
    }

    protected List<DBObjectNavigationList> createNavigationLists() {
        List<DBObjectNavigationList> objectNavigationLists = new ArrayList<DBObjectNavigationList>();

        if (columns != null) {
            objectNavigationLists.add(new DBObjectNavigationListImpl<DBColumn>("Columns", columns.getObjects()));
        }

        DBConstraint foreignKeyConstraint = getForeignKeyConstraint();
        if (foreignKeyConstraint != null) {
            objectNavigationLists.add(new DBObjectNavigationListImpl<DBConstraint>("Foreign key constraint", foreignKeyConstraint));
        }

        return objectNavigationLists;
    }

    @Override
    public String getPresentableTextConditionalDetails() {
         switch (constraintType) {
            case CHECK: return "Check (" + checkCondition + ")";
            case PRIMARY_KEY: return "Primary key";
            case FOREIGN_KEY: return "Foreign key (" + foreignKeyConstraint.getPath() + ")";
            case UNIQUE_KEY: return "Unique";
        }
        return null;
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
    private static final DynamicContentLoader COLUMNS_LOADER = new DBObjectListFromRelationListLoader();

    @Override
    public DBOperationExecutor getOperationExecutor() {
        return new DBOperationExecutor() {
            public void executeOperation(DBOperationType operationType) throws SQLException, DBOperationNotSupportedException {
                Connection connection = getConnectionHandler().getStandaloneConnection(getSchema());
                DatabaseMetadataInterface metadataInterface = getConnectionHandler().getInterfaceProvider().getMetadataInterface();
                if (operationType == DBOperationType.ENABLE) {
                    metadataInterface.enableConstraint(
                            getSchema().getName(),
                            getDataset().getName(),
                            getName(),
                            connection);
                    getStatus().set(DBObjectStatus.ENABLED, true);
                } else if (operationType == DBOperationType.DISABLE) {
                    metadataInterface.disableConstraint(
                            getSchema().getName(),
                            getDataset().getName(),
                            getName(),
                            connection);
                    getStatus().set(DBObjectStatus.ENABLED, false);
                } else {
                    throw new DBOperationNotSupportedException(operationType, getObjectType());
                }
            }
        };
    }
}
