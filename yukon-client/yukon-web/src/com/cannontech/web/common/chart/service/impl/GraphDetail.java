package com.cannontech.web.common.chart.service.impl;

import com.cannontech.common.chart.model.ChartColorsEnum;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.ChartBarOptions;
import com.cannontech.common.chart.model.ChartLineOptions;
import com.cannontech.common.chart.model.ChartPointOptions;

public class GraphDetail {
    private int pointId;
    private ConverterType converterType = ConverterType.RAW;
    private String yLabelUnits;
    private int axisIndex;
    private String yAxisPosition;
    private boolean isMin;
    private ChartColorsEnum chartColors;
    private Double yMin;
    private ChartBarOptions bars;
    private ChartLineOptions lines;
    private ChartPointOptions points;
    private ChartInterval interval;
    private String seriesName;

    public GraphDetail(int pointId, String yLabelUnits, int axisIndex, String yAxisPosition, boolean isMax, ChartColorsEnum chartColors, ChartInterval interval, String seriesName) {
        super();
        this.pointId = pointId;
        this.yLabelUnits = yLabelUnits;
        this.axisIndex = axisIndex;
        this.yAxisPosition = yAxisPosition;
        this.isMin = isMax;
        this.chartColors = chartColors;
        this.interval = interval;
        this.seriesName = seriesName;
    }

    public ChartInterval getInterval() {
        return interval;
    }

    public void setInterval(ChartInterval interval) {
        this.interval = interval;
    }

    public Double getyMin() {
        return yMin;
    }

    public void setyMin(Double yMin) {
        this.yMin = yMin;
    }

    public ChartColorsEnum getChartColors() {
        return chartColors;
    }

    public void setChartColors(ChartColorsEnum chartColors) {
        this.chartColors = chartColors;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getyAxisPosition() {
        return yAxisPosition;
    }

    public void setyAxisPosition(String yAxisPosition) {
        this.yAxisPosition = yAxisPosition;
    }

    public ConverterType getConverterType() {
        return converterType;
    }

    public void setConverterType(ConverterType converterType) {
        this.converterType = converterType;
    }

    public String getyLabelUnits() {
        return yLabelUnits;
    }

    public void setyLabelUnits(String yLabelUnits) {
        this.yLabelUnits = yLabelUnits;
    }
    
    public ChartBarOptions getBars() {
        return bars;
    }

    public void setBars(ChartBarOptions bars) {
        this.bars = bars;
    }

    public ChartLineOptions getLines() {
        return lines;
    }

    public void setLines(ChartLineOptions lines) {
        this.lines = lines;
    }

    public ChartPointOptions getPoints() {
        return points;
    }

    public void setPoints(ChartPointOptions points) {
        this.points = points;
    }

    public boolean isMin() {
        return isMin;
    }

    public void setMin(boolean isMin) {
        this.isMin = isMin;
    }

    public int getAxisIndex() {
        return axisIndex;
    }
    
    public void setAxisIndex(int axisIndex) {
        this.axisIndex = axisIndex;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

}
