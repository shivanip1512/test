package com.cannontech.web.bulk.service;

import com.cannontech.common.device.YukonDevice;

public interface ChangeDeviceTypeService {

    public YukonDevice processDeviceTypeChange(YukonDevice device, int newDeviceType);
}
