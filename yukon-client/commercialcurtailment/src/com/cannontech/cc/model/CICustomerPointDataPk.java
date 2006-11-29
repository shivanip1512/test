package com.cannontech.cc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cannontech.database.db.customer.CICustomerPointType;

@Embeddable
public class CICustomerPointDataPk implements Serializable {
    private CICustomerPointType type;
    private Integer customerId;
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    @Column(length=16, nullable=false)
    @Enumerated(EnumType.STRING)
    public CICustomerPointType getType() {
        return type;
    }
    
    public void setType(CICustomerPointType type) {
        this.type = type;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CICustomerPointDataPk == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CICustomerPointDataPk rhs = (CICustomerPointDataPk) obj;
        return new EqualsBuilder().append(type, rhs.type).append(customerId, rhs.customerId).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(customerId).toHashCode();
    }
}
