package com.cannontech.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.ChartService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteUnitMeasure;

/**
 * Implementation of the ChartService
 */
public class ChartServiceImpl implements ChartService {

    private RawPointHistoryDao rphDao = null;
    private PointDao pointDao = null;
    private UnitMeasureDao unitMeasureDao = null;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a");

    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }

    public List<Graph> getGraphs(int[] pointIds, Date startDate, Date stopDate, ChartInterval unit,
            GraphType graphType) {

        List<Graph> graphList = new ArrayList<Graph>();

        for (int pointId : pointIds) {

            // Get the point data for the time period
            List<PointValueHolder> pointData = rphDao.getPointData(pointId, startDate, stopDate);

            // Set up the formatting based on the point unit
            LitePointUnit pointUnit = pointDao.getPointUnit(pointId);
            NumberFormat pointValueFormat = new DecimalFormat();
            pointValueFormat.setMaximumFractionDigits(pointUnit.getDecimalPlaces());
            pointValueFormat.setMinimumFractionDigits(pointUnit.getDecimalPlaces());
            pointValueFormat.setGroupingUsed(false);
            
            LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(pointUnit.getUomID());
            String units = graphType.getUnits(unitMeasure);

            // Make a list of each of the data points
            List<ChartValue<Double>> chartData = new ArrayList<ChartValue<Double>>();
            for (PointValueHolder data : pointData) {

                ChartValue<Double> chartValue = new ChartValue<Double>();

                chartValue.setId(data.getPointDataTimeStamp().getTime());
                chartValue.setTime(data.getPointDataTimeStamp().getTime());
                chartValue.setValue(data.getValue());
                chartValue.setDescription(units + "\n" + timeFormat.format(data.getPointDataTimeStamp()));
                chartValue.setFormat(pointValueFormat);

                chartData.add(chartValue);
            }
            
            // Assign each chart value to an x-axis spot
            List<ChartValue<Double>> axisChartData = this.setXAxisIds(unit, startDate, chartData);

            // Convert data to specified graph type
            ChartDataConverter converter = graphType.getDataConverter();
            axisChartData = converter.convertValues(axisChartData, unit);

            // Get the lite point for the point name
            LitePoint lPoint = pointDao.getLitePoint(pointId);

            Graph graph = new Graph();
            graph.setChartData(axisChartData);
            graph.setSeriesTitle(lPoint.getPointName());
            graph.setFormat(pointValueFormat);
            graphList.add(graph);
        }

        return graphList;

    }

    public List<ChartValue> getXAxisData(Date startDate, Date stopDate, ChartInterval interval) {

        Date currDate = interval.roundDownToIntervalUnit(startDate);
        List<ChartValue> xAxisData = new ArrayList<ChartValue>();
        while (stopDate.compareTo(currDate) >= 0) {

            ChartValue<Date> chartValue = new ChartValue<Date>();
            chartValue.setId(currDate.getTime());
            chartValue.setValue(currDate);
            chartValue.setFormat(interval.getFormat());

            xAxisData.add(chartValue);

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
    private List<ChartValue<Double>> setXAxisIds(ChartInterval interval, Date startDate,
            List<ChartValue<Double>> chartData) {

        List<ChartValue<Double>> valuesForAxis = new ArrayList<ChartValue<Double>>();

        // Round the start date down to the nearest interval
        Date currDate = interval.roundDownToIntervalUnit(startDate);
        Date nextDate = interval.increment(currDate);

        // Iterate through each of the chart values
        for (int position = 0; position < chartData.size();) {

            ChartValue<Double> currValue = chartData.get(position);

            // Move forward to correct time interval for the current value
            while (!(currValue.getId() >= currDate.getTime() && currValue.getId() < nextDate.getTime())) {
                currDate = nextDate;
                nextDate = interval.increment(nextDate);
            }

            // Find all of the values in the current time interval
            Set<ChartValue<Double>> intervalValues = new HashSet<ChartValue<Double>>();
            while (currValue.getId() < nextDate.getTime()) {
                intervalValues.add(currValue);

                if (++position < chartData.size()) {
                    currValue = chartData.get(position);
                } else {
                    break;
                }
            }

            // Get the max value in the current time interval, set it's axis id
            // and add it to the chart values
            ChartValue<Double> maxValue = getMaxValue(intervalValues);
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
    private ChartValue<Double> getMaxValue(Set<ChartValue<Double>> intervalValues) {

        ChartValue<Double> maxValue = null;

        for (ChartValue<Double> currValue : intervalValues) {
            if (maxValue == null || currValue.getValue() >= maxValue.getValue()) {
                maxValue = currValue;
            }
        }

        return maxValue;
    }
}
