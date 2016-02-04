package com.cannontech.simulators.handler;

import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;

public abstract class SimulatorMessageHandler {
    private SimulatorType requestType;
    
    public SimulatorMessageHandler(SimulatorType requestType) {
        this.requestType = requestType;
    }
    
    public SimulatorType getHandledType() {
        return requestType;
    }
    
    public boolean canHandle(SimulatorRequest request) {
        return request.getRequestType() == requestType;
    }
    
    /**
     * This method should be able to process every subtype of SimulatorRequest related to the handled SimulatorType,
     * and return a SimulatorResponse.
     */
    public abstract SimulatorResponse handle(SimulatorRequest request);
}
