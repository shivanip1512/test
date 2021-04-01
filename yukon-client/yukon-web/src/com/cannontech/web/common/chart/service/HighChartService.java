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
     * Returns a Map to be converted to json and consumed by yukon.highChart.js (our implementation of HighCharts)
     * Json represents the Meter Trends chart 
     */
    Map<String, Object> getMeterGraphData(List<GraphDetail> graphDetails, Instant start, Instant stop, Double yMin, Double yMax,
            GraphType graphType, YukonUserContext userContext);

    /**
     * Returns a Map to be converted to json and consumed by yukon.highChart.js (our implementation of HighCharts)
     * Json represents the IVVC Chart that is used on the IVVC Bus and Zone detail pages.
     */
    Map<String, Object> getIVVCGraphData(VfGraph graph, boolean includeTitles);
}
