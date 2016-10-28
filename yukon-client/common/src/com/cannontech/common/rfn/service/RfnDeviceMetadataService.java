package com.cannontech.common.rfn.service;

import java.util.Map;

import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
/**
 * This service ask NM for the information about device.
 * RfnMetadata contains all device information.
 *
 */
public interface RfnDeviceMetadataService {

    /**
     * Attempts to send a request for meta-data for rf devices.
     */
    public void send(RfnDevice device, DataCallback<Map<RfnMetadata, Object>> callback, String keyPrefix);
    
    /**
     * Attempts to send a request for meta-data for rf device.
     * Waits for response.
     * 
     * @return metadata
     * @throws NmCommunicationException if there was communication error or if NM returned an error
     */
    public Map<RfnMetadata, Object> getMetadata(RfnDevice device) throws NmCommunicationException;

}