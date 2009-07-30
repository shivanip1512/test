package com.cannontech.amr.meter.dao;

import java.util.Comparator;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface MeterDao extends StandardDaoOperations<Meter> {
    public String getFormattedDeviceName(Meter device);
    
    public Meter getForMeterNumber(String meterNumber);
    
    public Meter getForPhysicalAddress(String address);

    public Meter getForPaoName(String paoName);

    public Meter getForYukonDevice(SimpleDevice yukonDevice);
    
    public List<Meter> getMetersByMeterNumber(String lastReceived, int maxRecordCount);
    
    public List<Meter> getMetersByPaoName(String lastReceived, int maxRecordCount);
    
    public Comparator<Meter> getMeterComparator();
    
    public void update(Meter newMeterInfo);
    
}
