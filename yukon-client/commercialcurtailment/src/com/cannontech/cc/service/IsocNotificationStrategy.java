package com.cannontech.cc.service;

import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.CurtailmentRemoveCustomerBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;

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
    public void verifyRemoveCustomer(EventBuilderBase builder, VerifiedCustomer vCustomer) {
        CurtailmentRemoveCustomerBuilder myBuilder = (CurtailmentRemoveCustomerBuilder) builder;
        isocCommonStrategy.checkEventRemoveCustomer(vCustomer, myBuilder.getOriginalEvent());
    }

    @Override
    public String getConstraintStatus(com.cannontech.cc.model.CICustomerStub customer) {
    	return isocCommonStrategy.getConstraintStatus(customer);
    }

    public IsocCommonStrategy getIsocCommonStrategy() {
        return isocCommonStrategy;
    }

    public void setIsocCommonStrategy(IsocCommonStrategy isocCommonStrategy) {
        this.isocCommonStrategy = isocCommonStrategy;
    }
    
}
