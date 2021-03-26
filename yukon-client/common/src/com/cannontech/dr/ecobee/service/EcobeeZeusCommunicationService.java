package com.cannontech.dr.ecobee.service;

public interface EcobeeZeusCommunicationService {

    /**
     * Check whether the provided thermostat serial number is registered in Ecobee portal and its already connected.
     */
    boolean isDeviceRegistered(String serialNumber);

}
