package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

//This is the old way of me handling messaging. This object is currently unused and will be deleted once I have confirmed that the other way works.
public class RfnMeterReadAndControlSimulatorStopRequest implements SimulatorRequest {

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_METER_READ_AND_CONTROL;
    }
    
}
