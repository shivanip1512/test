package com.cannontech.dr.pxmw.message;

import java.io.Serializable;

public class PxMWAuthTokenResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object token;
    private Object error;

    public PxMWAuthTokenResponse(Object token, Object error) {
        this.token = token;
        this.error = error;
    }
    
    public Object getToken() {
        return token;
    }

    public Object getError() {
        return error;
    }
}
