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

package com.dci.intellij.dbn.object.identifier;

import com.dci.intellij.dbn.common.options.PersistentConfiguration;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.object.DBMethod;
import com.dci.intellij.dbn.object.DBProgram;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

public class DBMethodIdentifier<T extends DBMethod> extends DBObjectIdentifier<DBMethod> implements PersistentConfiguration {
    private int overload;

    public DBMethodIdentifier(T method) {
        super(method);
        overload = method.getOverload();
    }

    public DBMethodIdentifier() {
        super();
    }

    public T lookupObject() {
        ConnectionHandler connectionHandler = lookupConnectionHandler();
        if (connectionHandler == null) return null;

        DBSchema schema = connectionHandler.getObjectBundle().getSchema(nodes[0].getName());
        if (schema == null) return null;

        DBMethod method;
        Node programNode = getProgramNode();
        Node methodNode = getMethodNode();
        DBObjectType methodObjectType = methodNode.getType();
        if (programNode != null) {
            DBProgram program = schema.getProgram(programNode.getName());
            if (program == null || program.getObjectType() != programNode.getType()) return null;

            method = program.getMethod(methodNode.getName(), overload);
        } else {
            method = schema.getMethod(methodNode.getName(), methodObjectType.getName());
        }

        return method != null && method.getObjectType() == methodObjectType ? (T) method : null;
    }

    public String getQualifiedMethodName() {
        String programName = getProgramName();
        String methodName = getMethodName();
        return programName == null ? methodName : programName + "." + methodName;
    }

    public String getSchemaName() {
        return nodes[0].getName();
    }

    public String getProgramName() {
        Node programNode = getProgramNode();
        return programNode == null ? null : programNode.getName();
    }

    public DBObjectType getProgramObjectType() {
        Node programNode = getProgramNode();
        return programNode == null ? null : programNode.getType();
    }

    public String getMethodName() {
        return getMethodNode().getName();
    }

    public DBObjectType getMethodObjectType() {
        return getMethodNode().getType();
    }

    public int getOverload() {
        return overload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DBMethodIdentifier that = (DBMethodIdentifier) o;

        return overload == that.overload;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + overload;
        return result;
    }

    @Override
    public String toString() {
        return getMethodNode().getType().getName() + " " + getPath();
    }

    /*********************************************************
     *                   JDOMExternalizable                  *
     *********************************************************/
    public void readConfiguration(Element element) throws InvalidDataException {
        connectionId = element.getAttributeValue("connection-id");
        String schemaName = element.getAttributeValue("schema-name");
        add(DBObjectType.SCHEMA, schemaName);

        String programTypeName = element.getAttributeValue("program-type");
        if (programTypeName != null) {
            String programName = element.getAttributeValue("program-name");
            DBObjectType programObjectType = DBObjectType.getObjectType(programTypeName);
            add(programObjectType, programName);
        }

        String methodName = element.getAttributeValue("method-name");
        DBObjectType methodObjectType = DBObjectType.getObjectType(element.getAttributeValue("method-type"));

        add(methodObjectType, methodName);

        String overload = element.getAttributeValue("method-overload");
        this.overload = Integer.parseInt(overload == null ? "0" : overload);
    }

    public void writeConfiguration(Element element) throws WriteExternalException {
        element.setAttribute("connection-id", connectionId);
        element.setAttribute("schema-name", nodes[0].getName());

        Node programNode = getProgramNode();
        if (programNode != null) {
            element.setAttribute("program-type", programNode.getType().getName());
            element.setAttribute("program-name", programNode.getName());
        }

        Node methodNode = getMethodNode();
        element.setAttribute("method-type", methodNode.getType().getName());
        element.setAttribute("method-name", methodNode.getName());

        element.setAttribute("method-overload", Integer.toString(overload));
    }

    private Node getProgramNode() {
        return nodes.length == 3 ? nodes[1] : null;
    }

    private Node getMethodNode() {
        return nodes.length == 3 ? nodes[2] : nodes[1];
    }
}
