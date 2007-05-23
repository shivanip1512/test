package com.cannontech.common.chart.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.service.ChartService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;

/**
 * Implementation of the ChartService
 */
public class ChartServiceImpl implements ChartService {

    private RawPointHistoryDao rphDao = null;
    private PointDao pointDao = null;
    private SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a");

    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public List<Graph> getGraphs(int[] pointIds, Date startDate, Date stopDate, ChartInterval unit) {

        List<Graph> graphList = new ArrayList<Graph>();

        for (int pointId : pointIds) {

            // Get the point data for the time period
            List<PointValueHolder> pointData = rphDao.getPointData(pointId, startDate, stopDate);

            // Make a list of each of the data points
            List<ChartValue> chartData = new ArrayList<ChartValue>();
            for (PointValueHolder data : pointData) {

                ChartValue value1 = new ChartValue();

                value1.setId(data.getPointDataTimeStamp().getTime());
                value1.setValue(String.valueOf(data.getValue()));
                value1.setDescription(format.format(data.getPointDataTimeStamp()));
                chartData.add(value1);
            }

            List<ChartValue> axisChartData = this.setXAxisIds(unit, startDate, chartData);

            // Get the lite point for the point name
            LitePoint lPoint = pointDao.getLitePoint(pointId);

            Graph graph = new Graph();
            graph.setChartData(axisChartData);
            graph.setSeriesTitle(lPoint.getPointName());
            graphList.add(graph);
        }

        return graphList;

    }

    public List<ChartValue> getXAxisData(Date startDate, Date stopDate, ChartInterval interval) {

        Date currDate = interval.roundDownToIntervalUnit(startDate);
        List<ChartValue> xAxisData = new ArrayList<ChartValue>();
        while (stopDate.compareTo(currDate) >= 0) {

            String formatString = interval.format(currDate);
            xAxisData.add(new ChartValue(currDate.getTime(), formatString));

            currDate = interval.increment(currDate);
        }

        return xAxisData;
    }

    /**
     * Helper method to set the x-axis id for a list of chart data
     * @param interval - Time interval for the x-axis
     * @param startDate - Start date for x-axis
     * @param chartData - List of chart data values
     * @return The original chart data list with x-axis ids set
     */
    private List<ChartValue> setXAxisIds(ChartInterval interval, Date startDate,
            List<ChartValue> chartData) {

        List<ChartValue> valuesForAxis = new ArrayList<ChartValue>();

        // Round the start date down to the nearest interval
        Date currDate = interval.roundDownToIntervalUnit(startDate);
        Date nextDate = interval.increment(currDate);

        Iterator<ChartValue> valueIter = chartData.iterator();
        // Get the first chart value if there is one
        ChartValue currValue = null;
        if (valueIter.hasNext()) {
            currValue = valueIter.next();
        }

        // Iterate through each of the chart values
        while (valueIter.hasNext()) {

            // Move forward to correct time interval for the current value
            while (!(currValue.getId() >= currDate.getTime() && currValue.getId() < nextDate.getTime())) {
                currDate = nextDate;
                nextDate = interval.increment(nextDate);
            }

            // Find all of the values in the current time interval
            Set<ChartValue> intervalValues = new HashSet<ChartValue>();
            while (currValue.getId() < nextDate.getTime() && valueIter.hasNext()) {
                intervalValues.add(currValue);
                currValue = valueIter.next();
            }

            // Get the max value in the current time interval, set it's axis id
            // and add it to the chart values
            ChartValue maxValue = getMaxValue(intervalValues);
            maxValue.setId(currDate.getTime());
            valuesForAxis.add(maxValue);

        }

        return valuesForAxis;

    }

    /**
     * Helper method to get the chart value with the maximum value from a list
     * @param intervalValues - List to get max from
     * @return - The chart value with the max value
     */
    private ChartValue getMaxValue(Set<ChartValue> intervalValues) {

        ChartValue maxValue = null;

        for (ChartValue currValue : intervalValues) {
            if (maxValue == null || Double.valueOf(currValue.getValue()) >= Double.valueOf(maxValue.getValue())) {
                maxValue = currValue;
            }
        }

        return maxValue;
    }
}
