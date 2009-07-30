package com.cannontech.common.bulk.service;

import com.cannontech.common.device.DeviceType;
import com.cannontech.common.device.model.SimpleDevice;

public interface ChangeDeviceTypeService {

    public SimpleDevice changeDeviceType(SimpleDevice device, DeviceType newDeviceType);
}
