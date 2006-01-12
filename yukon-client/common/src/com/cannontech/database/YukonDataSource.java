package com.cannontech.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.AbstractDataSource;

public class YukonDataSource extends AbstractDataSource {
    
    private final String pool;

    public YukonDataSource(String pool) {
        this.pool = pool;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = PoolManager.getInstance().getConnection( pool );
        return conn;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("No support for passing username and password.");
    }

}
