package com.cannontech.web.updater.capcontrol.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.updater.capcontrol.CapControlUpdaterTypeEnum;

public class CapControlLargestGraphTimeForZoneUpdaterHandler implements CapControlUpdaterHandler {

    private VoltageFlatnessGraphService voltageFlatnessGraphService;
    
    @Override
    public String handle(int id, YukonUserContext userContext) {
        long largestTime = voltageFlatnessGraphService.getLargestPointTimeForZoneGraph(id);
        return String.valueOf(largestTime);
    }
    
    @Override
    public CapControlUpdaterTypeEnum getUpdaterType() {
        return CapControlUpdaterTypeEnum.IVVC_LARGEST_GRAPH_TIME_FOR_ZONE;
    }    
    
    @Autowired
    public void setVoltageFlatnessGraphService(VoltageFlatnessGraphService voltageFlatnessGraphService) {
        this.voltageFlatnessGraphService = voltageFlatnessGraphService;
    }
}
