package com.cannontech.web.common.chart.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.user.YukonUserContext;

/**
 * Service used to get information for a chart
 */
public interface ChartService {

    /**
     * Method to get a list of graphs to be trended
     * @param pointIds - Ids of points to be graphed
     * @param startDate - Start date for graph
     * @param stopDate - End date for graph
     * @param converterType - Type of graph to be generated
     * @param userContext
     * @return A list containing one graph per point
     */
    public List<Graph> getGraphs(Set<Integer> pointIds, Date startDate, Date stopDate, ChartInterval unit,
                                 ConverterType converterType, YukonUserContext userContext);

    /**
     * Method to get the x-axis tick marks for a given time period and interval
     * @param startDate - Start date for tick marks
     * @param stopDate - End date for tick marks
     * @param interval - Interval between tick marks
     * @return A list of x-axis values
     */
    public List<ChartValue<Date>> getXAxisData(Date startDate, Date stopDate, ChartInterval unit, YukonUserContext userContext);
    
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