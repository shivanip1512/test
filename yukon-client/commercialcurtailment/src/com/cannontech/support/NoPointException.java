package com.cannontech.support;

import com.cannontech.common.exception.PointException;

public class NoPointException extends PointException {

    private String pointType;
    private Integer customerId;

    
    public NoPointException(Integer customerId, String pointType) {
        super("Customer " + customerId + " does not have a mapping for point type " + pointType + ".");
        this.customerId = customerId;
        this.pointType = pointType;
    }

    public String getPointType() {
        return pointType;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }

}
