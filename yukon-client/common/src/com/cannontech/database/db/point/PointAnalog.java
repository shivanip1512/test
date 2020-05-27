package com.cannontech.database.db.point;

import java.sql.SQLException;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.DBPersistent;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PointAnalog extends DBPersistent implements DBPersistentConverter<PointAnalog> {
    @JsonIgnore
    private Integer pointID;
    private Double deadband;
    private Double multiplier;
    private Double dataOffset;

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

    public static final String VALUE_COLUMNS[] = { "DEADBAND", "MULTIPLIER", "DATAOFFSET" };

    public static final String TABLE_NAME = "PointAnalog";

    public PointAnalog() {
        super();
    }

    public PointAnalog(Integer pointID, Double deadband, Double multiplier, Double dataOffset) {
        super();
        setPointID(pointID);
        setDeadband(deadband);
        setMultiplier(multiplier);
        setDataOffset(dataOffset);
    }

    @Override
    public void add() throws SQLException {

        Object addValues[] = { getPointID(), getDeadband(), getMultiplier(), getDataOffset() };
        add(PointAnalog.TABLE_NAME, addValues);
    }

    @Override
    public void delete() throws java.sql.SQLException {

        delete(PointAnalog.TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID());
    }

    public Double getDataOffset() {
        return dataOffset;
    }

    public Double getDeadband() {
        return deadband;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public Integer getPointID() {
        return pointID;
    }

    @Override
    public void retrieve() throws SQLException {

        Object constraintValues[] = { getPointID() };

        Object[] results = retrieve(VALUE_COLUMNS, PointAnalog.TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == VALUE_COLUMNS.length) {
            setDeadband((Double) results[0]);
            setMultiplier((Double) results[1]);
            setDataOffset((Double) results[2]);
        } else
            throw new Error(getClass() + " - Incorrect Number of results retrieved");

    }

    public void setDataOffset(Double newValue) {
        this.dataOffset = newValue;
    }

    public void setDeadband(Double newValue) {
        this.deadband = newValue;
    }

    public void setMultiplier(Double newValue) {
        this.multiplier = newValue;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    @Override
    public void update() throws SQLException {

        Object setValues[] = { getDeadband(), getMultiplier(), getDataOffset() };

        Object constraintValues[] = { getPointID() };

        update(PointAnalog.TABLE_NAME, VALUE_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    @Override
    public void buildModel(PointAnalog pointAnalog) {
        setDataOffset(pointAnalog.getDataOffset());
        setDeadband(pointAnalog.getDeadband());
        setMultiplier(pointAnalog.getMultiplier());

    }

    @Override
    public void buildDBPersistent(PointAnalog pointAnalog) {
        if (getDataOffset() != null) {
            pointAnalog.setDataOffset(getDataOffset());
        }
        if (getDeadband() != null) {
            pointAnalog.setDeadband(getDeadband());
        }
        if (getMultiplier() != null) {
            pointAnalog.setMultiplier(getMultiplier());
        }

    }

}
