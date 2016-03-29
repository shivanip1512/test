package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;

/**
 * This is a service that simulates a network of RFN Meters for use in
 * performance testing. The primary goal of the simulator is to simulate the
 * message as if they are coming from actual Network Manager. This simulator
 * does not require Network Manager to be installed, but it creates messages and
 * places them on the same queue as network manager, simulating the connection
 * between Yukon and NM.
 */
public interface RfnMeterDataSimulatorService {

    /**
     * Stops the RFN Meter data simulator.
     */
    void stopSimulator();

    /**
     * Starts the RFN Meter data simulator.
     */
    void startSimulator(SimulatorSettings settings);

    /**
     * Get the current settings if the simulator has been running. Otherwise, null is returned. If the
     * simulator was previously run (since web server startup) but is not running, this will return the
     * settings previously used.
     */
    SimulatorSettings getCurrentSettings();
    
    
    /**
     * Get data simulator status for existing devices if the simulator has been running.
     */
    RfnDataSimulatorStatus getStatus();
}
