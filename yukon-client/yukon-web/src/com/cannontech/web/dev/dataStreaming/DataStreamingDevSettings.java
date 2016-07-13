package com.cannontech.web.dev.dataStreaming;

/**
 * Object to contain data streaming developer settings, for simulators, etc.
 */
public class DataStreamingDevSettings {
    boolean simulatePorterConfigResponse = true;

    public boolean isSimulatePorterConfigResponse() {
        return simulatePorterConfigResponse;
    }

    public void setSimulatePorterConfigResponse(boolean simulatePorterConfigResponse) {
        this.simulatePorterConfigResponse = simulatePorterConfigResponse;
    }
}
