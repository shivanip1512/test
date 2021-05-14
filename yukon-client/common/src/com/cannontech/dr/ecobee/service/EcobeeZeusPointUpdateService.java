package com.cannontech.dr.ecobee.service;

import com.cannontech.dr.ecobee.model.EcobeeZeusDeviceReading;

public interface EcobeeZeusPointUpdateService {
    /**
     * Updates all ecobee points for pao with ecobee deviceReading.
     * @return
     */
    void updatePointData(EcobeeZeusDeviceReading ecobeeZeusDeviceReading);

}
