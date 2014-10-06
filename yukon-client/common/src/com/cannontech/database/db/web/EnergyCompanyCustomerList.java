package com.cannontech.database.db.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.DBPersistent;

public class EnergyCompanyCustomerList extends DBPersistent {
    public static final String tableName = "EnergyCompanyCustomerList";

    private static final String customerSql = "SELECT CustomerID FROM " + tableName + " WHERE EnergyCompanyID=?";

    @Override
    public void add() throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void delete() throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    public static long[] getCustomerIDs(long energyCompanyID, String dbAlias) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(dbAlias);

            pstmt = conn.prepareStatement(customerSql);
            pstmt.setLong(1, energyCompanyID);

            rset = pstmt.executeQuery();

            ArrayList<Long> idList = new ArrayList<>();
            while (rset.next()) {
                idList.add(new Long(rset.getLong(1)));
            }

            long[] retIDs = new long[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                retIDs[i] = idList.get(i).longValue();
            }

            return retIDs;
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);
            }
        }

        // An exception must have occured
        return new long[0];
    }

    @Override
    public void retrieve() throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update() throws SQLException {
        throw new RuntimeException("Not implemented");
    }
}
