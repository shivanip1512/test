package com.cannontech.web.common.chart.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.cannontech.web.common.chart.model.FlotOptionKey;
import com.cannontech.web.common.chart.model.FlotPieDatas;
import com.cannontech.web.common.chart.service.ChartService;
import com.cannontech.web.common.chart.service.FlotChartService;

public class FlotChartServiceImpl implements FlotChartService {

    @Autowired private ChartService chartService;

    @Override
    public JSONObject getMeterGraphData(Set<Integer> pointIds, Instant start, Instant stop, Double yMin, Double yMax,
                                       ChartInterval interval, ConverterType converterType, GraphType graphType, String yLabel,
                                       YukonUserContext userContext) {
        List<Graph> graphs = chartService.getGraphs(pointIds,
                                                    start.toDate(),
                                                    stop.toDate(),
                                                    interval,
                                                    converterType,
                                                    userContext);
        // datas
        JSONObject dataAndOptions = new JSONObject();
        JSONArray jsonDataContainer = new JSONArray();
        for (Graph graph : graphs) {
            JSONArray jsonArrayContainer = new JSONArray();
            List<ChartValue<?>> chartData = (List<ChartValue<?>>) graph.getChartData();
            buildUpChartValueJsonData(jsonArrayContainer, chartData);
            JSONObject dataObj = new JSONObject();
            dataObj.put("data", jsonArrayContainer);
            jsonDataContainer.add(dataObj);
        }
        /* if we have no data, then add an empty array to jsonData so a blank graph is displayed properly */
        if (jsonDataContainer.isEmpty()) jsonDataContainer.add(new JSONArray());
        
        // options
        Integer xAxisDataCount = chartService.getXAxisDataCount(start.toDate(),
                                                                stop.toDate(),
                                                                interval);
        int barWidthForTime = getBarWidthForTime(start, stop, xAxisDataCount);
        
        JSONObject options = new JSONObject();

        JSONObject series = new JSONObject();
        JSONObject bars = new JSONObject();
        setFlotOption(bars, FlotOptionKey.SERIES_BARS_BARWIDTH, barWidthForTime);
        setFlotOption(series, FlotOptionKey.SERIES_BARS, bars);
        setFlotOption(options, FlotOptionKey.SERIES, series);

        JSONObject yAxis = new JSONObject();
        setFlotOption(yAxis, FlotOptionKey.YAXIS_POSITION, "left");
        setFlotOption(yAxis, FlotOptionKey.YAXIS_AXISLABEL, yLabel);
        
        if (yMin == null) yMin = 0.0; 
        setFlotOption(yAxis, FlotOptionKey.YAXIS_MIN, yMin);
        if (yMax != null) setFlotOption(yAxis, FlotOptionKey.YAXIS_MAX, yMax);
        setFlotOption(options, FlotOptionKey.YAXIS, yAxis);
        
        JSONObject xAxis = new JSONObject();
        setFlotOption(xAxis, FlotOptionKey.XAXIS_MODE, "time");
        setFlotOption(xAxis, FlotOptionKey.XAXIS_MIN, start.getMillis());
        setFlotOption(xAxis, FlotOptionKey.XAXIS_MAX, stop.getMillis());
        setFlotOption(xAxis, FlotOptionKey.XAXIS_AUTOSCALEMARGIN, 0.1);
        setFlotOption(options, FlotOptionKey.XAXIS, xAxis);
        
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", graphType.getFlotType());
        dataAndOptions.put("options", options);
        
        return dataAndOptions;
    }

    private void buildUpChartValueJsonData(JSONArray jsonArrayContainer, List<ChartValue<?>> chartData) {
        for (ChartValue<?> chartValue : chartData) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(chartValue.getId()); // x
            jsonArray.add(chartValue.getValue());// y

            JSONObject obj = new JSONObject();
            obj.put("tooltip", chartValue.getDescription());
            jsonArray.add(obj);

            jsonArrayContainer.add(jsonArray);
        }
    }
    
    @Deprecated /* use getPieGraphDataWithColor(Map<String, FlotPieDatas> labelValueMap) */
    @Override
    public JSONObject getPieGraphData(Map<String, Integer> labelValueMap) {
        JSONObject dataAndOptions = new JSONObject();
        JSONArray jsonDataContainer = new JSONArray();
        for (Entry<String, Integer> labelValue : labelValueMap.entrySet()) {
            JSONObject dataObj = new JSONObject();
            dataObj.put("label", labelValue.getKey());
            dataObj.put("data", labelValue.getValue());
            dataObj.put("tooltip", labelValue.getKey() + ": " + labelValue.getValue());
            jsonDataContainer.add(dataObj);
        }
        
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", GraphType.PIE.getFlotType());
        return dataAndOptions;
    }

    @Override
    public JSONObject getPieGraphDataWithColor(Map<String, FlotPieDatas> labelValueMap, boolean showLegend, boolean showLabels, double radiusPercent) {
        JSONObject dataAndOptions = new JSONObject();
        JSONArray jsonDataContainer = new JSONArray();
        for (Entry<String, FlotPieDatas> labelValue : labelValueMap.entrySet()) {
            JSONObject dataObj = new JSONObject();
            dataObj.put("label", labelValue.getKey());
            dataObj.put("data", labelValue.getValue().getData());
            dataObj.put("tooltip", labelValue.getKey() + ": " + labelValue.getValue().getData());
            String color = labelValue.getValue().getColor();
            if (!StringUtils.isEmpty(color)) {
                // if not specified, flot chooses its own color
                dataObj.put("color", color);
            }
            jsonDataContainer.add(dataObj);
        }
        
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", GraphType.PIE.getFlotType());
        
        /* Hide/show the labels and legend, and set the radius */
        JSONObject pie = new JSONObject();
        pie.put("radius", radiusPercent);
        JSONObject label = new JSONObject();
        label.put("show", showLabels);
        pie.put("label", label);
        
        JSONObject series = new JSONObject();
        series.put("pie", pie);
        
        JSONObject legend = new JSONObject();
        legend.put("show", showLegend);
        
        JSONObject options = new JSONObject();
        options.put("series", series);
        options.put("legend", legend);
        
        dataAndOptions.put("options", options);
        
        return dataAndOptions;
    }

    @Override
    public JSONObject getIVVCGraphData(VfGraph graph, boolean includeTitles) {
        JSONObject dataAndOptions = new JSONObject();
        JSONArray jsonDataContainer = new JSONArray();

        /* data */
        for (VfLine line : graph.getLines()) {
            JSONArray linesArray = new JSONArray();
            VfPoint labelPoint = null;
            for (VfPoint point : line.getPoints()) {
                JSONArray pointArray = new JSONArray();
                pointArray.add(point.getX());
                pointArray.add(point.getY());

                if (includeTitles && point.isRegulator() && line.getZoneName() != null
                    && (labelPoint == null || labelPoint.getY() < point.getY())) {
                    labelPoint = point;
                }

                JSONObject obj = new JSONObject();
                obj.put("tooltip", point.getDescription());
                pointArray.add(obj);
                linesArray.add(pointArray);
            }
            JSONObject dataObj = new JSONObject();
            if (includeTitles && labelPoint != null) {
                dataObj.put("title", line.getZoneName());
                dataObj.put("titleXPos", labelPoint.getX());
                dataObj.put("titleYPos", labelPoint.getY());
            }

            dataObj.put("lineName", line.getLineName());
            dataObj.put("data", linesArray);
            dataObj.put("color", line.getSettings().getColor());
            dataObj.put("phase", line.getPhase());
            jsonDataContainer.add(dataObj);
        }
        /* if we have no data, then add an empty array to jsonData so a blank graph is displayed properly */
        boolean noData = jsonDataContainer.isEmpty();
        if (noData) jsonDataContainer.add(new JSONArray());

        /* options */
        JSONObject options = new JSONObject();
        
        JSONObject xAxis = new JSONObject();
        setFlotOption(xAxis, FlotOptionKey.XAXIS_AUTOSCALEMARGIN, 0.1);
        
        if (noData) setFlotOption(xAxis, FlotOptionKey.XAXIS_MIN, 0);
        setFlotOption(options, FlotOptionKey.XAXIS, xAxis);
        
        JSONObject yAxis = new JSONObject();
        setFlotOption(yAxis, FlotOptionKey.YAXIS_POSITION, "left");
        setFlotOption(yAxis, FlotOptionKey.YAXIS_AXISLABEL, graph.getSettings().getYAxisLabel());
        setFlotOption(yAxis, FlotOptionKey.YAXIS_MIN, graph.getSettings().getyMin());
        setFlotOption(yAxis, FlotOptionKey.YAXIS_MAX, graph.getSettings().getyMax());
        setFlotOption(yAxis, FlotOptionKey.YAXIS_AUTOSCALEMARGIN, 0.1);
        setFlotOption(options, FlotOptionKey.YAXIS, yAxis);
        
        JSONObject markingsObj = new JSONObject();
        JSONArray markingsArray = new JSONArray();
        JSONObject markingStrategyHigh = new JSONObject();
        JSONObject markingStrategyLow = new JSONObject();

        JSONObject yAxisFrom = new JSONObject();
        JSONObject yAxisTo = new JSONObject();
        setFlotOption(yAxisFrom, FlotOptionKey.GRID_MARKINGS_YAXIS_FROM, graph.getSettings().getYUpperBound());
        setFlotOption(yAxisTo, FlotOptionKey.GRID_MARKINGS_YAXIS_TO, graph.getSettings().getYLowerBound());
        
        setFlotOption(markingStrategyHigh, FlotOptionKey.GRID_MARKINGS_COLOR, "#f1f1f1");
        setFlotOption(markingStrategyHigh, FlotOptionKey.GRID_MARKINGS_YAXIS, yAxisFrom);
        setFlotOption(markingStrategyLow, FlotOptionKey.GRID_MARKINGS_COLOR, "#f1f1f1");
        setFlotOption(markingStrategyLow, FlotOptionKey.GRID_MARKINGS_YAXIS, yAxisTo);
        
        markingsArray.add(markingStrategyHigh);
        markingsArray.add(markingStrategyLow);
        
        setFlotOption(markingsObj, FlotOptionKey.GRID_MARKINGS, markingsArray);
        setFlotOption(options, FlotOptionKey.GRID, markingsObj);
        
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", GraphType.LINE.getFlotType());
        dataAndOptions.put("options", options);
        
        return dataAndOptions;
    }
    
    private void setFlotOption(JSONObject jsonObject, FlotOptionKey flotOptionKey, Object value) {
        jsonObject.put(flotOptionKey.getKey(), value);
    }

    /**
     * Method to return the width of the individual bars in units of the x axis,
     * contrary to most other measures that are specified in pixels. For instance, for time
     * series the unit is milliseconds so 1000*60*60*24 produces bars with the width of a day.
     * The API option for this is "barWidth".
     * 
     * For more info see: https://github.com/flot/flot/blob/master/API.md
     */
    private int getBarWidthForTime(Instant start, Instant end, int numDataPoints) {
        long milliDiff = Math.abs(start.getMillis() - end.getMillis());
        int barWidth = (int) Math.ceil(milliDiff / numDataPoints);
        return barWidth;
    }

    /**
     * The flotchart timeformat to use given a start and end date.
     * 
     * Specific format options: (from https://github.com/flot/flot/blob/master/API.md)
     * 
     * %a: weekday name (customizable)
     * %b: month name (customizable)
     * %d: day of month, zero-padded (01-31)
     * %e: day of month, space-padded ( 1-31)
     * %H: hours, 24-hour time, zero-padded (00-23)
     * %I: hours, 12-hour time, zero-padded (01-12)
     * %m: month, zero-padded (01-12)
     * %M: minutes, zero-padded (00-59)
     * %q: quarter (1-4)
     * %S: seconds, zero-padded (00-59)
     * %y: year (two digits)
     * %Y: year (four digits)
     * %p: am/pm
     * %P: AM/PM (uppercase version of %p)
     * %w: weekday as number (0-6, 0 being Sunday)
     */
    private String getTimeFormat(Instant start, Instant end) {
        long milliDiff = Math.abs(start.getMillis() - end.getMillis());
        int dayDiff = (int) Math.floor(milliDiff / 1000 / 60 / 60 / 24);

        if (dayDiff <= 1) {
            return "%h:%m";
        }
        return "%m/%d";
    }
}
