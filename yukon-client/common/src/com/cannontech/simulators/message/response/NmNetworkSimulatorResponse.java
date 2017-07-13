package com.cannontech.simulators.message.response;

import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;

public class NmNetworkSimulatorResponse extends SimulatorResponseBase {
    private final SimulatedNmMappingSettings settings;
    private boolean running;
    
    public NmNetworkSimulatorResponse(SimulatedNmMappingSettings settings) {
        super.success = true;
        this.settings = settings;
    }
    
    public SimulatedNmMappingSettings getSettings() {
        return settings;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
}
