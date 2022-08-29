package com.cannontech.simulators.message.request;

import com.cannontech.development.model.DemandResponseSetup;
import com.cannontech.simulators.SimulatorType;

public class DrSetupSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private DemandResponseSetup drSetup;
    
    public DrSetupSimulatorRequest(DemandResponseSetup drSetup) {
        this.drSetup = drSetup;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.DEMAND_RESPONSE_SETUP;
    }

    public DemandResponseSetup getDemandResponseSetup() {
        return drSetup;
    }

    @Override
    public String toString() {
        return String.format("DemandResponseSetupSimulatorRequest [drSetup=%s]", drSetup);
    }
}