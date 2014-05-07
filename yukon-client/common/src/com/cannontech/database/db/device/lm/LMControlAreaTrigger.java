package com.cannontech.database.db.device.lm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class LMControlAreaTrigger extends NestedDBPersistent {
    private Integer deviceID = null;
    private Integer triggerNumber = 0;
    private String triggerType = IlmDefines.TYPE_THRESHOLD;
    private Integer pointID = PointTypes.SYS_PID_SYSTEM;
    private Integer normalState = 0;
    private Double threshold = null;
    private String projectionType = CtiUtilities.STRING_NONE;
    private Integer projectionPoints = 0;
    private Integer projectAheadDuration = 0;
    private Integer thresholdKickPercent = 0;
    private Double minRestoreOffset = 0.0;
    private Integer peakPointID = 0;
    private Integer triggerID = null;
    private Integer thresholdPointID = 0;

    public static final String SETTER_COLUMNS[] = { "TriggerNumber", "TriggerType", "PointID", "NormalState",
        "Threshold", "ProjectionType", "ProjectionPoints", "ProjectAheadDuration", "ThresholdKickPercent",
        "MinRestoreOffset", "PeakPointID", "DeviceID", "ThresholdPointID" };

    public static final String CONSTRAINT_COLUMNS[] = { "TriggerID" };

    public static final String TABLE_NAME = "LMControlAreaTrigger";

    private transient LiteYukonPAObject liteDev = null;
    private transient LitePoint litePt = null;

    @Override
    public void add() throws SQLException {
        if (getTriggerID() == null) {
            setTriggerID(new Integer(getNextTriggerID(getDbConnection())));
        }

        Object addValues[] =
            { getDeviceID(), getTriggerNumber(), getTriggerType(), getPointID(), getNormalState(), getThreshold(),
                getProjectionType(), getProjectionPoints(), getProjectAheadDuration(), getThresholdKickPercent(),
                getMinRestoreOffset(), getPeakPointID(), getTriggerID(), getThresholdPointID() };

        add(TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws SQLException {
        delete("DynamicLMControlAreaTrigger", "DeviceID", getDeviceID());
        delete(TABLE_NAME, "TriggerID", getTriggerID());
    }

    public static boolean deleteAllControlAreaTriggers(Integer ctrlAreaDeviceID, Connection conn) {
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

    public static final LMControlAreaTrigger[] getAllControlAreaTriggers(Integer ctrlAreaDeviceID)
            throws SQLException {
        return getAllControlAreaTriggers(ctrlAreaDeviceID, CtiUtilities.getDatabaseAlias());
    }

    public static final LMControlAreaTrigger[] getAllControlAreaTriggers(Integer ctrlAreaDeviceID,
            String databaseAlias) throws SQLException {
        List<LMControlAreaTrigger> tmpList = new ArrayList<>(30);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "SELECT MinRestoreOffset,NormalState,PeakPointID,PointID,ProjectAheadDuration, "
                + "ProjectionPoints,ProjectionType,Threshold,ThresholdKickPercent, "
                + "TriggerNumber,TriggerType,DeviceID,TriggerID,ThresholdPointID " + "FROM " + TABLE_NAME
                + " WHERE DEVICEID= ?";

        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            }
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, ctrlAreaDeviceID.intValue());

            rset = pstmt.executeQuery();

            while (rset.next()) {
                LMControlAreaTrigger item = new LMControlAreaTrigger();

                item.setDbConnection(conn);
                item.setMinRestoreOffset(new Double(rset.getDouble("MinRestoreOffset")));
                item.setNormalState(new Integer(rset.getInt("NormalState")));
                item.setPeakPointID(new Integer(rset.getInt("PeakPointID")));
                item.setPointID(new Integer(rset.getInt("PointID")));
                item.setProjectAheadDuration(new Integer(rset.getInt("ProjectAheadDuration")));
                item.setProjectionPoints(new Integer(rset.getInt("ProjectionPoints")));
                item.setProjectionType(rset.getString("ProjectionType"));
                item.setThreshold(new Double(rset.getDouble("Threshold")));
                item.setThresholdKickPercent(new Integer(rset.getInt("ThresholdKickPercent")));
                item.setTriggerNumber(new Integer(rset.getInt("TriggerNumber")));
                item.setTriggerType(rset.getString("TriggerType"));
                item.setDeviceID(new Integer(rset.getInt("DeviceID")));
                item.setTriggerID(new Integer(rset.getInt("TriggerID")));
                item.setThresholdPointID(new Integer(rset.getInt("ThresholdPointID")));

                tmpList.add(item);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        LMControlAreaTrigger retVal[] = new LMControlAreaTrigger[tmpList.size()];
        tmpList.toArray(retVal);

        return retVal;
    }

    public static final Vector<LMControlAreaTrigger> getAllTriggersForAnArea(Integer ctrlAreaDeviceID, Connection conn) {
        Vector<LMControlAreaTrigger> tmpList = new Vector<>(5);
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "SELECT MinRestoreOffset,NormalState,PeakPointID,PointID,ProjectAheadDuration, "
                + "ProjectionPoints,ProjectionType,Threshold,ThresholdKickPercent, "
                + "TriggerNumber,TriggerType,DeviceID,TriggerID,ThresholdPointID " + "FROM " + TABLE_NAME
                + " WHERE DEVICEID= ?";

        try {
            if (conn == null) {
                throw new IllegalArgumentException("Received a (null) database connection");
            }
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, ctrlAreaDeviceID.intValue());

            rset = pstmt.executeQuery();

            while (rset.next()) {
                LMControlAreaTrigger item = new LMControlAreaTrigger();

                item.setDbConnection(conn);
                item.setMinRestoreOffset(new Double(rset.getDouble("MinRestoreOffset")));
                item.setNormalState(new Integer(rset.getInt("NormalState")));
                item.setPeakPointID(new Integer(rset.getInt("PeakPointID")));
                item.setPointID(new Integer(rset.getInt("PointID")));
                item.setProjectAheadDuration(new Integer(rset.getInt("ProjectAheadDuration")));
                item.setProjectionPoints(new Integer(rset.getInt("ProjectionPoints")));
                item.setProjectionType(rset.getString("ProjectionType"));
                item.setThreshold(new Double(rset.getDouble("Threshold")));
                item.setThresholdKickPercent(new Integer(rset.getInt("ThresholdKickPercent")));
                item.setTriggerNumber(new Integer(rset.getInt("TriggerNumber")));
                item.setTriggerType(rset.getString("TriggerType"));
                item.setDeviceID(new Integer(rset.getInt("DeviceID")));
                item.setTriggerID(new Integer(rset.getInt("TriggerID")));
                item.setThresholdPointID(new Integer(rset.getInt("ThresholdPointID")));

                tmpList.add(item);
            }
        }

        catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt);
        }

        return tmpList;
    }

    public Integer getDeviceID() {
        return deviceID;
    }

    public Double getMinRestoreOffset() {
        return minRestoreOffset;
    }

    public Integer getNormalState() {
        return normalState;
    }

    public Integer getPeakPointID() {
        return peakPointID;
    }

    public Integer getPointID() {
        return pointID;
    }

    public Integer getProjectAheadDuration() {
        return projectAheadDuration;
    }

    public Integer getProjectionPoints() {
        return projectionPoints;
    }

    public String getProjectionType() {
        return projectionType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public Integer getThresholdKickPercent() {
        return thresholdKickPercent;
    }

    public Integer getTriggerNumber() {
        return triggerNumber;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public Integer getTriggerID() {
        return triggerID;
    }

    public static final int getNextTriggerID(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql = "select MAX(TriggerID) + 1 from " + TABLE_NAME;

        try {
            if (conn == null) {
                throw new IllegalArgumentException("Received a (null) database connection");
            }

            pstmt = conn.prepareStatement(sql.toString());

            rset = pstmt.executeQuery();

            while (rset.next()) {
                return rset.getInt(1);
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2); // something is up
            }
        }

        throw new SQLException("Unable to retrieve the next TriggerID");
    }

    @Override
    public void retrieve() throws SQLException {
        Object constraintValues[] = { getTriggerID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setTriggerNumber((Integer) results[0]);
            setTriggerType((String) results[1]);
            setPointID((Integer) results[2]);
            setNormalState((Integer) results[3]);
            setThreshold((Double) results[4]);
            setProjectionType((String) results[5]);
            setProjectionPoints((Integer) results[6]);
            setProjectAheadDuration((Integer) results[7]);
            setThresholdKickPercent((Integer) results[8]);
            setMinRestoreOffset((Double) results[9]);
            setPeakPointID((Integer) results[10]);
            setDeviceID((Integer) results[11]);
            setThresholdPointID((Integer) results[12]);
        } else {
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
        }
    }

    public void setDeviceID(Integer newDeviceID) {
        deviceID = newDeviceID;
    }

    public void setMinRestoreOffset(Double newMinRestoreOffset) {
        minRestoreOffset = newMinRestoreOffset;
    }

    public void setNormalState(Integer newNormalState) {
        normalState = newNormalState;
    }

    public void setPeakPointID(Integer newPeakPointID) {
        peakPointID = newPeakPointID;
    }

    public void setPointID(Integer newPointID) {
        pointID = newPointID;
    }

    public void setProjectAheadDuration(Integer newProjectAheadDuration) {
        projectAheadDuration = newProjectAheadDuration;
    }

    public void setProjectionPoints(Integer newProjectionPoints) {
        projectionPoints = newProjectionPoints;
    }

    public void setProjectionType(String newProjectionType) {
        projectionType = newProjectionType;
    }

    public void setThreshold(Double newThreshold) {
        threshold = newThreshold;
    }

    public void setThresholdKickPercent(Integer newThresholdKickPercent) {
        thresholdKickPercent = newThresholdKickPercent;
    }

    public void setTriggerNumber(Integer newTriggerNumber) {
        triggerNumber = newTriggerNumber;
    }

    public void setTriggerType(String newTriggerType) {
        triggerType = newTriggerType;
    }

    public void setTriggerID(Integer newTriggerID) {
        triggerID = newTriggerID;
    }

    private LitePoint getLtPoint() {
        if (getPointID().intValue() == PointTypes.SYS_PID_SYSTEM) {
            litePt = null;
        } else if (litePt == null) {
            litePt = YukonSpringHook.getBean(PointDao.class).getLitePoint(getPointID().intValue());
        }

        return litePt;
    }

    public void clearNames() {
        litePt = null;
        liteDev = null;
    }

    private LiteYukonPAObject getLtPao() {
        if (getPointID().intValue() == PointTypes.SYS_PID_SYSTEM) {
            liteDev = null;
        } else if (liteDev == null) {
            liteDev = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(getLtPoint().getPaobjectID());
        }

        return liteDev;
    }

    @Override
    public String toString() {
        return getTriggerType()
            + (getLtPoint() != null ? " (" + getLtPao().getPaoName() + " / " + getLtPoint().getPointName() + ")"
                : " (PointID: " + getPointID() + ")");
    }

    @Override
    public void update() throws SQLException {
        Object setValues[] =
            { getTriggerNumber(), getTriggerType(), getPointID(), getNormalState(), getThreshold(),
                getProjectionType(), getProjectionPoints(), getProjectAheadDuration(), getThresholdKickPercent(),
                getMinRestoreOffset(), getPeakPointID(), getDeviceID(), getThresholdPointID() };

        Object constraintValues[] = { getTriggerID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public void setThresholdPointID(Integer thresholdPointID) {
        this.thresholdPointID = thresholdPointID;
    }

    public Integer getThresholdPointID() {
        return thresholdPointID;
    }
}
