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

package com.dci.intellij.dbn.code.common.completion.options.filter;

import com.dci.intellij.dbn.code.common.completion.options.filter.ui.CheckedTreeNodeProvider;
import com.dci.intellij.dbn.code.common.completion.options.filter.ui.CodeCompletionFilterSettingsForm;
import com.dci.intellij.dbn.code.common.completion.options.filter.ui.CodeCompletionFilterTreeNode;
import com.dci.intellij.dbn.common.options.Configuration;
import com.dci.intellij.dbn.language.common.TokenTypeIdentifier;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.dci.intellij.dbn.object.common.ObjectTypeFilter;
import com.intellij.ui.CheckedTreeNode;
import org.jdom.Element;

import java.util.Set;

public class CodeCompletionFilterSettings extends Configuration<CodeCompletionFilterSettingsForm> implements CheckedTreeNodeProvider, ObjectTypeFilter {
    public static final int SCHEMA_TYPE_USER = 0;
    public static final int SCHEMA_TYPE_PUBLIC = 1;
    public static final int SCHEMA_TYPE_ANY = 2;

    private boolean extended;
    private CodeCompletionFilterOptionBundle rootFilterOptions;
    private CodeCompletionFilterOptionBundle userSchemaOptions;
    private CodeCompletionFilterOptionBundle publicSchemaOptions;
    private CodeCompletionFilterOptionBundle anySchemaOptions;

    public CodeCompletionFilterSettings(boolean extended) {
        this.extended = extended;
        rootFilterOptions = new CodeCompletionFilterOptionBundle("Root elements", this);
        userSchemaOptions = new CodeCompletionFilterOptionBundle("User schema", this);
        publicSchemaOptions = new CodeCompletionFilterOptionBundle("Public schema", this);
        anySchemaOptions = new CodeCompletionFilterOptionBundle("Any schema", this);
    }

    public boolean isExtended() {
        return extended;
    }

    public String getDisplayName() {
        return extended ? "Extended code completion" : "Basic code completion";
    }

    public CodeCompletionFilterOptionBundle getUserSchemaOptions() {
        return userSchemaOptions;
    }

    public CodeCompletionFilterOptionBundle getPublicSchemaOptions() {
        return publicSchemaOptions;
    }

    public CodeCompletionFilterOptionBundle getAnySchemaOptions() {
        return anySchemaOptions;
    }

    public boolean acceptReservedWord(TokenTypeIdentifier tokenTypeIdentifier) {
        if (tokenTypeIdentifier != TokenTypeIdentifier.UNKNOWN) {
            for(CodeCompletionFilterOption option : rootFilterOptions.getOptions()) {
                if (option.getObjectType() == null && option.getTokenTypeIdentifier() == tokenTypeIdentifier) {
                    return option.isSelected();
                }
            }
        }
        return false;
    }

    public boolean acceptsRootObject(DBObjectType objectType) {
        Set<DBObjectType> objectTypes = objectType.isGeneric() ? objectType.getInheritingTypes() : null;
        for(CodeCompletionFilterOption option : rootFilterOptions.getOptions()) {
            if (objectTypes != null) {
                for (DBObjectType type : objectTypes) {
                    if (option.getObjectType() == type) {
                        return option.isSelected();
                    }
                }
            }
            else if (option.getObjectType() == objectType) {
                return option.isSelected();
            }
        }
        return true;   // return true for object types which are not configured
    }

    public boolean acceptsCurrentSchemaObject(DBObjectType objectType) {
        return showSchemaObject(SCHEMA_TYPE_USER, objectType);
    }

    public boolean acceptsPublicSchemaObject(DBObjectType objectType) {
        return showSchemaObject(SCHEMA_TYPE_PUBLIC, objectType);
    }

    public boolean acceptsAnySchemaObject(DBObjectType objcetType) {
        return showSchemaObject(SCHEMA_TYPE_ANY, objcetType);
    }

    public boolean acceptsObject(DBSchema schema, DBSchema currentSchema, DBObjectType objectType) {
        boolean isPublic = schema.isPublicSchema();
        boolean isCurrent = schema == currentSchema;
        return
            (isPublic && acceptsPublicSchemaObject(objectType)) ||
            (isCurrent && acceptsCurrentSchemaObject(objectType)) ||
            (!isPublic && !isCurrent && acceptsAnySchemaObject(objectType));
    }

    private boolean showSchemaObject(int schemaType, DBObjectType objectType) {
        Set<DBObjectType> objectTypes = objectType.isGeneric() ? objectType.getInheritingTypes() : null;
        CodeCompletionFilterOptionBundle schemaOptions;
        switch (schemaType) {
            case SCHEMA_TYPE_USER: schemaOptions = userSchemaOptions; break;
            case SCHEMA_TYPE_PUBLIC: schemaOptions = publicSchemaOptions; break;
            default: schemaOptions = anySchemaOptions; break;
        }

        for(CodeCompletionFilterOption option : schemaOptions.getOptions()) {
            if (objectTypes != null) {
                for (DBObjectType type : objectTypes) {
                    if (option.getObjectType() == type) {
                        return option.isSelected();
                    }
                }
            }
            else if (option.getObjectType() == objectType) {
                return option.isSelected();
            }
        }
        return false;
    }


    /*********************************************************
     *                     Configuration                     *
     *********************************************************/
    protected CodeCompletionFilterSettingsForm createConfigurationEditor() {
        return new CodeCompletionFilterSettingsForm(this);
    }

    @Override
    public String getConfigElementName() {
        return extended ? "extended-filter" : "basic-filter";
    }

    public void readConfiguration(Element element) {
        rootFilterOptions.readExternal(element);

        Element userSchemaElement = element.getChild("user-schema");
        userSchemaOptions.readExternal(userSchemaElement);

        Element publicSchemaElement = element.getChild("public-schema");
        publicSchemaOptions.readExternal(publicSchemaElement);

        Element anySchemaElement = element.getChild("any-schema");
        anySchemaOptions.readExternal(anySchemaElement);
    }

    public void writeConfiguration(Element element) {
        rootFilterOptions.writeExternal(element);

        Element userSchemaElement = new Element("user-schema");
        userSchemaOptions.writeExternal(userSchemaElement);
        element.addContent(userSchemaElement);

        Element publicSchemaElement = new Element("public-schema");
        publicSchemaOptions.writeExternal(publicSchemaElement);
        element.addContent(publicSchemaElement);

        Element anySchemaElement = new Element("any-schema");
        anySchemaOptions.writeExternal(anySchemaElement);
        element.addContent(anySchemaElement);
    }

    /*********************************************************
     *              CheckedTreeNodeProvider                  *
     *********************************************************/
    public CheckedTreeNode createCheckedTreeNode() {
        CodeCompletionFilterTreeNode rootNode = new CodeCompletionFilterTreeNode(this, false);
        for (CodeCompletionFilterOption option: rootFilterOptions.getOptions()) {
            rootNode.add(option.createCheckedTreeNode());
        }
        rootNode.add(getUserSchemaOptions().createCheckedTreeNode());
        rootNode.add(getPublicSchemaOptions().createCheckedTreeNode());
        rootNode.add(getAnySchemaOptions().createCheckedTreeNode());
        rootNode.updateCheckedStatusFromChildren();
        return rootNode;
    }


}

