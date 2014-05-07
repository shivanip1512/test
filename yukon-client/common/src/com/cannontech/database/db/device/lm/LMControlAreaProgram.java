package com.cannontech.database.db.device.lm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.NestedDBPersistent;

public class LMControlAreaProgram extends NestedDBPersistent implements DeviceListItem {
    private Integer deviceId = null;
    private Integer lmProgramDeviceId = 0;
    private Integer startPriority = 0;
    private Integer stopPriority = 0;

    public Integer getStartPriority() {
        return startPriority;
    }

    public void setStartPriority(Integer startPriority) {
        this.startPriority = startPriority;
    }

    public Integer getStopPriority() {
        return stopPriority;
    }

    public void setStopPriority(Integer stopPriority) {
        this.stopPriority = stopPriority;
    }

    private static final String SETTER_COLUMNS[] = { "StartPriority", "StopPriority" };
    private static final String CONSTRAINT_COLUMNS[] = { "DeviceID", "LMProgramDeviceID" };
    public static final String TABLE_NAME = "LMControlAreaProgram";

    @Override
    public void add() throws SQLException {
        Object addValues[] = { getDeviceID(), getLmProgramDeviceID(), getStartPriority(), getStopPriority() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        String values[] = { getDeviceID().toString(), getLmProgramDeviceID().toString() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, values);
    }

    /**
     * This method was created by Cannon Technologies Inc.
     */
    public static boolean deleteAllControlAreaProgramList(Integer ctrlAreaDeviceID, Connection conn) {
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + ctrlAreaDeviceID;

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection should not be (null)");
            }
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);// something is up
            }
        }

        return true;
    }

    public static final LMControlAreaProgram[] getAllControlAreaList(Integer ctrlAreaDeviceID, Connection conn)
            throws SQLException {
        List<LMControlAreaProgram> tmpList = new ArrayList<>(30);
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql = "select deviceId, lmProgramDeviceId, StartPriority, StopPriority"
                + " from " + TABLE_NAME + " where deviceId = ?";

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection should not be (null).");
            }
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, ctrlAreaDeviceID.intValue());

            rset = pstmt.executeQuery();

            while (rset.next()) {
                LMControlAreaProgram item = new LMControlAreaProgram();

                item.setDbConnection(conn);
                item.setDeviceID(rset.getInt("DeviceID"));
                item.setLmProgramDeviceID(rset.getInt("LMProgramDeviceID"));
                item.setStartPriority(rset.getInt("StartPriority"));
                item.setStopPriority(rset.getInt("StopPriority"));

                tmpList.add(item);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt);
        }

        LMControlAreaProgram retVal[] = new LMControlAreaProgram[tmpList.size()];
        tmpList.toArray(retVal);

        return retVal;
    }

    public static final Vector<Integer> getAllProgramsInControlAreas() {
        String sql = "select lmProgramDeviceId from " + TABLE_NAME;

        SqlStatement stmt = new SqlStatement(sql, CtiUtilities.getDatabaseAlias());

        try {
            stmt.execute();
            Vector<Integer> containedPrograms = new Vector<>(stmt.getRowCount());

            for (int i = 0; i < stmt.getRowCount(); i++) {
                Object[] row = stmt.getRow(i);
                containedPrograms.addElement(new Integer(((BigDecimal) row[0]).intValue()));
            }

            return containedPrograms;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final Vector<LMControlAreaProgram> getAllProgramsForAnArea(Integer ctrlAreaDeviceID, Connection conn) {
        Vector<LMControlAreaProgram> progList = new Vector<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "select deviceId, lmProgramDeviceId, StartPriority, StopPriority" + " from " + TABLE_NAME
                + " where deviceId = ?";

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection should not be (null).");
            }
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, ctrlAreaDeviceID.intValue());

            rset = pstmt.executeQuery();

            while (rset.next()) {
                LMControlAreaProgram item = new LMControlAreaProgram();

                item.setDbConnection(conn);
                item.setDeviceID(rset.getInt("DeviceID"));
                item.setLmProgramDeviceID(rset.getInt("LMProgramDeviceID"));
                item.setStartPriority(rset.getInt("StartPriority"));
                item.setStopPriority(rset.getInt("StopPriority"));

                progList.add(item);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt);
        }

        return progList;
    }

    @Override
    public Integer getDeviceID() {
        return deviceId;
    }

    public Integer getLmProgramDeviceID() {
        return lmProgramDeviceId;
    }

    @Override
    public void retrieve() throws SQLException {
        Object constraintValues[] = { getDeviceID(), getLmProgramDeviceID() };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setStartPriority((Integer) results[0]);
            setStopPriority((Integer) results[1]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    @Override
    public void setDeviceID(Integer newValue) {
        this.deviceId = newValue;
    }

    public void setLmProgramDeviceID(Integer newLmProgramDeviceID) {
        lmProgramDeviceId = newLmProgramDeviceID;
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] = { getStartPriority(), getStopPriority() };

        Object constraintValues[] = { getDeviceID(), getLmProgramDeviceID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
}
