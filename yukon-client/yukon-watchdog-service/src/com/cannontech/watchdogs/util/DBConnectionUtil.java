package com.cannontech.watchdogs.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.PoolManager;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.watchdogs.impl.DBName;

public class DBConnectionUtil {

    private static Logger log = YukonLogManager.getLogger(DBConnectionUtil.class);

    public static boolean isDBConnected(DBName dbName) {
        try {
            Connection connection = PoolManager.getYukonConnection();
            if (connection == null || connection.isClosed()) {
                return false;
            }
        } catch (SQLException | RuntimeException e) {
            log.error("Error Connecting " + dbName + " database.", e);
            return false;
        }
        return true;
    }

}
