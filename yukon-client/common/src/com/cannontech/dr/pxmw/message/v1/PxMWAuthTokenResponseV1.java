package com.cannontech.dr.pxmw.message.v1;

import java.io.Serializable;

import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;

public class PxMWAuthTokenResponseV1 implements Serializable {
    private static final long serialVersionUID = 1L;

    private PxMWTokenV1 token;
    private PxMWCommunicationExceptionV1 error;

    public PxMWAuthTokenResponseV1(PxMWTokenV1 token, PxMWCommunicationExceptionV1 error) {
        this.token = token;
        this.error = error;
    }
    
    public PxMWTokenV1 getToken() {
        return token;
    }

    public PxMWCommunicationExceptionV1 getError() {
        return error;
    }
}
