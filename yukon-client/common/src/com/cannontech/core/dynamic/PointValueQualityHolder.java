package com.cannontech.core.dynamic;

import java.io.Serializable;

import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.point.PointType;


public interface PointValueQualityHolder extends PointValueHolder, Serializable {
    public PointQuality getPointQuality();
    /**PointValueHolder.getType returns the raw int equivalent of this.*/
    public PointType getPointType();
}
