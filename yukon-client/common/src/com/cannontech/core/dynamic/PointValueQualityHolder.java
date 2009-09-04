package com.cannontech.core.dynamic;

import java.io.Serializable;

import com.cannontech.common.point.PointQuality;


public interface PointValueQualityHolder extends PointValueHolder, Serializable {
    public PointQuality getPointQuality();
}
