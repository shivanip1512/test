package com.cannontech.web.common.chart.service;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.chart.model.GraphType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.common.chart.service.impl.GraphDetail;

public interface HighChartService {
    
    /**
     * Returns a Map to be converted to json and consumed by TODO:Add doc here
     * The format of this object is as follows:
     * 
     * (if the format of this is changed, please update this comment)
     * TODO: Add json object format here.
     * 
     *  @return JSONObject
     */
    Map<String, Object> getMeterGraphData(List<GraphDetail> graphDetails, Instant start, Instant stop, Double yMin, Double yMax,
            GraphType graphType, YukonUserContext userContext);

    /**
     * Returns a Map to be converted to json and consumed by TODO:Add doc here
     * The format of this object is as follows:
     * 
     * (if the format of this is changed, please update this comment)
     * TODO: Add json object format here.
     * 
     *  @return JSONObject
     */
    Map<String, Object> getIVVCGraphData(VfGraph graph, boolean includeTitles);
}
