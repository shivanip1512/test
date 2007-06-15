package com.cannontech.amr.meter.dao;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface MeterDao extends StandardDaoOperations<Meter> {
    public String getFormattedDeviceName(Meter device);
    
    public Meter getForMeterNumber(String meterNumber);
    
    public Meter getForPaoName(String paoName);
    
    public List<Meter> getMetersByMeterNumber(String lastReceived, int maxRecordCount);
    
    public List<Meter> getMetersByPaoName(String lastReceived, int maxRecordCount);
}
