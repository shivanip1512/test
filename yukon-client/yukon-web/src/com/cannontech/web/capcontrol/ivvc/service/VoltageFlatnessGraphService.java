package com.cannontech.web.capcontrol.ivvc.service;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;

public interface VoltageFlatnessGraphService {
    /**
     * Generates the Data object the jsp will use to create the Data file for amCharts.
     * 
     * This will create the chart data for all zones attached to the Substation Bus.
     * @param user
     * @param subBusId
     */
    
    public VfGraph getSubBusGraph(YukonUserContext userContext, int subBusId);
    
    /**
     * Generates the Data object the jsp will use to create the Data file for amCharts.
     * 
     * This will create the chart data for the individual zone..
     * @param user
     * @param subBusId
     */
    public VfGraph getZoneGraph(YukonUserContext userContext, int zoneId);
}
