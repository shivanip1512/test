package com.cannontech.dr.ecobee.service;

import java.util.List;
import java.util.Set;

import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeePlusDrParameters;
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
     * Deletes the specified group
     */
    void deleteGroup(String zeusGroupId);

    /**
     * Enroll the specified device to the specified group and Program.
     */
    void enroll(int lmGroupId, String serialNumber, int inventoryId, int programId, boolean updateDeviceMapping);

    /**
     * Unenroll the specified device from the specified groups.
     */
    void unEnroll(Set<Integer> lmGroupIds, String serialNumber, int inventoryId, boolean updateDeviceMapping);

    
    /**
     * Create thermostat group
     */
    void createThermostatGroup(String zeusGroupId, List<String> thermostatIds);
    /**
     * Create push API configuration with a publicly accessible HTTPS endpoint and encoded private key.
     */
    void createPushApiConfiguration(String reportingUrl, String privateKey);

    /**
     * Show push API configuration.
     */
    ZeusShowPushConfig showPushApiConfiguration();

    /**
     * Initiates a duty cycle demand response event in Ecobee.
     */
    void sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters);
    
    /**
     * Get all groups for a program from ecobee.
     */
    List<ZeusGroup> getAllGroups();
    
    /**
     * Get thermostats for the group. 
     */
    List<ZeusThermostat> getThermostatsInGroup(String groupId);

    /**
     * Initiates a Setpoint demand response event in Ecobee.
     */
    void sendSetpointDR(EcobeeSetpointDrParameters parameters);

    /**
     * Sends a message to cancel the whole Demand Response event, or cancel it for specified thermostats only.
     */
    void cancelDemandResponse(List<Integer> groupIds, String... serialNumbers);

    /**
     * Initiates a eco+ demand response event in Ecobee.
     */
    void sendEcoPlusDR(EcobeePlusDrParameters parameters);
}
