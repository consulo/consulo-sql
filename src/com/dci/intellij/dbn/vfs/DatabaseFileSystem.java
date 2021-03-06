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

package com.dci.intellij.dbn.vfs;

import com.dci.intellij.dbn.browser.DatabaseBrowserManager;
import com.dci.intellij.dbn.common.event.EventManager;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.ReadActionRunner;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.common.util.EditorUtil;
import com.dci.intellij.dbn.connection.ConnectionCache;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.ddl.DDLFileType;
import com.dci.intellij.dbn.editor.DBContentType;
import com.dci.intellij.dbn.editor.code.SourceCodeMainEditor;
import com.dci.intellij.dbn.language.common.DBLanguageFileType;
import com.dci.intellij.dbn.language.sql.SQLFileType;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.dci.intellij.dbn.object.common.list.DBObjectListContainer;
import com.dci.intellij.dbn.object.common.property.DBObjectProperty;
import com.dci.intellij.dbn.object.identifier.DBObjectIdentifier;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectLifecycleListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class DatabaseFileSystem extends VirtualFileSystem implements ApplicationComponent, FileEditorManagerListener{
    public static final String PROTOCOL = "db";
    public static final String PROTOCOL_PREFIX = PROTOCOL + "://";

    private static final String ERR = "File manipulation not allowed within database file system!";
    private Map<DBObjectIdentifier, DatabaseEditableObjectFile> openFiles = new HashMap<DBObjectIdentifier, DatabaseEditableObjectFile>();
    private Map<DBObjectIdentifier, DatabaseEditableObjectFile> filesCache = new HashMap<DBObjectIdentifier, DatabaseEditableObjectFile>();

    public static DatabaseFileSystem getInstance() {
        return ApplicationManager.getApplication().getComponent(DatabaseFileSystem.class);
    }
                                                                            
    @NotNull
    public String getProtocol() {
        return PROTOCOL;
    }

    @Nullable
    public VirtualFile findFileByPath(@NotNull @NonNls String url) {
        int startIndex = 0;
        if (url.startsWith(PROTOCOL_PREFIX)) {
            startIndex = PROTOCOL_PREFIX.length();
        }

        int index = url.indexOf("/", startIndex);

        String connectionId = url.substring(startIndex, index == -1 ? url.length() : index);
        ConnectionHandler connectionHandler = ConnectionCache.findConnectionHandler(connectionId);
        if (connectionHandler != null) {
            if (index > -1) {
                StringTokenizer path = new StringTokenizer(url.substring(index + 1), ".");
                DBObject object = connectionHandler.getObjectBundle().getSchema(path.nextToken());
                if (object != null) {
                    while (path.hasMoreElements() && object != null) {
                        String token = path.nextToken();
                        if (path.hasMoreTokens()) {
                            object = object.getChildObject(token, false);
                        }

                    }
                    // object may have been deleted by another party
                    if (object != null && object.getProperties().is(DBObjectProperty.EDITABLE)) {
                        return findDatabaseFile((DBSchemaObject) object);
                    }
                }
            } else {
                return connectionHandler.getSQLConsoleFile();
            }

        }
        return null;
    }

    private DatabaseEditableObjectFile createDatabaseFile(final DBSchemaObject object) {
        return new ReadActionRunner<DatabaseEditableObjectFile>() {
            @Override
            protected DatabaseEditableObjectFile run() {
                return new DatabaseEditableObjectFile(object);
            }
        }.start();
    }

    @NotNull
    public DatabaseEditableObjectFile findDatabaseFile(DBSchemaObject object) {
        DBObjectIdentifier identifier = object.getIdentifier();
        DatabaseEditableObjectFile databaseFile = filesCache.get(identifier);
        if (databaseFile == null ){
            databaseFile = createDatabaseFile(object);

            filesCache.put(identifier, databaseFile);
        }
        return databaseFile;
    }

    public boolean isFileOpened(DBSchemaObject object) {
        return openFiles.containsKey(object.getIdentifier());
    }

    public static String createPath(DBObject object, DBContentType contentType) {
        StringBuilder buffer = new StringBuilder(object.getName());
        DBObject parent = object.getParentObject();
        while (parent != null) {
            buffer.insert(0, ".");
            buffer.insert(0, parent.getName());
            parent = parent.getParentObject();
        }
        buffer.insert(0, " - ");
        if (contentType == DBContentType.CODE_SPEC) {
            buffer.insert(0, " SPEC");
        }

        if (contentType == DBContentType.CODE_BODY) {
            buffer.insert(0, " BODY");
        }

        buffer.insert(0, object.getTypeName().toUpperCase());
        buffer.insert(0, "] ");
        buffer.insert(0, object.getConnectionHandler().getName());
        buffer.insert(0, "[");

        return buffer.toString();
    }

    public static String createPath(DBObject object) {
        StringBuilder buffer = new StringBuilder(object.getName());
        DBObject parent = object.getParentObject();
        while (parent != null) {
            buffer.insert(0, ".");
            buffer.insert(0, parent.getName());
            parent = parent.getParentObject();
        }
        buffer.insert(0, " - ");
        buffer.insert(0, object.getTypeName().toUpperCase());
        buffer.insert(0, "] ");
        buffer.insert(0, object.getConnectionHandler().getName());
        buffer.insert(0, "[");

        return buffer.toString();
    }

    public static String createUrl(DBObject object) {
        StringBuilder buffer = new StringBuilder(object.getName());
        buffer.append(".");
        buffer.append(getDefaultExtension(object));


        DBObject parent = object.getParentObject();
        while (parent != null) {
            buffer.insert(0, ".");
            buffer.insert(0, parent.getName());
            if (parent instanceof DBSchema) break;
            parent = parent.getParentObject();
        }
        buffer.insert(0, "/");
        buffer.insert(0, object.getConnectionHandler().getId());
        buffer.insert(0, "://");
        buffer.insert(0, PROTOCOL);
        return buffer.toString();
    }

    public static String createPath(ConnectionHandler connectionHandler) {
        return "["+ connectionHandler.getName() + "]";

    }

    public static String createUrl(ConnectionHandler connectionHandler) {
        return PROTOCOL + "://" + connectionHandler.getId();
    }

    public static String getDefaultExtension(DBObject object) {
        if (object instanceof DBSchemaObject) {
            DBSchemaObject schemaObject = (DBSchemaObject) object;
            DDLFileType ddlFileType = schemaObject.getDDLFileType(null);
            DBLanguageFileType fileType = ddlFileType == null ? SQLFileType.INSTANCE : ddlFileType.getLanguageFileType();
            return fileType.getDefaultExtension();
        }
        return "";
    }

    /*********************************************************
     *                  VirtualFileSystem                    *
     *********************************************************/

    public void refresh(boolean b) {

    }

    @Nullable
    public VirtualFile refreshAndFindFileByPath(@NotNull String s) {
        return null;
    }

    public void addVirtualFileListener(@NotNull VirtualFileListener listener) {

    }

    public void removeVirtualFileListener(@NotNull VirtualFileListener listener) {

    }

    public void forceRefreshFiles(boolean b, @NotNull VirtualFile... virtualFiles) {

    }

    protected void deleteFile(Object o, @NotNull VirtualFile virtualFile) throws IOException {
        throw new UnsupportedOperationException(ERR);
    }

    protected void moveFile(Object o, @NotNull VirtualFile virtualFile, @NotNull VirtualFile virtualFile1) throws IOException {
        throw new UnsupportedOperationException(ERR);
    }

    protected void renameFile(Object o, @NotNull VirtualFile virtualFile, @NotNull String s) throws IOException {
        throw new UnsupportedOperationException(ERR);
    }

    protected VirtualFile createChildFile(Object o, @NotNull VirtualFile virtualFile, @NotNull String s) throws IOException {
        throw new UnsupportedOperationException(ERR);
    }

    protected VirtualFile createChildDirectory(Object o, @NotNull VirtualFile virtualFile, @NotNull String s) throws IOException {
        throw new UnsupportedOperationException(ERR);
    }

    protected VirtualFile copyFile(Object o, @NotNull VirtualFile virtualFile, @NotNull VirtualFile virtualFile1, @NotNull String s) throws IOException {
        throw new UnsupportedOperationException(ERR);
    }

    public boolean isReadOnly() {
        return false;
    }

    /*********************************************************
     *                ApplicationComponent                   *
     *********************************************************/

    @NonNls
    @NotNull
    public String getComponentName() {
        return "DBNavigator.DatabaseFileSystem";
    }

    public void initComponent() {
        EventManager.subscribe(ProjectLifecycleListener.TOPIC, projectLifecycleListener);
    }

    public void disposeComponent() {
        EventManager.unsubscribe(projectLifecycleListener);
    }

    /*********************************************************
     *              FileEditorManagerListener                *
     *********************************************************/
    public void openEditor(final DBObject object) {
        openEditor(object, false);
    }

    public void openEditor(final DBObject object, final boolean scroll) {
        final Project project = object.getProject();
        new BackgroundTask(project, "Opening editor", false, true) {
            @Override
            public void execute(@NotNull ProgressIndicator progressIndicator) {
                initProgressIndicator(progressIndicator, true);
                if (object.getProperties().is(DBObjectProperty.SCHEMA_OBJECT)) {
                    DBObjectListContainer childObjects = object.getChildObjects();
                    if (childObjects != null) childObjects.load();
                    openSchemaObject((DBSchemaObject) object, progressIndicator, scroll);

                } else if (object.getParentObject().getProperties().is(DBObjectProperty.SCHEMA_OBJECT)) {
                    DBObjectListContainer childObjects = object.getParentObject().getChildObjects();
                    if (childObjects != null) childObjects.load();
                    openChildObject(object, progressIndicator, scroll);
                }

            }
        }.start();
    }

    private void openSchemaObject(final DBSchemaObject object, ProgressIndicator progressIndicator, final boolean scroll) {
        final DatabaseEditableObjectFile databaseFile = findDatabaseFile(object);
        if (!progressIndicator.isCanceled()) {
            new SimpleLaterInvocator() {
                @Override
                public void run() {
                    if (isFileOpened(object) || databaseFile.preOpen()) {
                        DatabaseBrowserManager.AUTOSCROLL_FROM_EDITOR.set(scroll);
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(object.getProject());
                        fileEditorManager.openFile(databaseFile, true);
                        DatabaseBrowserManager.AUTOSCROLL_FROM_EDITOR.set(true);
                    }
                }
            }.start();
        }
    }

    private void openChildObject(final DBObject object, ProgressIndicator progressIndicator, final boolean scroll) {
        final DBSchemaObject schemaObject = (DBSchemaObject) object.getParentObject();
        final DatabaseEditableObjectFile databaseFile = findDatabaseFile(schemaObject);
        if (!progressIndicator.isCanceled()) {
            new SimpleLaterInvocator() {

                @Override
                public void run() {
                    if (isFileOpened(schemaObject) || databaseFile.preOpen()) {
                        DatabaseBrowserManager.AUTOSCROLL_FROM_EDITOR.set(scroll);
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(object.getProject());
                        FileEditor[] fileEditors = fileEditorManager.openFile(databaseFile, true);
                        for (FileEditor fileEditor : fileEditors) {
                            if (fileEditor instanceof SourceCodeMainEditor) {
                                SourceCodeMainEditor sourceCodeEditor = (SourceCodeMainEditor) fileEditor;
                                EditorUtil.selectEditor(databaseFile, fileEditor);
                                sourceCodeEditor.navigateTo(object);
                                break;
                            }
                        }
                        DatabaseBrowserManager.AUTOSCROLL_FROM_EDITOR.set(true);
                    }

                }
            }.start();
        }
    }

    public void closeEditor(DBSchemaObject object) {
        VirtualFile virtualFile = findDatabaseFile(object);
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(object.getProject());
        fileEditorManager.closeFile(virtualFile);
    }

    public void reopenEditor(DBSchemaObject object) {
        VirtualFile virtualFile = findDatabaseFile(object);
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(object.getProject());
        fileEditorManager.closeFile(virtualFile);
        fileEditorManager.openFile(virtualFile, true);
    }

    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file instanceof DatabaseEditableObjectFile) {
            DatabaseEditableObjectFile databaseFile = (DatabaseEditableObjectFile) file;
            openFiles.put(databaseFile.getObjectIdentifier(), databaseFile);
        }
    }

    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (file instanceof DatabaseEditableObjectFile) {
            DatabaseEditableObjectFile databaseFile = (DatabaseEditableObjectFile) file;
            openFiles.remove(databaseFile.getObjectIdentifier());
        }
    }

    public void selectionChanged(@NotNull FileEditorManagerEvent event) {

    }

    /*********************************************************
     *              ProjectLifecycleListener                 *
     *********************************************************/
    private ProjectLifecycleListener projectLifecycleListener = new ProjectLifecycleListener.Adapter() {
        @Override
        public void afterProjectClosed(@NotNull Project project) {
            Iterator<DBObjectIdentifier> identifiers = filesCache.keySet().iterator();
            while (identifiers.hasNext()) {
                DBObjectIdentifier identifier = identifiers.next();
                DatabaseEditableObjectFile file = filesCache.get(identifier);
                if (file.getProject() == project) {
                    identifiers.remove();
                    file.dispose();
                }
            }
        }
    };
}
