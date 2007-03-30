package com.cannontech.web.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

public class CBCDBUtil {

    public static Connection getConnection() {
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }
    }

}
