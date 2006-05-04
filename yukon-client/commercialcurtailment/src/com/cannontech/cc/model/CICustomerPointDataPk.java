package com.cannontech.cc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CICustomerPointDataPk implements Serializable {
    private String type;
    private Integer customerId;
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    @Column(length=16, nullable=false)
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    

}
