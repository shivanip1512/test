package com.cannontech.dr.eatonCloud.message.v1;

import java.io.Serializable;

import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;

public class EatonCloudAuthTokenResponseV1 implements Serializable {
    private static final long serialVersionUID = 1L;

    private EatonCloudTokenV1 token;
    private EatonCloudCommunicationExceptionV1 error;

    public EatonCloudAuthTokenResponseV1(EatonCloudTokenV1 token, EatonCloudCommunicationExceptionV1 error) {
        this.token = token;
        this.error = error;
    }
    
    public EatonCloudTokenV1 getToken() {
        return token;
    }

    public EatonCloudCommunicationExceptionV1 getError() {
        return error;
    }
}
