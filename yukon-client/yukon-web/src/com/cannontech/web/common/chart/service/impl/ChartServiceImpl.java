package com.cannontech.web.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartColorsEnum;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
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

    private Logger log = YukonLogManager.getLogger(ChartServiceImpl.class);
    
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private PointDao pointDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public Map<Integer,Graph<ChartValue<Double>>> getGraphs(Map<Integer, GraphDetail> graphDetailMap, Date startDate, Date stopDate,
            ChartInterval interval, YukonUserContext userContext) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        Map<Integer,Graph<ChartValue<Double>>> graphMap = new HashMap<>();

        int colorIdx = 0;
        ChartColorsEnum[] colors = ChartColorsEnum.values();

        for (Map.Entry<Integer, GraphDetail> entry : graphDetailMap.entrySet()) {
            int pointId = entry.getKey().intValue();
            GraphDetail graphDetail = entry.getValue();
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
            if (pao.getPaoType() == PaoType.WEATHER_LOCATION) {
                axisChartData = getXAxisValuesForTemperature(interval, chartData);
            } else {
                axisChartData = getXAxisMinMaxValues(interval, chartData, false);
                // Convert data to specified graph type
                ChartDataConverter converter = graphDetail.getConverterType().getDataConverter();
                axisChartData = converter.convertValues(axisChartData, interval);
            }
           
            // graph
            Graph<ChartValue<Double>> graph = new Graph<>();
            graph.setChartData(axisChartData);
            graph.setSeriesTitle(lPoint.getPointName());
            graph.setFormat(pointValueFormat);

            graph.setColor(colors[colorIdx]);
            colorIdx++;
            if (colorIdx == colors.length) {
                colorIdx = 0;
            }

            // don't include zero-data graphs if there are more than one graph - amCharts chokes.
            if (graphDetailMap.size() == 1 || chartData.size() > 0) {
                graphMap.put(pointId, graph);
            }
        }

        return graphMap;

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
     * @return The original chart data list with x-axis ids set
     */
    private List<ChartValue<Double>> getXAxisMinMaxValues(ChartInterval interval, List<ChartValue<Double>> chartData,
            Boolean isMinRequired) {

        List<ChartValue<Double>> minMaxChartValues = new ArrayList<>();
        if (chartData.isEmpty()) {
            return minMaxChartValues;
        }
        long currentInterval = interval.roundDownToIntervalUnit(new Date(chartData.get(0).getId())).getTime();
        ChartValue<Double> currentMax = chartData.get(0);
        ChartValue<Double> currentMin = currentMax;
        for (ChartValue<Double> thisValue : chartData) {
            long thisInterval = interval.roundDownToIntervalUnit(new Date(thisValue.getId())).getTime();
            if (thisInterval != currentInterval) {
                // New interval, add last intervals min(if isMinRequired is true) , max
                addMinMaxValuesinOrder(minMaxChartValues, currentMin, currentMax, isMinRequired);
                currentMax = thisValue;
                if (isMinRequired) {
                    currentMin = thisValue;
                }
                currentInterval = thisInterval;
            } else if (thisValue.getValue() > currentMax.getValue() || isValueRepeated(currentMax, thisValue)) {
                /*
                 * Need to modify thisValue (which will eventually be added to maxChartValues)
                 * to have a time that is also normalized/modified down to interval, instead of it's actual
                 * time.
                 * This should only affect the graph'd data and not any raw data exports
                 */
                currentMax = thisValue;
            } else if (isMinRequired
                && (thisValue.getValue() < currentMin.getValue() || isValueRepeated(currentMin, thisValue))) {
                // Update minimum ChartValue with latest interval.
                currentMin = thisValue;
            }
        }
        // Don't forget the last one
        addMinMaxValuesinOrder(minMaxChartValues, currentMin, currentMax, isMinRequired);
        return minMaxChartValues;
    }
    
    /**
     * Place minimum and maximum chart value in order (whichever comes first add first in the list)
     * @return list of ChartValues.
     */
    private List<ChartValue<Double>> addMinMaxValuesinOrder(List<ChartValue<Double>> minMaxChartValues, ChartValue<Double> min, ChartValue<Double> max, boolean isMinRequired) {
        ChartValue<Double> adjustedMax = adjustForFlotTimezone(max);
        if (isMinRequired) {
            ChartValue<Double> adjustedMin = adjustForFlotTimezone(min);
        if (min.getTime() > max.getTime()) {
                minMaxChartValues.add(adjustedMax);
                minMaxChartValues.add(adjustedMin);
            } else {
                minMaxChartValues.add(adjustedMin);
                minMaxChartValues.add(adjustedMax);
            }
        } else {
            minMaxChartValues.add(adjustedMax);
        }
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
     * 
     * @return List of ChartValues based on Chart interval.
     */
    private List<ChartValue<Double>> getXAxisValuesForTemperature(ChartInterval interval,
            List<ChartValue<Double>> chartData) {
        switch (interval) {
        case FIVEMINUTE:
        case FIFTEENMINUTE:
        case HOUR:
            return chartData;
        case DAY:
        case WEEK:
        case MONTH:
            return getXAxisMinMaxValues(interval, chartData, true);
        default:
            break;
        }
        return null; // This should not happen.
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