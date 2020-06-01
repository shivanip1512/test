package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;

public class PointUnit implements DBPersistentConverter<com.cannontech.database.db.point.PointUnit>{

    private Integer uomId;
    private Integer decimalPlaces;
    private Double highReasonabilityLimit;
    private Double lowReasonabilityLimit;
    private Integer meterDials;

    public Integer getUomId() {
        return uomId;
    }

    public void setUomId(Integer uomId) {
        this.uomId = uomId;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Double getHighReasonabilityLimit() {
        return highReasonabilityLimit;
    }

    public void setHighReasonabilityLimit(Double highReasonabilityLimit) {
        this.highReasonabilityLimit = highReasonabilityLimit;
    }

    public Double getLowReasonabilityLimit() {
        return lowReasonabilityLimit;
    }

    public void setLowReasonabilityLimit(Double lowReasonabilityLimit) {
        this.lowReasonabilityLimit = lowReasonabilityLimit;
    }

    public Integer getMeterDials() {
        return meterDials;
    }

    public void setMeterDials(Integer meterDials) {
        this.meterDials = meterDials;
    }

    @Override
    public void buildModel(com.cannontech.database.db.point.PointUnit pointUnit) {
      setUomId(pointUnit.getUomID());
      setMeterDials(pointUnit.getMeterDials());
      setHighReasonabilityLimit(pointUnit.getHighReasonabilityLimit());
      setLowReasonabilityLimit(pointUnit.getLowReasonabilityLimit());
      setDecimalPlaces(pointUnit.getDecimalPlaces());
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.point.PointUnit pointUnit) {
        if (getUomId() != null) {
            pointUnit.setUomID(getUomId());
        }
        if (getDecimalPlaces() != null) {
            pointUnit.setDecimalPlaces(getDecimalPlaces());
        }
        if (getMeterDials() != null) {
            pointUnit.setMeterDials(getMeterDials());
        }
        if (getHighReasonabilityLimit() != null) {
            pointUnit.setHighReasonabilityLimit(getHighReasonabilityLimit());
        }
        if (getLowReasonabilityLimit() != null) {
            pointUnit.setLowReasonabilityLimit(getLowReasonabilityLimit());
        }
    }
}