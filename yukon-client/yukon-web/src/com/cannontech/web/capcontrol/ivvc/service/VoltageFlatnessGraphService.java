package com.cannontech.web.capcontrol.ivvc.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphData;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphSettings;

public interface VoltageFlatnessGraphService {
    /**
     * Generates the Data object the jsp will use to create the Data file for amCharts.
     * 
     * This will create the chart data for all zones attached to the Substation Bus.
     * @param user
     * @param subBusId
     */
    
    public VfGraphData getSubBusGraphData(LiteYukonUser user, int subBusId);
    /**
     * Generates the Settings object for the jsp to create a Settings file for amCharts.
     * 
     * This will create the chart settings for all zones attached to the Substation Bus.
     * @param user
     * @param subBusId
     */
    public VfGraphSettings getSubBusGraphSettings(LiteYukonUser user, int subBusId);
    
    /**
     * Generates the Data object the jsp will use to create the Data file for amCharts.
     * 
     * This will create the chart data for the individual zone..
     * @param user
     * @param subBusId
     */
    public VfGraphData getZoneGraphData(LiteYukonUser user, int zoneId);
    
    /**
     * Generates the Data object the jsp will use to create the Data file for amCharts.
     * 
     * This will create the chart settings for the individual zone..
     * @param user
     * @param subBusId
     */
    public VfGraphSettings getZoneGraphSettings(LiteYukonUser user, int zoneId);
}
