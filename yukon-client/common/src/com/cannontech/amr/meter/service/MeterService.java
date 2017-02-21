package com.cannontech.amr.meter.service;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.database.TransactionException;

public interface MeterService {

    public void addDisconnectAddress(SimpleDevice device, int disconnectAddress) throws IllegalArgumentException, TransactionException, IllegalUseOfAttribute;
    
    public void removeDisconnectAddress(SimpleDevice device) throws IllegalArgumentException, TransactionException, IllegalUseOfAttribute;
}
