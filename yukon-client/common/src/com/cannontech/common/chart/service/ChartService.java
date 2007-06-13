package com.cannontech.common.chart.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.model.GraphType;

/**
 * Service used to get information for a chart
 */
public interface ChartService {

    /**
     * Method to get a list of graphs to be trended
     * @param pointIds - Ids of points to be graphed
     * @param startDate - Start date for graph
     * @param stopDate - End date for graph
     * @param graphType - Type of graph to be generated
     * @return A list containing one graph per point
     */
    public List<Graph> getGraphs(int[] pointIds, Date startDate, Date stopDate, ChartInterval unit,
                                 GraphType graphType);

    /**
     * Method to get the x-axis tick marks for a given time period and interval
     * @param startDate - Start date for tick marks
     * @param stopDate - End date for tick marks
     * @param interval - Interval between tick marks
     * @return A list of x-axis values
     */
    public List<ChartValue> getXAxisData(Date startDate, Date stopDate, ChartInterval unit);

}