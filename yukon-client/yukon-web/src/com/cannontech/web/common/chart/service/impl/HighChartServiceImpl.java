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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.model.HighChartOptionKey;
import com.cannontech.web.common.chart.service.ChartService;
import com.cannontech.web.common.chart.service.HighChartService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HighChartServiceImpl implements HighChartService {

    @Autowired private ChartService chartService;

    @Override
    public Map<String, Object> getMeterGraphData(List<GraphDetail> graphDetails, Instant start, Instant stop, Double yMin,
                                                 Double yMax, GraphType graphType, YukonUserContext userContext) {
         //TODO: Ensure that adjustForFlotTimezone() is not called. These adjustments will be removed in YUK-23405.
         List <Graph<ChartValue<Double>>> graphs = 
                chartService.getGraphs(graphDetails, start.toDate(), stop.toDate(), userContext, graphType);

         List<Map<String, Object>> seriesList = Lists.newArrayList();
         for(int i=0; i<graphDetails.size();i++) {
             seriesList.add(getSeriesDetails(graphs.get(i), graphDetails.get(i), graphType));
         }
        
        List<Map<String, Object>> yAxesOptions = Lists.newArrayList();
        graphDetails.forEach(graphDetail -> {
            Map<String, Object> yAxes = new HashMap<>();
            yAxes.put(HighChartOptionKey.TITLE.getKey(),
                    Collections.singletonMap(HighChartOptionKey.TEXT.getKey(), graphDetail.getyLabelUnits()));
            if (yMin != null) {
                yAxes.put(HighChartOptionKey.MIN.getKey(), graphDetail.getyMin());
            }
            if (yMax != null) {
                yAxes.put(HighChartOptionKey.MAX.getKey(), yMax);
            }
            yAxesOptions.add(yAxes);
        });
        
        Map<String, Object> xaxisOptions = Maps.newHashMap();
        
        //TODO: These adjustments will be removed in YUK-23405.
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
    
    private Map<String, Object> getSeriesDetails(Graph<ChartValue<Double>> graph, GraphDetail graphDetail, GraphType graphType) {
        Map<String, Object> seriesDetails = Maps.newHashMap();
        seriesDetails.put(HighChartOptionKey.SERIES_DATA.getKey(), getDataArray(graph.getChartData()));
        seriesDetails.put(HighChartOptionKey.SERIES_GRAPH_TYPE.getKey(), graphType.getHighChartType());
        seriesDetails.put(HighChartOptionKey.SHOW_IN_LEGEND.getKey(), false);
        seriesDetails.put(HighChartOptionKey.COLOR.getKey(), graph.getColor().getColorHex());
        seriesDetails.put(HighChartOptionKey.FILL_OPACITY.getKey(), "0.45");
        seriesDetails.put(HighChartOptionKey.MARKER.getKey(), Collections.singletonMap("enabled", true));
        seriesDetails.put(HighChartOptionKey.Y_AXIS.getKey(), graphDetail.getAxisIndex() - 1); //The axis index in Highchart starts with 0
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
}