package com.cannontech.database.debug;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class LoggingStatement implements Statement {

    private static final Logger log = DatabaseDebugHelper.getMainLogger();
    private static final Logger logStack = DatabaseDebugHelper.getStackTraceLogger();

    private final Statement delegate;

    public LoggingStatement(Statement statement) {
        this.delegate = statement;
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

    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    public void close() throws SQLException {
        delegate.close();
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

    public ResultSet executeQuery(String sql) throws SQLException {
        log.debug(sql);
        return delegate.executeQuery(sql);
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

    public boolean getMoreResults() throws SQLException {
        return delegate.getMoreResults();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return delegate.getMoreResults(current);
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

    public void setCursorName(String name) throws SQLException {
        delegate.setCursorName(name);
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

    public void setMaxFieldSize(int max) throws SQLException {
        delegate.setMaxFieldSize(max);
    }

    public void setMaxRows(int max) throws SQLException {
        delegate.setMaxRows(max);
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        delegate.setQueryTimeout(seconds);
    }

    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    public boolean isPoolable() throws SQLException {
        return delegate.isPoolable();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }

    public void setPoolable(boolean poolable) throws SQLException {
        delegate.setPoolable(poolable);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate.unwrap(iface);
    }

 
}
