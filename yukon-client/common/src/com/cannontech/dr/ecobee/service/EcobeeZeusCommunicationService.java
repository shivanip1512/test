package com.cannontech.dr.ecobee.service;

public interface EcobeeZeusCommunicationService {

    /**
     * Create a Ecobee device if the provided thermostat serial number is registered in Ecobee portal and its already
     * enrolled.
     */
    boolean createZeusDevice(String serialNumber);

}
