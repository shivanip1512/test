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
import com.cannontech.database.data.lite.LiteSeasonSchedule;

public class SeasonScheduleLoader implements Runnable {
    private String databaseAlias = null;
    private ArrayList allSeasons = null;

    public SeasonScheduleLoader(ArrayList seasons, String dbAlias) {
        this.allSeasons = seasons;
        this.databaseAlias = dbAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code
        String sqlString = "SELECT SCHEDULEID,SCHEDULENAME FROM SEASONSCHEDULE WHERE SCHEDULEID >= 0";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int seasonID = rset.getInt(1);
                String seasonName = rset.getString(2).trim();

                LiteSeasonSchedule ss = new LiteSeasonSchedule(seasonID);

                ss.setScheduleName(seasonName);
                if (seasonID != 0) {
                    allSeasons.add(ss);
                }
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for SeasonScheduleLoader (" + allSeasons.size() + " loaded)");
            // temp code
        }
    }
}
