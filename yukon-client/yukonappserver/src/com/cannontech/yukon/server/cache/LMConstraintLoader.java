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
import com.cannontech.database.data.lite.LiteLMConstraint;

public class LMConstraintLoader implements Runnable {
    private String databaseAlias = null;
    private ArrayList allLMConstraints = null;

    public LMConstraintLoader(ArrayList constraints, String dbAlias) {
        this.allLMConstraints = constraints;
        this.databaseAlias = dbAlias;
    }

    @Override
    public void run() {
        // temp code
        Date timerStart = new Date();
        Date timerStop = null;
        // temp code
        String sqlString = "SELECT CONSTRAINTID, CONSTRAINTNAME FROM LMPROGRAMCONSTRAINTS WHERE CONSTRAINTID >= 0";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(this.databaseAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int constraintID = rset.getInt(1);
                String constraintName = rset.getString(2).trim();

                LiteLMConstraint strain = new LiteLMConstraint(constraintID, constraintName);

                strain.setConstraintName(constraintName);

                allLMConstraints.add(strain);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
            // temp code
            timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001
                + " Secs for LMConstraintLoader (" + allLMConstraints.size() + " loaded)");
            // temp code
        }
    }
}
