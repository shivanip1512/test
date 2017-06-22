package com.cannontech.web.dev;

import java.util.Map;

import com.cannontech.simulators.message.response.SimulatorStartupSettingsResponse;

public final class SimulatorStartupSettingsResponseOrError {
    public final SimulatorStartupSettingsResponse response;
    public final Map<String, Object> errorJson;

    public SimulatorStartupSettingsResponseOrError(SimulatorStartupSettingsResponse response) {
        this.response = response;
        errorJson = null;
    }

    public SimulatorStartupSettingsResponseOrError(Map<String, Object> errorJson) {
        response = null;
        this.errorJson = errorJson;
    }
}
