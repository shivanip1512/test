package com.cannontech.common.rfn.service;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.NotFoundException;

public interface RfnDeviceLookupService {

    /**
     * Will attempt to retrieve a meter based on rfn identifier, if not found will attempt to 
     * retrieve it based on new versions of model names defined in master.cfg property "RFN_METER_MODEL_CONVERSION".
     * If still not found, will throw a NotFountException.
     * @param rfnIdentifier
     * @return RfnDevice
     * @throws NotFoundException
     */
    public RfnDevice getDevice(RfnIdentifier rfnIdentifier) throws NotFoundException;
    
}