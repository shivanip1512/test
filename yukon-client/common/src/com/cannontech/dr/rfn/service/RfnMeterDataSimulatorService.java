package com.cannontech.dr.rfn.service;

import org.joda.time.DateTime;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.common.rfn.model.RfnDevice;
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
     * Starts the RFN Meter data simulator.
     */
    void startSimulator(SimulatorSettings settings);
    
    /**
     * Gets the RFN_METER simulator settings from the database.
     */
    SimulatorSettings getCurrentSettings();
    
    /**
     * Get data simulator status for existing devices if the simulator has been running.
     */
    RfnDataSimulatorStatus getStatus();

    void testSimulator(SimulatorSettings settings);

    // add comments
    RfnMeterReadingData createReadingForType(RfnDevice device, DateTime time, RfnMeterReadingType type,
            DateTime currentTime);
}
