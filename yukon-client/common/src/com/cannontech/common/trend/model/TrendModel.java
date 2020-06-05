package com.cannontech.common.trend.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.LocalDate;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;

public class TrendModel {
    private Integer trendId;
    private String name;
    private List<TrendSeries> trendSeries;

    public Integer getTrendId() {
        return trendId;
    }

    public void setTrendId(Integer trendId) {
        this.trendId = trendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TrendSeries> getTrendSeries() {
        return trendSeries;
    }

    public void setTrendSeries(List<TrendSeries> trendSeries) {
        this.trendSeries = trendSeries;
    }

    /*
     * Create DBPersistent object to insert
     */
    public void buildDBPersistent(GraphDefinition graph) {
        com.cannontech.database.db.graph.GraphDefinition graphDefinition = graph.getGraphDefinition();
        graphDefinition.setName(getName());
        ArrayList<GraphDataSeries> graphDataSeries = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(getTrendSeries())) {
            for (TrendSeries series : getTrendSeries()) {
                GraphDataSeries graphSeries = new GraphDataSeries();
                graphSeries.setPointID(series.getPointId());
                graphSeries.setLabel(series.getLabel());
                graphSeries.setAxis(
                        series.getAxis() == null ? TrendAxis.LEFT.getAbbreviation() : series.getAxis().getAbbreviation());
                graphSeries.setColor(series.getColor() == null ? Color.BLUE.getColorId() : (int) series.getColor().getColorId());
                graphSeries.setType(series.getType() == null ? GDSTypes.BASIC_GRAPH_TYPE
                        : GDSTypesFuncs.getTypeInt(TrendType.getStringValue(series.getType())));
                graphSeries.setMultiplier(series.getMultiplier() == null ? 1 : series.getMultiplier());
                graphSeries.setRenderer(series.getStyle() == null ? RenderType.LINE.getId() : series.getStyle().getId());
                // TODO
                graphSeries.setMoreData(series.getDate() == null ? CtiUtilities.STRING_NONE : series.getDate().toString());
                graphDataSeries.add(graphSeries);
            }
        }
        graph.setGraphDataSeries(graphDataSeries);
    }

    public void buildModel(GraphDefinition graph) {
        setName(graph.getGraphDefinition().getName());
        setTrendId(graph.getGraphDefinition().getGraphDefinitionID());
        
        ArrayList<TrendSeries> trendSeries = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(graph.getGraphDataSeries())) {
            ArrayList<GraphDataSeries> graphSeries = graph.getGraphDataSeries();
            for (GraphDataSeries data : graphSeries) {
                TrendSeries trend = new TrendSeries();
                trend.setAxis(TrendAxis.getAxis(data.getAxis()));
                trend.setColor(Color.getColor(data.getColor()));
                // TODO
                if (!data.getMoreData().equals(CtiUtilities.STRING_NONE)) {
                    trend.setDate(new LocalDate(Long.valueOf(data.getMoreData())));
                }
                trend.setLabel(data.getLabel());
                trend.setMultiplier(data.getMultiplier());
                trend.setPointId(data.getPointID());
                trend.setStyle(RenderType.getForId(data.getRenderer()));
                trend.setType(TrendType.of(data.getType()).getGraphType());
                trendSeries.add(trend);
            }
        }
        setTrendSeries(trendSeries);
    }

}
