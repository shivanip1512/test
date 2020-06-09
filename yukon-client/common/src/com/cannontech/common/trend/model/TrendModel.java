package com.cannontech.common.trend.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.cannontech.common.trend.model.TrendType.GraphType;
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
                graphSeries.setColor(series.getColor() == null ? (int) Color.BLUE.getDatabaseRepresentation()
                        : (int) series.getColor().getDatabaseRepresentation());
                graphSeries.setType(series.getType() == null ? GDSTypes.BASIC_GRAPH_TYPE
                        : GDSTypesFuncs.getTypeInt(series.getType().getStringType()));
                graphSeries.setMultiplier(series.getMultiplier() == null ? 1 : series.getMultiplier());
                graphSeries.setRenderer(series.getStyle() == null ? RenderType.LINE.getId() : series.getStyle().getId());
                if (series.getType() != null
                        && (series.getType() == GraphType.PEAK_TYPE || series.getType() == GraphType.DATE_TYPE)) {
                    if (series.getType() == GraphType.PEAK_TYPE) {
                        // Set to this months start date.
                        DateTime date = new DateTime(DateTimeZone.UTC);
                        DateTime startOfMonth = date.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
                        graphSeries.setMoreData(String.valueOf(startOfMonth.getMillis()));
                    } else {
                        graphSeries
                                .setMoreData(series.getDate() == null ? CtiUtilities.STRING_NONE
                                        : String.valueOf(series.getDate().getMillis()));
                    }
                } else {
                    graphSeries.setMoreData(CtiUtilities.STRING_NONE);
                }
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
                if (!data.getMoreData().equals(CtiUtilities.STRING_NONE)) {
                    trend.setDate(new DateTime(Long.valueOf(data.getMoreData())));
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
