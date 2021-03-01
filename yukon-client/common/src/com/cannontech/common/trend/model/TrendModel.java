package com.cannontech.common.trend.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.trend.model.TrendType.GraphType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrendModel implements DBPersistentConverter<GraphDefinition> {
    private Integer trendId;
    @JsonProperty("trendName")
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
        this.name = StringUtils.trim(name);
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
    @Override
    public void buildDBPersistent(GraphDefinition graph) {
        if (getName() != null) {
            graph.getGraphDefinition().setName(getName().trim());
        }
        ArrayList<GraphDataSeries> graphDataSeries = new ArrayList<>();
        graph.getGraphDataSeries().clear();
        if (CollectionUtils.isNotEmpty(getTrendSeries())) {
            for (TrendSeries series : getTrendSeries()) {
                GraphDataSeries graphSeries = new GraphDataSeries();
                graphSeries.setLabel(series.getLabel());
                graphSeries.setAxis(
                        series.getAxis() == null ? TrendAxis.LEFT.getAbbreviation() : series.getAxis().getAbbreviation());
                graphSeries.setColor(series.getColor() == null ? (int) YukonColorPalette.BLUE.getDatabaseRepresentation()
                        : (int) series.getColor().getYukonColor().getDatabaseRepresentation());
                graphSeries.setType(series.getType() == null ? GDSTypes.BASIC_GRAPH_TYPE
                        : GDSTypesFuncs.getTypeInt(series.getType().getStringType()));
                graphSeries.setMultiplier(series.getMultiplier() == null ? 1 : series.getMultiplier());
                if (series.getType() != null
                        && (series.getType() == GraphType.PEAK_TYPE || series.getType() == GraphType.DATE_TYPE)) {
                    if (series.getType() == GraphType.PEAK_TYPE) {
                        // Set to this months start date.
                        DateTime date = new DateTime();
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
                // use defaults when MarkerType
                if (series.getType() != null && series.getType().isMarkerType()) {
                    graphSeries.setPointID(-100);
                    graphSeries.setRenderer(RenderType.LINE);
                } else {
                    graphSeries.setPointID(series.getPointId());
                    graphSeries.setRenderer(series.getStyle() == null ? RenderType.LINE : series.getStyle());
                }
                // Set GraphDefinationId in case of Update flow.
                if (graph.getGraphDefinition().getGraphDefinitionID() != null) {
                    graphSeries.setGraphDefinitionID(graph.getGraphDefinition().getGraphDefinitionID());
                }
                graphDataSeries.add(graphSeries);
            }
        }
        if (!graphDataSeries.isEmpty()) {
            graph.setGraphDataSeries(graphDataSeries);
        }
    }

    @Override
    public void buildModel(GraphDefinition graph) {
        setName(graph.getGraphDefinition().getName());
        setTrendId(graph.getGraphDefinition().getGraphDefinitionID());

        ArrayList<TrendSeries> trendSeries = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(graph.getGraphDataSeries())) {
            ArrayList<GraphDataSeries> graphSeries = graph.getGraphDataSeries();
            for (GraphDataSeries data : graphSeries) {
                TrendSeries trend = new TrendSeries();
                trend.setAxis(TrendAxis.getAxis(data.getAxis()));
                trend.setColor(GraphColors.getByYukonColor(YukonColorPalette.getColor(data.getColor())));
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
