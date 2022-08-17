package com.cannontech.simulators.message.request;

import com.cannontech.development.model.DevAmr;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to create meters.
 */
public class AmrCreationSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private DevAmr devAmr;
    
    public AmrCreationSimulatorRequest(DevAmr devAmr) {
        this.devAmr = devAmr;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.AMR_CREATION;
    }

    public DevAmr getDevAmr() {
        return devAmr;
    }
}
