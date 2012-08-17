package com.cannontech.common.rfn.service;

import java.util.Map;

import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.model.RfnDevice;

public interface RfnDeviceMetadataService {

    /**
     * Attempts to send a request for meta-data for rf devices.
     */
    public void send(RfnDevice device, DataCallback<Map<RfnMetadata, Object>> callback, String keyPrefix);

}