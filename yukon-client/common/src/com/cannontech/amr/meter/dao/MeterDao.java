package com.cannontech.amr.meter.dao;

import java.util.Comparator;
import java.util.List;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.service.impl.PaoLoader;

public interface MeterDao {
    
    String getFormattedDeviceName(YukonMeter device);
    
    YukonMeter getForMeterNumber(String meterNumber);
    
    YukonMeter getForPaoName(String paoName);
    
    YukonMeter findForPaoName(String paoName);
    
    YukonMeter getForId(int id);
    
    SimpleMeter getSimpleMeterForId(int id);
    
    /**
     * Specific to PLC meters. Only use this if you really only want PLC meters.
     * Throws NotFoundException if id is not found for Plc Meter
     */
    PlcMeter getPlcMeterForId(int id);
    
    /**
     * Specific to Rfn meters. Only use this if you really only want Rfn meters.
     * Throws NotFoundException if id is not found for Rfn Meter
     */
    RfnMeter getRfnMeterForId(int id);
    
    List<YukonMeter> getMetersForMeterNumbers(List<String> meterNumbers);
    
    Comparator<YukonMeter> getMeterComparator();
    
    void update(YukonMeter newMeterInfo);
    
    PaoLoader<DisplayablePao> getDisplayableDeviceLoader();
    
    PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();
    
    List<YukonMeter> getMetersForYukonPaos(Iterable<? extends YukonPao> identifiers);
    
    int getMeterCount();
    
    /*
     * Returns a list of devices ids that have disconnect address.
     */
    List<Integer> getMetersWithDisconnectCollarAddress(Iterable<Integer> ids);
    
    List<SimpleMeter> getAllSimpleMeters();

}