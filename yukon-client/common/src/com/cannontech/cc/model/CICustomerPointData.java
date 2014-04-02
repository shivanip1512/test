package com.cannontech.cc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.cannontech.database.db.customer.CICustomerPointType;

/**
 * Yep. This class is pretty ugly. Maybe when the EJB3 Persistence stuff
 * is better documented I'll be able to figure out how to have a foreign key
 * (customerId in the customer table) refer to part of a composite primary key 
 * (custoemrId in this table) and have a propper ManyToOne pointing back. For
 * now this class will just have to be dealt with at the id level (although
 * there are some helper methods defined to ease the pain).
 */
public class CICustomerPointData {
    private CICustomerPointType type;
    private Integer customerId;
    private Integer pointId;
    private String optionalLabel = "";
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public CICustomerPointType getType() {
        return type;
    }
    
    public void setType(CICustomerPointType type) {
        this.type = type;
    }

    public CICustomerPointData() {
        super();
    }

    public String getOptionalLabel() {
        return optionalLabel;
    }

    public void setOptionalLabel(String optionalLabel) {
        this.optionalLabel = optionalLabel;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CICustomerPointData == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CICustomerPointData rhs = (CICustomerPointData) obj;
        return new EqualsBuilder().append(customerId, rhs.customerId).append(type, rhs.type).isEquals();
    }
}
