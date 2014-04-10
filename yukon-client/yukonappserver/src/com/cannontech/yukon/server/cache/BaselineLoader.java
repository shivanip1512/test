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
import com.cannontech.database.data.lite.LiteBaseline;

public class BaselineLoader implements Runnable {
    private String databaseAlias = null;
    private ArrayList allBaselines = null;

    public BaselineLoader(ArrayList baselines, String dbAlias) {
        this.allBaselines = baselines;
        this.databaseAlias = dbAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code
        String sqlString = "SELECT BASELINEID, BASELINENAME FROM BASELINE WHERE BASELINEID >= 0";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int baselineID = rset.getInt(1);
                String baselineName = rset.getString(2).trim();

                LiteBaseline basil = new LiteBaseline(baselineID);

                basil.setBaselineName(baselineName);

                allBaselines.add(basil);
            }

        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for BaselineLoader (" + allBaselines.size() + " loaded)");
            // temp code
        }
    }
}
