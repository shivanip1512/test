package com.cannontech.common.mock;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.cannontech.common.util.MethodNotImplementedException;

public class MockDataSource implements DataSource {
    @Override
    public Connection getConnection() throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new MethodNotImplementedException();
    }
}
