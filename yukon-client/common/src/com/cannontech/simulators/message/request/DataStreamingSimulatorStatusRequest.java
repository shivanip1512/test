package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class DataStreamingSimulatorStatusRequest implements SimulatorRequest {
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.DATA_STREAMING;
    }
}
