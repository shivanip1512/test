package com.cannontech.web.common.chart.service.impl;

import java.util.List;

import com.cannontech.common.chart.model.ChartBarOptions;
import com.cannontech.common.chart.model.ChartLineOptions;
import com.cannontech.common.chart.model.ChartPointOptions;

public class TrendData {
    public TrendData(List<Object> data, int yaxis, String color, ChartBarOptions bars, ChartLineOptions lines
            , ChartPointOptions points) {
        super();
        this.data = data;
        this.yaxis = yaxis;
        this.color = color;
        this.bars = bars;
        this.lines = lines;
        this.points = points;
    }

    private List<Object> data;
    private int yaxis;
    private String color;
    private ChartBarOptions bars;
    private ChartLineOptions lines;
    private ChartPointOptions points;

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public int getYaxis() {
        return yaxis;
    }

    public void setYaxis(int yaxis) {
        this.yaxis = yaxis;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ChartBarOptions getBars() {
        if(bars == null) {
            bars = new ChartBarOptions();
        }
        return bars;
    }

    public void setBars(ChartBarOptions bars) {
        this.bars = bars;
    }

    public ChartLineOptions getLines() {
        if(lines == null) {
            lines = new ChartLineOptions();
        }
        return lines;
    }

    public void setLines(ChartLineOptions lines) {
        this.lines = lines;
    }

    public ChartPointOptions getPoints() {
        if (points == null) {
            points = new ChartPointOptions();
        }
        return points;
    }

    public void setPoints(ChartPointOptions points) {
        this.points = points;
    }
}
