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

package com.dci.intellij.dbn.object.filter.name;

import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBObjectType;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;

import java.util.StringTokenizer;

public class SimpleFilterCondition implements FilterCondition {
    private CompoundFilterCondition parent;
    private ConditionOperator operator;
    private String text;

    public SimpleFilterCondition() {
    }


    public SimpleFilterCondition(ConditionOperator operator, String text) {
        this.operator = operator;
        this.text = text;
    }

    public ObjectNameFilterSettings getSettings() {
        return parent.getSettings();
    }

    public boolean accepts(DBObject object) {
        String name = object.getName();
        switch (operator) {
            case EQUAL: return isEqual(name);
            case NOT_EQUAL: return !isEqual(name);
            case LIKE: return isLike(name);
            case NOT_LIKE: return !isLike(name);
        }
        return false;
    }

    private boolean isEqual(String name) {
        return name.equalsIgnoreCase(text);
    }

    private boolean isLike(String name) {
        StringTokenizer tokenizer = new StringTokenizer(text, "*%");
        int startIndex = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int index = StringUtil.indexOfIgnoreCase(name, token, startIndex);
            if (index == -1 || (index > 0 && startIndex == 0 && !startsWithWildcard())) return false;
            startIndex = index + token.length();
        }

        return true;
    }

    private boolean startsWithWildcard() {
        return text.indexOf('*') == 0 || text.indexOf('%') == 0;
    }



    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setParent(CompoundFilterCondition parent) {
        this.parent = parent;
    }

    public CompoundFilterCondition getParent() {
        return parent;
    }

    public DBObjectType getObjectType() {
        return parent.getObjectType();
    }

    public String getConditionString() {
        return "OBJECT_NAME " + operator + " '" + text + "'";
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return getObjectType().getName().toUpperCase() + "_NAME " + operator + " '" + text + "'";
    }

    /*********************************************************
     *                     Configuration                     *
     *********************************************************/
    public void readConfiguration(Element element) throws InvalidDataException {
        operator = ConditionOperator.valueOf(element.getAttributeValue("operator"));
        text = element.getAttributeValue("text");
    }

    public void writeConfiguration(Element element) throws WriteExternalException {
        element.setAttribute("operator", operator.name());
        element.setAttribute("text", text);
    }
}
