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

package com.dci.intellij.dbn.debugger;

import gnu.trove.THashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.dci.intellij.dbn.common.AbstractProjectComponent;
import com.dci.intellij.dbn.common.util.MessageUtil;
import com.dci.intellij.dbn.common.util.NamingUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.debugger.breakpoint.BreakpointUpdaterFileEditorListener;
import com.dci.intellij.dbn.debugger.execution.DBProgramRunConfiguration;
import com.dci.intellij.dbn.debugger.execution.DBProgramRunConfigurationFactory;
import com.dci.intellij.dbn.debugger.execution.DBProgramRunConfigurationType;
import com.dci.intellij.dbn.debugger.execution.DBProgramRunner;
import com.dci.intellij.dbn.object.DBMethod;
import com.dci.intellij.dbn.object.DBPrivilege;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.DBUser;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.dci.intellij.dbn.object.common.status.DBObjectStatus;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.RunnerRegistry;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.containers.ContainerUtil;

public class DatabaseDebuggerManager extends AbstractProjectComponent implements JDOMExternalizable {
    private Set<ConnectionHandler> activeDebugSessions = new THashSet<ConnectionHandler>();

    private DatabaseDebuggerManager(Project project) {
        super(project);
        FileEditorManager.getInstance(project).addFileEditorManagerListener(new BreakpointUpdaterFileEditorListener());
    }

    public void registerDebugSession(ConnectionHandler connectionHandler) {
        activeDebugSessions.add(connectionHandler);
    }

    public void unregisterDebugSession(ConnectionHandler connectionHandler) {
        activeDebugSessions.remove(connectionHandler);
    }

    public boolean checkForbiddenOperation(ConnectionHandler connectionHandler) {
        if (activeDebugSessions.contains(connectionHandler)) {
            MessageUtil.showErrorDialog("Operation not supported during active debug session.");
            return false;
        }
        return true;
    }

    public static DBProgramRunConfigurationType getConfigurationType() {
        ConfigurationType[] configurationTypes = Extensions.getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP);
        return ContainerUtil.findInstance(configurationTypes, DBProgramRunConfigurationType.class);
    }

    public static String createConfigurationName(DBMethod method) {
        DBProgramRunConfigurationType configurationType = getConfigurationType();
        RunManagerEx runManager = (RunManagerEx) RunManagerEx.getInstance(method.getProject());
        RunnerAndConfigurationSettings[] configurationSettings = runManager.getConfigurationSettings(configurationType);

        String name = method.getName();
        while (nameExists(configurationSettings, name)) {
            name = NamingUtil.getNextNumberedName(name, true);
        }
        return name;
    }

    private static boolean nameExists(RunnerAndConfigurationSettings[] configurationSettings, String name) {
        for (RunnerAndConfigurationSettings configurationSetting : configurationSettings) {
            if (configurationSetting.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void createDebugConfiguration(DBMethod method, DataContext dataContext) {
        RunManagerEx runManager = (RunManagerEx) RunManagerEx.getInstance(method.getProject());
        DBProgramRunConfigurationType configurationType = getConfigurationType();

        RunnerAndConfigurationSettings runConfigurationSetting = null;
        RunnerAndConfigurationSettings[] configurationSettings = runManager.getConfigurationSettings(configurationType);
        for (RunnerAndConfigurationSettings configurationSetting : configurationSettings) {
            DBProgramRunConfiguration availableRunConfiguration = (DBProgramRunConfiguration) configurationSetting.getConfiguration();
            if (method.equals(availableRunConfiguration.getMethod())) {
                runConfigurationSetting = configurationSetting;
                break;
            }
        }

        // check whether a configuration already exists for the given method
        if (runConfigurationSetting == null) {
            DBProgramRunConfigurationFactory configurationFactory = configurationType.getConfigurationFactory();
            DBProgramRunConfiguration runConfiguration = configurationFactory.createConfiguration(method);
            runConfigurationSetting = runManager.createConfiguration(runConfiguration, configurationFactory);
            runManager.addConfiguration((RunnerAndConfigurationSettingsImpl) runConfigurationSetting, false);
            runManager.setTemporaryConfiguration((RunnerAndConfigurationSettingsImpl) runConfigurationSetting);

        }

        runManager.setActiveConfiguration((RunnerAndConfigurationSettingsImpl) runConfigurationSetting);
        ProgramRunner programRunner = RunnerRegistry.getInstance().findRunnerById(DBProgramRunner.RUNNER_ID);
        try {
            ExecutionEnvironment executionEnvironment = new ExecutionEnvironment(DefaultDebugExecutor.getDebugExecutorInstance(), programRunner, runConfigurationSetting, method.getProject());
            programRunner.execute(executionEnvironment);
        } catch (ExecutionException e) {
            MessageUtil.showErrorDialog(
                    "Could not start debugger for " + method.getQualifiedName() + ". \n" +
                    "Reason: " + e.getMessage());
        }
    }

    public List<DBSchemaObject> loadCompileDependencies(DBMethod method, ProgressIndicator progressIndicator) {
        DBSchemaObject executable = method.getProgram() == null ? method : method.getProgram();
        List<DBSchemaObject> compileList = new ArrayList<DBSchemaObject>();
        if (!executable.getStatus().is(DBObjectStatus.DEBUG)) {
            compileList.add(executable);
        }

        for (DBObject object : executable.getReferencedObjects()) {
            if (object instanceof DBSchemaObject && object != executable) {
                if (!progressIndicator.isCanceled()) {
                    DBSchemaObject schemaObject = (DBSchemaObject) object;
                    DBSchema schema = schemaObject.getSchema();
                    if (!schema.isPublicSchema() && !schema.isSystemSchema() && schemaObject.getStatus().has(DBObjectStatus.DEBUG)) {
                        if (!schemaObject.getStatus().is(DBObjectStatus.DEBUG)) {
                            compileList.add(schemaObject);
                            progressIndicator.setText("Loading dependencies of " + schemaObject.getQualifiedNameWithType());
                            schemaObject.getReferencedObjects();
                        }
                    }
                }
            }
        }

        Collections.sort(compileList, DEPENDENCY_COMPARATOR);
        return compileList;
    }

    public List<String> getMissingDebugPrivileges(ConnectionHandler connectionHandler) {
        String userName = connectionHandler.getUserName();
        DBUser user = connectionHandler.getObjectBundle().getUser(userName);
        String[] privilegeNames = connectionHandler.getInterfaceProvider().getDebuggerInterface().getRequiredPrivilegeNames();
        List<String> missingPrivileges = new ArrayList<String>();
        for (String privilegeName : privilegeNames) {
            DBPrivilege privilege = connectionHandler.getObjectBundle().getPrivilege(privilegeName);
            if (privilege == null || !user.hasPrivilege(privilege))  {
                 missingPrivileges.add(privilegeName);
            }
        }

        return missingPrivileges;
    }

    private static final Comparator<DBSchemaObject> DEPENDENCY_COMPARATOR = new Comparator<DBSchemaObject>() {
        public int compare(DBSchemaObject schemaObject1, DBSchemaObject schemaObject2) {
            if (schemaObject1.getReferencedObjects().contains(schemaObject2)) return 1;
            if (schemaObject2.getReferencedObjects().contains(schemaObject1)) return -1;
            return 0;
        }
    };



    /***************************************
     *            ProjectComponent         *
     ***************************************/
    public static DatabaseDebuggerManager getInstance(Project project) {
        return project.getComponent(DatabaseDebuggerManager.class);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "DBNavigator.Project.DebuggerManager";
    }
    public void disposeComponent() {
        super.disposeComponent();
    }

    /****************************************
    *            JDOMExternalizable         *
    *****************************************/
    public void readExternal(Element element) throws InvalidDataException {
    }

    public void writeExternal(Element element) throws WriteExternalException {
    }
}