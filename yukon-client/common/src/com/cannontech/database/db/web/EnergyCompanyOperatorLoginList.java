package com.cannontech.database.db.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

public class EnergyCompanyOperatorLoginList extends DBPersistent {
    public static final String tableName = "EnergyCompanyOperatorLoginList";

    private Integer energyCompanyID = null;
    private Integer operatorLoginID = null;

    private static final String energyCompanySql = "SELECT EnergyCompanyID FROM " + tableName
        + " WHERE OperatorLoginID=?";

    public EnergyCompanyOperatorLoginList() {
        super();
    }

    public EnergyCompanyOperatorLoginList(Integer ecID, Integer oplID) {
        super();

        energyCompanyID = ecID;
        operatorLoginID = oplID;

    }

    @Override
    public void add() throws SQLException {
        Object[] addValues = { getEnergyCompanyID(), getOperatorLoginID() };

        add(tableName, addValues);
    }

    public Integer getEnergyCompanyID() {
        return energyCompanyID;
    }

    public Integer getOperatorLoginID() {
        return operatorLoginID;
    }

    @Override
    public void delete() throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    public static long getEnergyCompanyID(long operatorLoginID) {
        return getEnergyCompanyID(operatorLoginID, "yukon");
    }

    private static long getEnergyCompanyID(long operatorLoginID, String dbAlias) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(dbAlias);

            pstmt = conn.prepareStatement(energyCompanySql);
            pstmt.setLong(1, operatorLoginID);

            rset = pstmt.executeQuery();

            if (rset.next()) {
                return rset.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        // An exception must have occured
        return -1;
    }

    @Override
    public void retrieve() throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void update() throws SQLException {
        Object[] setValues = { getEnergyCompanyID() };
        String[] setColumns = { "EnergyCompanyID" };

        String[] constraintColumns = { "OperatorLoginID" };
        Object[] constraintValues = { getOperatorLoginID() };

        update(tableName, setColumns, setValues, constraintColumns, constraintValues);
    }

    public void setOperatorLoginID(Integer newID) {
        operatorLoginID = newID;
    }

}