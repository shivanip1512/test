package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class FieldSimulatorStatusRequest implements SimulatorRequest {

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.FIELD_SIMULATOR;
    }
}
