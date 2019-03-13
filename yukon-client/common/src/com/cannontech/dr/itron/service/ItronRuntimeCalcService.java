package com.cannontech.dr.itron.service;


/**
 * This service uses stored runtime state data to calculate runtimes and shedtimes for Itron LCRs.
 */
public interface ItronRuntimeCalcService {

    /**
     * Loads runtime status data for each Itron LCR, between the last recorded runtime for that device
     * and the start of the previous hour (or last time we received Itron data). Uses this runtime status data
     * to calculate the runtime for that period, for each device.
     */
    public void calculateDataLogs();
}
