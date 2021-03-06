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

import com.dci.intellij.dbn.browser.DatabaseBrowserUtils;
import com.dci.intellij.dbn.browser.model.BrowserTreeNode;
import com.dci.intellij.dbn.ddl.DDLFileManager;
import com.dci.intellij.dbn.ddl.DDLFileType;
import com.dci.intellij.dbn.ddl.DDLFileTypeId;
import com.dci.intellij.dbn.editor.DBContentType;
import com.dci.intellij.dbn.language.common.SqlLikeLanguage;
import com.dci.intellij.dbn.language.sql.SQLLanguage;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.DBType;
import com.dci.intellij.dbn.object.DBView;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBObjectBundle;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.dci.intellij.dbn.object.common.list.DBObjectNavigationList;
import com.dci.intellij.dbn.object.common.list.DBObjectNavigationListImpl;
import com.dci.intellij.dbn.object.common.loader.DBSourceCodeLoader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBViewImpl extends DBDatasetImpl implements DBView {
    private boolean isSystemView;
    private DBType type;
    public DBViewImpl(DBSchema schema, ResultSet resultSet) throws SQLException {
        super(schema, DBContentType.CODE_AND_DATA, resultSet);
    }

    @Override
    protected void initObject(ResultSet resultSet) throws SQLException {
        name = resultSet.getString("VIEW_NAME");
        isSystemView = resultSet.getString("IS_SYSTEM_VIEW").equals("Y");
        if (isSystemView) setContentType(DBContentType.DATA);
        String typeOwner = resultSet.getString("VIEW_TYPE_OWNER");
        String typeName = resultSet.getString("VIEW_TYPE");
        if (typeOwner != null && typeName != null) {
            DBObjectBundle objectBundle = getConnectionHandler().getObjectBundle();
            DBSchema typeSchema = objectBundle.getSchema(typeOwner);
            type = typeSchema.getType(typeName);
        }
    }

    public DBObjectType getObjectType() {
        return DBObjectType.VIEW;
    }

    public DBType getType() {
        return type;
    }

    /*********************************************************
     *                     TreeElement                       *
     *********************************************************/
    @Override
    public void reload() {
        columns.reload(true);
        constraints.reload(true);
        triggers.reload(true);
    }

    @NotNull
    public List<BrowserTreeNode> buildAllPossibleTreeChildren() {
        return DatabaseBrowserUtils.createList(
                columns,
                constraints,
                triggers);
    }

    @Override
    public boolean isEditable(DBContentType contentType) {
        return contentType == DBContentType.CODE;
    }

    protected List<DBObjectNavigationList> createNavigationLists() {
        List<DBObjectNavigationList> objectNavigationLists = new ArrayList<DBObjectNavigationList>();

        if (type != null) {
            objectNavigationLists.add(new DBObjectNavigationListImpl<DBType>("Type", type));
        }
        return objectNavigationLists;
    }

    /*********************************************************
     *                         Loaders                       *
     *********************************************************/

    private class SourceCodeLoader extends DBSourceCodeLoader {
        protected SourceCodeLoader(DBObject object) {
            super(object, false);
        }

        public ResultSet loadSourceCode(Connection connection) throws SQLException {
            return getConnectionHandler().getInterfaceProvider().getMetadataInterface().loadViewSourceCode(
                   getSchema().getName(), getName(), connection);
        }
    };

    /*********************************************************
     *                  DBEditableCodeObject                 *
     ********************************************************/

    public String loadCodeFromDatabase(DBContentType contentType) throws SQLException {
        SourceCodeLoader loader = new SourceCodeLoader(this);
        return loader.load();
    }

    public void executeUpdateDDL(DBContentType contentType, String oldCode, String newCode) throws SQLException {
        Connection connection = getConnectionHandler().getStandaloneConnection(getSchema());
        getConnectionHandler().getInterfaceProvider().getDDLInterface().
                updateView(getName(), oldCode, newCode, connection);
    }

    public String getCodeParseRootId(DBContentType contentType) {
        return "subquery";
    }

    public SqlLikeLanguage getCodeLanguage(DBContentType contentType) {
        return SQLLanguage.INSTANCE;
    }

    /*********************************************************
     *                    DBEditableObject                   *
     ********************************************************/
    public DDLFileType getDDLFileType(DBContentType contentType) {
        return DDLFileManager.getInstance(getProject()).getDDLFileType(DDLFileTypeId.VIEW);
    }

    @Override
    public DDLFileType[] getDDLFileTypes() {
        return new DDLFileType[]{getDDLFileType(null)};
    }
}
