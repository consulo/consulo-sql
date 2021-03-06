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

package com.dci.intellij.dbn.object.action;

import com.dci.intellij.dbn.object.common.DBObject;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import java.util.List;

public class ObjectListActionGroup extends DefaultActionGroup {

    private ObjectListShowAction listShowAction;
    private List<? extends DBObject> objects;

    public ObjectListActionGroup(ObjectListShowAction listShowAction, List<? extends DBObject> objects) {
        super("", true);
        this.objects = objects;
        this.listShowAction = listShowAction;

        if (objects != null) {
            buildNavigationActions();
        }
    }

    private void buildNavigationActions() {
        for (int i=0; i<objects.size(); i++) {
            if (i == objects.size()) {
                return;
            }
            DBObject object = objects.get(i);
            add(listShowAction.createObjectAction(object));
        }
    }
}