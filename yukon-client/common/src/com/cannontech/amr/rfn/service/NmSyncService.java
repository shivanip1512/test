package com.cannontech.amr.rfn.service;

import com.cannontech.common.rfn.model.RfnDevice;

public interface NmSyncService {

    /**
     * Sends sync and gateway name update request to NM
     */
    void sendSyncRequest();

    /**
     * Schedules start-up sync and gateway name update request to be send to NM after 5 minutes.
     */
    void scheduleSyncRequest();

    /**
     * Sends request to NM to change the gateway name, if gateway name in NM doesn't match gateway name in Yukon.
     */
    void syncGatewayName(RfnDevice rfnDevice, String nmGatewayName);

    /**
     * Sends request to NM to change the gateway name.
     */
    void syncGatewayName(RfnDevice rfnDevice);
}
