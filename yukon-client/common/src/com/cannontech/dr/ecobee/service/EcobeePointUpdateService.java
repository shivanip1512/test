package com.cannontech.dr.ecobee.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;

public interface EcobeePointUpdateService {
    /**
     * Updates all ecobee points for pao with ecobee deviceReadings.
     */
    void updatePointData(PaoIdentifier paoIdentifier, EcobeeDeviceReadings deviceReadings);
}