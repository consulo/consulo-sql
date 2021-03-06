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

package com.dci.intellij.dbn.execution.method.browser;

import com.dci.intellij.dbn.common.options.PersistentConfiguration;
import com.dci.intellij.dbn.connection.ConnectionCache;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.object.DBMethod;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.dci.intellij.dbn.object.identifier.DBMethodIdentifier;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jdom.Element;

import java.util.Map;
import java.util.Set;

public class MethodBrowserSettings implements PersistentConfiguration {
    private String connectionId;
    private String schemaName;
    private DBMethodIdentifier methodIdentifier;
    private Map<DBObjectType, Boolean> objectVisibility = new THashMap<DBObjectType, Boolean>();

    public MethodBrowserSettings() {
        objectVisibility.put(DBObjectType.FUNCTION, true);
        objectVisibility.put(DBObjectType.PROCEDURE, true);
    }

    public ConnectionHandler getConnectionHandler() {
        return ConnectionCache.findConnectionHandler(connectionId);
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionId = connectionHandler == null ? null : connectionHandler.getId();
    }

    public DBSchema getSchema() {
        return getConnectionHandler() == null || schemaName == null ? null : getConnectionHandler().getObjectBundle().getSchema(schemaName);
    }

    public Set<DBObjectType> getVisibleObjectTypes() {
        Set<DBObjectType> objectTypes = new THashSet<DBObjectType>();
        for (DBObjectType objectType : objectVisibility.keySet()) {
            if (objectVisibility.get(objectType)) {
                objectTypes.add(objectType);
            }
        }
        return objectTypes;
    }

    public boolean getObjectVisibility(DBObjectType objectType) {
        return objectVisibility.get(objectType);
    }

    public boolean setObjectVisibility(DBObjectType objectType, boolean visibility) {
        if (getObjectVisibility(objectType) != visibility) {
            objectVisibility.put(objectType, visibility);
            return true;
        }
        return false;        
    }

    public void setSchema(DBSchema schema) {
        this.schemaName = schema == null ? null : schema.getName();
    }

    public DBMethod getMethod() {
        return methodIdentifier == null ? null : methodIdentifier.lookupObject();
    }

    public void setMethod(DBMethod method) {
        methodIdentifier = method.getIdentifier();
    }

    public void readConfiguration(Element element) throws InvalidDataException {
        connectionId = element.getAttributeValue("connection-id");
        schemaName = element.getAttributeValue("schema");

        Element methodElement = element.getChild("selected-method");
        if (methodElement != null) {
            methodIdentifier = new DBMethodIdentifier();
            methodIdentifier.readConfiguration(methodElement);
        }
    }

    public void writeConfiguration(Element element) throws WriteExternalException {
        ConnectionHandler connectionHandler = getConnectionHandler();
        if (connectionHandler != null) element.setAttribute("connection-id", connectionHandler.getId());
        if (schemaName != null) element.setAttribute("schema", schemaName);
        if(methodIdentifier != null) {
            Element methodElement = new Element("selected-method");
            methodIdentifier.writeConfiguration(methodElement);
            element.addContent(methodElement);
        }
    }
}
