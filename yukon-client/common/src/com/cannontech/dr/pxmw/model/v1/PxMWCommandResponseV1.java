package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

public class PxMWCommandResponseV1 implements Serializable {
    private final String status;
    private final String msg;
    
    public PxMWCommandResponseV1(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

}
