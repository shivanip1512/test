package com.cannontech.dr.eatonCloud.message;

import java.io.Serializable;

import com.google.common.base.Strings;

public class EatonCloudHeartbeatResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String error;

    public EatonCloudHeartbeatResponse() {

    }

    public EatonCloudHeartbeatResponse(String error) {
        this.error = error;
    }

    public boolean hasError() {
        return !Strings.isNullOrEmpty(error);
    }

    public String getError() {
        return error;
    }
}