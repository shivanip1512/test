package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.device.DeviceBase;
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
     * 6/4/13: DaoFactory no longer exists, use YukonSpringHook.getBean() instead.
     *         See also YUK-11881.
     */
    public LiteYukonPAObject getLiteDevice(final int deviceID);

    public SimpleDevice getYukonDevice(int paoId);
    public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject);
    
    public SimpleDevice getYukonDeviceObjectById(int deviceId);
    
    /**
     * This will fail for paos that have the same name; 
     * ie repeaters and their routes are always named the same
     * but they have differing category and class so having the same 
     * name is legal. Also it only handles devices that are of the 
     * classes: PaoClass.CARRIER, PaoClass.METER, PaoClass.IED, PaoClass.RFMESH;
     * So cbc's would not qualify.
     * 
     * @param name - The name of the device
     */
    public SimpleDevice getYukonDeviceObjectByName(String name);
    
    /**
     * Search for Yukon device by name.
     * Return YukonDevice if found, otherwise return null.
     * @param name
     * @return
     */
    public SimpleDevice findYukonDeviceObjectByName(String name);
    public SimpleDevice getYukonDeviceObjectByMeterNumber(String meterNumber);
    public SimpleDevice getYukonDeviceObjectByAddress(Long address);
    
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

    public List<Integer> getDevicesByPort(int portId);

    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress,
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
     * Higher level method exists in {@link PaoDefinitionService} that takes a device
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
     * Method to change the devicecarriersettings address for given device
     * Higher level method exists in {@link PaoDefinitionService} that checks if address
     * is in valid range for device.
     * @param deviceId
     * @param newAddress
     */
    public void changeAddress(YukonDevice device, int newAddress);
    
    /**
     * Method to change the meter number of given device
     * @param deviceId
     * @param newMeterNumber
     */
    public void changeMeterNumber(YukonDevice device, String newMeterNumber);
    
    /**
     * Method to get the name of a device. If device is a Meter, meterDao
     * is used to get formatted name. PaoName otherwise.
     * @param device
     * @return
     */
    public String getFormattedName(YukonDevice device);
    
    /**
     * Method to get the name of a device. If device is a Meter, meterDao
     * is used to get formatted name. PaoName otherwise.
     * @param deviceId
     * @return
     */
    public String getFormattedName(int deviceId);
    
    public SimpleDevice getYukonDeviceForDevice(DeviceBase oldDevice);
    
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();

    /**
     * Returns a List<SimpleDevice> of devices that are on the specified route;
     * @param routeId
     * @return List<SimpleDevice>
     */
    public List<SimpleDevice> getDevicesForRouteId(int routeId);

    /**
     * Returns the number of devices on the given route id.
     * @param routeId
     * @return int
     */
    public int getRouteDeviceCount(int routeId);

    /**
     * Returns a list of {@link SimpleDevice} objects for a list of pao ids.
     */
    public List<SimpleDevice> getYukonDeviceObjectByIds(Iterable<Integer> ids);
}