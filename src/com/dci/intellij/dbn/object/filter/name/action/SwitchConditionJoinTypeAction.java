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

package com.dci.intellij.dbn.object.filter.name.action;

import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.util.ActionUtil;
import com.dci.intellij.dbn.object.filter.name.CompoundFilterCondition;
import com.dci.intellij.dbn.object.filter.name.ObjectNameFilterManager;
import com.dci.intellij.dbn.object.filter.name.ui.ObjectNameFilterSettingsForm;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;

public class SwitchConditionJoinTypeAction extends ObjectNameFilterAction{

    public SwitchConditionJoinTypeAction(ObjectNameFilterSettingsForm settingsForm) {
        super("Switch join type", Icons.CONDITION_JOIN_TYPE, settingsForm);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = ActionUtil.getProject(e);
        Object selection = getSelection();
        ObjectNameFilterManager filterManager = ObjectNameFilterManager.getInstance(project);
        if (selection instanceof CompoundFilterCondition) {
            CompoundFilterCondition condition = (CompoundFilterCondition) selection;
            filterManager.switchConditionJoinType(condition, settingsForm);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Object selection = getSelection();
        if (selection instanceof CompoundFilterCondition) {
            CompoundFilterCondition condition = (CompoundFilterCondition) selection;
            presentation.setEnabled(condition.getConditions().size() > 1);
        } else {
            presentation.setEnabled(false);
        }
    }
}
