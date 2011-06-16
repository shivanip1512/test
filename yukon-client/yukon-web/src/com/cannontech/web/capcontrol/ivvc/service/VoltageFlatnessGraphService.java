package com.cannontech.web.capcontrol.ivvc.service;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.data.lite.LiteYukonUser;
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

    
    /**
     * Returns a long representing the largest time value (the number of milliseconds since 
     * January 1, 1970, 00:00:00 GMT) of all the points in the IVVC SubBus graph
     * @param subBusId
     * @return long
     */
    public long getLargestPointTimeForSubBusGraph(int subBusId);

    /**
     * Returns a long representing the largest time value (the number of milliseconds since
     * January 1, 1970, 00:00:00 GMT) of all the points in the IVVC Zone graph
     * @param zoneId
     * @return long
     */
    public long getLargestPointTimeForZoneGraph(int zoneId);

    /**
     * Returns true if the regulators for all zones have a point existing for the BuiltInAttribute VOLTAGE_Y. 
     * This method may change in the future to check more than just this attribute.
     * @param subBusId
     * @param user
     * @return
     */
    public boolean allZonesHaveRequiredAttributes(int subBusId, LiteYukonUser user);

    /**
     * Returns true if the regulators for this zone have a point existing for the BuiltInAttribute VOLTAGE_Y. 
     * This method may change in the future to check more than just this attribute.
     * @param zoneId
     * @param user
     * @return
     */
    public boolean zoneHasRequiredAttribute(int zoneId, BuiltInAttribute attribute, LiteYukonUser user);
}
