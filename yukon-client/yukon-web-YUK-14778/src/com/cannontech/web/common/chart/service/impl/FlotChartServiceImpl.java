package com.cannontech.web.common.chart.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.awt.Color;

public class FlotChartServiceImpl implements FlotChartService {

    @Autowired private ChartService chartService;

    @Override
    public Map<String, Object> getMeterGraphData(Set<Integer> pointIds, Instant start, Instant stop, Double yMin,
                                                 Double yMax,  ChartInterval interval, ConverterType converterType,
                                                 GraphType graphType, String yLabel, YukonUserContext userContext) {
        List<Graph<ChartValue<Double>>> graphs = 
                chartService.getGraphs(pointIds, start.toDate(), stop.toDate(), interval, converterType, userContext);

        // datas
        List<Object> jsonDataContainer = new ArrayList<>();
        for (Graph<ChartValue<Double>> graph : graphs) {
            List<Object> dataArray = getDataArray(graph.getChartData());
            jsonDataContainer.add(Collections.singletonMap("data", dataArray));
        }
        // if we have no data, then add an empty array to jsonData so a blank graph is displayed properly
        if (graphs.isEmpty()) {
            jsonDataContainer.add(Collections.emptyList());
        }

        // options
        int xAxisDataCount = chartService.getXAxisDataCount(start.toDate(), stop.toDate(), interval);
        int barWidthForTime = getBarWidthForTime(start, stop, xAxisDataCount);

        Map<String, ?> bars = Collections.singletonMap(FlotOptionKey.SERIES_BARS_BARWIDTH.getKey(), barWidthForTime);
        Map<String, ?> series = Collections.singletonMap(FlotOptionKey.SERIES_BARS.getKey(), bars);

        Map<String, Object> options = Maps.newHashMapWithExpectedSize(3);
        options.put(FlotOptionKey.SERIES.getKey(), series);

        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put(FlotOptionKey.YAXIS_POSITION.getKey(), "left");
        yAxis.put(FlotOptionKey.YAXIS_AXISLABEL.getKey(), yLabel);

        if (yMin != null) {
            yAxis.put(FlotOptionKey.YAXIS_MIN.getKey(), yMin);
        }
        if (yMax != null) {
            yAxis.put(FlotOptionKey.YAXIS_MAX.getKey(), yMax);
        }
        options.put(FlotOptionKey.YAXIS.getKey(), yAxis);

        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put(FlotOptionKey.XAXIS_MODE.getKey(), "time");

        /*
         * jquery.flot.js v 0.7 does not support time zones and always displays UTC time
         * Here we fake it out by adding the server timezone offset to the timestamp
         * so the times line up between the plot and the data.
         */
        long xAxisMin = start.getMillis() + TimeZone.getDefault().getOffset(start.getMillis());
        long xAxisMax = stop.getMillis() + TimeZone.getDefault().getOffset(stop.getMillis());
        xAxis.put(FlotOptionKey.XAXIS_MIN.getKey(), xAxisMin);
        xAxis.put(FlotOptionKey.XAXIS_MAX.getKey(), xAxisMax);

        xAxis.put(FlotOptionKey.XAXIS_AUTOSCALEMARGIN.getKey(), 0.1);
        options.put(FlotOptionKey.XAXIS.getKey(), xAxis);

        Map<String, Object> dataAndOptions = Maps.newHashMapWithExpectedSize(3);
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", graphType.getFlotType());
        dataAndOptions.put("options", options);

        return dataAndOptions;
    }

    private List<Object> getDataArray(List<ChartValue<Double>> chartData) {
        List<Object> jsonArrayContainer = new ArrayList<>();
        for (ChartValue<Double> chartValue : chartData) {
            List<Object> jsonArray = new ArrayList<>();
            jsonArray.add(chartValue.getId()); // x
            jsonArray.add(chartValue.getValue()); // y
            jsonArray.add(Collections.singletonMap("tooltip", chartValue.getDescription()));
            jsonArrayContainer.add(jsonArray);
        }
        return jsonArrayContainer;
    }
    
    @Deprecated /* use getPieGraphDataWithColor(Map<String, FlotPieDatas> labelValueMap) */
    @Override
    public Map<String, Object> getPieGraphData(Map<String, Integer> labelValueMap) {
        Map<String, Object> dataAndOptions = Maps.newHashMapWithExpectedSize(2);
        List<Object> jsonDataContainer = new ArrayList<>();
        for (Entry<String, Integer> labelValue : labelValueMap.entrySet()) {
            Map<String, Object> dataObj = Maps.newHashMapWithExpectedSize(3);
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
    public Map<String, Object> getPieGraphDataWithColor(Map<String, FlotPieDatas> labelValueMap, boolean showLegend,
                                                        boolean showLabels, double radiusPercent) {
        List<Object> jsonDataContainer = new ArrayList<>();
        for (Entry<String, FlotPieDatas> labelValue : labelValueMap.entrySet()) {
            Map<String, Object> dataObj = new HashMap<>();
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

        Map<String, Object> dataAndOptions = Maps.newHashMapWithExpectedSize(3);
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", GraphType.PIE.getFlotType());

        /* Hide/show the labels and legend, and set the radius */
        Map<String, Object> pie = Maps.newHashMapWithExpectedSize(2);
        pie.put("radius", radiusPercent);
        pie.put("label", Collections.singletonMap("show", showLabels));

        Map<String, Object> options = Maps.newHashMapWithExpectedSize(2);
        options.put("series", Collections.singletonMap("pie", pie));
        options.put("legend", Collections.singletonMap("show", showLegend));

        dataAndOptions.put("options", options);

        return dataAndOptions;
    }

    @Override
    public Map<String, Object> getIVVCGraphData(VfGraph graph, boolean includeTitles) {
        List<Object> seriesData = new ArrayList<>();
        /*format
         * [{name:'',
         *  phase:'',
         *  zone: '',
         *  color: '',
         *  marker: {object},**static in this version
         *  data: {timeStamp, device, x, y, color},
         *  }]
         * */
        /* data */
        boolean noData = graph.getLines().isEmpty();
        for (VfLine line : graph.getLines()) {
            List<Object> linesArray = new ArrayList<>();
            VfPoint labelPoint = null;
            for (VfPoint point : line.getPoints()) {
                
                Map<String, Object> dataPlot = new HashMap<>();
                dataPlot.put("x", point.getX());
                dataPlot.put("y", point.getY());
                dataPlot.put("color",line.getSettings().getColor());
                dataPlot.put("timestamp", point.getTimestamp());
                dataPlot.put("event", point.getEventName());

                if (includeTitles && point.isRegulator() && line.getZoneName() != null
                    && (labelPoint == null || labelPoint.getY() < point.getY())) {
                    labelPoint = point;
                }
                linesArray.add(dataPlot);
            }
            Map<String, Object> dataObj = new HashMap<>();
            if (includeTitles && labelPoint != null) {
                dataObj.put("title", line.getZoneName());
                dataObj.put("titleXPos", labelPoint.getX());
                dataObj.put("titleYPos", labelPoint.getY());
            }
            /*marker section:*/
            Map<String, Object> markerObject = new HashMap<>();
            markerObject.put("radius", 4);
            String markerFillColor = hex2Rgb(line.getSettings().getColor(), 0);
            markerObject.put("fillColor", markerFillColor);
            markerObject.put("lineWidth", "'2'");
            markerObject.put("lineColor", line.getSettings().getColor());
            markerObject.put("symbol", "circle");
            Map<String, Object> markerStates = new HashMap<>();
            Map<String, Object> markerStatesHover = new HashMap<>();
            markerStatesHover.put("radius", 2);
            markerStates.put("hover", markerStatesHover);
            markerObject.put("states", markerStates);
            /*--marker section end---*/
            /*build single series*/
            String lineIdentifier = line.getZoneName() + " Phase:" +line.getPhase();
            dataObj.put("name", lineIdentifier);
            dataObj.put("data", linesArray);
            dataObj.put("color", line.getSettings().getColor());
            dataObj.put("phase", line.getPhase());
            dataObj.put("zone",line.getZoneName());
            dataObj.put("marker", markerObject);
            seriesData.add(dataObj);
        }
        /* if we have no data, then add an empty array to jsonData so a blank graph is displayed properly */
        if (noData) {
            seriesData.add(Collections.emptyList());
        }
        /* Build chart configuration options */
        
        Map<String, Object> chart = new HashMap<>();
        chart.put("type", GraphType.LINE.getFlotType());
        
        Map<String, Object> title = new HashMap<>();
        title.put("text", graph.getSettings().getGraphTitle());
        
        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put("tickInterval", 1);
        
        Map<String, Object> yAxis = new HashMap<>();
        Map<String, Object> yTitle = new HashMap<>();
        yTitle.put("text", graph.getSettings().getYAxisLabel());
        yAxis.put("title", yTitle);
        
        Map<String, Object> legend = new HashMap<>();
        legend.put("layout", "vertical");
        legend.put("align", "center");
   
        
        Map<String, Object> plotOptions = new HashMap<>();
        Map<String, Object> line = new HashMap<>();
        Map<String, Object> dataLabels = new HashMap<>();
        dataLabels.put("enabled", true);
        dataLabels.put("format", "Phase: {series.options.phase} Zone:{series.options.zone}");
        line.put("enableMouseTracking", true);
        line.put("dataLabels", dataLabels);
        plotOptions.put("line", line);

        Map<String, Object> tooltip = new HashMap<>();
        tooltip.put("backgroundColor", "#F5D72C");
        tooltip.put("shared", false);
        tooltip.put("useHTML", true);
        tooltip.put("headerFormat", "<b>Voltage: {point.y}</b><br>");
        tooltip.put("pointFormat", "Phase:{series.options.phase} <br>Event: {point.event} <br>Time:{point.timeStamp} <br>Zone: {series.options.zone}");
        tooltip.put("valueDecimals", 2);
        
        //Map<String, ?> yAxisFrom = Collections.singletonMap(FlotOptionKey.GRID_MARKINGS_YAXIS_FROM.getKey(),
        //                                                    graph.getSettings().getYUpperBound());
        //Map<String, ?> yAxisTo = Collections.singletonMap(FlotOptionKey.GRID_MARKINGS_YAXIS_TO.getKey(),
        //                                                  graph.getSettings().getYLowerBound());
        Map<String, Object> responseData = Maps.newHashMapWithExpectedSize(3);
        responseData.put("series", seriesData);
        responseData.put("chart", chart);
        responseData.put("title", title);
        responseData.put("xAxis", xAxis);
        responseData.put("yAxis", yAxis);
        responseData.put("legend", legend);
        responseData.put("plotOptions", plotOptions);
        responseData.put("tooltip", tooltip);
        return responseData;
    }

    /**
     * Method to return the width of the individual bars in units of the x axis,
     * contrary to most other measures that are specified in pixels. For instance, for time
     * series the unit is milliseconds so 1000*60*60*24 produces bars with the width of a day.
     * The API option for this is "barWidth".
     * 
     * For more info see: https://github.com/flot/flot/blob/master/API.md
     */
    private static String hex2Rgb(String hexColor, double alpha) {
        Color c =  new Color(Integer.valueOf( hexColor.substring( 1, 3 ), 16 ),
                Integer.valueOf( hexColor.substring( 3, 5 ), 16 ),
                Integer.valueOf( hexColor.substring( 5, 7 ), 16 ) );
        StringBuffer sb = new StringBuffer();
        sb.append("rgba(");
        sb.append(c.getRed());
        sb.append(",");
        sb.append(c.getGreen());
        sb.append(",");
        sb.append(c.getBlue());
        sb.append(",");
        sb.append(alpha);
        sb.append(")");
        return sb.toString();
    }
    private int getBarWidthForTime(Instant start, Instant end, int numDataPoints) {
        long milliDiff = Math.abs(start.getMillis() - end.getMillis());
        int barWidth = (int) Math.ceil(milliDiff / numDataPoints);
        return barWidth;
    }
}
