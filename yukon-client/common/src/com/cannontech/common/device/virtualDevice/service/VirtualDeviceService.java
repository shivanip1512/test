package com.cannontech.common.device.virtualDevice.service;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBase;

public interface VirtualDeviceService {

    VirtualDeviceBase create(VirtualDeviceBase virtualDevice);
    VirtualDeviceBase retrieve(int virtualDeviceId);
    VirtualDeviceBase update(int virtualDeviceId, VirtualDeviceBase virtualDevice);
}
