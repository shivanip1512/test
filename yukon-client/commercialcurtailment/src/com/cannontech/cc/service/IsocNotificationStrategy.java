package com.cannontech.cc.service;

import java.math.BigDecimal;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.common.exception.PointException;

public class IsocNotificationStrategy extends BaseNotificationStrategy {
    IsocCommonStrategy isocCommonStrategy;

    public IsocNotificationStrategy() {
        super();
    }

    @Override
    protected void 
    verifyCustomer(EventBuilderBase builder, 
                   VerifiedCustomer vCustomer) {
        CurtailmentBuilder myBuilder = (CurtailmentBuilder) builder;
        
        isocCommonStrategy.checkEventCustomer(vCustomer, myBuilder.getEvent());
    }
    
    @Override
    public BigDecimal getInterruptibleLoad(CICustomerStub customer) throws PointException {
        return isocCommonStrategy.getInterruptibleLoad(customer);
    }
    
    public IsocCommonStrategy getIsocCommonStrategy() {
        return isocCommonStrategy;
    }

    public void setIsocCommonStrategy(IsocCommonStrategy isocCommonStrategy) {
        this.isocCommonStrategy = isocCommonStrategy;
    }

}
