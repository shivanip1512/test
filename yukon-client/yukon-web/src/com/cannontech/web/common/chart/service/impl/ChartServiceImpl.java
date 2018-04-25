package com.cannontech.web.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartColorsEnum;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
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
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public List<Graph<ChartValue<Double>>> getGraphs(Set<Integer> pointIds, Date startDate, Date stopDate,
            ChartInterval interval, ConverterType converterType, YukonUserContext userContext) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<Graph<ChartValue<Double>>> graphList = new ArrayList<>();

        int colorIdx = 0;
        ChartColorsEnum[] colors = ChartColorsEnum.values();

        for (int pointId : pointIds) {

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
                messageSourceAccessor.getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));

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
            List<ChartValue<Double>> axisChartData = getXAxisMaxValues(interval, startDate, chartData);

            // Convert data to specified graph type
            ChartDataConverter converter = converterType.getDataConverter();
            axisChartData = converter.convertValues(axisChartData, interval);

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
            if (pointIds.size() == 1 || chartData.size() > 0) {
                graphList.add(graph);
            }
        }

        return graphList;

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
     * @param startDate - Start date for x-axis
     * @param chartData - List of chart data values
     * @return The original chart data list with x-axis ids set
     */
    private List<ChartValue<Double>> getXAxisMaxValues(ChartInterval interval, Date startDate,
            List<ChartValue<Double>> chartData) {

        List<ChartValue<Double>> maxChartValues = new ArrayList<>();
        if (chartData.isEmpty()) {
            return maxChartValues;
        }

        long currentInterval = interval.roundDownToIntervalUnit(new Date(chartData.get(0).getId())).getTime();
        ChartValue<Double> currentMax = chartData.get(0);

        for (ChartValue<Double> thisValue : chartData) {
            long thisInterval = interval.roundDownToIntervalUnit(new Date(thisValue.getId())).getTime();

            if (thisInterval != currentInterval) {
                // New interval, add last intervals max
                ChartValue<Double> adjusted = adjustForFlotTimezone(currentMax);
                maxChartValues.add(adjusted);
                currentMax = thisValue;
                currentInterval = thisInterval;
            } else if (thisValue.getValue() > currentMax.getValue()
                || (currentMax.getValue().doubleValue() == thisValue.getValue().doubleValue() && thisValue.getTime() > currentMax.getTime())) {
                
                /* Need to modify thisValue (which will eventually be added to maxChartValues) 
                 * to have a time that is also normalized/modified down to interval, instead of it's actual time.
                 * This should only affect the graph'd data and not any raw data exports
                 * */
                currentMax = thisValue;

            }
        }
        // Don't forget the last one
        ChartValue<Double> adjusted = adjustForFlotTimezone(currentMax);
        maxChartValues.add(adjusted);

        return maxChartValues;
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