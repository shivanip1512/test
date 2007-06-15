package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.service.Meter;

public interface MspMeterDao
{
    public List<Meter> getAMRSupportedMeters(String lastReceived, String key);
    
    public List<Meter> getCDSupportedMeters(String lastReceived, String key);
    
    public boolean isCDSupportedMeter(String objectID, String key);
}
