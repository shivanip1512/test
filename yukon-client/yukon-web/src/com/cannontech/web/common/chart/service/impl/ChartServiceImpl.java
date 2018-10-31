package com.cannontech.web.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.ChartService;

/**
 * Implementation of the ChartService
 */
public class ChartServiceImpl implements ChartService {
    
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private PointDao pointDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public List<Graph<ChartValue<Double>>> getGraphs(List<GraphDetail> graphDetails, Date startDate, Date stopDate,
            ChartInterval interval, YukonUserContext userContext, GraphType graphType) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<Graph<ChartValue<Double>>> graphs = new ArrayList<>();

        for (GraphDetail graphDetail : graphDetails) {
            int pointId = graphDetail.getPointId();
            // Get the point data for the time period
            
            List<PointValueHolder> pointData = rphDao.getPointDataWithDisabledPaos(pointId, startDate, stopDate);
            LitePoint lPoint = pointDao.getLitePoint(pointId);

            // Set up the formatting based on the point unit
            LitePointUnit pointUnit = pointDao.getPointUnit(pointId);
            NumberFormat pointValueFormat = new DecimalFormat();
            pointValueFormat.setMaximumFractionDigits(pointUnit.getDecimalPlaces());
            pointValueFormat.setMinimumFractionDigits(pointUnit.getDecimalPlaces());
            pointValueFormat.setGroupingUsed(false);

            LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(pointUnit.getUomID());
            String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
            String units =
                messageSourceAccessor.getMessage(graphDetail.getConverterType().getFormattedUnits(unitMeasure, chartIntervalString));

            // Make a list of each of the data points
            List<ChartValue<Double>> chartData = new ArrayList<>();
            for (PointValueHolder data : pointData) {
                ChartValue<Double> chartValue = new ChartValue<>();

                long timeStamp = data.getPointDataTimeStamp().getTime();

                chartValue.setId(timeStamp);
                chartValue.setTime(timeStamp); // x-axis
                chartValue.setValue(data.getValue()); // y-axis
                chartValue.setUnits(units);
                chartValue.setPointName(lPoint.getPointName());
                chartValue.setFormattedValue(pointValueFormat.format(data.getValue()));

                chartData.add(chartValue);
            }

            // Assign each chart value to an x-axis spot
            List<ChartValue<Double>> axisChartData = new ArrayList<>();
            LiteYukonPAObject pao = cache.getAllPaosMap().get(lPoint.getPaobjectID());
            
            Graph<ChartValue<Double>> graph = new Graph<>();
            if (pao.getPaoType() == PaoType.WEATHER_LOCATION) {
                if (interval.getMillis() >= ChartInterval.DAY.getMillis()) {
                    axisChartData = getXAxisMinMaxValues(interval, chartData, graphDetail.isMin());
                } else {
                    axisChartData = chartData;
                }
                graph.setLines(graphDetail.getLines());
                graph.setPoints(graphDetail.getPoints());
            } else {
                axisChartData = getXAxisMinMaxValues(interval, chartData, graphDetail.isMin());
                // Convert data to specified graph type
                ChartDataConverter converter = graphDetail.getConverterType().getDataConverter();
                axisChartData = converter.convertValues(axisChartData, interval);
                if (graphType == GraphType.COLUMN) {
                    graph.setBars(graphDetail.getBars());
                } else if (graphType == GraphType.LINE) {
                    graph.setLines(graphDetail.getLines());
                    graph.setPoints(graphDetail.getPoints());
                }
            }

            // graph
            graph.setColor(graphDetail.getChartColors());
            graph.setChartData(axisChartData);
            graph.setSeriesTitle(lPoint.getPointName());
            graph.setFormat(pointValueFormat);
            graph.setAxisIndex(graphDetail.getAxisIndex());

            // don't include zero-data graphs if there are more than one graph - amCharts chokes.
            if (graphDetails.size() == 1 || chartData.size() > 0) {
                graphs.add(graph);
            }
        }

        return graphs;

    }

    @Override
    public int getXAxisDataCount(Date startDate, Date stopDate, ChartInterval interval) {

        Date currDate = interval.roundDownToIntervalUnit(startDate);
        Calendar currCal = Calendar.getInstance();
        currCal.setTime(currDate);

        int count = 0;
        while (stopDate.compareTo(currCal.getTime()) >= 0) {
            count++;
            interval.increment(currCal);
        }

        return count;
    }

    /**
     * Helper method to set the x-axis id for a list of chart data
     *
     * @param interval - Time interval for the x-axis
     * @param chartData - List of chart data values
     * @param isMinRequired - type of values needs to be filtered(min or max) from chartData
     * @return The original chart data list with x-axis ids set
     */
    private List<ChartValue<Double>> getXAxisMinMaxValues(ChartInterval interval, List<ChartValue<Double>> chartData,
            Boolean isMinRequired) {

        List<ChartValue<Double>> minMaxChartValues = new ArrayList<>();
        if (chartData.isEmpty()) {
            return minMaxChartValues;
        }
        long currentInterval = interval.roundDownToIntervalUnit(new Date(chartData.get(0).getId())).getTime();
        ChartValue<Double> currentMinMax = chartData.get(0);
        for (ChartValue<Double> thisValue : chartData) {
            long thisInterval = interval.roundDownToIntervalUnit(new Date(thisValue.getId())).getTime();
            if (thisInterval != currentInterval) {
                // New interval, add last intervals min(if isMinRequired is true) , max
                ChartValue<Double> adjustedValue = adjustForFlotTimezone(currentMinMax);
                minMaxChartValues.add(adjustedValue);
                currentMinMax = thisValue;
                currentInterval = thisInterval;
            } else if (!isMinRequired && (thisValue.getValue() > currentMinMax.getValue() || isValueRepeated(currentMinMax, thisValue))) {
                /*
                 * Need to modify thisValue (which will eventually be added to maxChartValues)
                 * to have a time that is also normalized/modified down to interval, instead of it's actual
                 * time.
                 * This should only affect the graph'd data and not any raw data exports
                 */
                currentMinMax = thisValue;
            } else if (isMinRequired && (thisValue.getValue() < currentMinMax.getValue() || isValueRepeated(currentMinMax, thisValue))) {
                // Update minimum ChartValue with latest interval.
                currentMinMax = thisValue;
            }
        }
        // Don't forget the last one
        minMaxChartValues.add(adjustForFlotTimezone(currentMinMax));
        return minMaxChartValues;
    }

    /**
     * 
     * @return true if current ChartValue value is same as old ChartValue.
     */
    private boolean isValueRepeated(ChartValue<Double> oldValue, ChartValue<Double> currentValue) {
        return oldValue.getValue().doubleValue() == currentValue.getValue().doubleValue()
                                                    && currentValue.getTime() > oldValue.getTime();
    }
    /**
     * jquery.flot.js v 0.7 does not support time zones and always displays UTC time
     * Here we fake it out by adding the server timezone offset to the timestamp
     * so the times line up between the plot and the data.
     */
    private ChartValue<Double> adjustForFlotTimezone(ChartValue<Double> originalChartValue) {
        ChartValue<Double> adjusted = new ChartValue<Double>(originalChartValue);
        long timeStamp = adjusted.getTime();
        timeStamp += TimeZone.getDefault().getOffset(timeStamp);
        adjusted.setTime(timeStamp);
        adjusted.setId(timeStamp);
        return adjusted;
    }
}