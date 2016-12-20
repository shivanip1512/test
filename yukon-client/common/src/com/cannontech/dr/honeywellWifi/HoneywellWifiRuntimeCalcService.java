package com.cannontech.dr.honeywellWifi;


/**
 * This service uses stored runtime state data to calculate hourly runtimes for Honeywell wifi thermostats.
 */
public interface HoneywellWifiRuntimeCalcService {

    /**
     * Loads runtime status data for each Honeywell wifi thermostat, between the last recorded runtime for that device
     * and the start of the previous hour (or last time we received a Honeywell message). Uses this runtime status data
     * to calculate the hourly runtimes for that period, for each device.
     */
    public void calculateRuntimes();
}
