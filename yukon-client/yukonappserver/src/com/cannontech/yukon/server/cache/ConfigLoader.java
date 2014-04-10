package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteConfig;

public class ConfigLoader implements Runnable {
    private String databaseAlias = null;
    private ArrayList allConfigs = null;

    public ConfigLoader(ArrayList configs, String dbAlias) {
        this.allConfigs = configs;
        this.databaseAlias = dbAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code
        String sqlString =
            "SELECT CONFIGID, CONFIGNAME, CONFIGTYPE FROM MCTCONFIG WHERE CONFIGID >= 0 ORDER BY CONFIGNAME";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int configID = rset.getInt(1);
                String configName = rset.getString(2).trim();
                int configType = rset.getInt(3);

                LiteConfig basil = new LiteConfig(configID, configName, new Integer(configType));

                basil.setConfigName(configName);

                allConfigs.add(basil);
            }

        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for ConfigLoader (" + allConfigs.size() + " loaded)");
            // temp code
        }
    }
}
