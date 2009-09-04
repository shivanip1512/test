package com.cannontech.services.points;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.dynamic.RichPointDataService;

public class RichPointUpdateInjector {
    
    private RichPointDataService richPointDataService;
    private RichPointDataGateway gateway;
    
    @PostConstruct
    public void initialize() {
        richPointDataService.registerForAll(new RichPointDataListener() {
            @Override
            public void pointDataReceived(RichPointData pointData) {
                gateway.sendRichPointData(pointData);
            }
        });
    }
    
    
    @Autowired
    public void setRichPointDataService(RichPointDataService richPointDataService) {
        this.richPointDataService = richPointDataService;
    }
    
    @Autowired
    public void setGateway(RichPointDataGateway gateway) {
        this.gateway = gateway;
    }
    
}
