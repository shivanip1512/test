package com.cannontech.multispeak.dao;

import com.cannontech.multispeak.data.MspMeterReturnList;

public interface MspMeterDao
{
    /**
     * Returns all AMR _supported_ meters (those paoIds having both DeviceMeterGroup and DeviceCarrierSettings db entries)
     * @param lastReceived
     * @param maxRecords
     * @return
     */
    public MspMeterReturnList getAMRSupportedMeters(String lastReceived, int maxRecords);
    
    public MspMeterReturnList getCDSupportedMeters(String lastReceived, int maxRecords);
    
    public boolean isCDSupportedMeter(String meterNumber);
}
