package com.cannontech.database;

import com.cannontech.common.util.SqlStatementBuilder;

public abstract class SqlParameterBase implements SqlParameterSink {

    @Override
    final public void addValueSafe(String paramName, String value) {
        addValue(paramName, SqlUtils.convertStringToDbValue(value));
    }
    
    @Override
    final public void addValue(String paramName, Object value) {
        addValueRaw(paramName, SqlStatementBuilder.convertArgumentToJdbcObject(value));
    }

}
