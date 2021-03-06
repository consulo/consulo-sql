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

package com.dci.intellij.dbn.editor.data;

import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.dci.intellij.dbn.common.Constants;
import com.dci.intellij.dbn.common.action.DBNDataKeys;
import com.dci.intellij.dbn.common.event.EventManager;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.SimpleLaterInvocator;
import com.dci.intellij.dbn.common.util.EditorUtil;
import com.dci.intellij.dbn.common.util.MessageUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.connection.ConnectionStatusListener;
import com.dci.intellij.dbn.connection.mapping.FileConnectionMappingProvider;
import com.dci.intellij.dbn.connection.transaction.TransactionAction;
import com.dci.intellij.dbn.connection.transaction.TransactionListener;
import com.dci.intellij.dbn.database.DatabaseMessageParserInterface;
import com.dci.intellij.dbn.editor.data.filter.DatasetFilter;
import com.dci.intellij.dbn.editor.data.filter.DatasetFilterManager;
import com.dci.intellij.dbn.editor.data.filter.DatasetFilterType;
import com.dci.intellij.dbn.editor.data.model.DatasetEditorModel;
import com.dci.intellij.dbn.editor.data.model.DatasetEditorModelRow;
import com.dci.intellij.dbn.editor.data.options.DataEditorSettings;
import com.dci.intellij.dbn.editor.data.record.ui.DatasetRecordEditorDialog;
import com.dci.intellij.dbn.editor.data.structure.DatasetEditorStructureViewModel;
import com.dci.intellij.dbn.editor.data.ui.DatasetEditorForm;
import com.dci.intellij.dbn.editor.data.ui.table.DatasetEditorTable;
import com.dci.intellij.dbn.object.DBDataset;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.identifier.DBObjectIdentifier;
import com.dci.intellij.dbn.vfs.DatabaseEditableObjectFile;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;

public class DatasetEditor extends UserDataHolderBase implements FileEditor, FileConnectionMappingProvider, ConnectionStatusListener,
		TransactionListener
{
	private DatabaseEditableObjectFile databaseFile;
	private DBObjectIdentifier datasetIdentifier;
	private DatasetEditorForm editorForm;
	private StructureViewModel structureViewModel;
	private ConnectionHandler connectionHandler;
	private boolean isDisposed;
	private DataEditorSettings settings;
	private Project project;


	private Set<PropertyChangeListener> propertyChangeListeners = new HashSet<PropertyChangeListener>();

	public DatasetEditor(DatabaseEditableObjectFile databaseFile, DBDataset dataset)
	{
		this.project = dataset.getProject();
		this.databaseFile = databaseFile;
		this.datasetIdentifier = dataset.getIdentifier();
		this.settings = DataEditorSettings.getInstance(project);

		connectionHandler = dataset.getConnectionHandler();
		editorForm = new DatasetEditorForm(this);


		if(!EditorUtil.hasEditingHistory(databaseFile, project))
		{
			load(true, true);
		}
		EventManager.subscribe(project, TransactionListener.TOPIC, this);
		EventManager.subscribe(project, ConnectionStatusListener.TOPIC, this);
	}

	public DBDataset getDataset()
	{
		return (DBDataset) datasetIdentifier.lookupObject();
	}

	public DBObjectIdentifier getDatasetIdentifier()
	{
		return datasetIdentifier;
	}

	public DataEditorSettings getSettings()
	{
		return settings;
	}

	@Nullable
	public DatasetEditorTable getEditorTable()
	{
		return editorForm == null ? null : editorForm.getEditorTable();
	}

	public DatasetEditorForm getEditorForm()
	{
		return editorForm;
	}

	public void showSearchHeader()
	{
		editorForm.showSearchHeader();
	}

	@Nullable
	public DatasetEditorModel getTableModel()
	{
		DatasetEditorTable editorTable = getEditorTable();
		return editorTable == null ? null : editorTable.getModel();
	}

	public DatabaseEditableObjectFile getDatabaseFile()
	{
		return databaseFile;
	}

	@Override
	public ConnectionHandler getActiveConnection()
	{
		return connectionHandler;
	}

	@Override
	public DBSchema getCurrentSchema()
	{
		return getDataset().getSchema();
	}

	public Project getProject()
	{
		return project;
	}

	@Override
	@NotNull
	public JComponent getComponent()
	{
		return editorForm.getComponent();
	}

	@Override
	@Nullable
	public JComponent getPreferredFocusedComponent()
	{
		return getEditorTable();
	}

	@Override
	@NonNls
	@NotNull
	public String getName()
	{
		return "Data";
	}

	@Override
	@NotNull
	public FileEditorState getState(@NotNull FileEditorStateLevel level)
	{
		DatasetEditorModel model = getTableModel();
		if(model != null && model.getState() != null)
		{
			DatasetEditorState datasetEditorState = (DatasetEditorState) model.getState();
			return datasetEditorState.clone();
		}

		return FileEditorState.INSTANCE;
	}

	@Override
	public void setState(@NotNull FileEditorState fileEditorState)
	{
		DatasetEditorModel model = getTableModel();
		if(model != null)
		{
			boolean isNew = model.getState().getRowCount() == 0;
			if(fileEditorState instanceof DatasetEditorState)
			{
				DatasetEditorState datasetEditorState = (DatasetEditorState) fileEditorState;
				model.setState(datasetEditorState);
			}

			if(isNew)
			{
				load(true, false);
			}
		}
	}

	@Override
	public boolean isModified()
	{
		DatasetEditorModel model = getTableModel();
		return model != null && model.isModified();
	}

	public void sort(int tableColumnIndex, int direction)
	{

	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public void selectNotify()
	{

	}

	@Override
	public void deselectNotify()
	{

	}

	@Override
	public void addPropertyChangeListener(@NotNull PropertyChangeListener listener)
	{
		propertyChangeListeners.add(listener);
	}

	@Override
	public void removePropertyChangeListener(@NotNull PropertyChangeListener listener)
	{
		propertyChangeListeners.remove(listener);
	}

	@Override
	@Nullable
	public BackgroundEditorHighlighter getBackgroundHighlighter()
	{
		return null;
	}

	@Override
	@Nullable
	public FileEditorLocation getCurrentLocation()
	{
		return null;
	}

	@Override
	@Nullable
	public StructureViewBuilder getStructureViewBuilder()
	{
		return new TreeBasedStructureViewBuilder()
		{
			@Override
			@NotNull
			public StructureViewModel createStructureViewModel(@Nullable Editor editor)
			{
				// Structure does not change. so it can be cached.
				if(structureViewModel == null)
				{
					structureViewModel = new DatasetEditorStructureViewModel(DatasetEditor.this);
				}
				return structureViewModel;
			}
		};
	}

	@Nullable
	@Override
	public VirtualFile getVirtualFile()
	{
		return null;
	}

	@Override
	public void dispose()
	{
		if(!isDisposed)
		{
			isDisposed = true;
			EventManager.unsubscribe(this);
			editorForm.dispose();
			editorForm = null;
			databaseFile = null;
			structureViewModel = null;
			settings = null;
		}
	}

	public boolean isDisposed()
	{
		return isDisposed;
	}

	public static DatasetEditor getSelected(Project project)
	{
		if(project != null)
		{
			FileEditor[] fileEditors = FileEditorManager.getInstance(project).getSelectedEditors();
			for(FileEditor fileEditor : fileEditors)
			{
				if(fileEditor instanceof DatasetEditor)
				{
					return (DatasetEditor) fileEditor;
				}
			}
		}
		return null;
	}

	/**
	 * *****************************************************
	 * Model operations                  *
	 * ******************************************************
	 */
	public void fetchNextRecords(int records)
	{
		try
		{
			DatasetEditorModel model = getTableModel();
			if(model != null)
			{
				model.fetchNextRecords(records, false);
			}
		}
		catch(SQLException e)
		{
			String message = "Error loading data for " + getDataset().getQualifiedNameWithType() + ".\nCause: " + e.getMessage();
			MessageUtil.showErrorDialog(message, e);
		}
	}

	public void load(final boolean useCurrentFilter, final boolean keepChanges)
	{
		new BackgroundTask(project, "Loading data", true)
		{
			@Override
			public void execute(@NotNull ProgressIndicator progressIndicator)
			{
				if(isDisposed)
				{
					return;
				}
				initProgressIndicator(progressIndicator, true);
				try
				{
					DatasetEditorModel model = getTableModel();
					if(model != null)
					{
						editorForm.showLoadingHint();
						editorForm.getEditorTable().cancelEditing();
						setLoading(true);
						model.load(progressIndicator, useCurrentFilter, keepChanges);
					}
				}
				catch(final SQLException e)
				{
					new SimpleLaterInvocator()
					{
						@Override
						public void run()
						{
							if(isDisposed)
							{
								return;
							}
							focusEditor();
							DBDataset dataset = getDataset();
							DatabaseMessageParserInterface messageParserInterface = getConnectionHandler().getInterfaceProvider()
									.getMessageParserInterface();
							DatasetFilterManager filterManager = DatasetFilterManager.getInstance(getProject());

							DatasetFilter filter = filterManager.getActiveFilter(dataset);
							if(getConnectionHandler().isValid())
							{
								if(filter == null || filter == DatasetFilterManager.EMPTY_FILTER || filter.getError() != null)
								{
									String message = "Error loading data for " + dataset.getQualifiedNameWithType() + ".\n" +
											(messageParserInterface.isTimeoutException(e) ? "The operation was timed out. Please check your timeout " +
													"configuration in Data Editor settings." : "Database error message: " + e.getMessage());

									MessageUtil.showErrorDialog(message);
								}
								else
								{
									String message = "Error loading data for " + dataset.getQualifiedNameWithType() + ".\n" +
											(messageParserInterface.isTimeoutException(e) ? "The operation was timed out. Please check your timeout " +
													"configuration in Data Editor settings." : "Filter \"" + filter.getName() + "\" may be invalid" +
													".\n" +
											"Database error message: " + e.getMessage());
									String[] options = {
											"Edit filter",
											"Remove filter",
											"Ignore filter",
											"Cancel"
									};

									int option = Messages.showDialog(message, Constants.DBN_TITLE_PREFIX + "Error", options, 0,
											Messages.getErrorIcon());
									if(option == 0)
									{
										filterManager.openFiltersDialog(dataset, false, false, DatasetFilterType.NONE);
										load(true, keepChanges);
									}
									else if(option == 1)
									{
										filterManager.setActiveFilter(dataset, null);
										load(true, keepChanges);
									}
									else if(option == 2)
									{
										filter.setError(e.getMessage());
										load(false, keepChanges);
									}
								}
							}
							else
							{
								String message = "Error loading data for " + dataset.getQualifiedNameWithType() + ". Could not connect to database" +
										".\n" +
										"Database error message: " + e.getMessage();
								MessageUtil.showErrorDialog(message);
							}
						}
					}.start();
				}
				finally
				{
					if(editorForm != null)
					{
						editorForm.hideLoadingHint();
					}
					setLoading(false);

				}
			}
		}.start();
	}

	private void focusEditor()
	{
		FileEditorManager.getInstance(project).openFile(databaseFile, true);
	}

	protected void setLoading(boolean loading)
	{
		DatasetEditorTable editorTable = getEditorTable();
		if(editorTable != null)
		{
			editorTable.setLoading(loading);
			editorTable.repaint();
		}
	}

	public void deleteRecords()
	{
		DatasetEditorTable editorTable = getEditorTable();
		DatasetEditorModel model = getTableModel();

		if(editorTable != null && model != null)
		{
			int[] indexes = editorTable.getSelectedRows();
			model.deleteRecords(indexes);
		}
	}

	public void insertRecord()
	{
		DatasetEditorTable editorTable = getEditorTable();
		DatasetEditorModel model = getTableModel();

		if(editorTable != null && model != null)
		{
			int[] indexes = editorTable.getSelectedRows();

			int rowIndex = indexes.length > 0 && indexes[0] < model.getSize() ? indexes[0] : 0;
			model.insertRecord(rowIndex);
		}
	}

	public void duplicateRecord()
	{
		DatasetEditorTable editorTable = getEditorTable();
		DatasetEditorModel model = getTableModel();
		if(editorTable != null && model != null)
		{
			int[] indexes = editorTable.getSelectedRows();
			if(indexes.length == 1)
			{
				model.duplicateRecord(indexes[0]);
			}
		}
	}

	public void openRecordEditor()
	{
		DatasetEditorTable editorTable = getEditorTable();
		DatasetEditorModel model = getTableModel();

		if(editorTable != null && model != null)
		{
			int index = editorTable.getSelectedRow();
			if(index == -1)
			{
				index = 0;
			}
			DatasetEditorModelRow row = model.getRowAtIndex(index);
			editorTable.stopCellEditing();
			editorTable.selectRow(row.getIndex());
			DatasetRecordEditorDialog editorDialog = new DatasetRecordEditorDialog(row);
			editorDialog.show();
		}
	}

	public void openRecordEditor(int index)
	{
		DatasetEditorTable editorTable = getEditorTable();
		DatasetEditorModel model = getTableModel();

		if(editorTable != null && model != null)
		{
			DatasetEditorModelRow row = model.getRowAtIndex(index);
			DatasetRecordEditorDialog editorDialog = new DatasetRecordEditorDialog(row);
			editorDialog.show();
		}
	}

	public boolean isInserting()
	{
		DatasetEditorModel model = getTableModel();
		return model != null && model.isInserting();
	}

	public boolean isLoading()
	{
		DatasetEditorTable editorTable = getEditorTable();
		return editorTable != null && editorTable.isLoading();
	}

	/**
	 * The dataset is readonly. This can not be changed by the flag isReadonly
	 */
	public boolean isReadonlyData()
	{
		DatasetEditorModel model = getTableModel();
		return model == null || model.isReadonly();
	}

	public boolean isReadonly()
	{
		DatasetEditorModel model = getTableModel();
		return model == null || model.getState().isReadonly();
	}

	public void setReadonly(boolean readonly)
	{
		DatasetEditorModel model = getTableModel();
		if(model != null)
		{
			model.getState().setReadonly(readonly);
		}
	}

	public int getRowCount()
	{
		return getEditorTable().getRowCount();
	}

	public ConnectionHandler getConnectionHandler()
	{
		return datasetIdentifier.lookupConnectionHandler();
	}

	/**
	 * *****************************************************
	 * ConnectionStatusListener                  *
	 * ******************************************************
	 */
	@Override
	public void statusChanged(String connectionId)
	{
		DatasetEditorTable editorTable = getEditorTable();
		if(editorTable != null && getConnectionHandler().getId().equals(connectionId))
		{
			editorTable.repaint();
		}
	}

	/**
	 * ******************************************************
	 * TransactionListener                     *
	 * ******************************************************
	 */
	@Override
	public void beforeAction(ConnectionHandler connectionHandler, TransactionAction action)
	{
		if(connectionHandler == getConnectionHandler())
		{
			DatasetEditorModel model = getTableModel();
			DatasetEditorTable editorTable = getEditorTable();
			if(model != null && editorTable != null)
			{
				if(action == TransactionAction.COMMIT)
				{

					if(editorTable.isEditing())
					{
						editorTable.stopCellEditing();
					}

					if(isInserting())
					{
						try
						{
							model.postInsertRecord(true, false);
						}
						catch(SQLException e1)
						{
							MessageUtil.showErrorDialog("Could not create row in " + getDataset().getQualifiedNameWithType() + ".", e1);
						}
					}
				}

				if(action == TransactionAction.ROLLBACK || action == TransactionAction.ROLLBACK_IDLE)
				{
					if(editorTable.isEditing())
					{
						editorTable.stopCellEditing();
					}
					if(isInserting())
					{
						model.cancelInsert(true);
					}
				}
			}
		}
	}

	@Override
	public void afterAction(ConnectionHandler connectionHandler, TransactionAction action, boolean succeeded)
	{
		if(connectionHandler == getConnectionHandler())
		{
			DatasetEditorModel model = getTableModel();
			DatasetEditorTable editorTable = getEditorTable();
			if(model != null && editorTable != null)
			{
				if(action == TransactionAction.COMMIT || action == TransactionAction.ROLLBACK)
				{
					if(succeeded && isModified())
					{
						load(true, false);
					}
				}

				if(action == TransactionAction.DISCONNECT)
				{
					editorTable.stopCellEditing();
					model.revertChanges();
					editorTable.repaint();
				}
			}
		}
	}

	public boolean isEditable()
	{
		DatasetEditorModel tableModel = getTableModel();
		return tableModel != null && tableModel.isEditable() && tableModel.getConnectionHandler().isConnected();
	}


	/**
	 * *****************************************************
	 * Data Provider                     *
	 * ******************************************************
	 */
	public DataProvider dataProvider = new DataProvider()
	{
		@Override
		public Object getData(@NonNls String dataId)
		{
			if(DBNDataKeys.DATASET_EDITOR.is(dataId))
			{
				return DatasetEditor.this;
			}
			if(PlatformDataKeys.PROJECT.is(dataId))
			{
				return project;
			}
			return null;
		}
	};

	public DataProvider getDataProvider()
	{
		return dataProvider;
	}

}
