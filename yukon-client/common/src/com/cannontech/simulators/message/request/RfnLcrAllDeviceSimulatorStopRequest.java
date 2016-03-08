package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class RfnLcrAllDeviceSimulatorStopRequest implements SimulatorRequest {

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_LCR;
    }
}
