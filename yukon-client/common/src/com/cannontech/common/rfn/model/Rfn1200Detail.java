package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.database.db.device.RfnAddress;

public class Rfn1200Detail {
    
    private Integer id;
    private String name;
    private RfnAddress rfnAddress = new RfnAddress();
    private PaoType paoType;
    private Boolean enabled;
    private int postCommWait;

    public int getPostCommWait() {
        return postCommWait;
    }

    public void setPostCommWait(int postCommWait) {
        this.postCommWait = postCommWait;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RfnAddress getRfnAddress() {
        return rfnAddress;
    }

    public void setRfnAddress(RfnAddress rfnAddress) {
        this.rfnAddress = rfnAddress;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

}