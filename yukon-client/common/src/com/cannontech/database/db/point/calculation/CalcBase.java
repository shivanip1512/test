package com.cannontech.database.db.point.calculation;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class CalcBase extends DBPersistent {

    private Integer pointID = null;
    private String updateType = null;
    private Integer periodicRate = null;
    private char calculateQuality = 'N';

    private static final String tableName = "CalcBase";

    public CalcBase() {
        this(null);
    }

    public CalcBase(Integer pointID) {
        this(pointID, null, null);
    }

    public CalcBase(Integer pointID, String updateType, Integer periodicRate) {

        this(pointID, updateType, periodicRate, 'N');
    }

    public CalcBase(Integer pointID, String updateType, Integer periodicRate, char calcQual) {
        
        setPointID(pointID);
        setUpdateType(updateType);
        setPeriodicRate(periodicRate);
        setCalculateQuality(calcQual);
    }

    public void add() throws SQLException {
        Object addValues[] = { getPointID(), getUpdateType(), getPeriodicRate(), getCalculateQuality() };

        add(tableName, addValues);
    }

    public void delete() throws SQLException {
        delete(tableName, "POINTID", getPointID());
    }

    public Integer getPeriodicRate() {
        return periodicRate;
    }

    public Integer getPointID() {
        return pointID;
    }

    public String getUpdateType() {
        return updateType;
    }

    public char getCalculateQuality() {
        return calculateQuality;
    }

    public void retrieve() throws SQLException {
        String selectColumns[] = { "UPDATETYPE", "PERIODICRATE", "QUALITYFLAG" };

        String constraintColumns[] = { "POINTID" };
        Object constraintValues[] = { getPointID() };

        Object results[] = retrieve(selectColumns, tableName, constraintColumns, constraintValues);

        if (results.length == selectColumns.length) {
            setUpdateType((String) results[0]);
            setPeriodicRate((Integer) results[1]);
            setCalculateQuality((String) results[2]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setPeriodicRate(Integer newValue) {
        this.periodicRate = newValue;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void setUpdateType(String newValue) {
        this.updateType = newValue;
    }

    public void setCalculateQuality(char newValue) {
        this.calculateQuality = newValue;
    }

    public void setCalculateQuality(String newValue) {
        this.calculateQuality = newValue.charAt(0);
    }

    public void update() throws SQLException {
        String setColumns[] = { "UPDATETYPE", "PERIODICRATE", "QUALITYFLAG" };
        Object setValues[] = { getUpdateType(), getPeriodicRate(), getCalculateQuality() };

        String constraintColumns[] = { "POINTID" };
        Object constraintValues[] = { getPointID() };

        update(tableName, setColumns, setValues, constraintColumns, constraintValues);
    }
}
