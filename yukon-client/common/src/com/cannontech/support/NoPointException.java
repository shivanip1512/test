package com.cannontech.support;

import com.cannontech.common.exception.PointException;
import com.cannontech.database.db.customer.CICustomerPointType;

public class NoPointException extends PointException {

    private CICustomerPointType pointType;
    private Integer customerId;

    
    public NoPointException(Integer customerId, CICustomerPointType pointType) {
        super("Customer " + customerId + " does not have a mapping for point type " + pointType + ".");
        this.customerId = customerId;
        this.pointType = pointType;
    }

    public CICustomerPointType getPointType() {
        return pointType;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }

}
