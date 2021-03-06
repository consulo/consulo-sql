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

package com.dci.intellij.dbn.common.options.setting;

import com.dci.intellij.dbn.common.ui.DBNColor;
import com.dci.intellij.dbn.common.util.StringUtil;
import org.jdom.Element;

import java.awt.Color;

public class SettingsUtil {
    public static boolean isDebugEnabled;

    public static int getInteger(Element parent, String childName, int originalValue) {
        Element element = parent.getChild(childName);
        String stringValue = getStringValue(element);
        return stringValue == null ? originalValue : Integer.parseInt(stringValue);
    }

    public static String getString(Element parent, String childName, String originalValue) {
        Element element = parent.getChild(childName);
        String stringValue = getStringValue(element);
        return stringValue == null ? originalValue : stringValue;
    }

    public static boolean getBoolean(Element parent, String childName, boolean originalValue) {
        Element element = parent.getChild(childName);
        String stringValue = getStringValue(element);
        return stringValue == null ? originalValue : Boolean.parseBoolean(stringValue);
    }

    public static <T extends Enum> T getEnum(Element parent, String childName, T originalValue) {
        Element element = parent.getChild(childName);
        String stringValue = getStringValue(element);
        return stringValue == null ? originalValue : (T) T.valueOf(originalValue.getClass(), stringValue);
    }

    private static String getStringValue(Element element) {
        if (element != null) {
            String value = element.getAttributeValue("value");
            if (StringUtil.isNotEmptyOrSpaces(value)) {
                return value;
            }
        }
        return null;
    }


    public static void setInteger(Element parent, String childName, int value) {
        Element element = new Element(childName);
        element.setAttribute("value", Integer.toString(value));
        parent.addContent(element);
    }

    public static void setString(Element parent, String childName, String value) {
        Element element = new Element(childName);
        element.setAttribute("value", value == null ? "" : value);
        parent.addContent(element);
    }

    public static void setBoolean(Element parent, String childName, boolean value) {
        Element element = new Element(childName);
        element.setAttribute("value", Boolean.toString(value));
        parent.addContent(element);
    }

    public static <T extends Enum> void setEnum(Element parent, String childName, T value) {
        Element element = new Element(childName);
        element.setAttribute("value",value.name());
        parent.addContent(element);
    }

    public static boolean getBooleanAttribute(Element element, String attributeName, boolean defaultValue) {
        String attributeValue = element.getAttributeValue(attributeName);
        return StringUtil.isEmptyOrSpaces(attributeValue) ? defaultValue : Boolean.parseBoolean(attributeValue);
    }

    public static void setBooleanAttribute(Element element, String attributeName, boolean value) {
        element.setAttribute(attributeName, Boolean.toString(value));
    }

    public static int getIntegerAttribute(Element element, String attributeName, int defaultValue) {
        String attributeValue = element.getAttributeValue(attributeName);
        if (attributeValue == null || attributeValue.trim().length() == 0) {
            return defaultValue;
        }
        return Integer.parseInt(attributeValue);
    }

    public static void setIntegerAttribute(Element element, String attributeName, int value) {
        element.setAttribute(attributeName, Integer.toString(value));
    }

    public static DBNColor getColorAttribute(Element element, String attributeName, DBNColor defaultValue) {
        String value = element.getAttributeValue(attributeName);
        if (StringUtil.isEmptyOrSpaces(value)) return defaultValue;
        int index = value.indexOf("/");
        if (index > -1) {
            int rgbBright = Integer.parseInt(value.substring(0, index));
            int rgbDark = Integer.parseInt(value.substring(index + 1));
            return new DBNColor(new Color(rgbBright), new Color(rgbDark));
        } else {
            int rgb = Integer.parseInt(value);
            return new DBNColor(rgb, rgb);
        }
    }

    public static void setColorAttribute(Element element, String attributeName, DBNColor value) {
        if (value != null) {
            int regularRgb = value.getRegularRgb();
            int darkRgb = value.getDarkRgb();
            String attributeValue = Integer.toString(regularRgb) + "/" + Integer.toString(darkRgb);
            element.setAttribute(attributeName, attributeValue);
        }
    }
    

    public static <T extends Enum<T>> T getEnumAttribute(Element element, String attributeName, T defaultValue) {
        String attributeValue = element.getAttributeValue(attributeName);
        Class<T> enumClass = (Class<T>) defaultValue.getClass();
        return StringUtil.isEmpty(attributeValue) ? defaultValue : T.valueOf(enumClass, attributeValue);
    }

    public static <T extends Enum<T>> T getEnumAttribute(Element element, String attributeName, Class<T> enumClass) {
        String attributeValue = element.getAttributeValue(attributeName);
        return StringUtil.isEmpty(attributeValue) ? null : T.valueOf(enumClass, attributeValue);
    }

    public static <T extends Enum<T>> void setEnumAttribute(Element element, String attributeName, T value) {
        element.setAttribute(attributeName, value.name());
    }
}
