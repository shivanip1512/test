package com.cannontech.common.rfn.service;

public interface RfnDeviceDeletionMessageService {

    /**
     * Attempts to send a RFN device deletion request on the com.eaton.eas.yukon.networkmanager.RfnDeviceDeleteRequest queue.
     * Will expect TWO responses.
     * 
     * The first is a status message indicating this device is present in the NM Db.
     * This response should come back within seconds to a minute.
     * 
     * The second response confirms the device deletion. This response can take anywhere from seconds to minutes to ten minutes
     * depending
     * on network performance.
     */
    public void sendRfnDeviceDeletionRequest(Integer paoId);
}
