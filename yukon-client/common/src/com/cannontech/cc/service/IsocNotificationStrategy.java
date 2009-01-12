package com.cannontech.cc.service;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.CurtailmentRemoveCustomerBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

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
    
    @Override
    public Boolean canEventBeAdjusted(CurtailmentEvent event, LiteYukonUser user) {
    	//The addition of new customer restrictions (max 4 hr control in 24 hr period and less than 4 hour control
    	//  make it so that it no longer is possible to adjust an event safely without reselecting all the customers.
    	return false;
    }
}
