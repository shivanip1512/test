package com.cannontech.dr.honeywell.service;

import java.util.List;

import com.cannontech.dr.honeywellWifi.model.HoneywellWifiDutyCycleDrParameters;

/**
 * This service provides the communications layer with the Honeywell portal.
 *
 */
public interface HoneywellCommunicationService {

    /**
     * Cancels DREvent for specified devices.
     */
    void cancelDREventForDevices(List<Integer> thermostatIds, int eventId, boolean immediateCancel);

    /**
     * Adds devices to demand-response group (max. 2000 devices).
     * 
     * @param thermostatIds deviceId's that need to be added to the DR Group.Must contain no more than 2000
     *        devices
     * @param groupId ranging from 1 - 10000
     */
    void addDevicesToGroup(List<Integer> thermostatIds, int groupId);

    /**
     * Removes specified devices from demand-response group.
     * 
     * @param thermostatIds deviceId's that need to be removed from the DR Group
     * @param groupId ranging from 1 - 10000
     */
    void removeDeviceFromDRGroup(List<Integer> thermostatIds, int groupId);
    
    /**
     * Send DREvent for a group.
     * 
     * @param honeywellDutyCycleParameters parameters for sending DREvent
     */
    void sendDREventForGroup(HoneywellWifiDutyCycleDrParameters honeywellDutyCycleParameters);
    
    /**
     * Cancel DREvent for a group.
     * 
     * @param groupId ranging from 1 - 10000
     * @param eventId is the eventId
     * @param immediateCancel is to cancel the event immediately
     */
    void cancelDREventForGroup(int groupId, int eventId, boolean immediateCancel);
    

}