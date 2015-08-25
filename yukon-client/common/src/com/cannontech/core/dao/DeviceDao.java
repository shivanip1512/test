package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;

public interface DeviceDao {
    SimpleDevice getYukonDevice(int paoId);

    SimpleDevice getYukonDevice(LiteYukonPAObject pao);

    SimpleDevice getYukonDeviceObjectById(int deviceId);

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

    void removeDevice(YukonDevice device);

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
     */
    String getFormattedName(int deviceId);

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
     * Returns Device based on the given deviceId.
     */
    Device getDevice(int deviceId);

    /**
     * Returns DeviceDirectCommSettings based on the given deviceId.
     */
    DeviceDirectCommSettings getDeviceDirectCommSettings(int deviceId);

    /**
     * Returns DeviceDirectCommSettings based on the given deviceId.
     */
    DeviceDialupSettings getDeviceDialupSettings(int deviceId);

    /**
     * Returns DeviceAddress information based on the given deviceId.
     */
    DeviceAddress getDeviceAddress(int deviceId);

    /**
     * Returns DeviceWindow information based on the given deviceId.
     */
    DeviceWindow getDeviceWindow(int deviceId);

    /**
     * Saves the provided data in DeviceAddress table and returns the deviceId.
     */
    int saveDeviceAddress(DeviceAddress deviceAddress, int cbcId);

    /**
     * Saves the provided data in DeviceCBC table and returns the deviceId.
     */
    int saveDeviceCBC(DeviceCBC deviceCBC, int cbcId);

    /**
     * Saves the provided data in YukonPao table and returns the deviceId.
     */
    int saveYukonPao(String paoName, String disableFlag, int cbcId);

    /**
     * Returns DeviceCBC information based on the given deviceId.
     */
    DeviceCBC getDeviceCBC(int deviceId);

    /**
     * Returns a List<DeviceScanRate> for given deviceId;
     */
    List<DeviceScanRate> getDeviceScanRates(int deviceId);

    /**
     * Saves the provided data in DeviceDirectCommSettings table and returns the deviceId.
     */
    int saveDeviceDirectCommSettings(int portId, int cbcId);

    /**
     * Saves the provided data in DeviceScanRates table and returns the deviceId.
     */
    int saveDeviceScanRates(DeviceScanRate deviceScanRate, int deviceId, String type);
}
