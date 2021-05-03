package com.cannontech.dr.ecobee.service;

import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;

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
     * Initiates a duty cycle demand response event in Ecobee and return the created DR event ID.
     */
    String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters);
}
