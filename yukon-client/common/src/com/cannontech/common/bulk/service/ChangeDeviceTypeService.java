package com.cannontech.common.bulk.service;

import com.cannontech.common.device.DeviceType;
import com.cannontech.common.device.YukonDevice;

public interface ChangeDeviceTypeService {

    public YukonDevice changeDeviceType(YukonDevice device, DeviceType newDeviceType);
}
