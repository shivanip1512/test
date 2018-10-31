package com.cannontech.common.chart.model;

import java.text.NumberFormat;
import java.util.List;

public class Graph<T> {

    private String seriesTitle = null;
    private String yAxis = "left";
    private List<T> chartData = null;
    private NumberFormat format = null;
    private ChartColorsEnum color = null;
    private int axisIndex;
    private FlotLineOptions lines;
    private FlotBarOptions bars;
    private FlotPointOptions points;

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

    public FlotLineOptions getLines() {
        return lines;
    }

    public void setLines(FlotLineOptions lines) {
        this.lines = lines;
    }

    public FlotBarOptions getBars() {
        return bars;
    }

    public void setBars(FlotBarOptions bars) {
        this.bars = bars;
    }

    public FlotPointOptions getPoints() {
        return points;
    }

    public void setPoints(FlotPointOptions points) {
        this.points = points;
    }

}
