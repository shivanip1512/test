package com.cannontech.amr.meter.service;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.database.TransactionException;

public interface MeterService {

    public void addDisconnectAddress(YukonDevice device, int disconnectAddress) throws IllegalArgumentException, TransactionException;
    
    public void removeDisconnectAddress(YukonDevice device) throws IllegalArgumentException, TransactionException;
}
