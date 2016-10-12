package com.cannontech.common.rfn.simulation.service;

import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;


public interface NmNetworkSimulatorService {
    /**
     * -Deletes all location with an origin Simulator 
     * -Creates locations for all of the RF devices that do not have locations already creates
     */
    void setupLocations();

    void start(SimulatedNmMappingSettings settings);

    void stop();

    /**
     * Caches new settings
     */
    void updateSettings(SimulatedNmMappingSettings settings);
    
    /**
     * Returns current settings
     */
    SimulatedNmMappingSettings getSettings();
}
