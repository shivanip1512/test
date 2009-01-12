package com.cannontech.cc.service;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.common.exception.PointException;
import com.cannontech.database.data.lite.LiteYukonUser;

public class IsocSupersedeDirectStrategy extends IsocDirectStrategy {
    
    @Override
    public Boolean canEventBeCancelled(CurtailmentEvent event, LiteYukonUser user) {
        return false;
    }
    
    /**
     * We only want to check notification time limits, all others are ignored.
     */
    @Override
    protected void verifyCustomer(EventBuilderBase builder, VerifiedCustomer vCustomer) {
        CurtailmentBuilder myBuilder = (CurtailmentBuilder) builder;
        
        try {
            isocCommonStrategy.checkNoticeTime(vCustomer, myBuilder.getEvent());
        } catch (PointException e) {
            isocCommonStrategy.handlePointException(vCustomer, e);
        }

    }
}
