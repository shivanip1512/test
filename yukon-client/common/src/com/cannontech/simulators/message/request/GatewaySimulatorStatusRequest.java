package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

/**
 * Requests the status of the gateway simulator threads from the simulator service.
 */
public class GatewaySimulatorStatusRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.GATEWAY;
    }
    
}
