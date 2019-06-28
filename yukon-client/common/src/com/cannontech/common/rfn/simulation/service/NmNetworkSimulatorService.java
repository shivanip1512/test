package com.cannontech.common.rfn.simulation.service;

import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;
import com.cannontech.simulators.AutoStartableSimulator;


public interface NmNetworkSimulatorService extends AutoStartableSimulator {
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

    boolean isRunning();

    void sendDemandResetStatusArchiveRequest(Set<RfnIdentifier> identifiers, Integer limit);
}
