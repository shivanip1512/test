package com.cannontech.database.db.point;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class PointAccumulator extends DBPersistent {
    private Integer pointID = null;
    private Double multiplier = 1.0;
    private Double dataOffset = 0.0;

    private static final String tableName = "PointAccumulator";

    public PointAccumulator() {
        super();
    }

    public PointAccumulator(Integer newPointID, Double newMultiplier, Double newDataOffset) {
        super();
        setPointID(newPointID);
        setMultiplier(newMultiplier);
        setDataOffset(newDataOffset);
    }

    public void add() throws SQLException {

        Object addValues[] = { getPointID(), getMultiplier(), getDataOffset() };

        add(tableName, addValues);
    }

    public void delete() throws SQLException {
        delete(tableName, "POINTID", getPointID());
    }

    public Double getDataOffset() {
        return dataOffset;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public Integer getPointID() {
        return pointID;
    }

    public void retrieve() throws SQLException {
        String selectColumns[] = { "MULTIPLIER", "DATAOFFSET" };

        String constraintColumns[] = { "POINTID" };
        Object constraintValues[] = { getPointID() };

        Object results[] = retrieve(selectColumns, tableName, constraintColumns, constraintValues);

        if (results.length == selectColumns.length) {
            setMultiplier((Double) results[0]);
            setDataOffset((Double) results[1]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");
    }

    public void setDataOffset(Double newValue) {
        this.dataOffset = newValue;
    }

    public void setMultiplier(Double newValue) {
        this.multiplier = newValue;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void update() throws SQLException {

        String setColumns[] = { "MULTIPLIER", "DATAOFFSET" };
        Object setValues[] = { getMultiplier(), getDataOffset() };

        String constraintColumns[] = { "POINTID" };
        Object constraintValues[] = { getPointID() };

        update(tableName, setColumns, setValues, constraintColumns, constraintValues);
    }
}
