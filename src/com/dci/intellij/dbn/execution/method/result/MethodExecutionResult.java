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

package com.dci.intellij.dbn.execution.method.result;

import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.data.model.resultSet.ResultSetDataModel;
import com.dci.intellij.dbn.execution.ExecutionResult;
import com.dci.intellij.dbn.execution.common.options.ExecutionEngineSettings;
import com.dci.intellij.dbn.execution.method.ArgumentValue;
import com.dci.intellij.dbn.execution.method.MethodExecutionInput;
import com.dci.intellij.dbn.execution.method.result.ui.MethodExecutionResultForm;
import com.dci.intellij.dbn.object.DBArgument;
import com.dci.intellij.dbn.object.DBMethod;
import com.dci.intellij.dbn.object.DBTypeAttribute;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;

import javax.swing.Icon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodExecutionResult implements ExecutionResult, Disposable {
    private MethodExecutionInput executionInput;
    private MethodExecutionResultForm resultPanel;
    private List<ArgumentValue> argumentValues = new ArrayList<ArgumentValue>();
    private Map<DBArgument, ResultSetDataModel> cursorModels;
    private int executionDuration;
    private boolean debug;

    public MethodExecutionResult(MethodExecutionInput executionInput, boolean debug) {
        this.executionInput = executionInput;
        executionInput.setExecutionResult(this);
        this.debug = debug;
    }

    public MethodExecutionResult(MethodExecutionInput executionInput, MethodExecutionResultForm resultPanel, boolean debug) {
        this(executionInput, debug);
        this.resultPanel = resultPanel;
    }

    public int getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(int executionDuration) {
        this.executionDuration = executionDuration;
    }

    public void addArgumentValue(DBArgument argument, Object value) throws SQLException {
        ArgumentValue argumentValue = new ArgumentValue(argument, value);
        argumentValues.add(argumentValue);
        if (value instanceof ResultSet) {
            ResultSet resultSet = (ResultSet) value;
            if (cursorModels == null) {
                cursorModels = new HashMap<DBArgument, ResultSetDataModel>();
            }

            ExecutionEngineSettings settings = ExecutionEngineSettings.getInstance(argument.getProject());
            int maxRecords = settings.getStatementExecutionSettings().getResultSetFetchBlockSize();
            ResultSetDataModel dataModel = new ResultSetDataModel(resultSet, getConnectionHandler(), maxRecords);
            cursorModels.put(argument, dataModel);
        }
    }

    public void addArgumentValue(DBArgument argument, DBTypeAttribute attribute, Object value) {
        ArgumentValue argumentValue = new ArgumentValue(argument, attribute, value);
        argumentValues.add(argumentValue);
    }


    public List<ArgumentValue> getArgumentValues() {
        return argumentValues;
    }

    public MethodExecutionResultForm getResultPanel() {
        if (resultPanel == null) {
            resultPanel = new MethodExecutionResultForm(this);
        }
        return resultPanel;
    }

    public String getResultName() {
        return getMethod().getName();
    }

    public Icon getResultIcon() {
        return getMethod().getOriginalIcon();
    }

    public boolean isOrphan() {
        return false;
    }

    public MethodExecutionInput getExecutionInput() {
        return executionInput;
    }

    public DBMethod getMethod() {
        return executionInput.getMethod();
    }

    public Project getProject() {
        return getMethod().getProject();
    }

    public ConnectionHandler getConnectionHandler() {
        return getMethod().getConnectionHandler();
    }

    public boolean hasCursorResults() {
        for (ArgumentValue argumentValue: argumentValues) {
            if (argumentValue.isCursor()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSimpleResults() {
        for (ArgumentValue argumentValue: argumentValues) {
            if (!argumentValue.isCursor()) {
                return true;
            }
        }
        return false;
    }


    public void dispose() {
        resultPanel = null;
        executionInput.setExecutionResult(null);
        executionInput = null;
        argumentValues.clear();
    }

    public void setResultPanel(MethodExecutionResultForm resultPanel) {
        this.resultPanel = resultPanel;
    }

    public boolean isDebug() {
        return debug;
    }

    public ResultSetDataModel getTableModel(DBArgument argument) {
        return cursorModels.get(argument);
    }
}
