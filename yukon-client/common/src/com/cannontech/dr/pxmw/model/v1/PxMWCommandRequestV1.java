package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

public class PxMWCommandRequestV1 implements Serializable {
    private final String method;
    private final PxMWCommandParamsV1 params;
    public PxMWCommandRequestV1(String method, PxMWCommandParamsV1 params) {
        this.method = method;
        this.params = params;
    }
    public String getMethod() {
        return method;
    }
    public PxMWCommandParamsV1 getParams() {
        return params;
    }
}
