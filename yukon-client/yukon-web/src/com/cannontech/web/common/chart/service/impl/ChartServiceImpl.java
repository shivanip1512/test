package com.cannontech.web.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
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

	@Autowired private RawPointHistoryDao rphDao = null;
	@Autowired private PointDao pointDao = null;
	@Autowired private UnitMeasureDao unitMeasureDao = null;
	@Autowired private DateFormattingService dateFormattingService = null;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a");
    
    @Override
    public List<Graph> getGraphs(List<Integer> pointIds, Date startDate, Date stopDate, ChartInterval interval,
                                 ConverterType converterType, YukonUserContext userContext) {

        List<Graph> graphList = new ArrayList<Graph>();

        int colorIdx = 0;
        ChartColorsEnum[] colors = ChartColorsEnum.values();
        
        for (int pointId : pointIds) {

            // Get the point data for the time period
            List<PointValueHolder> pointData = rphDao.getPointData(pointId, startDate, stopDate);
            LitePoint lPoint = pointDao.getLitePoint(pointId);
            
            // Set up the formatting based on the point unit
            LitePointUnit pointUnit = pointDao.getPointUnit(pointId);
            NumberFormat pointValueFormat = new DecimalFormat();
            pointValueFormat.setMaximumFractionDigits(pointUnit.getDecimalPlaces());
            pointValueFormat.setMinimumFractionDigits(pointUnit.getDecimalPlaces());
            pointValueFormat.setGroupingUsed(false);
            
            LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(pointUnit.getUomID());
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
            String units = messageSourceAccessor.getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));

            // Make a list of each of the data points
            List<ChartValue<Double>> chartData = new ArrayList<ChartValue<Double>>();
            for (PointValueHolder data : pointData) {

                ChartValue<Double> chartValue = new ChartValue<Double>();

                chartValue.setId(data.getPointDataTimeStamp().getTime());
                chartValue.setTime(data.getPointDataTimeStamp().getTime());
                chartValue.setValue(data.getValue());
                chartValue.setDescription("<div>" + pointValueFormat.format(data.getValue()) + "</div>" + "<div>" + units + "</div>" + "<div>" + timeFormat.format(data.getPointDataTimeStamp()) + "</div>" + "<div>" + lPoint.getPointName() + "</div>");
                chartValue.setFormattedValue(pointValueFormat.format(data.getValue()));

                chartData.add(chartValue);
            }
            
            // Assign each chart value to an x-axis spot
            List<ChartValue<Double>> axisChartData = this.setXAxisIds(interval, startDate, chartData);

            // Convert data to specified graph type
            ChartDataConverter converter = converterType.getDataConverter();
            axisChartData = converter.convertValues(axisChartData, interval);

            // graph
            Graph graph = new Graph();
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
    public List<ChartValue<Date>> getXAxisData(Date startDate, Date stopDate, ChartInterval interval, YukonUserContext userContext) {

        Date currDate = interval.roundDownToIntervalUnit(startDate);
        Calendar currCal = Calendar.getInstance();
        currCal.setTime(currDate);
        
        List<ChartValue<Date>> xAxisData = new ArrayList<ChartValue<Date>>();
        DateFormatEnum format = interval.getFormat();
        while (stopDate.compareTo(currCal.getTime()) >= 0) {

            ChartValue<Date> chartValue = new ChartValue<Date>();
            chartValue.setId(currCal.getTimeInMillis());
            chartValue.setValue(currCal.getTime());
            chartValue.setFormattedValue(dateFormattingService.format(currCal.getTime(), format, userContext));

            xAxisData.add(chartValue);

            interval.increment(currCal);
        }

        return xAxisData;
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
        
        // create calendar to make increment more efficient
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(currDate);
        interval.increment(nextCal);
        //Date nextDate = interval.increment(currDate);

        // Iterate through each of the chart values
        for (int position = 0; position < chartData.size();) {

            ChartValue<Double> currValue = chartData.get(position);

            // Move forward to correct time interval for the current value
            while (!(currValue.getId() >= currDate.getTime() && currValue.getId() < nextCal.getTimeInMillis())) {
                currDate = nextCal.getTime();
                interval.increment(nextCal);
            }

            // Find all of the values in the current time interval
            Set<ChartValue<Double>> intervalValues = new HashSet<ChartValue<Double>>();
            while (currValue.getId() < nextCal.getTimeInMillis()) {
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
        	
        	// record maxValue when currentValue is greater than previous maxValue.
        	// if currentValue and maxValue are the same, record maxValue for the one with greater time
            if (maxValue == null || 
            		(currValue.getValue() > maxValue.getValue()) ||
            		(currValue.getValue() == maxValue.getValue() && currValue.getTime() > maxValue.getTime())) {
                maxValue = currValue;
            }
        }

        return maxValue;
    }
}
