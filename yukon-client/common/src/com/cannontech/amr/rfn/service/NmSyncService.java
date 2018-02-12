package com.cannontech.amr.rfn.service;

public interface NmSyncService {

    /**
     * Sends sync and gateway name update request to NM
     */
    void sendSyncRequest();

    /**
     * Schedules start-up sync and gateway name update request to be send to NM after 5 minutes.
     */
    void scheduleSyncRequest();
}
