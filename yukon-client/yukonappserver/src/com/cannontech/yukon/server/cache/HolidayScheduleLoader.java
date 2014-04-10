package com.cannontech.yukon.server.cache;

import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteHolidaySchedule;

public class HolidayScheduleLoader implements Runnable {
    private String databaseAlias = null;
    private ArrayList allHolidaySchedules = null;

    public HolidayScheduleLoader(ArrayList holidaySchedules, String dbAlias) {
        this.allHolidaySchedules = holidaySchedules;
        this.databaseAlias = dbAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code
        String sqlString =
            "SELECT HOLIDAYSCHEDULEID,HOLIDAYSCHEDULENAME FROM HOLIDAYSCHEDULE WHERE HOLIDAYSCHEDULEID >= 0";

        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;
        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int holidayScheduleID = rset.getInt(1);
                String holidayScheduleName = rset.getString(2).trim();

                LiteHolidaySchedule hs = new LiteHolidaySchedule(holidayScheduleID);

                hs.setHolidayScheduleName(holidayScheduleName);

                allHolidaySchedules.add(hs);
            }
        } catch (java.sql.SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for HolidayScheduleLoader (" + allHolidaySchedules.size() + " loaded)");

            // temp code
        }
    }
}
