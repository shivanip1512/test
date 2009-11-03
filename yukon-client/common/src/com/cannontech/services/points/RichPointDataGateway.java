package com.cannontech.services.points;

import org.springframework.integration.annotation.Gateway;

import com.cannontech.core.dynamic.RichPointData;

public interface RichPointDataGateway {
    
    @Gateway(requestChannel="yukonRichPointData")
    public void sendRichPointData(RichPointData richPointData);
}
