package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.point.PointControl;

public class PointControlModel<T extends PointControl> implements DBPersistentConverter<T> {
    private Integer controlOffset;
    private Boolean controlInhibited;

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
    public void buildModel(PointControl pointControl) {
        setControlInhibited(pointControl.isControlInhibited());
        setControlOffset(pointControl.getControlOffset());
    }

    @Override
    public void buildDBPersistent(PointControl pointControl) {
        if (getControlInhibited() != null) {
            pointControl.setControlInhibited(getControlInhibited());
        }
        if (getControlOffset() != null) {
            pointControl.setControlOffset(getControlOffset());
        }
    }

}
