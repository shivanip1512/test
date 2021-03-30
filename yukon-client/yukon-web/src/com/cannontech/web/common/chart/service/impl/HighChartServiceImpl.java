package com.cannontech.web.common.chart.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.YukonColorPalette;
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
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
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
        xaxisOptions.put(HighChartOptionKey.TYPE.getKey(), "datetime");

        Map<String, Object> dataAndOptions = Maps.newHashMap();
        dataAndOptions.put("seriesDetails", seriesList);
        dataAndOptions.put(HighChartOptionKey.X_AXIS.getKey(), xaxisOptions);
        dataAndOptions.put(HighChartOptionKey.Y_AXIS.getKey(), yAxesOptions);

        return dataAndOptions;
    }

    private Map<String, Object> getSeriesDetails(Graph<ChartValue<Double>> graph, GraphType graphType, GraphDetail graphDetail) {
        Map<String, Object> seriesDetails = Maps.newHashMap();
        seriesDetails.put(HighChartOptionKey.SERIES_DATA.getKey(), getDataArray(graph, graphDetail));
        seriesDetails.put(HighChartOptionKey.SHOW_IN_LEGEND.getKey(), false);
        seriesDetails.put(HighChartOptionKey.BORDER_COLOR.getKey(), ChartColorsEnum.GREEN);
        if (isTemperaturePoint(graph.getPointId())) {
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

    private List<Map<String, Object>> getDataArray(Graph<ChartValue<Double>> graph, GraphDetail graphDetail) {
        List<Map<String, Object>> jsonArrayContainer = Lists.newArrayList();
        for (ChartValue<Double> chartValue : graph.getChartData()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put(HighChartOptionKey.SERIES_X_COORDINATE.getKey(), chartValue.getId());
            map.put(HighChartOptionKey.SERIES_Y_COORDINATE.getKey(), chartValue.getValue());
            StringBuilder tooltipBuilder = new StringBuilder();
            tooltipBuilder
                    .append("<span style='color:" + graph.getColor().getColorHex() + "'>\u25CF</span>&nbsp;" + graphDetail.getSeriesName());
            tooltipBuilder.append(chartValue.getFormattedDescription());
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
    
    @Override
    public Map<String, Object> getIVVCGraphData(VfGraph graph, boolean includeTitles) {
        List<Object> jsonDataContainer = new ArrayList<>();

        /* data */
        boolean noData = graph.getLines().isEmpty();
        List<String> lineNames = new ArrayList<String>();

        for (VfLine line : graph.getLines()) {
            List<Object> linesArray = new ArrayList<>();
            VfPoint labelPoint = null;
            //find label point
            for (VfPoint point : line.getPoints()) {
                if (includeTitles && point.isRegulator() && line.getZoneName() != null
                    && (labelPoint == null || labelPoint.getY() < point.getY())) {
                    labelPoint = point;
                }
            }
            for (VfPoint point : line.getPoints()) {
                Map<String, Object> map = Maps.newHashMap();
                map.put(HighChartOptionKey.SERIES_X_COORDINATE.getKey(), point.getX());
                map.put(HighChartOptionKey.SERIES_Y_COORDINATE.getKey(), point.getY());
                
                if (includeTitles && labelPoint != null && labelPoint == point) {
                    Map<String, Object> dataLabel = new HashMap<>();
                    dataLabel.put(HighChartOptionKey.FORMAT.getKey(), line.getZoneName());
                    dataLabel.put(HighChartOptionKey.ENABLED.getKey(), true);
                    Map<String, Object> format = new HashMap<>();
                    format.put(HighChartOptionKey.FONT_WEIGHT.getKey(), "normal");
                    dataLabel.put(HighChartOptionKey.STYLE.getKey(), format);
                    map.put(HighChartOptionKey.DATA_LABELS.getKey(), dataLabel);
                }
                
                map.put(HighChartOptionKey.POINT_TOOLTIP.getKey(), point.getDescription());
                map.put("ignore", point.isIgnore());
                linesArray.add(map);
            }
            
            Map<String, Object> dataObj = new HashMap<>();    
            dataObj.put(HighChartOptionKey.SERIES_DATA.getKey(), linesArray);
            dataObj.put(HighChartOptionKey.COLOR.getKey(), line.getSettings().getColor());
            dataObj.put(HighChartOptionKey.NAME.getKey(), line.getLineName());
            dataObj.put(HighChartOptionKey.ID.getKey(), line.getLineName());
            if (lineNames.contains(line.getLineName())) {
                dataObj.put(HighChartOptionKey.LINKED_TO.getKey(), line.getLineName());
            }
            jsonDataContainer.add(dataObj);
            lineNames.add(line.getLineName());
        }
        /* if we have no data, then add an empty array to jsonData so a blank graph is displayed properly */
        if (noData) {
            jsonDataContainer.add(Collections.emptyList());
        }

        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put(HighChartOptionKey.TYPE.getKey(), "linear");
        xAxis.put(HighChartOptionKey.MIN_PADDING.getKey(), 0.1);
        xAxis.put(HighChartOptionKey.MAX_PADDING.getKey(), 0.1);

        if (noData) {
            xAxis.put(HighChartOptionKey.MIN.getKey(), 0);
        }

        Map<String, Object> options = new HashMap<>();
        options.put(HighChartOptionKey.X_AXIS.getKey(), xAxis);
        
        Map<String, Object> yAxis = new HashMap<>();
        Map<String, Object> titleOptions = new HashMap<>();
        titleOptions.put(HighChartOptionKey.TEXT.getKey(), graph.getSettings().getYAxisLabel());
        yAxis.put(HighChartOptionKey.TITLE.getKey(), titleOptions);
        yAxis.put(HighChartOptionKey.MIN.getKey(), graph.getSettings().getyMin());
        yAxis.put(HighChartOptionKey.MAX.getKey(), graph.getSettings().getyMax());
        yAxis.put(HighChartOptionKey.START_ON_TICK.getKey(), false);
        yAxis.put(HighChartOptionKey.END_ON_TICK.getKey(), false);

        List<Object> markingsArray = new ArrayList<>();

        Map<String, Object> markingStrategyHigh = Maps.newHashMapWithExpectedSize(2);
        markingStrategyHigh.put(HighChartOptionKey.COLOR.getKey(), YukonColorPalette.SILVER.getHexValue());
        markingStrategyHigh.put(HighChartOptionKey.FROM.getKey(), graph.getSettings().getyMax());
        markingStrategyHigh.put(HighChartOptionKey.TO.getKey(), graph.getSettings().getYUpperBound());
        markingsArray.add(markingStrategyHigh);

        Map<String, Object> markingStrategyLow = Maps.newHashMapWithExpectedSize(2);
        markingStrategyLow.put(HighChartOptionKey.COLOR.getKey(), YukonColorPalette.SILVER.getHexValue());
        markingStrategyLow.put(HighChartOptionKey.FROM.getKey(), graph.getSettings().getyMin());
        markingStrategyLow.put(HighChartOptionKey.TO.getKey(), graph.getSettings().getYLowerBound());
        markingsArray.add(markingStrategyLow);
        
        yAxis.put(HighChartOptionKey.PLOT_BANDS.getKey(), markingsArray);
        
        options.put(HighChartOptionKey.Y_AXIS.getKey(), yAxis);

        Map<String, Object> dataAndOptions = Maps.newHashMapWithExpectedSize(3);
        dataAndOptions.put("seriesDetails", jsonDataContainer);
        dataAndOptions.put(HighChartOptionKey.TYPE.getKey(), GraphType.LINE.getHighChartType());
        dataAndOptions.putAll(options);

        return dataAndOptions;
    }
}