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

package com.dci.intellij.dbn.object.common;

import com.dci.intellij.dbn.ddl.DDLFileType;
import com.dci.intellij.dbn.editor.DBContentType;
import com.dci.intellij.dbn.language.common.SqlLikeLanguage;
import com.dci.intellij.dbn.object.common.loader.DBObjectTimestampLoader;
import com.dci.intellij.dbn.object.common.status.DBObjectStatusHolder;
import com.dci.intellij.dbn.vfs.DatabaseEditableObjectFile;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface DBSchemaObject extends DBObject {
    List<DBObject> getReferencedObjects();
    List<DBObject> getReferencingObjects();
    DBContentType getContentType();
    boolean isEditable(DBContentType contentType);

    Timestamp loadChangeTimestamp(DBContentType contentType) throws SQLException;
    DBObjectTimestampLoader getTimestampLoader(DBContentType contentType);

    String loadCodeFromDatabase(DBContentType contentType) throws SQLException;
    SqlLikeLanguage getCodeLanguage(DBContentType contentType);
    String getCodeParseRootId(DBContentType contentType);

    void executeUpdateDDL(DBContentType contentType, String oldCode, String newCode) throws SQLException;
    String createDDLStatement(String code);
    DDLFileType getDDLFileType(DBContentType contentType);
    DDLFileType[] getDDLFileTypes();

    DBObjectStatusHolder getStatus();

    @NotNull
    DatabaseEditableObjectFile getVirtualFile();
}
