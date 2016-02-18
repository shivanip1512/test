package com.cannontech.development.model;

import java.io.Serializable;

import com.cannontech.common.pao.PaoType;

public class DevPaoType implements Serializable{
    private boolean create = false;
    private PaoType paoType;

    public DevPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    public boolean isCreate() {
        return create;
    }
    public void setCreate(boolean create) {
        this.create = create;
    }
    public PaoType getPaoType() {
        return paoType;
    }
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

}
