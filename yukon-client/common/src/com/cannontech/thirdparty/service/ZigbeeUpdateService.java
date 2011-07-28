package com.cannontech.thirdparty.service;

import com.cannontech.thirdparty.model.ZigbeeDevice;

/**
 * Zigbee Service that Polls for updates versus a 'by exception' updating service.
 */
public interface ZigbeeUpdateService {
    
    public void enableActiveDevicePoll(ZigbeeDevice device);
}
