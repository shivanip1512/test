package com.cannontech.dr.ecobee.service;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;

public interface EcobeePointUpdateService {
    /**
     * Updates all ecobee points for pao with ecobee deviceReadings.
     */
    Set<PointValueHolder> updatePointData(PaoIdentifier paoIdentifier, EcobeeDeviceReadings deviceReadings);
}