package com.cannontech.simulators.message.response;

/**
 * A bare-bones implementation of Simulator response, suitable for use as a simple success/fail
 * response. This can also be extended by more complex responses.
 */
public class SimulatorResponseBase implements SimulatorResponse {
    private static final long serialVersionUID = 1L;
    protected boolean success = false;
    protected Exception exception;
    
    public SimulatorResponseBase() { }
    
    public SimulatorResponseBase(boolean success) {
        this.success = success;
    }
    
    public SimulatorResponseBase(Exception e) {
        exception = e;
    }
    
    @Override
    public boolean isSuccessful() {
        return success;
    }
    
    @Override
    public void setSuccessful(boolean success) {
        this.success = success;
    }
    
    @Override
    public boolean hasError() {
        return exception != null;
    }

    @Override
    public void setException(Exception e) {
        exception = e;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "SimulatorResponseBase [success=" + success + ", exception=" + exception + "]";
    }
    
}
