package com.cannontech.stars.dr.thirdparty.digi.dao;

import com.cannontech.stars.dr.thirdparty.digi.model.ZigbeeThermostat;


public interface ZigbeeDeviceDao {

    public ZigbeeThermostat getZigbeeUtilPro(int deviceId);
    
    public void createZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void updateZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
    public void deleteZigbeeUtilPro(ZigbeeThermostat zigbeeThermostat);
}
