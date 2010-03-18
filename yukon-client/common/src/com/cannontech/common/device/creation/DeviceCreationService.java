package com.cannontech.common.device.creation;

import com.cannontech.common.device.model.SimpleDevice;


public interface DeviceCreationService {

    public SimpleDevice createDeviceByTemplate(String templateName, String newDeviceName, boolean copyPoints);
    
    public SimpleDevice createDeviceByDeviceType(int deviceType, String name, int address, int routeId, boolean createPoints) throws DeviceCreationException;
}
