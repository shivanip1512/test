package com.cannontech.amr.meter.service;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.TransactionException;

public interface MeterService {

    public void addDisconnectAddress(SimpleDevice device, int disconnectAddress) throws IllegalArgumentException, TransactionException;
    
    public void removeDisconnectAddress(SimpleDevice device) throws IllegalArgumentException, TransactionException;
}
