package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.point.PointLimit;

public class PointLimitModel implements DBPersistentConverter<PointLimit> {

    private Integer limitNumber;
    private Double highLimit;
    private Double lowLimit;
    private Integer limitDuration;

    public Integer getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(Integer limitNumber) {
        this.limitNumber = limitNumber;
    }

    public Double getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(Double highLimit) {
        this.highLimit = highLimit;
    }

    public Double getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(Double lowLimit) {
        this.lowLimit = lowLimit;
    }

    public Integer getLimitDuration() {
        return limitDuration;
    }

    public void setLimitDuration(Integer limitDuration) {
        this.limitDuration = limitDuration;
    }

    @Override
    public void buildModel(PointLimit pointLimit) {
        setLimitNumber(pointLimit.getLimitNumber());
        setLimitDuration(pointLimit.getLimitDuration());
        setLowLimit(pointLimit.getLowLimit());
        setHighLimit(pointLimit.getHighLimit());
    }

    @Override
    public void buildDBPersistent(PointLimit pointLimit) {

        if (getLimitNumber() != null) {
            pointLimit.setLimitNumber(getLimitNumber());
        }

        if (getLimitDuration() != null) {
            pointLimit.setLimitDuration(getLimitDuration());
        }

        if (getLowLimit() != null) {
            pointLimit.setLowLimit(getLowLimit());
        }

        if (getHighLimit() != null) {
            pointLimit.setHighLimit(getHighLimit());
        }
    }

}