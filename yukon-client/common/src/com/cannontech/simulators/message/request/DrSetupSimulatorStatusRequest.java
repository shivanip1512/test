package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class DrSetupSimulatorStatusRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.DEMAND_RESPONSE_SETUP;
    }
}