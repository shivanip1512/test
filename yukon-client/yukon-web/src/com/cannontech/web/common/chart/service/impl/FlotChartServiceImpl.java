package com.cannontech.web.common.chart.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FlotChartServiceImpl implements FlotChartService {

    @Autowired private ChartService chartService;

    @Override
    public Map<String, Object> getMeterGraphData(List<GraphDetail> graphDetails, Instant start, Instant stop, Double yMin,
                                                 Double yMax, GraphType graphType, ChartInterval interval, YukonUserContext userContext) {
         List <Graph<ChartValue<Double>>> graphs = 
                chartService.getGraphs(graphDetails, start.toDate(), stop.toDate(), interval, userContext, graphType);

        // datas
        List<Object> jsonDataContainer = new ArrayList<>();
        graphs.forEach(graph -> {
            jsonDataContainer.add(new TrendData (getDataArray(graph.getChartData()), graph.getAxisIndex(), graph.getColor().getRgb()
                , graph.getBars(), graph.getLines(), graph.getPoints()));
        });
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

        List<Map<String, Object>> yaxesList = Lists.newArrayList();
        graphDetails.forEach(graphDetail -> {
            Map<String, Object> yAxes = new HashMap<>();
            yAxes.put(FlotOptionKey.YAXIS_POSITION.getKey(), graphDetail.getyAxisPosition());
            yAxes.put(FlotOptionKey.YAXIS_AXISLABEL.getKey(), graphDetail.getyLabelUnits());
            if (yMin != null) {
                yAxes.put(FlotOptionKey.YAXIS_MIN.getKey(), graphDetail.getyMin());
            }
            if (yMax != null) {
                yAxes.put(FlotOptionKey.YAXIS_MAX.getKey(), yMax);
            }
            yaxesList.add(yAxes);
        });
        options.put(FlotOptionKey.YAXES.getKey(), yaxesList);

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
        List<Object> jsonDataContainer = new ArrayList<>();

        /* data */
        boolean noData = graph.getLines().isEmpty();
        for (VfLine line : graph.getLines()) {
            List<Object> linesArray = new ArrayList<>();
            VfPoint labelPoint = null;
            for (VfPoint point : line.getPoints()) {
                List<Object> pointArray = new ArrayList<>();
                pointArray.add(point.getX());
                pointArray.add(point.getY());

                if (includeTitles && point.isRegulator() && line.getZoneName() != null
                    && (labelPoint == null || labelPoint.getY() < point.getY())) {
                    labelPoint = point;
                }

                pointArray.add(Collections.singletonMap("tooltip", point.getDescription()));
                pointArray.add(Collections.singletonMap("ignore", point.isIgnore()));
                linesArray.add(pointArray);
            }
            Map<String, Object> dataObj = new HashMap<>();
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
        if (noData) {
            jsonDataContainer.add(Collections.emptyList());
        }

        /* options */
        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put(FlotOptionKey.XAXIS_AUTOSCALEMARGIN.getKey(), 0.1);

        if (noData) {
            xAxis.put(FlotOptionKey.XAXIS_MIN.getKey(), 0);
        }

        Map<String, Object> options = new HashMap<>();
        options.put(FlotOptionKey.XAXIS.getKey(), xAxis);
        
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put(FlotOptionKey.YAXIS_POSITION.getKey(), "left");
        yAxis.put(FlotOptionKey.YAXIS_AXISLABEL.getKey(), graph.getSettings().getYAxisLabel());
        yAxis.put(FlotOptionKey.YAXIS_MIN.getKey(), graph.getSettings().getyMin());
        yAxis.put(FlotOptionKey.YAXIS_MAX.getKey(), graph.getSettings().getyMax());
        yAxis.put(FlotOptionKey.YAXIS_AUTOSCALEMARGIN.getKey(), 0.1);
        options.put(FlotOptionKey.YAXIS.getKey(), yAxis);
        
        Map<String, Object> lines = ImmutableMap.of("fill", false);
        Map<String, Object> series = ImmutableMap.of("lines", lines);
        options.put("series", series);
        
        Map<String, ?> yAxisFrom = Collections.singletonMap(FlotOptionKey.GRID_MARKINGS_YAXIS_FROM.getKey(),
                                                            graph.getSettings().getYUpperBound());
        Map<String, ?> yAxisTo = Collections.singletonMap(FlotOptionKey.GRID_MARKINGS_YAXIS_TO.getKey(),
                                                          graph.getSettings().getYLowerBound());

        List<Object> markingsArray = new ArrayList<>();

        Map<String, Object> markingStrategyHigh = Maps.newHashMapWithExpectedSize(2);
        markingStrategyHigh.put(FlotOptionKey.GRID_MARKINGS_COLOR.getKey(), "#f1f1f1");
        markingStrategyHigh.put(FlotOptionKey.GRID_MARKINGS_YAXIS.getKey(), yAxisFrom);
        markingsArray.add(markingStrategyHigh);

        Map<String, Object> markingStrategyLow = Maps.newHashMapWithExpectedSize(2);
        markingStrategyLow.put(FlotOptionKey.GRID_MARKINGS_COLOR.getKey(), "#f1f1f1");
        markingStrategyLow.put(FlotOptionKey.GRID_MARKINGS_YAXIS.getKey(), yAxisTo);
        markingsArray.add(markingStrategyLow);

        Map<String, ?> markingsObj = Collections.singletonMap(FlotOptionKey.GRID_MARKINGS.getKey(), markingsArray);
        options.put(FlotOptionKey.GRID.getKey(), markingsObj);

        Map<String, Object> dataAndOptions = Maps.newHashMapWithExpectedSize(3);
        dataAndOptions.put("datas", jsonDataContainer);
        dataAndOptions.put("type", GraphType.LINE.getFlotType());
        dataAndOptions.put("options", options);

        return dataAndOptions;
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
    
}
