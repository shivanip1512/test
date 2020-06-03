package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;

public class PointAnalog implements DBPersistentConverter<com.cannontech.database.db.point.PointAnalog> {

    private Double deadband;
    private Double multiplier;
    private Double dataOffset;

    public Double getDeadband() {
        return deadband;
    }

    public void setDeadband(Double deadband) {
        this.deadband = deadband;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public Double getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(Double dataOffset) {
        this.dataOffset = dataOffset;
    }

    @Override
    public void buildModel(com.cannontech.database.db.point.PointAnalog pointAnalog) {
        setDataOffset(pointAnalog.getDataOffset());
        setDeadband(pointAnalog.getDeadband());
        setMultiplier(pointAnalog.getMultiplier());

    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.point.PointAnalog pointAnalog) {
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
