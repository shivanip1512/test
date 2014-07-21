package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteAlarmCategory;

public class AlarmCategoryLoader implements Runnable {
    private List<LiteAlarmCategory> allAlarmStates = null;
    private String databaseAlias = null;

    public AlarmCategoryLoader(List<LiteAlarmCategory> allAlarmStates, String databaseAlias) {
        this.allAlarmStates = allAlarmStates;
        this.databaseAlias = databaseAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = null;
        Date timerStop = null;
        // temp code

        // temp code
        timerStart = new Date();
        // temp code
        String sqlString = "SELECT AlarmCategoryID, CategoryName FROM AlarmCategory "
                + "WHERE AlarmCategoryID > 0 ORDER BY AlarmCategoryID";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int alarmID = rset.getInt(1);
                String alarmName = rset.getString(2).trim();

                LiteAlarmCategory la = new LiteAlarmCategory(alarmID, alarmName);

                allAlarmStates.add(la);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for AlarmCategoryLoader (" + allAlarmStates.size() + " loaded)");
            // temp code
        }
    }
}
