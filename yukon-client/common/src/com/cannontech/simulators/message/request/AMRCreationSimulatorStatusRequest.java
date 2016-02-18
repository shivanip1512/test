package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

/**
 * Request to create meters.
 */
public class AMRCreationSimulatorStatusRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
       
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.AMR_CREATION;
    }
}
