package com.cannontech.common.device.virtualDevice.service;

import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;

public interface VirtualDeviceService {

    /*
     * Create a new virtual device
     */
    VirtualDeviceModel create(VirtualDeviceModel virtualDevice);

    /*
     * Retrieve an existing virtual device
     */
    VirtualDeviceModel retrieve(int virtualDeviceId);

    /*
     * Update an existing virtual device
     */
    VirtualDeviceModel update(int virtualDeviceId, VirtualDeviceModel virtualDevice);
}
