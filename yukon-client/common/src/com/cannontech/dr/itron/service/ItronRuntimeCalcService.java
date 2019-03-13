package com.cannontech.dr.itron.service;


/**
 * This service uses stored runtime state data to calculate runtimes and shedtimes for Itron LCRs.
 */
public interface ItronRuntimeCalcService {

    /**
     * Loads relay status data (runtime and shedtime) for each Itron LCR, between the time of the last recorded data logs for that device
     * and the latest point data update from the device. Uses the relay status data to calculate the runtime and shedtime for that period, 
     * at any defined log point intervals, for each device.
     */
    public void calculateDataLogs();
}
