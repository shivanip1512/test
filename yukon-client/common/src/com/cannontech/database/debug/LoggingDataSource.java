package com.cannontech.database.debug;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.AbstractDataSource;

public class LoggingDataSource extends AbstractDataSource {

    private final DataSource delegate;
    private static final Logger log = DatabaseDebugHelper.getMainLogger();
    private static final Logger logStack = DatabaseDebugHelper.getStackTraceLogger();

    public LoggingDataSource(DataSource delegate) {
        super();
        this.delegate = delegate;
    }

    public Connection getConnection() throws SQLException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String stackStr = StringUtils.join(stackTrace, "\n    ");
        log.info("DataSource.getConnection() called");
        logStack.debug("\n    " + stackStr);
        return new LoggingConnection(delegate.getConnection());
    }

    public Connection getConnection(String arg0, String arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object unwrap(Class iface) throws SQLException {
        return delegate.unwrap(iface);
    }

    public boolean isWrapperFor(Class iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }

}
