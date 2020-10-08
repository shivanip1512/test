package com.cannontech.watchdogs.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

public class DBConnectionUtil {

    private static Logger log = YukonLogManager.getLogger(DBConnectionUtil.class);

    public static boolean isDBConnected(DBName dbName) {
        try {
            YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            if (connection == null || connection.isClosed())
                return false;
        } catch (SQLException | RuntimeException e) {
            log.error("Database Connection Error", e);
            return false;
        }
        return true;
    }

}
