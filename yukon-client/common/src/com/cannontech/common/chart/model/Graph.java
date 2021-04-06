package com.cannontech.common.chart.model;

import java.text.NumberFormat;
import java.util.List;

public class Graph<T> {

    private int pointId;
    private String seriesTitle = null;
    private String yAxis = "left";
    private List<T> chartData = null;
    private NumberFormat format = null;
    private ChartColorsEnum color = null;
    private int axisIndex;
    private ChartLineOptions lines;
    private ChartBarOptions bars;
    private ChartPointOptions points;

    public int getAxisIndex() {
        return axisIndex;
    }

    public void setAxisIndex(int axisIndex) {
        this.axisIndex = axisIndex;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getYAxis() {
        return yAxis;
    }

    public void setYAxis(String axis) {
        yAxis = axis;
    }

    public List<T> getChartData() {
        return chartData;
    }

    public void setChartData(List<T> chartData) {
        this.chartData = chartData;
    }

    public NumberFormat getFormat() {
        return format;
    }

    public void setFormat(NumberFormat format) {
        this.format = format;
    }

    public ChartColorsEnum getColor() {
        return color;
    }

    public void setColor(ChartColorsEnum color) {
        this.color = color;
    }

    public ChartLineOptions getLines() {
        return lines;
    }

    public void setLines(ChartLineOptions lines) {
        this.lines = lines;
    }

    public ChartBarOptions getBars() {
        return bars;
    }

    public void setBars(ChartBarOptions bars) {
        this.bars = bars;
    }

    public ChartPointOptions getPoints() {
        return points;
    }

    public void setPoints(ChartPointOptions points) {
        this.points = points;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
}
