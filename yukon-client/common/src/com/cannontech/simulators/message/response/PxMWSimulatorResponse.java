package com.cannontech.simulators.message.response;

public class PxMWSimulatorResponse extends SimulatorResponseBase {

    private static final long serialVersionUID = 1L;
    private Object response;
    private int status;

    public PxMWSimulatorResponse(Object response, int status) {
        this.response = response;
        this.status = status;
    }

    public Object getResponse() {
        return response;
    }

    public int getStatus() {
        return status;
    }
}
