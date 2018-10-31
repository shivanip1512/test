package com.cannontech.web.common.chart.service.impl;

import com.cannontech.common.chart.model.ChartColorsEnum;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.FlotBarOptions;
import com.cannontech.common.chart.model.FlotLineOptions;
import com.cannontech.common.chart.model.FlotPointOptions;

public class GraphDetail {
    private int pointId;
    private ConverterType converterType = ConverterType.RAW;
    private String yLabelUnits;
    private int axisIndex;
    private String yAxisPosition;
    private boolean isMin;
    private ChartColorsEnum chartColors;
    private Double yMin;
    private FlotBarOptions bars;
    private FlotLineOptions lines;
    private FlotPointOptions points;

    public GraphDetail(int pointId, String yLabelUnits, int axisIndex, String yAxisPosition, boolean isMax, ChartColorsEnum chartColors) {
        super();
        this.pointId = pointId;
        this.yLabelUnits = yLabelUnits;
        this.axisIndex = axisIndex;
        this.yAxisPosition = yAxisPosition;
        this.isMin = isMax;
        this.chartColors = chartColors;
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
    
    public FlotBarOptions getBars() {
        return bars;
    }

    public void setBars(FlotBarOptions bars) {
        this.bars = bars;
    }

    public FlotLineOptions getLines() {
        return lines;
    }

    public void setLines(FlotLineOptions lines) {
        this.lines = lines;
    }

    public FlotPointOptions getPoints() {
        return points;
    }

    public void setPoints(FlotPointOptions points) {
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

}
