package com.cannontech.dr.ecobee.service;

import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;

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
    void unEnroll(int lmGroupId,String serialNumber, int inventoryId);
    
    /**
     * Create push API configuration with a publicly accessible HTTPS endpoint and encoded private key.
     */
    void createPushApiConfiguration(String reportingUrl, String privateKey);

    /**
     * Show push API configuration.
     */
    ZeusShowPushConfig showPushApiConfiguration();
}
