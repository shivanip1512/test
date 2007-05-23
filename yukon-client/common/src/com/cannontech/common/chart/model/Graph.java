package com.cannontech.common.chart.model;

import java.util.List;

public class Graph {

    private String seriesTitle = null;
    private String yAxis = "left";
    private List<ChartValue> chartData = null;

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

    public List<ChartValue> getChartData() {
        return chartData;
    }

    public void setChartData(List<ChartValue> chartData) {
        this.chartData = chartData;
    }

}
