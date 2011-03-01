package com.cannontech.stars.dr.hardware.dao;

import com.cannontech.common.model.ZigbeeThermostat;


public interface ZigbeeDeviceDao {

    public ZigbeeThermostat getZigbeeUtilPro(int deviceId);
    
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void deleteZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
}
