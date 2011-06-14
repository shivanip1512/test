package com.cannontech.amr.meter.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.service.impl.PaoLoader;

public interface MeterDao {
    public String getFormattedDeviceName(Meter device);
    
    public Meter getForMeterNumber(String meterNumber);
    
    public Meter getForPhysicalAddress(String address);

    public Meter getForPaoName(String paoName);

    public Meter getForYukonDevice(YukonDevice yukonDevice);
    
    public Meter getForId(Integer id);
    
    public List<Meter> getMetersForMeterNumbers(List<String> meterNumbers);
    
    public List<Meter> getMetersByMeterNumber(String lastReceived, int maxRecordCount);
    
    public List<Meter> getMetersByPaoName(String lastReceived, int maxRecordCount);
    
    public Comparator<Meter> getMeterComparator();
    
    public void update(Meter newMeterInfo);
    
    public PaoLoader<DisplayablePao> getDisplayableDeviceLoader();
    
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();
    
    public List<Meter> getMetersForYukonPaos(Iterable<? extends YukonPao> identifiers);
    
    public int getMeterCount();

    public Map<PaoIdentifier, Meter> getPaoIdMeterMap(Iterable<PaoIdentifier> paoIds);
    
}
