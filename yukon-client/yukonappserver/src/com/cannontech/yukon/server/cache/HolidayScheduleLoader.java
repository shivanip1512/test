package com.cannontech.yukon.server.cache;

import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteHolidaySchedule;

public class HolidayScheduleLoader implements Runnable {
    private String databaseAlias = null;
    private List<LiteHolidaySchedule> allHolidaySchedules = null;

    public HolidayScheduleLoader(List<LiteHolidaySchedule> allHolidaySchedules, String databaseAlias) {
        this.allHolidaySchedules = allHolidaySchedules;
        this.databaseAlias = databaseAlias;
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
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);
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
