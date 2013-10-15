package com.cannontech.amr.meter.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.service.impl.PaoLoader;

public interface MeterDao {
    public String getFormattedDeviceName(YukonMeter device);
    
    public YukonMeter getForMeterNumber(String meterNumber);
    
    /**
     * Specific to PLC meters. Only use this if you really only want PLC meters.
     */
    public PlcMeter getForPhysicalAddress(String address);

    public YukonMeter getForPaoName(String paoName);
    
    public YukonMeter findForPaoName(String paoName);

    public YukonMeter getForYukonDevice(YukonDevice yukonDevice);
    
    public YukonMeter getForId(Integer id);
    
    public SimpleMeter getSimpleMeterForId(int id);

    /**
     * Specific to PLC meters. Only use this if you really only want PLC meters.
     * Throws NotFoundException if id is not found for Plc Meter
     */
    public PlcMeter getPlcMeterForId(int id);
    /**
     * Specific to Rfn meters. Only use this if you really only want Rfn meters.
     * Throws NotFoundException if id is not found for Rfn Meter
     */
    public RfnMeter getRfnMeterForId(int id);
    
    public List<YukonMeter> getMetersForMeterNumbers(List<String> meterNumbers);
    
    public Comparator<YukonMeter> getMeterComparator();
    
    public void update(YukonMeter newMeterInfo);
    
    public PaoLoader<DisplayablePao> getDisplayableDeviceLoader();
    
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();
    
    public List<YukonMeter> getMetersForYukonPaos(Iterable<? extends YukonPao> identifiers);
    
    public int getMeterCount();

    public Map<PaoIdentifier, YukonMeter> getPaoIdMeterMap(Iterable<PaoIdentifier> paoIds);
    
}
