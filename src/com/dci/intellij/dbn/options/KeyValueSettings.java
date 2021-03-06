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

package com.dci.intellij.dbn.options;

import com.dci.intellij.dbn.common.options.Configuration;
import com.dci.intellij.dbn.common.options.ui.ConfigurationEditorForm;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated
 */
public class KeyValueSettings extends Configuration {
    private Map<String,String> settings = new HashMap<String, String>();

    private static final String YES = "YES";
    private static final String NO = "NO";

    public String getSetting(String key) {
        return settings.get(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String setting = settings.get(key);
        if (setting != null) {
            return setting.equals(YES);
        } else {
            settings.put(key, defaultValue ? YES : NO);
            return defaultValue;
        }
    }

    public void setBoolean(String key, boolean value) {
        settings.put(key, value ? YES : NO);
    }

    public void registerSetting(String key, String value) {
        settings.put(key, value);
    }



    /*********************************************************
     *                      Configuration                    *
     *********************************************************/
    public void readConfiguration(Element element) throws InvalidDataException {
        Element optionsElement = element.getChild("general");
        if (optionsElement != null) {
            for (Object o : optionsElement.getChildren()) {
                Element option = (Element) o;
                settings.put(option.getAttributeValue("key"), option.getAttributeValue("value"));
            }
        }
    }

    public void writeConfiguration(Element element) throws WriteExternalException {
        Element optionsElement = new Element("general");
        element.addContent(optionsElement);
        for (Object o : settings.keySet()) {
            String key = (String) o;
            String value = settings.get(key);
            Element option = new Element("setting");

            option.setAttribute("key", key );
            option.setAttribute("value", value );
            optionsElement.addContent(option);
        }
    }

    public ConfigurationEditorForm createConfigurationEditor() {
        return null;
    }
}
