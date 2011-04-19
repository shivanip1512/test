package com.cannontech.thirdparty.digi.dao;

import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeThermostat;


public interface ZigbeeDeviceDao {

    public ZigbeeDevice getZigbeeDevice(int deviceId);
    public ZigbeeThermostat getZigbeeUtilPro(int deviceId);
    
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void deleteZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
}
