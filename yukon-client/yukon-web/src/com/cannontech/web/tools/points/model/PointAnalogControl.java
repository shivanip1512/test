package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.point.AnalogControlType;

public class PointAnalogControl implements DBPersistentConverter<com.cannontech.database.db.point.PointAnalogControl> {

    private AnalogControlType controlType;
    private Integer controlOffset;
    private Boolean controlInhibited;

    public AnalogControlType getControlType() {
        return controlType;
    }

    public void setControlType(AnalogControlType controlType) {
        this.controlType = controlType;
    }

    public Integer getControlOffset() {
        return controlOffset;
    }

    public void setControlOffset(Integer controlOffset) {
        this.controlOffset = controlOffset;
    }



    public Boolean getControlInhibited() {
        return controlInhibited;
    }

    public void setControlInhibited(Boolean controlInhibited) {
        this.controlInhibited = controlInhibited;
    }

    @Override
    public void buildModel(com.cannontech.database.db.point.PointAnalogControl analogControl) {
        setControlInhibited(analogControl.isControlInhibited());
        setControlType(AnalogControlType.getAnalogControlTypeValue(analogControl.getControlType()));
        setControlOffset(analogControl.getControlOffset());
    }

    @Override
    public void buildDBPersistent(com.cannontech.database.db.point.PointAnalogControl analogControl) {
        if (getControlInhibited() != null) {
            analogControl.setControlInhibited(getControlInhibited());
        }
        if (getControlOffset() != null) {
            analogControl.setControlOffset(getControlOffset());
        }
        if (getControlType() != null) {
            analogControl.setControlType(getControlType().getControlName());
        }
    }
}
