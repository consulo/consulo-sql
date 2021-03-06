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

package com.dci.intellij.dbn.database.oracle;

import com.dci.intellij.dbn.connection.DatabaseType;
import com.dci.intellij.dbn.database.DatabaseCompatibilityInterface;
import com.dci.intellij.dbn.database.DatabaseDDLInterface;
import com.dci.intellij.dbn.database.DatabaseDebuggerInterface;
import com.dci.intellij.dbn.database.DatabaseExecutionInterface;
import com.dci.intellij.dbn.database.DatabaseMessageParserInterface;
import com.dci.intellij.dbn.database.DatabaseMetadataInterface;
import com.dci.intellij.dbn.database.common.DatabaseInterfaceProviderImpl;
import com.dci.intellij.dbn.database.common.DatabaseNativeDataTypes;
import com.dci.intellij.dbn.language.psql.dialect.oracle.OraclePLSQLLanguageDialect;
import com.dci.intellij.dbn.language.sql.dialect.oracle.OracleSQLLanguageDialect;

public class OracleInterfaceProvider extends DatabaseInterfaceProviderImpl {
    private DatabaseMessageParserInterface MESSAGE_PARSER_INTERFACE = new OracleMessageParserInterface();
    private DatabaseCompatibilityInterface COMPATIBILITY_INTERFACE = new OracleCompatibilityInterface();
    private DatabaseMetadataInterface METADATA_INTERFACE = new OracleMetadataInterface(this);
    private DatabaseDebuggerInterface DEBUGGER_INTERFACE = new OracleDebuggerInterface(this);
    private DatabaseDDLInterface DDL_INTERFACE = new OracleDDLInterface(this);
    private DatabaseExecutionInterface EXECUTION_INTERFACE = new OracleExecutionInterface();
    private DatabaseNativeDataTypes NATIVE_DATA_TYPES = new OracleNativeDataTypes();


    public OracleInterfaceProvider() {
        super(OracleSQLLanguageDialect.class,
				OraclePLSQLLanguageDialect.class);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.ORACLE;
    }

    public DatabaseNativeDataTypes getNativeDataTypes() {
        return NATIVE_DATA_TYPES;
    }

    public DatabaseMessageParserInterface getMessageParserInterface() {
        return MESSAGE_PARSER_INTERFACE;
    }

    public DatabaseCompatibilityInterface getCompatibilityInterface() {
        return COMPATIBILITY_INTERFACE;
    }

    public DatabaseMetadataInterface getMetadataInterface() {
        return METADATA_INTERFACE;
    }

    public DatabaseDebuggerInterface getDebuggerInterface() {
        return DEBUGGER_INTERFACE;
    }

    public DatabaseDDLInterface getDDLInterface() {
        return DDL_INTERFACE;
    }

    public DatabaseExecutionInterface getDatabaseExecutionInterface() {
        return EXECUTION_INTERFACE;
    }


}