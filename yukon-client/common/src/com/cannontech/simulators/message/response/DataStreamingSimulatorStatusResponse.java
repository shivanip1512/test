package com.cannontech.simulators.message.response;

public class DataStreamingSimulatorStatusResponse extends SimulatorResponseBase {
    private boolean isRunning;
    
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
}
