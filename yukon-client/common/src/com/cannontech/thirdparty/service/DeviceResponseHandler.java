package com.cannontech.thirdparty.service;

import java.util.List;

import com.cannontech.thirdparty.digi.model.DeviceResponse;

public interface DeviceResponseHandler {

    /**
     * Returns the number of devices that are reported to receive the control.
     * 
     * @param deviceResponse
     * @return
     */
    public int handleResponses(List<DeviceResponse> deviceResponse);
    
}
