package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.RfnLcrDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;

/**
 * This is a service that simulates a network of RFN-6200 and RFN-6600 RF mesh LCR devices for use
 * in performance testing.  The primary goal of the simulator is to provide a mechanism to test Yukon's
 * capability to handle a very large number of RFN devices reporting daily and help to discover problem
 * areas that need further testing/improvement in handling large-scale two-way load control systems over
 * RF networks.  This simulator does not require Network Manager to be installed, but it creates messages
 * and places them on the same queue as network manager, simulating the connection between Yukon and NM.
 * 
 * By default the simulator starts up with 100,000 RFN-6200 devices and 20,000 RFN-6600 devices, though this is
 * configurable by the user at startup.  The simulator can not be started automatically at launch and must
 * be initiated manually by the user.
 */
public interface RfnLcrDataSimulatorService {
    /**
     * Starts the RFN LCR data simulator.
     * @param settings Defines the parameters of the simulation, particularly the device serial number ranges.
     */
    void sendMessagesByRange(SimulatorSettings settings);

    /**
     * Stops the RFN LCR data simulator.
     */
    void stopRangeSimulator();

    /**
     * Get the current settings if the simulator has been running. Otherwise, null is returned. If the
     * simulator was previously run (since web server startup) but is not running, this will return the
     * settings previously used.
     */
    SimulatorSettings getCurrentSettings();

    /**
     * Get the LCR data simulator status if the simulator has been running.
     */
    RfnLcrDataSimulatorStatus getStatusByRange();
    
    /**
     * Get the LCR data simulator status for existing devices if the simulator has been running.
     */
    RfnLcrDataSimulatorStatus getAllDevicesStatus();

    /**
     * Send message for LCR devices.
     */
    void sendMessagesToAllDevices();

    /**
     * Stop sending message to LCR devices.
     */
    void stopAllDeviceSimulator();
}
