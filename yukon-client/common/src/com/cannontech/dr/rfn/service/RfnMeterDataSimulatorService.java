package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.simulators.AutoStartableSimulator;

/**
 * This is a service that simulates a network of RFN Meters for use in
 * performance testing. The primary goal of the simulator is to simulate the
 * message as if they are coming from actual Network Manager. This simulator
 * does not require Network Manager to be installed, but it creates messages and
 * places them on the same queue as network manager, simulating the connection
 * between Yukon and NM.
 */
public interface RfnMeterDataSimulatorService extends AutoStartableSimulator {

    /**
     * Stops the RFN Meter data simulator.
     */
    void stopSimulator();
    
    /**
     * Get data simulator status for existing devices if the simulator has been running.
     */
    RfnDataSimulatorStatus getStatus();

    void testSimulator(SimulatorSettings settings);
}
