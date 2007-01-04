package com.cannontech.database.debug;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class LoggingPreparedStatement implements PreparedStatement {

    private static final Logger log = DatabaseDebugHelper.getMainLogger();
    private static final Logger logStack = DatabaseDebugHelper.getStackTraceLogger();

    private final PreparedStatement delegate;
    private String initialSql = "";

    public LoggingPreparedStatement(PreparedStatement statement) {
        this.delegate = statement;
    }

    public LoggingPreparedStatement(PreparedStatement statement, String initialSql) {
        this.delegate = statement;
        this.initialSql  = initialSql;
    }
    
    public void addBatch() throws SQLException {
        delegate.addBatch();
    }

    public void addBatch(String sql) throws SQLException {
        delegate.addBatch(sql);
    }

    public void cancel() throws SQLException {
        delegate.cancel();
    }

    public void clearBatch() throws SQLException {
        delegate.clearBatch();
    }

    public void clearParameters() throws SQLException {
        delegate.clearParameters();
    }

    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    public void close() throws SQLException {
        delegate.close();
    }

    public boolean execute() throws SQLException {
        log.debug(initialSql);
        return delegate.execute();
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        log.debug(sql);
        return delegate.execute(sql, autoGeneratedKeys);
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        log.debug(sql);
        return delegate.execute(sql, columnIndexes);
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        log.debug(sql);
        return delegate.execute(sql, columnNames);
    }

    public boolean execute(String sql) throws SQLException {
        log.debug(sql);
        return delegate.execute(sql);
    }

    public int[] executeBatch() throws SQLException {
        return delegate.executeBatch();
    }

    public ResultSet executeQuery() throws SQLException {
        log.debug(initialSql);
        return delegate.executeQuery();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        log.debug(sql);
        return delegate.executeQuery(sql);
    }

    public int executeUpdate() throws SQLException {
        log.debug(initialSql);
        return delegate.executeUpdate();
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        log.debug(sql);
        return delegate.executeUpdate(sql, autoGeneratedKeys);
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        log.debug(sql);
        return delegate.executeUpdate(sql, columnIndexes);
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        log.debug(sql);
        return delegate.executeUpdate(sql, columnNames);
    }

    public int executeUpdate(String sql) throws SQLException {
        log.debug(sql);
        return delegate.executeUpdate(sql);
    }

    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    public int getFetchDirection() throws SQLException {
        return delegate.getFetchDirection();
    }

    public int getFetchSize() throws SQLException {
        return delegate.getFetchSize();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return delegate.getGeneratedKeys();
    }

    public int getMaxFieldSize() throws SQLException {
        return delegate.getMaxFieldSize();
    }

    public int getMaxRows() throws SQLException {
        return delegate.getMaxRows();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    public boolean getMoreResults() throws SQLException {
        return delegate.getMoreResults();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return delegate.getMoreResults(current);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return delegate.getParameterMetaData();
    }

    public int getQueryTimeout() throws SQLException {
        return delegate.getQueryTimeout();
    }

    public ResultSet getResultSet() throws SQLException {
        return delegate.getResultSet();
    }

    public int getResultSetConcurrency() throws SQLException {
        return delegate.getResultSetConcurrency();
    }

    public int getResultSetHoldability() throws SQLException {
        return delegate.getResultSetHoldability();
    }

    public int getResultSetType() throws SQLException {
        return delegate.getResultSetType();
    }

    public int getUpdateCount() throws SQLException {
        return delegate.getUpdateCount();
    }

    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    public void setArray(int i, Array x) throws SQLException {
        delegate.setArray(i, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        delegate.setAsciiStream(parameterIndex, x, length);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        delegate.setBigDecimal(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        delegate.setBinaryStream(parameterIndex, x, length);
    }

    public void setBlob(int i, Blob x) throws SQLException {
        delegate.setBlob(i, x);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        delegate.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        delegate.setByte(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        delegate.setBytes(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        delegate.setCharacterStream(parameterIndex, reader, length);
    }

    public void setClob(int i, Clob x) throws SQLException {
        delegate.setClob(i, x);
    }

    public void setCursorName(String name) throws SQLException {
        delegate.setCursorName(name);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        delegate.setDate(parameterIndex, x, cal);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        delegate.setDate(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        delegate.setDouble(parameterIndex, x);
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        delegate.setEscapeProcessing(enable);
    }

    public void setFetchDirection(int direction) throws SQLException {
        delegate.setFetchDirection(direction);
    }

    public void setFetchSize(int rows) throws SQLException {
        delegate.setFetchSize(rows);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        delegate.setFloat(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        delegate.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        delegate.setLong(parameterIndex, x);
    }

    public void setMaxFieldSize(int max) throws SQLException {
        delegate.setMaxFieldSize(max);
    }

    public void setMaxRows(int max) throws SQLException {
        delegate.setMaxRows(max);
    }

    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        delegate.setNull(paramIndex, sqlType, typeName);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        delegate.setNull(parameterIndex, sqlType);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        delegate.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        delegate.setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        delegate.setObject(parameterIndex, x);
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        delegate.setQueryTimeout(seconds);
    }

    public void setRef(int i, Ref x) throws SQLException {
        delegate.setRef(i, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        delegate.setShort(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        delegate.setString(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        delegate.setTime(parameterIndex, x, cal);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        delegate.setTime(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        delegate.setTimestamp(parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        delegate.setTimestamp(parameterIndex, x);
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        delegate.setUnicodeStream(parameterIndex, x, length);
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        delegate.setURL(parameterIndex, x);
    }

 
}
