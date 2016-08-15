package com.cannontech.simulators.message.response;

import com.cannontech.common.rfn.simulation.SimulatedDataStreamingSettings;

public class DataStreamingSimulatorStatusResponse extends SimulatorResponseBase {
    private SimulatedDataStreamingSettings settings;
    
    public void setSettings(SimulatedDataStreamingSettings settings) {
        this.settings = settings;
    }
    
    public SimulatedDataStreamingSettings getSettings() {
        return settings;
    }
    
    public boolean isRunning() {
        return settings != null;
    }
}
