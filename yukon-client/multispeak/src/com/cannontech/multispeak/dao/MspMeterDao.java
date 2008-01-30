package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.deploy.service.Meter;

public interface MspMeterDao
{
    public List<Meter> getAMRSupportedMeters(String lastReceived, int maxRecords);
    
    public List<Meter> getCDSupportedMeters(String lastReceived, int maxRecords);
    
    public boolean isCDSupportedMeter(String meterNumber);
}
