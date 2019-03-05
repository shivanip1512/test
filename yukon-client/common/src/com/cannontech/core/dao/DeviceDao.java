package com.cannontech.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface DeviceDao {
    
    /**
     * Returns a SimpleDevice loaded from ServerDatabaseCache
     */
    SimpleDevice getYukonDevice(int paoId);

    /**
     * Helper method to translate a LiteYukonPaobject to SimpleDevice.
     */
    SimpleDevice getYukonDevice(LiteYukonPAObject pao);

    /**
     * This will fail for paos that have the same name;
     * ie repeaters and their routes are always named the same
     * but they have differing category and class so having the same
     * name is legal. Also it only handles devices that are of the
     * classes: PaoClass.CARRIER, PaoClass.METER, PaoClass.IED, PaoClass.RFMESH;
     * So cbc's would not qualify.
     */
    SimpleDevice getYukonDeviceObjectByName(String name);

    /**
     * Search for Yukon device by name.
     * Return YukonDevice if found, otherwise return null.
     */
    SimpleDevice findYukonDeviceObjectByName(String name);

    SimpleDevice getYukonDeviceObjectByMeterNumber(String meterNumber);

    SimpleDevice getYukonDeviceObjectByAddress(Long address);

    LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID);

    /**
     * This returns a LiteYukonPaobject for the meterNumber.
     * WARNING: This is a "BEST GUESS" (or the first one in the deviceMeterNumber cache) as
     * MeterNumber is NOT distinct for all general purposes, but may be a utilities distinct field.
     */
    LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber);

    List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber);

    /**
     * This returns a LiteYukonPaobject for the PaoName.
     * WARNING: This is a "BEST GUESS" (or the first one in the DEVICE cache) as
     * PaoName is NOT necessarily distinct for all general purposes, but may be a utilities distinct field.
     */
    LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName);

    /**
     * Will find a device based on the four parameters that make up its unique key.
     * This method will return null if a device couldn't be found.
     * 
     * @return the LiteYukonPaoObject that matches the criteria
     */
    LiteYukonPAObject getLiteYukonPAObject(String deviceName, PaoType paoType);

    List<Integer> getDevicesByPort(int portId);

    List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress);

    void enableDevice(YukonDevice device);

    void disableDevice(YukonDevice device);

    /**
     * Removes the given device
     */
    void removeDevice(YukonDevice device);
    
    /**
     * Removes the device with the given id
     */
    void removeDevice(int id);

    /**
     * Method to change the route of a given device based on a route id
     * Higher level method exists in {@link PaoDefinitionService} that takes a device
     * name and checks if it is valid, then delegates here for update.
     */
    void changeRoute(YukonDevice device, int newRouteId);

    /**
     * Method to change the pao name of a given device
     */
    void changeName(YukonDevice device, String newName);

    /**
     * Method to change the devicecarriersettings address for given device
     * Higher level method exists in {@link PaoDefinitionService} that checks if address
     * is in valid range for device.
     */
    void changeAddress(YukonDevice device, int newAddress);

    /**
     * Method to change the meter number of given device
     */
    void changeMeterNumber(YukonDevice device, String newMeterNumber);

    /**
     * Method to get the name of a device. If device is a Meter, meterDao
     * is used to get formatted name. PaoName otherwise.
     */
    String getFormattedName(YukonDevice device);

    /**
     * Method to get the name of a device. If device is a Meter, meterDao
     * is used to get formatted name. PaoName otherwise.
     * Database hit is required to determine if deviceId is for a Meter.
     */
    String getFormattedName(int deviceId);

    /** 
     * Helper method to translate DeviceBase object to SimpleDevice
     */
    SimpleDevice getYukonDeviceForDevice(DeviceBase oldDevice);

    PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();

    /**
     * Returns a List<SimpleDevice> of devices that are on the specified route;
     */
    List<SimpleDevice> getDevicesForRouteId(int routeId);

    /**
     * Returns the number of devices on the given route id.
     */
    int getRouteDeviceCount(int routeId);

    /**
     * Returns a list of {@link SimpleDevice} objects for a list of pao ids.
     */
    List<SimpleDevice> getYukonDeviceObjectByIds(Iterable<Integer> ids);

    /**
     * Returns a list of {@link SimpleDevice} objects for a list of pao types.
     */
    List<SimpleDevice> getDevicesForPaoTypes(Iterable<PaoType> types);

    /**
     * Returns list of child devices by parent id
     */
    List<DisplayableDevice> getChildDevices(int parentId);

    /**
     * Creates or updates device's Mac address
     * @throws StarsInvalidArgumentException if the address is invalid
     */
    void updateDeviceMacAddress(int deviceId, String macAddress);

    /**
     * Returns Mac address
     */
    String getDeviceMacAddress(int deviceId);

    /**
     * True if Mac address exists
     */
    boolean isMacAddressExists(String macAddress);

    /**
     * Returns a map for device ids and Mac addresses
     */
    Map<Integer, String> getDeviceMacAddresses(Collection<Integer> deviceIds);
    
    /**
     * 
     * @param macAddress
     * @return
     */
    int getDeviceIdFromMacAddress(String macAddress);
}
