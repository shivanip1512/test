package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.deploy.service.Meter;

public interface MspMeterDao
{
    /**
     * Returns all AMR _supported_ meters (those paoIds having both DeviceMeterGroup and DeviceCarrierSettings db entries)
     * @param lastReceived
     * @param maxRecords
     * @return
     */
    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords);
    
    public List<Meter> getCDSupportedMeters(String lastReceived, int maxRecords);
    
    public boolean isCDSupportedMeter(String meterNumber);
}
