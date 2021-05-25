package com.cannontech.dr.ecobee.service;

import java.util.List;

import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeSetpointDrParameters;

public interface EcobeeZeusCommunicationService {

    /**
     * Check whether the provided thermostat serial number is registered in Ecobee portal and its already connected.
     */
    boolean isDeviceRegistered(String serialNumber);

    /**
     * Deletes the specified thermostat from a program's root group and deletes from all child groups.
     */
    void deleteDevice(String serialNumber);

    /**
     * Enroll the specified device to the specified group.
     */
    void enroll(int lmGroupId, String serialNumber, int inventoryId);

    /**
     * Unenroll the specified device from the specified group.
     */
    void unEnroll(int lmGroupId, String serialNumber, int inventoryId);

    /**
     * Create push API configuration with a publicly accessible HTTPS endpoint and encoded private key.
     */
    void createPushApiConfiguration(String reportingUrl, String privateKey);

    /**
     * Show push API configuration.
     */
    ZeusShowPushConfig showPushApiConfiguration();

    /**
     * Initiates a duty cycle demand response event in Ecobee and return the created DR event ID.
     */
    String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters);
    
    /**
     * Get all groups for a program from ecobee.
     */
    List<ZeusGroup> getAllGroups();
    
    /**
     * Get thermostats for the group. 
     */
    List<ZeusThermostat> getThermostatsInGroup(String groupId);

    /**
     * Initiates a Setpoint demand response event in Ecobee and return the created DR event ID.
     */
    String sendSetpointDR(EcobeeSetpointDrParameters parameters);

    /**
     * Sends a message to cancel the whole Demand Response event, or cancel it for specified thermostats only.
     */
    void cancelDemandResponse(int yukonGroupId, String... serialNumbers);
}