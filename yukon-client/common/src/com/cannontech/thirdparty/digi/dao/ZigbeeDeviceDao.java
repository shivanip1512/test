package com.cannontech.thirdparty.digi.dao;

import java.util.List;

import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeThermostat;


public interface ZigbeeDeviceDao {

    public ZigbeeDevice getZigbeeDevice(int deviceId);
    public ZigbeeThermostat getZigbeeUtilPro(int deviceId);
    public ZigbeeThermostat getZigbeeUtilProByMACAddress(String macAddress);
    
    public int getDeviceIdForMACAddress(String macAddress);
    public List<Integer> getDeviceIdsForMACAddresses(List<String> macAddresses);
    
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void deleteZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
}
