package com.cannontech.simulators.message.response;

import java.io.Serializable;

/**
 * Implemented by messages that are sent as responses from the simulator service.
 * SimulatorResponseBase provides a bare-bones implementation that is useful for a boolean
 * success/fail response.
 */
public interface SimulatorResponse extends Serializable { 
    public boolean isSuccessful();
    public void setSuccessful(boolean success);
    public boolean hasError();
    public void setException(Exception e);
    public Exception getException();
}
