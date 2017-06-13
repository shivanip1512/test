package com.cannontech.simulators.message.response;

public class SimulatorStartupSettingsResponse extends SimulatorResponseBase {

    private boolean runOnStartup = false;

    public SimulatorStartupSettingsResponse(boolean success) {
        this.success = success;
    }

    public SimulatorStartupSettingsResponse(boolean success, boolean runOnStartup) {
        this.success = success;
        this.runOnStartup = runOnStartup;
    }

    public boolean isRunOnStartup() {
        return runOnStartup;
    }
}
