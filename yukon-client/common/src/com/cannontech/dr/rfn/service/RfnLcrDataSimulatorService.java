package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.SimulatorSettings;

public interface RfnLcrDataSimulatorService {

    /**
     * Starts the RFN LCR data simulator. 
     * @param settings Defines the parameters of the simulation, particularly the device serial number ranges.
     */
    public void startSimulator(SimulatorSettings settings);

    /**
     * Stops the RFN LCR data simulator.
     */
    public void stopSimulator();

}
