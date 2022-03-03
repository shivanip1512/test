package com.cannontech.simulators.message.response;

import com.cannontech.dr.rfn.model.FieldSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;

public class FieldSimulatorStatusResponse extends SimulatorResponseBase {

    private FieldSimulatorSettings settings;

    public FieldSimulatorSettings getSettings() {
        return settings;
    }

    public void setSettings(FieldSimulatorSettings settings) {
        this.settings = settings;
    }
}
