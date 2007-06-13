package com.cannontech.common.chart.model;

import java.text.NumberFormat;
import java.util.List;

public class Graph {

    private String seriesTitle = null;
    private String yAxis = "left";
    private List<?> chartData = null;
    private NumberFormat format = null;

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

    public List<?> getChartData() {
        return chartData;
    }

    public void setChartData(List<?> chartData) {
        this.chartData = chartData;
    }

    public NumberFormat getFormat() {
        return format;
    }

    public void setFormat(NumberFormat format) {
        this.format = format;
    }

}
