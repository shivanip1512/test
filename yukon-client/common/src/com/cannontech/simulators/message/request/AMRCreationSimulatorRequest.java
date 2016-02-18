package com.cannontech.simulators.message.request;

import com.cannontech.development.model.DevAMR;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to create meters.
 */
public class AMRCreationSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private DevAMR devAMR;
    
    public AMRCreationSimulatorRequest(DevAMR devAMR) {
        this.devAMR = devAMR;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.AMR_CREATION;
    }

    public DevAMR getDevAMR() {
        return devAMR;
    }
}
