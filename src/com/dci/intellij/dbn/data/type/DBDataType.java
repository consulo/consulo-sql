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

package com.dci.intellij.dbn.data.type;

import com.dci.intellij.dbn.object.DBPackage;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.DBType;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBObjectBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class DBDataType {
    private DBType declaredType;
    private DBNativeDataType nativeDataType;
    private String typeName;
    private String qualifiedName;
    private long length;
    private int precision;
    private int scale;

    public DBDataType(DBObject parent, ResultSet resultSet) throws SQLException {
        length = resultSet.getLong("DATA_LENGTH");
        precision = resultSet.getInt("DATA_PRECISION");
        scale = resultSet.getInt("DATA_SCALE");

        String typeOwner = resultSet.getString("DATA_TYPE_OWNER");
        String typePackage = resultSet.getString("DATA_TYPE_PACKAGE");
        String dataTypeName = resultSet.getString("DATA_TYPE_NAME");
        DBObjectBundle objectBundle = parent.getConnectionHandler().getObjectBundle();
        if (typeOwner != null) {
            DBSchema typeSchema = objectBundle.getSchema(typeOwner);
            if (typePackage != null) {
                DBPackage packagee = typeSchema.getPackage(typePackage);
                declaredType = packagee.getType(dataTypeName);
            } else {
                declaredType = typeSchema.getType(dataTypeName);
            }
            if (declaredType == null) typeName = dataTypeName;
        } else {
            nativeDataType = objectBundle.getNativeDataType(dataTypeName);
            if (nativeDataType == null) typeName = dataTypeName;
        }
    }

    public DBDataType(DBNativeDataType nativeDataType, int precision, int scale) throws SQLException {
        this.precision = precision;
        this.scale = scale;
        this.nativeDataType = nativeDataType;
    }

    public boolean isDeclared() {
        return declaredType != null;
    }

    public boolean isNative() {
        return nativeDataType != null;
    }

    public String getName() {
        return nativeDataType == null && declaredType == null ? typeName :
                nativeDataType == null ? declaredType.getQualifiedName() :
                nativeDataType.getName();
    }

    public Class getTypeClass() {
        return nativeDataType == null ? Object.class : nativeDataType.getDataTypeDefinition().getTypeClass();
    }

    public int getSqlType() {
        return nativeDataType == null ? Types.CHAR : nativeDataType.getSqlType();
    }

    public DBNativeDataType getNativeDataType() {
        return nativeDataType;
    }

    public DBType getDeclaredType() {
        return declaredType;
    }

    public long getLength() {
        return length;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }


    public Object getValueFromResultSet(ResultSet resultSet, int columnIndex) throws SQLException {
        if (nativeDataType != null) {
            return nativeDataType.getValueFromResultSet(resultSet, columnIndex);
        } else {
            return declaredType == null ? "[UNKNOWN]" : "[" + declaredType.getName() + "]";
        }
    }

    public void setValueToResultSet(ResultSet resultSet, int columnIndex, Object value) throws SQLException {
        if (nativeDataType != null) {
            nativeDataType.setValueToResultSet(resultSet, columnIndex, value);
        }
    }

    public void setValueToPreparedStatement(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        if (nativeDataType != null) {
            nativeDataType.setValueToPreparedStatement(preparedStatement, index, value);
        }
    }

    public String getQualifiedName() {
        if (qualifiedName == null) {
            StringBuilder buffer = new StringBuilder();
            String name = getName();
            buffer.append(name == null ? "" : name.toLowerCase());
            if (getPrecision() > 0) {
                buffer.append(" (");
                buffer.append(getPrecision());
                if (getScale() > 0) {
                    buffer.append(", ");
                    buffer.append(getScale());
                }
                buffer.append(")");
            } else if (getLength() > 0) {
                buffer.append(" (");
                buffer.append(getLength());
                buffer.append(")");
            }
            qualifiedName = buffer.toString();
        }
        return qualifiedName;
    }

    /**
     * @deprecated
     */
    public Object convert(String stringValue) throws Exception{
        Class clazz = getTypeClass();
        if (String.class.isAssignableFrom(clazz)) {
            return stringValue;
        }
        if (Date.class.isAssignableFrom(clazz)) {
            Method method = clazz.getMethod("valueOf", String.class);
            return method.invoke(clazz, stringValue);
        } else {
            Constructor constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(stringValue);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}