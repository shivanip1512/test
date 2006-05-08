package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.util.Date;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.NoPointException;
import com.cannontech.common.util.TimeUtil;

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
        
        int durationHours = myBuilder.getEventDuration();
        CICustomerStub customer = vCustomer.getCustomer();
        
        Date notifTime = myBuilder.getNotificationTime();
        Date startTime = myBuilder.getStartTime();
        int notifMinutes = TimeUtil.differenceMinutes(notifTime, startTime);
        
        isocCommonStrategy.checkEventCustomer(vCustomer, durationHours, notifMinutes);
    }
    
    @Override
    public BigDecimal getInterruptibleLoad(CICustomerStub customer) throws NoPointException {
        return isocCommonStrategy.getInterruptibleLoad(customer);
    }
    
    public IsocCommonStrategy getIsocCommonStrategy() {
        return isocCommonStrategy;
    }

    public void setIsocCommonStrategy(IsocCommonStrategy isocCommonStrategy) {
        this.isocCommonStrategy = isocCommonStrategy;
    }

}
