package com.cannontech.common.bulk.service;

import com.cannontech.common.device.YukonDevice;

public interface ChangeDeviceTypeService {

    public YukonDevice changeDeviceType(YukonDevice device, int newDeviceType);
}
