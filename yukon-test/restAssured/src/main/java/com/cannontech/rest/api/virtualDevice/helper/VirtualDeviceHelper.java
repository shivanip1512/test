package com.cannontech.rest.api.virtualDevice.helper;

import com.cannontech.rest.api.virtualDevice.request.MockVirtualDeviceModel;

public class VirtualDeviceHelper {

    public static MockVirtualDeviceModel buildDevice() {
        MockVirtualDeviceModel virtualDevice = MockVirtualDeviceModel.builder()
                .enable(true)
                .name("Virtual Device API Test")
                .build();
        return virtualDevice;
    }

}
