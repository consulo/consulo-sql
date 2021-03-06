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

package com.dci.intellij.dbn.connection.mapping;

import com.dci.intellij.dbn.common.util.DocumentUtil;
import com.dci.intellij.dbn.common.util.EditorUtil;
import com.dci.intellij.dbn.common.util.StringUtil;
import com.dci.intellij.dbn.common.util.VirtualFileUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.connection.ConnectionManager;
import com.dci.intellij.dbn.connection.ProjectConnectionBundle;
import com.dci.intellij.dbn.ddl.DDLFileAttachmentManager;
import com.dci.intellij.dbn.language.editor.ui.DBLanguageFileEditorToolbarForm;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.dci.intellij.dbn.vfs.DatabaseContentFile;
import com.dci.intellij.dbn.vfs.DatabaseEditableObjectFile;
import com.dci.intellij.dbn.vfs.SQLConsoleFile;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import gnu.trove.THashSet;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileConnectionMappingManager extends VirtualFileAdapter implements ProjectComponent, JDOMExternalizable {
    private Project project;
    private Set<FileConnectionMapping> mappings = new THashSet<FileConnectionMapping>();
    private Key<ConnectionHandler> ACTIVE_CONNECTION_KEY = Key.create("ACTIVE_CONNECTION");
    private Key<DBSchema> CURRENT_SCHEMA_KEY = Key.create("CURRENT_SCHEMA");

    public boolean setActiveConnection(VirtualFile virtualFile, ConnectionHandler connectionHandler) {
        if (VirtualFileUtil.isLocalFileSystem(virtualFile)) {
            virtualFile.putUserData(ACTIVE_CONNECTION_KEY, connectionHandler);

            String connectionId = connectionHandler == null ? null : connectionHandler.getId();
            String currentSchema = connectionHandler == null ? null  : connectionHandler.getUserName().toUpperCase();

            FileConnectionMapping mapping = lookupMapping(virtualFile);
            if (mapping == null) {
                mapping = new FileConnectionMapping(virtualFile.getUrl(), connectionId, currentSchema);
                mappings.add(mapping);
                return true;
            } else {
                if (mapping.getConnectionId() == null || !mapping.getConnectionId().equals(connectionId)) {
                    mapping.setConnectionId(connectionId);
                    if (connectionHandler != null) {
                        // overwrite current schema only if the existing
                        // selection is not a valid schema for the given connection
                        DBSchema schema = connectionHandler.isVirtual() ? null : connectionHandler.getObjectBundle().getSchema(mapping.getCurrentSchema());
                        setCurrentSchema(virtualFile, schema);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean setCurrentSchema(VirtualFile virtualFile, DBSchema schema) {
        if (VirtualFileUtil.isLocalFileSystem(virtualFile) || VirtualFileUtil.isVirtualFileSystem(virtualFile)) {
            virtualFile.putUserData(CURRENT_SCHEMA_KEY, schema);
            FileConnectionMapping mapping = lookupMapping(virtualFile);
            if (schema != null && mapping != null && !schema.getName().equals(mapping.getCurrentSchema())) {
                mapping.setCurrentSchema(schema.getName());
                return true;
            }
        }

        if (virtualFile instanceof SQLConsoleFile) {
            SQLConsoleFile sqlConsoleFile = (SQLConsoleFile) virtualFile;
            sqlConsoleFile.setCurrentSchema(schema);
            return true;
        }
        return false;
    }

    @Nullable
    public ConnectionHandler getActiveConnection(VirtualFile virtualFile) {
        // if the file is a database content file then get the connection from the underlying database object
        if (VirtualFileUtil.isDatabaseFileSystem(virtualFile)) {
            if (virtualFile instanceof DatabaseContentFile) {
                DatabaseContentFile contentFile = (DatabaseContentFile) virtualFile;
                return contentFile.getActiveConnection();
            }
            if (virtualFile instanceof DatabaseEditableObjectFile) {
                DatabaseEditableObjectFile databaseFile = (DatabaseEditableObjectFile) virtualFile;
                return databaseFile.getActiveConnection();
            }

            if (virtualFile instanceof SQLConsoleFile) {
                SQLConsoleFile sqlConsoleFile = (SQLConsoleFile) virtualFile;
                return sqlConsoleFile.getConnectionHandler();
            }
        }

        if (VirtualFileUtil.isLocalFileSystem(virtualFile)) {
            // if the file is a bound ddl file, then resolve the object which it is
            // linked to, and return its database connection
            DBSchemaObject object = DDLFileAttachmentManager.getInstance(project).getEditableObject(virtualFile);
            if (object != null) {
                return object.getConnectionHandler();
            }

            // lookup connection mappings
            ConnectionHandler connectionHandler = virtualFile.getUserData(ACTIVE_CONNECTION_KEY);
            if (connectionHandler == null) {
                FileConnectionMapping mapping = lookupMapping(virtualFile);
                if (mapping != null) {
                    ConnectionManager connectionManager = ConnectionManager.getInstance(project);
                    connectionHandler = connectionManager.getConnectionHandler(mapping.getConnectionId());
                    if (connectionHandler == null) connectionHandler =
                            ProjectConnectionBundle.getInstance(project).getVirtualConnection(mapping.getConnectionId());

                    if (connectionHandler != null)
                        virtualFile.putUserData(ACTIVE_CONNECTION_KEY, connectionHandler);
                }
            }
            return connectionHandler;
        }

        return null;
    }

    public DBSchema getCurrentSchema(VirtualFile virtualFile) {
        // if the file is a database content file then get the schema from the underlying schema object
        if (VirtualFileUtil.isDatabaseFileSystem(virtualFile)) {
            if (virtualFile instanceof DatabaseContentFile) {
                DatabaseContentFile contentFile = (DatabaseContentFile) virtualFile;
                return contentFile.getCurrentSchema();
            }

            if (virtualFile instanceof SQLConsoleFile) {
                SQLConsoleFile sqlConsoleFile = (SQLConsoleFile) virtualFile;
                return sqlConsoleFile.getCurrentSchema();
            }
        }

        if (VirtualFileUtil.isLocalFileSystem(virtualFile) || VirtualFileUtil.isVirtualFileSystem(virtualFile)) {
            // if the file is a bound ddl file, then resolve the object which it is
            // linked to, and return its parent schema
            DBSchemaObject object = DDLFileAttachmentManager.getInstance(project).getEditableObject(virtualFile);
            if (object != null) {
                return object.getSchema();
            }

            // lookup schema mappings
            DBSchema currentSchema = virtualFile.getUserData(CURRENT_SCHEMA_KEY);
            if (currentSchema == null) {
                ConnectionHandler connectionHandler = getActiveConnection(virtualFile);
                if (connectionHandler != null && !connectionHandler.isVirtual()) {
                    FileConnectionMapping mapping = lookupMapping(virtualFile);
                    if (mapping != null) {
                        String schemaName = mapping.getCurrentSchema();
                        if (StringUtil.isEmptyOrSpaces(schemaName)) {
                            currentSchema = connectionHandler.getUserSchema();
                            schemaName = currentSchema == null ? null : currentSchema.getName();
                        } else {
                            currentSchema = connectionHandler.getObjectBundle().getSchema(schemaName);
                        }
                        mapping.setCurrentSchema(schemaName);
                        virtualFile.putUserData(CURRENT_SCHEMA_KEY, currentSchema);
                    }
                }
            }
            return currentSchema;
        }
        return null;
    }


    private FileConnectionMapping lookupMapping(VirtualFile virtualFile) {
        String fileUrl = virtualFile.getUrl();
        return lookupMapping(fileUrl);
    }

    private FileConnectionMapping lookupMapping(String fileUrl) {
        for (FileConnectionMapping mapping : mappings) {
            if (fileUrl.equals(mapping.getFileUrl())) {
                return mapping;
            }
        }
        return null;
    }

    public void removeMapping(VirtualFile virtualFile) {
        FileConnectionMapping mapping = lookupMapping(virtualFile);
        if (mapping != null) {
            mappings.remove(mapping);
        }
    }

    public List<VirtualFile> getMappedFiles(ConnectionHandler connectionHandler) {
        List<VirtualFile> list = new ArrayList<VirtualFile>();

        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        for (FileConnectionMapping mapping : mappings) {
            String connectionId = mapping.getConnectionId();
            if (connectionHandler.getId().equals(connectionId)) {
                VirtualFile file = virtualFileManager.findFileByUrl(mapping.getFileUrl());
                if (file != null) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**********************************************
     *            Contextual utilities            *
     **********************************************/
    public ConnectionHandler lookupActiveConnectionForEditor(String actionPlace) {
        VirtualFile virtualFile = EditorUtil.getVirtualFile(project, actionPlace);
        return virtualFile == null ? null : getActiveConnection(virtualFile);
    }

    public DBSchema lookupCurrentSchemaForEditor(String actionPlace) {
        VirtualFile virtualFile = EditorUtil.getVirtualFile(project, actionPlace);
        return virtualFile == null ? null : getCurrentSchema(virtualFile);
    }

    public void selectActiveConnectionForEditor(String actionPlace, @Nullable ConnectionHandler connectionHandler) {

        Editor editor = EditorUtil.getSelectedEditor(project);
        if (editor!= null) {
            Document document = editor.getDocument();
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
            if (VirtualFileUtil.isLocalFileSystem(virtualFile) ) {
                boolean changed = setActiveConnection(virtualFile, connectionHandler);
                if (changed) {
                    DocumentUtil.touchDocument(editor);

                    FileEditor fileEditor = FileEditorManager.getInstance(project).getSelectedEditor(virtualFile);
                    DBLanguageFileEditorToolbarForm toolbarForm = fileEditor.getUserData(DBLanguageFileEditorToolbarForm.USER_DATA_KEY);
                    if (toolbarForm != null) {
                        toolbarForm.getAutoCommitLabel().setConnectionHandler(connectionHandler);
                    }
                }
            }
        }
    }

    public void setCurrentSchemaForSelectedEditor(String actionPlace, DBSchema schema) {
        Editor editor = EditorUtil.getSelectedEditor(project);
        if (editor!= null) {
            Document document = editor.getDocument();
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
            if (VirtualFileUtil.isLocalFileSystem(virtualFile) || virtualFile instanceof SQLConsoleFile) {
                boolean changed = setCurrentSchema(virtualFile, schema);
                if (changed) {
                    DocumentUtil.touchDocument(editor);
                }
            }
        }
    }

    /***************************************
     *         VirtualFileListener         *
     ***************************************/

    @Override
    public void fileDeleted(VirtualFileEvent event) {
        removeMapping(event.getFile());
    }

    public void fileMoved(VirtualFileMoveEvent event) {
        String oldFileUrl = event.getOldParent().getUrl() + "/" + event.getFileName();
        FileConnectionMapping fileConnectionMapping = lookupMapping(oldFileUrl);
        if (fileConnectionMapping != null) {
            fileConnectionMapping.setFileUrl(event.getFile().getUrl());
        }
    }

    public void propertyChanged(VirtualFilePropertyEvent event) {
        VirtualFile file = event.getFile();
        VirtualFile parent = file.getParent();
        if (file.isInLocalFileSystem() && parent != null) {
            String oldFileUrl = parent.getUrl() + "/" + event.getOldValue();
            FileConnectionMapping fileConnectionMapping = lookupMapping(oldFileUrl);
            if (fileConnectionMapping != null) {
                fileConnectionMapping.setFileUrl(file.getUrl());
            }
        }
    }

    /***************************************
     *          ProjectComponent         *
     ***************************************/
    private FileConnectionMappingManager(Project project) {
        this.project = project;
        VirtualFileManager.getInstance().addVirtualFileListener(this);
    }

    public static FileConnectionMappingManager getInstance(Project project) {
        return project.getComponent(FileConnectionMappingManager.class);
    }

    @NotNull
    public String getComponentName() {
        return "DBNavigator.Project.FileConnectionMappingManager";
    }

    public void projectOpened() {}
    public void projectClosed() {}
    public void initComponent() {}
    public void disposeComponent() {
        mappings.clear();
        project = null;
    }

    /***************************************
     *          JDOMExternalizable         *
     ***************************************/
    public void readExternal(Element element) throws InvalidDataException {
        if (element != null) {
            for (Object child : element.getChildren()) {
                Element mappingElement = (Element) child;
                FileConnectionMapping mapping = new FileConnectionMapping(mappingElement);
                mappings.add(mapping);
            }
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        for (FileConnectionMapping mapping : mappings) {
            Element mappingElement = new Element("mapping");
            mapping.writeConfiguration(mappingElement);
            element.addContent(mappingElement);
        }
    }
}

