package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

public class RfnDeviceDeletionSimulatorStatusRequest implements SimulatorRequest {

    public SimulatorType getRequestType() {
        return SimulatorType.RFN_DEVICE_DELETE;
    }
}
