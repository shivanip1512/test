package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.service.SimpleDeviceDefinitionService;
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

    public YukonDevice getYukonDevice(int paoId);
    public YukonDevice getYukonDevice(LiteYukonPAObject yukonPAObject);
    
    public YukonDevice getYukonDeviceObjectById(int deviceId);
    public YukonDevice getYukonDeviceObjectByName(String name);
    public YukonDevice getYukonDeviceObjectByMeterNumber(String meterNumber);
    public YukonDevice getYukonDeviceObjectByAddress(Long address);
    
    public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID);
    
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
     * 
     * @param meterNumber
     * @return
     */
    
    public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber);
    
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

    /**
     * Method used to enable a device
     * @param device - Device to enable
     */
    public void enableDevice(YukonDevice device);

    /**
     * Method used to disable a device
     * @param device - Device to disable
     */
    public void disableDevice(YukonDevice device);
    
    /**
     * Method to remove a device
     * @param device - Device to remove
     */
    public void removeDevice(YukonDevice device);
    
    /**
     * Method to change the route of a given device based on a route id
     * Higher level method exists in {@link SimpleDeviceDefinitionService} that takes a device
     * name and checks if it is valid, then delegates here for update.
     * @param newRouteId
     */
    public void changeRoute(YukonDevice device, int newRouteId);
    
    /**
     * Method to change the pao name of a given device
     * @param device
     * @param newName
     */
    public void changeName(YukonDevice device, String newName);
    
    /**
     * Method to change the devicecarriersettings address for givin device
     * Higher level method exists in {@link SimpleDeviceDefinitionService} that checks if address
     * is in valid range for device.
     * @param deviceId
     * @param newAddress
     */
    public void changeAddress(YukonDevice device, int newAddress);
    
    /**
     * Method to change the meter number of givin device
     * @param deviceId
     * @param newMeterNumber
     */
    public void changeMeterNumber(YukonDevice device, String newMeterNumber);
    
    /**
     * Method to get the name of a device. If device is a Meter, meterDao
     * is used to get formatted name. PaoName oterwise.
     * @param device
     * @return
     */
    public String getFormattedName(YukonDevice device);
    
    /**
     * Method to get the name of a device. If device is a Meter, meterDao
     * is used to get formatted name. PaoName oterwise.
     * @param deviceId
     * @return
     */
    public String getFormattedName(int deviceId);
}