package com.cannontech.web.common.chart.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

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
            seriesList.add(getSeriesDetails(graph, graphType));
        });
        
        
        int noOfTempreaturePoints = 0;
        for (Graph<ChartValue<Double>> graph : graphs) {
            if (isTempreaturePoint(graph.getPointId())) {
                noOfTempreaturePoints++;
            }
        }
        boolean isTempreatureAxisDetailsAdded = false;
        List<Map<String, Object>> yAxesOptions = Lists.newArrayList();
        for (GraphDetail graphDetail : graphDetails) {
            Map<String, Object> yAxes = new HashMap<>();
            if (isTempreaturePoint(graphDetail.getPointId())) {
                if (noOfTempreaturePoints == 2 && isTempreatureAxisDetailsAdded) {
                    yAxes.put(HighChartOptionKey.TITLE.getKey(),
                            Collections.singletonMap(HighChartOptionKey.TEXT.getKey(), ""));
                } else {
                    yAxes.put(HighChartOptionKey.TITLE.getKey(),
                            Collections.singletonMap(HighChartOptionKey.TEXT.getKey(), graphDetail.getyLabelUnits()));
                    isTempreatureAxisDetailsAdded = true;
                }
            } else {
                yAxes.put(HighChartOptionKey.TITLE.getKey(),
                        Collections.singletonMap(HighChartOptionKey.TEXT.getKey(), graphDetail.getyLabelUnits()));
            }
            if (yMin != null && graphDetail.getyMin() != null) {
                yAxes.put(HighChartOptionKey.MIN.getKey(), graphDetail.getyMin());
            }
            if (yMax != null) {
                yAxes.put(HighChartOptionKey.MAX.getKey(), yMax);
            }
            if (isTempreaturePoint(graphDetail.getPointId())) {
                yAxes.put(HighChartOptionKey.OPPOSITE.getKey(), true);
            }
            yAxesOptions.add(yAxes);
        }

        Map<String, Object> xaxisOptions = Maps.newHashMap();

        // TODO: These adjustments will be removed in YUK-23405.
        long xAxisMin = start.getMillis() + TimeZone.getDefault().getOffset(start.getMillis());
        long xAxisMax = stop.getMillis() + TimeZone.getDefault().getOffset(stop.getMillis());
        xaxisOptions.put(HighChartOptionKey.MIN.getKey(), xAxisMin);
        xaxisOptions.put(HighChartOptionKey.MAX.getKey(), xAxisMax);
        
        Duration duration = new Duration(start.getMillis(), stop.getMillis());
        if (duration.getStandardDays() > 365) {
            xaxisOptions.put("datetimeFormat", HighChartOptionKey.DATE_FORMAT_MONTH_YEAR.getKey());
        } else {
            xaxisOptions.put("datetimeFormat", HighChartOptionKey.DATE_FORMAT_MONTH_DATE.getKey());
        }
        
        Map<String, Object> dataAndOptions = Maps.newHashMap();
        dataAndOptions.put("seriesDetails", seriesList);
        dataAndOptions.put(HighChartOptionKey.X_AXIS.getKey(), xaxisOptions);
        dataAndOptions.put(HighChartOptionKey.Y_AXIS.getKey(), yAxesOptions);

        return dataAndOptions;
    }
    
    private Map<String, Object> getSeriesDetails(Graph<ChartValue<Double>> graph, GraphType graphType) {
        Map<String, Object> seriesDetails = Maps.newHashMap();
        seriesDetails.put(HighChartOptionKey.SERIES_DATA.getKey(), getDataArray(graph.getChartData()));
        seriesDetails.put(HighChartOptionKey.SHOW_IN_LEGEND.getKey(), false);
        seriesDetails.put(HighChartOptionKey.COLOR.getKey(), graph.getColor().getColorHex());
        if (isTempreaturePoint(graph.getPointId())) {
            seriesDetails.put(HighChartOptionKey.FILL_OPACITY.getKey(), "0");
            seriesDetails.put(HighChartOptionKey.MARKER.getKey(), Collections.singletonMap("enabled", false));
            seriesDetails.put(HighChartOptionKey.SERIES_GRAPH_TYPE.getKey(), GraphType.LINE.getHighChartType());
        } else {
            seriesDetails.put(HighChartOptionKey.FILL_OPACITY.getKey(), "0.45");
            seriesDetails.put(HighChartOptionKey.MARKER.getKey(), Collections.singletonMap("enabled", true));
            seriesDetails.put(HighChartOptionKey.SERIES_GRAPH_TYPE.getKey(), graphType.getHighChartType());
            if (graphType == GraphType.COLUMN) {
                seriesDetails.put(HighChartOptionKey.POINT_WIDTH.getKey(), 10);
            }
        }
        seriesDetails.put(HighChartOptionKey.SERIES_Y_AXIS.getKey(), graph.getAxisIndex() - 1); //The axis index in Highchart starts with 0
        return seriesDetails;
    }

    private List<Map<String, Object>> getDataArray(List<ChartValue<Double>> chartData) {
        List<Map<String, Object>> jsonArrayContainer = Lists.newArrayList();
        for (ChartValue<Double> chartValue : chartData) {
            Map<String, Object> map = Maps.newHashMap();
            map.put(HighChartOptionKey.SERIES_X_COORDINATE.getKey(), chartValue.getId());
            map.put(HighChartOptionKey.SERIES_Y_COORDINATE.getKey(), chartValue.getValue());
            map.put(HighChartOptionKey.POINT_TOOLTIP.getKey(), chartValue.getDescription());
            jsonArrayContainer.add(map);
        }
        return jsonArrayContainer;
    }
    
    private boolean isTempreaturePoint(int pointId) {
        LitePoint lPoint = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(lPoint.getPaobjectID());
        return pao.getPaoType() == PaoType.WEATHER_LOCATION;
    }
}