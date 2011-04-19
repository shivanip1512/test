package com.cannontech.thirdparty.service.impl;

import java.util.List;

import com.cannontech.thirdparty.digi.model.DeviceResponse;
import com.cannontech.thirdparty.service.DeviceResponseHandler;

public class DigiDeviceResponseHandler implements DeviceResponseHandler {
    
    @Override
    public int handleResponses(List<DeviceResponse> deviceResponses) {
        int deviceCount = 0;
        
        for (DeviceResponse response:deviceResponses) {
            if (response.hasError())
                continue;
            deviceCount += response.getDeviceList().size();
        }
        
        return deviceCount;
    }

}
