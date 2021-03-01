package com.cannontech.simulators.message.request;

import com.cannontech.dr.rfn.model.FieldSimulatorSettings;
import com.cannontech.simulators.SimulatorType;

public class ModifyFieldSimulatorRequest implements SimulatorRequest {

    private FieldSimulatorSettings fieldSimulatorSettings;
    
    public ModifyFieldSimulatorRequest(FieldSimulatorSettings fieldSimulatorSettings) {
        this.fieldSimulatorSettings = fieldSimulatorSettings;
    }

    public FieldSimulatorSettings getFieldSimulatorSettings() {
        return fieldSimulatorSettings;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.FIELD_SIMULATOR;
    }
}
