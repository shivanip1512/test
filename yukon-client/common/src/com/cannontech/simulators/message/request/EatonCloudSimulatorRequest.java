package com.cannontech.simulators.message.request;

import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.simulators.SimulatorType;

public class EatonCloudSimulatorRequest implements SimulatorRequest{
    private static final long serialVersionUID = 1L;
    private EatonCloudRetrievalUrl url;
    private String method;
    private Class<?>[] paramClasses;
    private Object[] paramValues;
    private String header;

    public EatonCloudSimulatorRequest(EatonCloudRetrievalUrl url, String method, Class<?>[] paramClasses, Object[] paramValues, String header) {
        this.url = url;
        this.method = method;
        this.paramClasses = paramClasses;
        this.paramValues = paramValues;
        this.header = header;
    }

    public EatonCloudRetrievalUrl getUrl() {
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
        return SimulatorType.EATON_CLOUD;
    }

    public String getHeader() {
        return header;
    }
}
