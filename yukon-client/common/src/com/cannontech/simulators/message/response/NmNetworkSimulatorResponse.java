package com.cannontech.simulators.message.response;

import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;

public class NmNetworkSimulatorResponse extends SimulatorResponseBase {
    private SimulatedNmMappingSettings settings;
    
    public NmNetworkSimulatorResponse(SimulatedNmMappingSettings settings) {
        super.success = true;
        this.settings = settings;
    }
    
    public SimulatedNmMappingSettings getSettings() {
        return settings;
    }
    
    public boolean isRunning() {
        return settings != null;
    }
}
