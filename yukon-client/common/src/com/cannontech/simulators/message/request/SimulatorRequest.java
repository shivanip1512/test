package com.cannontech.simulators.message.request;

import java.io.Serializable;

import com.cannontech.simulators.SimulatorType;

/**
 * Implemented by messages that are sent as requests to the simulator service.
 */
public interface SimulatorRequest extends Serializable { 
    
    /**
     * Get the type of simulator request (i.e. the simulator being targeted by this message).
     */
    public SimulatorType getRequestType();
    
}
