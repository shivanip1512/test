package com.cannontech.database.db.point;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

public class PointLimit extends DBPersistent {
    
    private Integer pointID = null;
    private Integer limitNumber = 1;
    private Double highLimit = 0.0;
    private Double lowLimit = 0.0;
    private Integer limitDuration = 0;

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID", "LimitNumber" };

    public static final String SETTER_COLUMNS[] = { "HighLimit", "LowLimit", "LimitDuration" };

    public static final String TABLE_NAME = "PointLimits";

    public PointLimit() {}

    public PointLimit(Integer pointID, Integer limitNumber, Double highLimit, Double lowLimit, Integer limitDuration) {
        super();
        
        setPointID(pointID);
        setLimitNumber(limitNumber);
        setHighLimit(highLimit);
        setLowLimit(lowLimit);
        setLimitDuration(limitDuration);
    }

    public void add() throws SQLException {
        Object addValues[] = { getPointID(), getLimitNumber(), getHighLimit(), getLowLimit(), getLimitDuration() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        Object constraintValues[] = { getPointID(), getLimitNumber() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
    }

    public static boolean deletePointLimits(Integer pointID, java.sql.Connection conn) {
        SqlStatement stmt =new SqlStatement("DELETE FROM " + TABLE_NAME + 
                                            " WHERE PointID=" + pointID, conn);
        try {
            stmt.execute();
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public Double getHighLimit() {
        return highLimit;
    }

    public Integer getLimitDuration() {
        return limitDuration;
    }

    public Integer getLimitNumber() {
        return limitNumber;
    }

    public Double getLowLimit() {
        return lowLimit;
    }

    public Integer getPointID() {
        return pointID;
    }

    public final static PointLimit[] getPointLimits(Integer pointID, String databaseAlias) {
        List<PointLimit> tmpList = new ArrayList<>();
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        String sql = "SELECT LimitNumber, HighLimit, LowLimit, LimitDuration " + 
                     " FROM " + TABLE_NAME + 
                     " WHERE PointID = ?";

        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                pstmt.setInt(1, pointID.intValue());

                rset = pstmt.executeQuery();

                while (rset.next()) {
                    tmpList.add(new PointLimit(pointID, 
                                               rset.getInt("LimitNumber"), 
                                               rset.getDouble("HighLimit"),
                                               rset.getDouble("LowLimit"),
                                               rset.getInt("LimitDuration")));
                }

            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        PointLimit retVal[] = new PointLimit[tmpList.size()];
        tmpList.toArray(retVal);

        return retVal;
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getPointID(), getLimitNumber() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setHighLimit((Double) results[0]);
            setLowLimit((Double) results[1]);
            setLimitDuration((Integer) results[2]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
    }

    public void setHighLimit(Double newValue) {
        this.highLimit = newValue;
    }

    public void setLimitDuration(Integer newLimitDuration) {
        limitDuration = newLimitDuration;
    }

    public void setLimitNumber(Integer newValue) {
        this.limitNumber = newValue;
    }

    public void setLowLimit(Double newValue) {
        this.lowLimit = newValue;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void update() throws SQLException {
        Object setValues[] = { getHighLimit(), getLowLimit(), getLimitDuration() };

        Object constraintValues[] = { getPointID(), getLimitNumber() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
    
}