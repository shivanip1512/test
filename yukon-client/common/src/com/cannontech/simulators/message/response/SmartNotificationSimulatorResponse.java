package com.cannontech.simulators.message.response;

import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;

public class SmartNotificationSimulatorResponse extends SimulatorResponseBase {
    
    SmartNotificationSimulatorSettings settings;
    
    public SmartNotificationSimulatorResponse(SmartNotificationSimulatorSettings settings) {
        this.settings = settings;
    }

    public SmartNotificationSimulatorSettings getSettings() {
        return settings;
    }

    public void setSettings(SmartNotificationSimulatorSettings settings) {
        this.settings = settings;
    }
}
