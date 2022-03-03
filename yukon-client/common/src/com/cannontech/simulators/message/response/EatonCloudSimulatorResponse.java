package com.cannontech.simulators.message.response;

public class EatonCloudSimulatorResponse extends SimulatorResponseBase {

    private static final long serialVersionUID = 1L;
    private Object response;
    private int status;

    public EatonCloudSimulatorResponse(Object response, int status) {
        super(true);
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
