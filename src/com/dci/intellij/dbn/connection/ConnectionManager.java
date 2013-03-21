package com.dci.intellij.dbn.connection;

import com.dci.intellij.dbn.browser.DatabaseBrowserManager;
import com.dci.intellij.dbn.common.AbstractProjectComponent;
import com.dci.intellij.dbn.common.event.EventManager;
import com.dci.intellij.dbn.common.option.InteractiveOptionHandler;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.common.ui.MessageDialog;
import com.dci.intellij.dbn.common.util.EditorUtil;
import com.dci.intellij.dbn.connection.config.ConnectionBundleSettingsListener;
import com.dci.intellij.dbn.connection.config.ConnectionDatabaseSettings;
import com.dci.intellij.dbn.connection.config.ConnectionDetailSettings;
import com.dci.intellij.dbn.connection.config.ConnectionSettings;
import com.dci.intellij.dbn.connection.mapping.FileConnectionMappingManager;
import com.dci.intellij.dbn.connection.transaction.DatabaseTransactionManager;
import com.dci.intellij.dbn.connection.transaction.TransactionAction;
import com.intellij.ProjectTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.ModuleAdapter;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionManager extends AbstractProjectComponent implements ProjectManagerListener{
    public static final int FIVE_MINUTES_TIMEOUT = 1000 * 60 * 5;
    private List<ConnectionBundle> connectionBundles = new ArrayList<ConnectionBundle>();
    private Timer idleConnectionCleaner;

    private InteractiveOptionHandler closeProjectOptionHandler =
            new InteractiveOptionHandler(
                    "Uncommitted changes",
                    "You have uncommitted changes on one or more connections for project \"{0}\". \n" +
                    "Please specify whether to commit or rollback these changes before closing the project",
                    2, "Commit", "Rollback", "Review Changes", "Cancel");

    private InteractiveOptionHandler automaticDisconnectOptionHandler =
            new InteractiveOptionHandler(
                    "Idle connection ",
                    "The connection \"{0}\" is been idle for more than {1} minutes. You have uncommitted changes on this connection. \n" +
                            "Please specify whether to commit or rollback the changes. You can chose to keep the connection alive for {2} more minutes. \n\n" +
                            "NOTE: Connection will close automatically if this prompt stais unattended for more than 5 minutes.",
                    2, "Commit", "Rollback", "Review Changes", "Keep Alive");


    public static ConnectionManager getInstance(Project project) {
        return project.getComponent(ConnectionManager.class);
    }

    private ConnectionManager(Project project) {
        super(project);
        ProjectManager projectManager = ProjectManager.getInstance();
        projectManager.addProjectManagerListener(project, this);
    }

    @Override
    public void initComponent() {
        super.initComponent();
        Project project = getProject();
        EventManager.subscribe(project, ProjectTopics.MODULES, moduleListener);
        EventManager.subscribe(project, ConnectionBundleSettingsListener.TOPIC, connectionBundleSettingsListener);
        initConnectionBundles();
        idleConnectionCleaner = new Timer("Idle connection cleaner [" + project.getName() + "]");
        idleConnectionCleaner.schedule(new CloseIdleConnectionTask(), ConnectionPool.ONE_MINUTE, ConnectionPool.ONE_MINUTE);
    }

    @Override
    public void disposeComponent() {
        idleConnectionCleaner.cancel();
        EventManager.unsubscribe(
                moduleListener,
                connectionBundleSettingsListener);
    }

    /*********************************************************
    *                       Listeners                        *
    *********************************************************/
    private ModuleListener moduleListener = new ModuleAdapter() {
        public void moduleAdded(Project project, Module module) {
            initConnectionBundles();
        }

        public void moduleRemoved(Project project, Module module) {
            initConnectionBundles();
        }

        public void modulesRenamed(Project project, List<Module> modules) {
            for (Module module : modules) {
                ModuleConnectionBundle connectionBundle = ModuleConnectionBundle.getInstance(module);
                if (connectionBundle.getConnectionHandlers().size() > 0) {
                    initConnectionBundles();
                    break;
                }
            }
        }
    };

    private ConnectionBundleSettingsListener connectionBundleSettingsListener = new ConnectionBundleSettingsListener() {
        @Override
        public void settingsChanged() {
            initConnectionBundles();
        }
    };

    /*********************************************************
    *                        Custom                         *
    *********************************************************/
    public List<ConnectionBundle> getConnectionBundles() {
        return connectionBundles;
    }

    private synchronized void initConnectionBundles() {
        Project project = getProject();
        connectionBundles.clear();
        ProjectConnectionBundle projectConnectionBundle = ProjectConnectionBundle.getInstance(project);
        if (projectConnectionBundle.getConnectionHandlers().size() > 0) {
            connectionBundles.add(projectConnectionBundle);
        }
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleConnectionBundle moduleConnectionBundle = ModuleConnectionBundle.getInstance(module);
            if (moduleConnectionBundle.getConnectionHandlers().size() > 0) {
                connectionBundles.add(moduleConnectionBundle);
            }
        }
        Collections.sort(connectionBundles);
        EventManager.notify(project, ConnectionManagerListener.TOPIC).connectionsChanged();
    }

    public void testConnection(ConnectionHandler connectionHandler, boolean showMessageDialog) {
        Project project = getProject();
        ConnectionDatabaseSettings databaseSettings = connectionHandler.getSettings().getDatabaseSettings();
        try {
            connectionHandler.getStandaloneConnection();
            if (showMessageDialog) {
                MessageDialog.showInfoDialog(
                        project,
                        "Successfully connected to \"" + connectionHandler.getName() + "\".",
                        databaseSettings.getConnectionDetails(),
                        false);
            }
        } catch (Exception e) {
            if (showMessageDialog) {
                MessageDialog.showErrorDialog(
                        project,
                        "Could not connect to \"" + connectionHandler.getName() + "\".",
                        databaseSettings.getConnectionDetails() + "\n\n" + e.getMessage(),
                        false);
            }
        }
    }

    public void testConfigConnection(ConnectionDatabaseSettings databaseSettings, boolean showMessageDialog) {
        Project project = getProject();
        try {
            Connection connection = ConnectionUtil.connect(databaseSettings, null, false, null);
            ConnectionUtil.closeConnection(connection);
            databaseSettings.setConnectivityStatus(ConnectivityStatus.VALID);
            if (showMessageDialog) {
                MessageDialog.showInfoDialog(
                        project,
                        "Test connection to \"" + databaseSettings.getName() + "\" succeeded. Configuration is valid.",
                        databaseSettings.getConnectionDetails(),
                        false);
            }

        } catch (Exception e) {
            databaseSettings.setConnectivityStatus(ConnectivityStatus.INVALID);
            if (showMessageDialog) {
                MessageDialog.showErrorDialog(
                        project,
                        "Could not connect to \"" + databaseSettings.getName() + "\".",
                        databaseSettings.getConnectionDetails() + "\n\n" + e.getMessage(),
                        false);
            }
        }
    }

    public ConnectionInfo showConnectionInfo(ConnectionSettings connectionSettings) {
        ConnectionDatabaseSettings databaseSettings = connectionSettings.getDatabaseSettings();
        ConnectionDetailSettings detailSettings = connectionSettings.getDetailSettings();
        return showConnectionInfo(databaseSettings, detailSettings);
    }

    public ConnectionInfo showConnectionInfo(ConnectionDatabaseSettings databaseSettings, @Nullable ConnectionDetailSettings detailSettings) {
        try {
            Map<String, String> connectionProperties = detailSettings == null ? null : detailSettings.getProperties();
            Connection connection = ConnectionUtil.connect(databaseSettings, connectionProperties, false, null);
            ConnectionInfo connectionInfo = new ConnectionInfo(connection.getMetaData());
            ConnectionUtil.closeConnection(connection);
            MessageDialog.showInfoDialog(
                    getProject(),
                    "Database details for connection \"" + databaseSettings.getName() + "\"",
                    connectionInfo.toString(),
                    false);
            return connectionInfo;

        } catch (Exception e) {
            MessageDialog.showErrorDialog(
                    getProject(),
                    "Could not connect to \"" + databaseSettings.getName() + "\".",
                    databaseSettings.getConnectionDetails() + "\n\n" + e.getMessage(),
                    false);
            return null;
        }
    }

    /*********************************************************
     *                     Miscellaneous                     *
     *********************************************************/
     public ConnectionHandler getConnectionHandler(String connectionId) {
         for (ConnectionBundle connectionBundle : connectionBundles) {
             for (ConnectionHandler connectionHandler : connectionBundle.getConnectionHandlers().getFullList()) {
                if (connectionHandler.getId().equals(connectionId)) {
                    return connectionHandler;
                }
             }
         }
         return null;
     }

     public Set<ConnectionHandler> getConnectionHandlers() {
         Set<ConnectionHandler> connectionHandlers = new THashSet<ConnectionHandler>();
         for (ConnectionBundle connectionBundle : connectionBundles) {
             connectionHandlers.addAll(connectionBundle.getConnectionHandlers());
         }
         return connectionHandlers;
     }

     public ConnectionHandler getActiveConnection(Project project) {
         ConnectionHandler connectionHandler = null;
         VirtualFile virtualFile = EditorUtil.getSelectedFile(project);
         if (DatabaseBrowserManager.getInstance(project).getBrowserToolWindow().isActive() || virtualFile == null) {
             connectionHandler = DatabaseBrowserManager.getInstance(project).getActiveConnection();
         }

         if (connectionHandler == null && virtualFile!= null) {
             connectionHandler = FileConnectionMappingManager.getInstance(project).getActiveConnection(virtualFile);
         }

         return connectionHandler;
     }

    public boolean hasUncommittedChanges() {
        for (ConnectionBundle connectionBundle : getConnectionBundles()) {
            for (ConnectionHandler connectionHandler : connectionBundle.getConnectionHandlers()) {
                if (connectionHandler.hasUncommittedChanges()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void commitAll() {
        DatabaseTransactionManager transactionManager = DatabaseTransactionManager.getInstance(getProject());
        for (ConnectionBundle connectionBundle : getConnectionBundles()) {
            for (ConnectionHandler connectionHandler : connectionBundle.getConnectionHandlers()) {
                if (connectionHandler.hasUncommittedChanges()) {
                    transactionManager.commit(connectionHandler, false, false);
                }
            }
        }
    }

    public void rollbackAll() {
        DatabaseTransactionManager transactionManager = DatabaseTransactionManager.getInstance(getProject());
        for (ConnectionBundle connectionBundle : getConnectionBundles()) {
            for (ConnectionHandler connectionHandler : connectionBundle.getConnectionHandlers()) {
                if (connectionHandler.hasUncommittedChanges()) {
                    transactionManager.rollback(connectionHandler, false, false);
                }
            }
        }
    }

    private class CloseIdleConnectionTask extends TimerTask {
        public void run() {
            for (ConnectionBundle connectionBundle : getConnectionBundles()) {
                for (ConnectionHandler connectionHandler : connectionBundle.getConnectionHandlers()) {
                    resolveIdleStatus(connectionHandler);
                }
            }
        }
        private void resolveIdleStatus(final ConnectionHandler connectionHandler) {
            final DatabaseTransactionManager transactionManager = DatabaseTransactionManager.getInstance(getProject());
            ConnectionStatus connectionStatus = connectionHandler.getConnectionStatus();
            if (!connectionStatus.isResolvingIdleStatus()) {
                final int idleMinutes = connectionHandler.getIdleMinutes();
                final int idleMinutesToDisconnect = connectionHandler.getSettings().getDetailSettings().getIdleTimeToDisconnect();
                if (idleMinutes > idleMinutesToDisconnect) {
                    if (connectionHandler.hasUncommittedChanges()) {
                        connectionStatus.setResolvingIdleStatus(true);
                        new BackgroundTask(getProject(), "Close idle connection " + connectionHandler.getName(), true, false) {
                            protected void execute(@NotNull ProgressIndicator progressIndicator) throws InterruptedException {
                                Thread.currentThread().sleep(FIVE_MINUTES_TIMEOUT);
                                if (connectionHandler.getConnectionStatus().isResolvingIdleStatus()) {
                                    transactionManager.execute(connectionHandler, false, TransactionAction.ROLLBACK, TransactionAction.DISCONNECT_IDLE);
                                }
                            }
                        }.start();

                        new SimpleLaterInvocator() {
                            public void run() {
                                int result = automaticDisconnectOptionHandler.resolve(
                                        connectionHandler.getName(),
                                        Integer.toString(idleMinutes),
                                        Integer.toString(idleMinutesToDisconnect));

                                if (connectionHandler.getConnectionStatus().isResolvingIdleStatus()) {
                                    // status was not resolved by the prompt timeout
                                    switch (result) {
                                        case 0: transactionManager.execute(connectionHandler, false, TransactionAction.COMMIT); break;
                                        case 1: transactionManager.execute(connectionHandler, false, TransactionAction.ROLLBACK_IDLE, TransactionAction.DISCONNECT_IDLE); break;
                                        case 2: transactionManager.showUncommittedChangesDialog(connectionHandler, TransactionAction.DISCONNECT_IDLE); break;
                                        case 3: transactionManager.execute(connectionHandler, false, TransactionAction.PING); break;
                                    }
                                }
                            }
                        }.start();
                    } else {
                        transactionManager.execute(connectionHandler, false, TransactionAction.DISCONNECT_IDLE);
                    }
                }
            }
        }
    }

    /**********************************************
    *            ProjectManagerListener           *
    ***********************************************/

    @Override
    public void projectOpened(Project project) {}

    @Override
    public boolean canCloseProject(Project project) {
        if (hasUncommittedChanges()) {
            int result = closeProjectOptionHandler.resolve(project.getName());
            switch (result) {
                case 0: commitAll(); return true;
                case 1: rollbackAll(); return true;
                case 2: return DatabaseTransactionManager.getInstance(project).showUncommittedChangesOverviewDialog(null);
                case 3: return false;
            }
        }
        return true;
    }

    @Override
    public void projectClosed(Project project) {
    }

    @Override
    public void projectClosing(Project project) {
    }

    /**********************************************
    *                ProjectComponent             *
    ***********************************************/
    @NonNls
    @NotNull
    public String getComponentName() {
        return "DBNavigator.Project.DatabaseConnectionManager";
    }
}