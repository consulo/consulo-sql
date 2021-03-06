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

package com.dci.intellij.dbn.code.common.completion;

import com.intellij.codeInsight.intention.IntentionManager;
import com.dci.intellij.dbn.code.common.intention.ExecuteStatementIntentionAction;
import com.dci.intellij.dbn.code.common.intention.JumpToExecutionResultIntentionAction;
import com.dci.intellij.dbn.code.common.intention.SelectCurrentSchemaIntentionAction;
import com.dci.intellij.dbn.code.common.intention.SelectConnectionIntentionAction;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CodeCompletionManager extends AbstractProjectComponent implements JDOMExternalizable {
    public static final int BASIC_CODE_COMPLETION = 0;
    public static final int SMART_CODE_COMPLETION = 1;

    private CodeCompletionManager(Project project) {
        super(project);
        // fixme move these calls to a more appropriate place (nothing to do with code completion)
        IntentionManager intentionManager = IntentionManager.getInstance(project);
        intentionManager.addAction(new ExecuteStatementIntentionAction());
        intentionManager.addAction(new JumpToExecutionResultIntentionAction());
        intentionManager.addAction(new SelectConnectionIntentionAction());
        intentionManager.addAction(new SelectCurrentSchemaIntentionAction());
        //intentionManager.addAction(new SetupCodeCompletionIntentionAction());
    }

    public static CodeCompletionManager getInstance(Project project) {
        return project.getComponent(CodeCompletionManager.class);
    }

    /***************************************
    *            ProjectComponent           *
    ****************************************/
    @NonNls
    @NotNull
    public String getComponentName() {
        return "DBNavigator.Project.CodeCompletionManager";
    }

    /***************************************
    *            JDOMExternalizable           *
    ****************************************/
    public void readExternal(Element element) throws InvalidDataException {
    }

    public void writeExternal(Element element) throws WriteExternalException {
    }
}
