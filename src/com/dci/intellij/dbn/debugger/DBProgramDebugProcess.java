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

import com.dci.intellij.dbn.common.Constants;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.common.util.DocumentUtil;
import com.dci.intellij.dbn.common.util.EditorUtil;
import com.dci.intellij.dbn.common.util.MessageUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.database.DatabaseDebuggerInterface;
import com.dci.intellij.dbn.database.common.debug.BreakpointInfo;
import com.dci.intellij.dbn.database.common.debug.DebuggerRuntimeInfo;
import com.dci.intellij.dbn.database.common.debug.DebuggerSessionInfo;
import com.dci.intellij.dbn.database.common.debug.ExecutionBacktraceInfo;
import com.dci.intellij.dbn.debugger.breakpoint.DBProgramBreakpointHandler;
import com.dci.intellij.dbn.debugger.evaluation.DBProgramDebuggerEditorsProvider;
import com.dci.intellij.dbn.debugger.execution.DBProgramRunConfiguration;
import com.dci.intellij.dbn.debugger.frame.DBProgramDebugSuspendContext;
import com.dci.intellij.dbn.editor.code.SourceCodeEditor;
import com.dci.intellij.dbn.execution.method.MethodExecutionInput;
import com.dci.intellij.dbn.execution.method.MethodExecutionManager;
import com.dci.intellij.dbn.language.common.element.util.ElementTypeAttribute;
import com.dci.intellij.dbn.language.common.psi.BasePsiElement;
import com.dci.intellij.dbn.language.psql.PSQLFile;
import com.dci.intellij.dbn.object.DBMethod;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.dci.intellij.dbn.object.identifier.DBMethodIdentifier;
import com.dci.intellij.dbn.vfs.DatabaseEditableObjectFile;
import com.dci.intellij.dbn.vfs.SourceCodeFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class DBProgramDebugProcess extends XDebugProcess {
    private Connection targetConnection;
    private Connection debugConnection;
    private ConnectionHandler connectionHandler;
    private DBProgramBreakpointHandler breakpointHandler;
    private DBProgramBreakpointHandler[] breakpointHandlers;
    private MethodExecutionInput executionInput;
    private BreakpointInfo defaultBreakpointInfo;
    private DBProgramDebugProcessStatus status = new DBProgramDebugProcessStatus();

    private transient DebuggerRuntimeInfo runtimeInfo;
    private transient ExecutionBacktraceInfo backtraceInfo;


    public DBProgramDebugProcess(@NotNull XDebugSession session, ConnectionHandler connectionHandler) {
        super(session);
        this.connectionHandler = connectionHandler;
        Project project = session.getProject();
        DatabaseDebuggerManager.getInstance(project).registerDebugSession(connectionHandler);

        DBProgramRunConfiguration runProfile = (DBProgramRunConfiguration) session.getRunProfile();
        executionInput = runProfile.getExecutionInput();

        breakpointHandler = new DBProgramBreakpointHandler(session, this);
        breakpointHandlers = new DBProgramBreakpointHandler[]{breakpointHandler};
    }

    public DBProgramDebugProcessStatus getStatus() {
        return status;
    }

    public Connection getTargetConnection() {
        return targetConnection;
    }

    public Connection getDebugConnection() {
        return debugConnection;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        return breakpointHandlers;
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return DBProgramDebuggerEditorsProvider.INSTANCE;
    }

    @Override
    public void sessionInitialized() {
        final Project project = getSession().getProject();
        new BackgroundTask(project, "Initialize debug environment", true) {
            @Override
            public void execute(@NotNull ProgressIndicator progressIndicator) {
                try {
                    targetConnection = connectionHandler.getPoolConnection(executionInput.getExecutionSchema());
                    debugConnection = connectionHandler.getPoolConnection();

                    DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                    progressIndicator.setText("Initializing debugger target session");
                    DebuggerSessionInfo sessionInfo = debuggerInterface.initializeSession(targetConnection);
                    debuggerInterface.enableDebugging(targetConnection);
                    debuggerInterface.attachSession(sessionInfo.getSessionId(), debugConnection);

                    synchronizeSession();
                    executeMethod();
                } catch (SQLException e) {
                    getSession().stop();
                    showErrorDialog(e);
                }
            }
        }.start();
    }

    private void synchronizeSession() {
        final Project project = getSession().getProject();
        new BackgroundTask(project, "Initialize debug environment", true) {

            public void execute(@NotNull ProgressIndicator progressIndicator) {
                DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                if (getStatus().PROCESS_IS_TERMINATING) getSession().stop();
                try {
                    progressIndicator.setText("Synchronizing debug session");
                    runtimeInfo = debuggerInterface.synchronizeSession(debugConnection);

                    if (getStatus().TARGET_EXECUTION_TERMINATED) {
                        getSession().stop();
                    } else {
                        getStatus().CAN_SET_BREAKPOINTS = true;
                        progressIndicator.setText("Registering breakpoints");
                        registerBreakpoints();
                        runtimeInfo = debuggerInterface.stepOver(debugConnection);

                        progressIndicator.setText("Suspending session");
                        suspendSession();
                    }
                } catch (SQLException e) {
                    // typically a timeout
                    getSession().stop();
                }
            }
        }.start();
    }

    private void executeMethod() {
        new DebugOperationThread("execute method") {
            public void executeOperation() throws SQLException {
                XDebugSession session = getSession();
                MethodExecutionManager executionManager = MethodExecutionManager.getInstance(session.getProject());
                if (getStatus().PROCESS_IS_TERMINATING) return;

                boolean success = executionManager.debugExecute(executionInput, targetConnection);

                if (!success) {
                    // if the method execution threw exception, the debugger-off statement is not reached,
                    // hence the session will hag as debuggable. To avoid this, disable debugging has
                    // to explicitly be called here
                    getDebuggerInterface().disableDebugging(targetConnection);
                }

                getStatus().TARGET_EXECUTION_TERMINATED = true;
                connectionHandler.closePoolConnection(targetConnection);
                targetConnection = null;
            }
        }.start();
    }

    /**
     * breakpoints need to be registered after the database session is started,
     * otherwise they do not get valid ids
     */
    private void registerBreakpoints() {
        Runnable readProcess = new Runnable() {
            public void run() {
                XBreakpointType localXBreakpointType = XDebuggerUtil.getInstance().findBreakpointType(breakpointHandler.getBreakpointTypeClass());
                Project project = getSession().getProject();
                XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
                Collection<XLineBreakpoint> breakpoints= breakpointManager.getBreakpoints(localXBreakpointType);

                for (XLineBreakpoint breakpoint : breakpoints) {
                    breakpointHandler.registerBreakpoint(breakpoint);
                }

                registerDefaultBreakpoint();
            }
        };
        ApplicationManager.getApplication().runReadAction(readProcess);
    }

    private void registerDefaultBreakpoint() {
        SourceCodeFile sourceCodeFile = (SourceCodeFile) getMainDatabaseFile().getMainContentFile();
        PSQLFile psqlFile = (PSQLFile) sourceCodeFile.getPsiFile();
        if (psqlFile != null) {
            DBMethod method = executionInput.getMethod();
            BasePsiElement basePsiElement = psqlFile.lookupObjectDeclaration(method.getObjectType().getGenericType(), method.getName());
            if (basePsiElement != null) {
                BasePsiElement subject = basePsiElement.lookupFirstPsiElement(ElementTypeAttribute.SUBJECT);
                int offset = subject.getTextOffset();
                Document document = DocumentUtil.getDocument(psqlFile);
                int line = document.getLineNumber(offset);

                DBSchemaObject schemaObject = getMainDatabaseObject();
                try {
                    defaultBreakpointInfo = getDebuggerInterface().addBreakpoint(
                            method.getSchema().getName(),
                            schemaObject.getName(),
                            schemaObject.getObjectType().getName().toUpperCase(),
                            line,
                            debugConnection);
                } catch (SQLException e) {
                     e.printStackTrace();
                }
            }
        }
    }

    /**
     * breakpoints need to be unregistered before closing the database session, otherwise they remain resident.
     */
    private void unregisterBreakpoints() {
        Runnable readProcess = new Runnable() {
            public void run() {
                XBreakpointType localXBreakpointType = XDebuggerUtil.getInstance().findBreakpointType(breakpointHandler.getBreakpointTypeClass());
                Project project = getSession().getProject();
                XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
                Collection<XLineBreakpoint> breakpoints= breakpointManager.getBreakpoints(localXBreakpointType);

                for (XLineBreakpoint breakpoint : breakpoints) {
                    breakpointHandler.unregisterBreakpoint(breakpoint, false);
                }

                try {
                    if (defaultBreakpointInfo != null) {
                        getDebuggerInterface().removeBreakpoint(defaultBreakpointInfo.getBreakpointId(), debugConnection);
                    }

                } catch (SQLException e) {
                }
            }
        };
        ApplicationManager.getApplication().runReadAction(readProcess);
    }

    @Override
    public void stop() {
        executionInput.setExecutionCancelled(!getStatus().PROCESS_STOPPED_NORMALLY);
        final Project project = getSession().getProject();

        if (getStatus().PROCESS_IS_TERMINATING) return;

        new BackgroundTask(project, "Stopping debugger", true) {
            public void execute(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setText("Cancelling / resuming method execution.");
                try {
                    getStatus().PROCESS_IS_TERMINATING = true;
                    unregisterBreakpoints();
                    rollOutDebugger();
                    getStatus().CAN_SET_BREAKPOINTS = false;

                    DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                    runtimeInfo = debuggerInterface.stopExecution(debugConnection);
                    debuggerInterface.detachSession(debugConnection);

                    connectionHandler.closePoolConnection(debugConnection);
                    debugConnection = null;
                    getStatus().PROCESS_IS_TERMINATED = true;
                } catch (final SQLException e) {
                    showErrorDialog(e);
                } finally {
                    DatabaseDebuggerManager.getInstance(project).unregisterDebugSession(connectionHandler);
                }
            }
        }.start();
    }

    @Override
    public void startStepOver() {
        new DebugOperationThread("step over") {
            public void executeOperation() throws SQLException {
                DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                runtimeInfo = debuggerInterface.stepOver(debugConnection);
                suspendSession();
            }
        }.start();
    }

    @Override
    public void startStepInto() {
        new DebugOperationThread("step into") {
            public void executeOperation() throws SQLException {
                DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                runtimeInfo = debuggerInterface.stepInto(debugConnection);
                suspendSession();
            }
        }.start();
    }

    @Override
    public void startStepOut() {
        new DebugOperationThread("step out") {
            public void executeOperation() throws SQLException {
                DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                runtimeInfo = debuggerInterface.stepOut(debugConnection);
                suspendSession();
            }
        }.start();
    }

    @Override
    public void resume() {
        new DebugOperationThread("resume execution") {
            public void executeOperation() throws SQLException {
                DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                runtimeInfo = debuggerInterface.resumeExecution(debugConnection);
                suspendSession();
            }
        }.start();
    }

    @Override
    public void runToPosition(@NotNull final XSourcePosition position) {
        new DebugOperationThread("run to position") {
            public void executeOperation() throws SQLException {
                DBSchemaObject object = DBProgramDebugUtil.getObject(position);
                if (object != null) {
                    DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                    runtimeInfo = debuggerInterface.runToPosition(
                            object.getSchema().getName(),
                            object.getName(),
                            object.getObjectType().getName().toUpperCase(),
                            position.getLine(),
                            debugConnection);
                }

                suspendSession();
            }
        }.start();
    }

    @Override
    public void startPausing() {
        // NOT SUPPORTED!!!
        new DebugOperationThread("run to position") {
            public void executeOperation() throws SQLException {
                DatabaseDebuggerInterface debuggerInterface = getDebuggerInterface();
                runtimeInfo = debuggerInterface.synchronizeSession(debugConnection);
                suspendSession();
            }
        }.start();
    }

    private void showErrorDialog(SQLException e) {
        MessageUtil.showErrorDialog("Could not perform operation.", e);
    }

    private void suspendSession() {
        if (getStatus().PROCESS_IS_TERMINATING) return;

        if (runtimeInfo.getOwnerName() == null) {
            getStatus().PROCESS_STOPPED_NORMALLY = true;
            getSession().stop();
        } else {
            try {
                backtraceInfo = getDebuggerInterface().getExecutionBacktraceInfo(debugConnection);
            } catch (SQLException e) {
                showErrorDialog(e);
            }
            DatabaseEditableObjectFile databaseFile = getDatabaseFile(runtimeInfo);
            DBProgramDebugSuspendContext suspendContext = new DBProgramDebugSuspendContext(this);
            getSession().positionReached(suspendContext);
            navigateInEditor(databaseFile, runtimeInfo.getLineNumber());
        }
    }

    public DatabaseEditableObjectFile getDatabaseFile(DebuggerRuntimeInfo runtimeInfo) {
        DBSchemaObject schemaObject = getDatabaseObject(runtimeInfo);
        return schemaObject.getVirtualFile();
    }

    public DBSchemaObject getDatabaseObject(DebuggerRuntimeInfo runtimeInfo) {
        DBSchema schema = connectionHandler.getObjectBundle().getSchema(runtimeInfo.getOwnerName());
        DBSchemaObject schemaObject = schema.getProgram(runtimeInfo.getProgramName());
        if (schemaObject == null) schemaObject = schema.getMethod(runtimeInfo.getProgramName());
        return schemaObject;
    }

    private DatabaseEditableObjectFile getMainDatabaseFile() {
        DBSchemaObject schemaObject = getMainDatabaseObject();
        return schemaObject.getVirtualFile();
    }

    private DBSchemaObject getMainDatabaseObject() {
        DBMethodIdentifier methodIdentifier = executionInput.getMethodIdentifier();
        DBSchema schema = connectionHandler.getObjectBundle().getSchema(methodIdentifier.getSchemaName());
        DBSchemaObject schemaObject = methodIdentifier.getProgramName() == null ?
                schema.getMethod(methodIdentifier.getMethodName()) :
                schema.getProgram(methodIdentifier.getProgramName());
        return schemaObject;
    }

    private void rollOutDebugger() {
        try {
            while (runtimeInfo!= null && !runtimeInfo.isTerminated()) {
                runtimeInfo = getDebuggerInterface().stepOut(debugConnection);
            }
        } catch (SQLException e) {
            showErrorDialog(e);
        }
    }

    private void navigateInEditor(final DatabaseEditableObjectFile databaseFile, final int line) {
        new SimpleLaterInvocator() {
            public void run() {
                // todo review this!!!
                SourceCodeEditor sourceCodeEditor = null;
                if (databaseFile.getContentFiles().size() > 1) {
                    FileEditorManager editorManager = FileEditorManager.getInstance(databaseFile.getProject());
                    FileEditor[] fileEditors = editorManager.getEditors(databaseFile);
                    if (fileEditors.length >= runtimeInfo.getNamespace()) {
                        FileEditor fileEditor = fileEditors[runtimeInfo.getNamespace() -1];
                        sourceCodeEditor = (SourceCodeEditor) fileEditor;
                        databaseFile.FAKE_DOCUMENT.set(sourceCodeEditor.getEditor().getDocument());
                    } else {
                        FileEditor fileEditor = EditorUtil.getFileEditor(databaseFile, databaseFile.getMainContentFile());
                        if (fileEditor != null && fileEditor instanceof SourceCodeEditor) {
                            sourceCodeEditor = (SourceCodeEditor) fileEditor;
                        }
                    }
                } else {
                    FileEditor fileEditor = EditorUtil.getFileEditor(databaseFile, databaseFile.getMainContentFile());
                    if (fileEditor != null && fileEditor instanceof SourceCodeEditor) {
                        sourceCodeEditor = (SourceCodeEditor) fileEditor;
                    }
                }
                LogicalPosition position = new LogicalPosition(line, 0);
                EditorUtil.selectEditor(databaseFile, sourceCodeEditor);
                if (sourceCodeEditor != null) sourceCodeEditor.getEditor().getScrollingModel().scrollTo(position, ScrollType.CENTER);
            }
        }.start();
    }

    public DatabaseDebuggerInterface getDebuggerInterface() {
        return connectionHandler.getInterfaceProvider().getDebuggerInterface();
    }

    public DebuggerRuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    public ExecutionBacktraceInfo getBacktraceInfo() {
        return backtraceInfo;
    }

    abstract class DebugOperationThread extends Thread {
        private String operationName;
        protected DebugOperationThread(String operationName) {
            super(Constants.DBN_TITLE_PREFIX + "Debug (" + operationName + ")");
            this.operationName = operationName;
        }

        @Override
        public final void run() {
            try {
                executeOperation();
            } catch (final SQLException e) {
                MessageUtil.showErrorDialog("Could not perform debug operation (" + operationName + ").", e);
            }
        }
        public abstract void executeOperation() throws SQLException;
    }
}
