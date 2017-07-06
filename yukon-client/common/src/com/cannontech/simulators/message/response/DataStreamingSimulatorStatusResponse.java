package com.cannontech.simulators.message.response;

import com.cannontech.common.rfn.simulation.SimulatedDataStreamingSettings;

public class DataStreamingSimulatorStatusResponse extends SimulatorResponseBase {
    private SimulatedDataStreamingSettings settings;
    private boolean running;
    
    public void setSettings(SimulatedDataStreamingSettings settings) {
        this.settings = settings;
    }
    
    public SimulatedDataStreamingSettings getSettings() {
        return settings;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public boolean isRunning() {
        return running;
    }
}
