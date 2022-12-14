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
import com.google.common.collect.ListMultimap;

public interface MeterDao {
    
    String getFormattedDeviceName(YukonMeter device);
    
    /**
     * Returns the meter that has the specified meter number.  Assumes only one meter 
     * will have this meter number.
     */
    YukonMeter getForMeterNumber(String meterNumber);
    
    /**
     * Returns a list of meters that have the specified meter number.
     * @param meterNumber The meter number to search for.
     * @param excludedIds Optionaly exlcuded device ids. Not used if null or empty.
     * @return The list of meters that share this meter number.
     */
    List<YukonMeter> getForMeterNumber(String meterNumber, Integer... excludedIds);
    
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
    
    /**
     * Returns a multimap of <MeterNumber, YukonMeter> objects.
     */
    ListMultimap<String, YukonMeter> getMetersMapForMeterNumbers(final List<String> meterNumbers);
    
    Comparator<YukonMeter> getMeterComparator();
    
    /** 
     * Updates the meters fields include plc fields and rfn fields. 
     * See {@link RfnMeter} and {@link PlcMeter}. 
     */
    void update(YukonMeter meter);
    
    PaoLoader<DisplayablePao> getDisplayableDeviceLoader();
    
    PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();
    
    List<YukonMeter> getMetersForYukonPaos(Iterable<? extends YukonPao> identifiers);
    
    int getMeterCount();
    
    /*
     * Returns a list of devices ids that have disconnect address.
     */
    List<Integer> getMetersWithDisconnectCollarAddress(Iterable<Integer> ids);
    
    List<SimpleMeter> getAllSimpleMeters();
    
    /**
     * Returns Disconnect Address from the DeviceMCT400Series table for MCT ID or else returns null
     * @param mctID
     * @returns address Id if found or else returns null
     */
    public Integer getDisconnectAddress(Integer mctID) ;

}