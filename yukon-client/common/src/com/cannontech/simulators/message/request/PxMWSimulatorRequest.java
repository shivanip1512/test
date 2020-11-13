package com.cannontech.simulators.message.request;

import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.simulators.SimulatorType;

public class PxMWSimulatorRequest implements SimulatorRequest{
    private static final long serialVersionUID = 1L;
    private PxMWRetrievalUrl url;
    private String method;
    private Class<?>[] paramClasses;
    private Object[] paramValues;

    public PxMWSimulatorRequest(PxMWRetrievalUrl url, String method, Class<?>[] paramClasses, Object[] paramValues) {
        this.url = url;
        this.method = method;
        this.paramClasses = paramClasses;
        this.paramValues = paramValues;
    }

    public PxMWRetrievalUrl getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Class<?>[] getParamClasses() {
        return paramClasses;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.PX_MIDDLEWARE;
    }
}
