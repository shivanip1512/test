package com.cannontech.amr.rfn.service.pointmapping;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;

public interface PointValueHandler {
    public PaoPointIdentifier getPaoPointIdentifier();
    public double convert(double rawValue);
}
