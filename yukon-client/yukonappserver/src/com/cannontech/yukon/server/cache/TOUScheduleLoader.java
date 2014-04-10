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
import com.cannontech.database.data.lite.LiteTOUSchedule;

public class TOUScheduleLoader implements Runnable {
    private String databaseAlias = null;
    private ArrayList allTOUSchedules = null;

    public TOUScheduleLoader(ArrayList touSchedules, String dbAlias) {
        this.allTOUSchedules = touSchedules;
        this.databaseAlias = dbAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code
        String sqlString =
            "SELECT TOUSCHEDULEID,TOUSCHEDULENAME, TOUDEFAULTRATE FROM TOUSCHEDULE WHERE TOUSCHEDULEID >= 0 ORDER BY TOUSCHEDULENAME";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int scheduleID = rset.getInt(1);
                String scheduleName = rset.getString(2).trim();
                String defaultRate = rset.getString(3).trim();

                LiteTOUSchedule tou = new LiteTOUSchedule(scheduleID);

                tou.setScheduleName(scheduleName);
                tou.setDefaultRate(defaultRate);

                if (scheduleID != 0) {
                    allTOUSchedules.add(tou);
                }
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for TOUScheduleLoader (" + allTOUSchedules.size() + " loaded)");
            // temp code
        }
    }
}