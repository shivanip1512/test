package com.cannontech.common.device.virtualDevice.service;

import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;

public interface VirtualDeviceService {

    VirtualDeviceModel create(VirtualDeviceModel virtualDevice);
    VirtualDeviceModel retrieve(int virtualDeviceId);
    VirtualDeviceModel update(int virtualDeviceId, VirtualDeviceModel virtualDevice);
}
