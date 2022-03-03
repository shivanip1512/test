package com.cannontech.dr.ecobee.service;

public interface EcobeeZeusRuntimeCalcService {

    /**
     * Loads runtime status data for each ecobee thermostat, between the last recorded runtime for that device
     * and the start of the previous hour (or last time we received a ecobee message). Uses this runtime status data
     * to calculate the hourly runtimes for that period, for each device.
     */
    public void calculateRuntimes();
}
