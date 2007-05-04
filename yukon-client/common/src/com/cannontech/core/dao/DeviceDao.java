package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface DeviceDao {
    
    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     * 
     * @deprecated replaced by DaoFactory.getPaoDao().getLiteYukonPAO( deviceID )
     */
    public LiteYukonPAObject getLiteDevice(final int deviceID);

    public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID);
    
    public String getFormattedDeviceName(LiteYukonPAObject device);

    /**
     * This returns a LiteYukonPaobject for the meterNumber.
     * WARNING: This is a "BEST GUESS" (or the first one in the deviceMeterNumber cache) as
     *  MeterNumber is NOT distinct for all general purposes, but may be a utilities distinct field.
     * @param deviceID
     * @return
     */
    public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(
            String meterNumber);

    /**
     * This returns a LiteYukonPaobject for the PaoName.
     * WARNING: This is a "BEST GUESS" (or the first one in the DEVICE cache) as
     *  PaoName is NOT necessarily distinct for all general purposes, but may be a utilities distinct field.
     * @param deviceID
     * @return
     */
    public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName);

    /**
     * Will find a device based on the four parameters that make up its unique key.
     * This method will return null if a device couldn't be found.
     * @param deviceName
     * @param category
     * @param paoClass
     * @param type
     * @return the LiteYukonPaoObject that matches the criteria
     */
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
            int category, int paoClass, int type);

    /**
     * Will find a device based on the four parameters that make up its unique key.
     * This method will return null if a device couldn't be found.
     * @param deviceName
     * @param category
     * @param paoClass
     * @param type
     * @return the LiteYukonPaoObject that matches the criteria
     */
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName,
            String category, String paoClass, String type);

    public List getDevicesByPort(int portId);

    public List getDevicesByDeviceAddress(Integer masterAddress,
            Integer slaveAddress);


}