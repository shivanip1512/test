package com.cannontech.web.common.chart.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartColorsEnum;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.Graph;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.model.HighChartOptionKey;
import com.cannontech.web.common.chart.service.ChartService;
import com.cannontech.web.common.chart.service.HighChartService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HighChartServiceImpl implements HighChartService {

    @Autowired private ChartService chartService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private PointDao pointDao;

    @Override
    public Map<String, Object> getMeterGraphData(List<GraphDetail> graphDetails, Instant start, Instant stop, Double yMin,
            Double yMax, GraphType graphType, YukonUserContext userContext) {
        // TODO: Ensure that adjustForFlotTimezone() is not called. These adjustments will be removed in YUK-23405.
        List<Graph<ChartValue<Double>>> graphs = chartService.getGraphs(graphDetails, start.toDate(), stop.toDate(), userContext,
                graphType);

        List<Map<String, Object>> seriesList = Lists.newArrayList();
        graphs.forEach(graph -> {
            GraphDetail graphDetail = graphDetails.stream().filter(gd -> gd.getChartColors() == graph.getColor()).findFirst().orElse(null);
            seriesList.add(getSeriesDetails(graph, graphType, graphDetail));
        });

        boolean isTemperatureAxisDetailsAdded = false;
        List<Map<String, Object>> yAxesOptions = Lists.newArrayList();
        for (GraphDetail graphDetail : graphDetails) {
            Map<String, Object> yAxes = new HashMap<>();
            Map<String, Object> titleOptions = new HashMap<>();
            String titleTxt;
            int rotation = 270;
            if (isTemperaturePoint(graphDetail.getPointId())) {
                if (isTemperatureAxisDetailsAdded) {
                    titleTxt = "";
                } else {
                    titleTxt = graphDetail.getyLabelUnits();
                    rotation = -270;
                    isTemperatureAxisDetailsAdded = true;
                }
                yAxes.put(HighChartOptionKey.OPPOSITE.getKey(), true);
                yAxes.put(HighChartOptionKey.GRID_LINE_WIDTH.getKey(), 0);
            } else {
                titleTxt = graphDetail.getyLabelUnits();
                if (yMin != null && graphDetail.getyMin() != null) {
                    yAxes.put(HighChartOptionKey.MIN.getKey(), graphDetail.getyMin());
                }
                if (yMax != null) {
                    yAxes.put(HighChartOptionKey.MAX.getKey(), yMax);
                }
                yAxes.put(HighChartOptionKey.GRID_LINE_WIDTH.getKey(), 1);
            }
            titleOptions.put(HighChartOptionKey.TEXT.getKey(), titleTxt);
            titleOptions.put(HighChartOptionKey.ROTATION.getKey(), rotation);
            titleOptions.put(HighChartOptionKey.ALIGN.getKey(), "middle");
            yAxes.put(HighChartOptionKey.TITLE.getKey(), titleOptions);
            yAxes.put(HighChartOptionKey.ALIGN_TICKS.getKey(), true);
            yAxes.put(HighChartOptionKey.START_ON_TICK.getKey(), false);
            yAxes.put(HighChartOptionKey.END_ON_TICK.getKey(), false);
            yAxes.put(HighChartOptionKey.TICK_AMOUNT.getKey(), 6);
            yAxesOptions.add(yAxes);
        }

        Map<String, Object> xaxisOptions = Maps.newHashMap();

        // TODO: These adjustments will be removed in YUK-23405.
        long xAxisMin = start.getMillis() + TimeZone.getDefault().getOffset(start.getMillis());
        long xAxisMax = stop.getMillis() + TimeZone.getDefault().getOffset(stop.getMillis());
        xaxisOptions.put(HighChartOptionKey.MIN.getKey(), xAxisMin);
        xaxisOptions.put(HighChartOptionKey.MAX.getKey(), xAxisMax);

        Map<String, Object> dataAndOptions = Maps.newHashMap();
        dataAndOptions.put("seriesDetails", seriesList);
        dataAndOptions.put(HighChartOptionKey.X_AXIS.getKey(), xaxisOptions);
        dataAndOptions.put(HighChartOptionKey.Y_AXIS.getKey(), yAxesOptions);

        return dataAndOptions;
    }

    private Map<String, Object> getSeriesDetails(Graph<ChartValue<Double>> graph, GraphType graphType, GraphDetail graphDetail) {
        boolean isTemperaturePoint = isTemperaturePoint(graph.getPointId());
        Map<String, Object> seriesDetails = Maps.newHashMap();
        seriesDetails.put(HighChartOptionKey.SERIES_DATA.getKey(), getDataArray(graph, isTemperaturePoint, graphDetail));
        seriesDetails.put(HighChartOptionKey.SHOW_IN_LEGEND.getKey(), false);
        seriesDetails.put(HighChartOptionKey.BORDER_COLOR.getKey(), ChartColorsEnum.GREEN);
        if (isTemperaturePoint) {
            seriesDetails.put(HighChartOptionKey.THRESHOLD.getKey(), null);
            seriesDetails.put(HighChartOptionKey.COLOR.getKey(), graph.getColor().getColorHex());
            seriesDetails.put(HighChartOptionKey.FILL_OPACITY.getKey(), "0");
            seriesDetails.put(HighChartOptionKey.MARKER.getKey(), Collections.singletonMap("enabled", false));
            seriesDetails.put(HighChartOptionKey.SERIES_GRAPH_TYPE.getKey(), GraphType.LINE.getHighChartType());
        } else {
            seriesDetails.put(HighChartOptionKey.MARKER.getKey(), Collections.singletonMap("enabled", true));
            seriesDetails.put(HighChartOptionKey.SERIES_GRAPH_TYPE.getKey(), graphType.getHighChartType());
            if (graphType == GraphType.COLUMN) {
                seriesDetails.put(HighChartOptionKey.BORDER_COLOR.getKey(), ChartColorsEnum.GREEN.getColorHex());
                seriesDetails.put(HighChartOptionKey.COLOR.getKey(), graph.getColor().getColorHex());
                seriesDetails.put(HighChartOptionKey.BORDER_WIDTH.getKey(), "2");
            } else {
                seriesDetails.put(HighChartOptionKey.FILL_OPACITY.getKey(), "0.45");
                seriesDetails.put(HighChartOptionKey.COLOR.getKey(), ChartColorsEnum.GREEN.getColorHex());
            }
        }
        seriesDetails.put(HighChartOptionKey.SERIES_Y_AXIS.getKey(), graph.getAxisIndex() - 1); //The axis index in Highchart starts with 0
        return seriesDetails;
    }

    private List<Map<String, Object>> getDataArray(Graph<ChartValue<Double>> graph, boolean isTemperaturePoint, GraphDetail graphDetail) {
        List<Map<String, Object>> jsonArrayContainer = Lists.newArrayList();
        for (ChartValue<Double> chartValue : graph.getChartData()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put(HighChartOptionKey.SERIES_X_COORDINATE.getKey(), chartValue.getId());
            map.put(HighChartOptionKey.SERIES_Y_COORDINATE.getKey(), chartValue.getValue());
            StringBuilder tooltipBuilder = new StringBuilder();
            tooltipBuilder
                    .append("<span style='color:" + graph.getColor().getColorHex() + "'>\u25CF</span>&nbsp;" + graphDetail.getSeriesName());
            tooltipBuilder.append(chartValue.getFormattedDiscription());
            map.put(HighChartOptionKey.POINT_TOOLTIP.getKey(), tooltipBuilder.toString());
            jsonArrayContainer.add(map);
        }
        return jsonArrayContainer;
    }
    
    private boolean isTemperaturePoint(int pointId) {
        LitePoint lPoint = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(lPoint.getPaobjectID());
        return pao.getPaoType() == PaoType.WEATHER_LOCATION;
    }
}