package com.cannontech.web.common.chart.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.impl.GraphDetail;

/**
 * Service used to get information for a chart
 */
public interface ChartService {

    /**
     * Method to get a list of graphs to be trended
     * @param graphDetails - list of GraphDetail
     * @param startDate - Start date for graph
     * @param stopDate - End date for graph
     * @param converterType - Type of graph to be generated
     * @param userContext
     * @return A list containing one graph per point
     */
    public List<Graph<ChartValue<Double>>> getGraphs(List<GraphDetail> graphDetails, Date startDate, Date stopDate, ChartInterval unit,
                                  YukonUserContext userContext, GraphType graphType);

    /**
     * Method to get the number of x-axis values only. Does not create list of actual CharValues.
     * Does much less work than the getXAxisData method if all you are interested in is the count.
     * @param startDate
     * @param stopDate
     * @param interval
     * @return
     */
    public int getXAxisDataCount(Date startDate, Date stopDate, ChartInterval interval);

}