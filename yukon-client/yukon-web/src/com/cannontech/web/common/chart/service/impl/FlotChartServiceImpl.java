package com.cannontech.web.common.chart.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.cannontech.web.common.chart.model.FlotOptionKey;
import com.cannontech.web.common.chart.model.FlotPieDatas;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class FlotChartServiceImpl implements FlotChartService {

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
        markingStrategyHigh.put(FlotOptionKey.GRID_MARKINGS_COLOR.getKey(), YukonColorPalette.SILVER.getHexValue());
        markingStrategyHigh.put(FlotOptionKey.GRID_MARKINGS_YAXIS.getKey(), yAxisFrom);
        markingsArray.add(markingStrategyHigh);

        Map<String, Object> markingStrategyLow = Maps.newHashMapWithExpectedSize(2);
        markingStrategyLow.put(FlotOptionKey.GRID_MARKINGS_COLOR.getKey(), YukonColorPalette.SILVER.getHexValue());
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
}
