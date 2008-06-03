package com.cannontech.common.device.creation;

import java.sql.SQLException;

import com.cannontech.common.device.YukonDevice;


public interface DeviceCreationService {

    public YukonDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints);
    
    public YukonDevice createDeviceByDeviceType(int deviceType, String name, int address, int routeId, boolean createPoints) throws SQLException;
}
