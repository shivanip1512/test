package com.cannontech.multispeak.dao;

import com.cannontech.amr.meter.model.YukonMeter;
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
    
    /**
     * Returns a YukonMeter for meterNumber.
     * Throws NotFoundException if no matching results.
     * Uses GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS to determine if disabled meters should be included
     *  in query. If excludeDisabled = true, a MeterNumber _may_ exist in the database, be disabled, and this 
     *  would method throw NotFoundException.
     * 
     * The expectation (or rather reality) with MultiSpeak is that multiple meters may exist in Yukon with the 
     *  same meterNumber, but only one of them is enabled. Others may exist in Yukon but be disabled. 
     *  This is most likely a temporary period of time during change outs/replacement.   
     */
    public YukonMeter getMeterForMeterNumber(String meterNumber);
    
    /**
     * Returns a YukonMeter for serialNumber or address.
     * NOTE: This method assumes uniqueness across RFN sensorSerialNumbers and PLC address values.
     * Queries for match against either RFN sensorSerialNumber OR PLC address.
     * For RFN sensorSerialNumber, one should technically include model and manufacturer when looking up,
     *  however, the business logic of the calling methods must instead take care of checking if the model/manufacturer
     *  of the returned YukonMeter matches the expected values.
     *  Use this method with caution! 
     */
    public YukonMeter getForSerialNumberOrAddress(String serialNumberOrAddress);
}
