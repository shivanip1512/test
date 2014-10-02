package com.cannontech.database.db.esub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.esub.SubstationItem;

public class DCB extends DBPersistent implements SubstationItem {
    private static final String tableName = "DCB";

    private int id;
    private String name;
    private int substationID;
    private int statusID;

    // analog points
    private int mwID;
    private int mvarID;
    private int lastTripTime;
    private int previousTripTime1;
    private int previousTripTime2;
    private int previousTripTime3;

    // graph
    private int graphDefinitionID;

    // SQL
    private static final String substationDCBSql =
        "SELECT DCBID,Name,StatusID,MWID,MVARID,LastTripTime,PreviousTripTime1,PreviousTripTime2,PreviousTripTime3,GraphDefinitionID FROM "
            + tableName + " WHERE SubstationID = ?";

    public DCB() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void delete() throws java.sql.SQLException {
        delete(tableName, "DCBID", new Integer(getID()));
    }

    public static DCB[] getAllDCBs(int substationID, String dbAlias) {
        DCB[] retVal = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(dbAlias);
            pstmt = conn.prepareStatement(substationDCBSql);
            pstmt.setInt(1, substationID);

            rset = pstmt.executeQuery();

            List<DCB> dcbList = new ArrayList<>();
            while (rset.next()) {
                DCB dcb = new DCB();
                dcb.setSubstationID(substationID);

                dcb.setId(rset.getInt(1));
                dcb.setName(rset.getString(2));
                dcb.setStatusID(rset.getInt(3));
                dcb.setMwID(rset.getInt(4));
                dcb.setMvarID(rset.getInt(5));
                dcb.setLastTripTime(rset.getInt(6));
                dcb.setPreviousTripTime1(rset.getInt(7));
                dcb.setPreviousTripTime2(rset.getInt(8));
                dcb.setPreviousTripTime3(rset.getInt(9));
                dcb.setGraphDefinitionID(rset.getInt(10));

                dcbList.add(dcb);
            }

            retVal = new DCB[dcbList.size()];
            dcbList.toArray(retVal);

        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        return retVal;
    }

    public int getGraphDefinitionID() {
        return graphDefinitionID;
    }

    @Override
    public int getID() {
        return id;
    }

    public int getLastTripTime() {
        return lastTripTime;
    }

    public int getMvarID() {
        return mvarID;
    }

    public int getMwID() {
        return mwID;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getPreviousTripTime1() {
        return previousTripTime1;
    }

    public int getPreviousTripTime2() {
        return previousTripTime2;
    }

    public int getPreviousTripTime3() {
        return previousTripTime3;
    }

    public int getStatusID() {
        return statusID;
    }

    @Override
    public int getSubstationID() {
        return substationID;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        String[] selectColumns =
            { "Name", "SubstationID", "StatusID", "MWID", "MVARID", "LastTripTime", "PreviousTripTime1",
                "PreviousTripTime2", "PreviousTripTime3", "GraphDefinitionID" };

        String[] constraintColumns = { "DCBID" };
        Object[] constraintValues = { new Integer(getID()) };

        Object[] results = retrieve(selectColumns, tableName, constraintColumns, constraintValues);

        if (results.length == selectColumns.length) {
            setName((String) results[0]);
            setSubstationID(((Integer) results[1]).intValue());

            // The rest are possibly null
            Integer status = (Integer) results[2];
            Integer mw = (Integer) results[3];
            Integer mvar = (Integer) results[4];
            Integer lastTripTime = (Integer) results[5];
            Integer previousTripTime1 = (Integer) results[6];
            Integer previousTripTime2 = (Integer) results[7];
            Integer previousTripTime3 = (Integer) results[8];
            Integer gdef = (Integer) results[9];

            if (status != null) {
                setStatusID(statusID);
            }

            if (mw != null) {
                setMwID(mw.intValue());
            }

            if (mvar != null) {
                setMvarID(mvar.intValue());
            }

            if (lastTripTime != null) {
                setLastTripTime(lastTripTime.intValue());
            }

            if (previousTripTime1 != null) {
                setPreviousTripTime1(previousTripTime1.intValue());
            }

            if (previousTripTime2 != null) {
                setPreviousTripTime2(previousTripTime2.intValue());
            }

            if (previousTripTime3 != null) {
                setPreviousTripTime3(previousTripTime3.intValue());
            }

            if (gdef != null) {
                setGraphDefinitionID(gdef.intValue());
            }
        }

    }

    public void setGraphDefinitionID(int newGraphDefinitionID) {
        graphDefinitionID = newGraphDefinitionID;
    }

    public void setId(int newId) {
        id = newId;
    }

    public void setLastTripTime(int newLastTripTime) {
        lastTripTime = newLastTripTime;
    }

    public void setMvarID(int newMvarID) {
        mvarID = newMvarID;
    }

    public void setMwID(int newMwID) {
        mwID = newMwID;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setPreviousTripTime1(int newPreviousTripTime1) {
        previousTripTime1 = newPreviousTripTime1;
    }

    public void setPreviousTripTime2(int newPreviousTripTime2) {
        previousTripTime2 = newPreviousTripTime2;
    }

    public void setPreviousTripTime3(int newPreviousTripTime3) {
        previousTripTime3 = newPreviousTripTime3;
    }

    public void setStatusID(int newStatusID) {
        statusID = newStatusID;
    }

    public void setSubstationID(int newSubstationID) {
        substationID = newSubstationID;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void update() throws java.sql.SQLException {
        throw new RuntimeException("Not implemented");
    }
}
