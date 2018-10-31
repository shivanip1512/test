package com.cannontech.web.common.chart.service.impl;

import java.util.List;

import com.cannontech.common.chart.model.FlotBarOptions;
import com.cannontech.common.chart.model.FlotLineOptions;
import com.cannontech.common.chart.model.FlotPointOptions;

public class TrendData {
    public TrendData(List<Object> data, int yaxis, String color, FlotBarOptions bars, FlotLineOptions lines
            , FlotPointOptions points) {
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
    private FlotBarOptions bars;
    private FlotLineOptions lines;
    private FlotPointOptions points;

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

    public FlotBarOptions getBars() {
        if(bars == null) {
            bars = new FlotBarOptions();
        }
        return bars;
    }

    public void setBars(FlotBarOptions bars) {
        this.bars = bars;
    }

    public FlotLineOptions getLines() {
        if(lines == null) {
            lines = new FlotLineOptions();
        }
        return lines;
    }

    public void setLines(FlotLineOptions lines) {
        this.lines = lines;
    }

    public FlotPointOptions getPoints() {
        if (points == null) {
            points = new FlotPointOptions();
        }
        return points;
    }

    public void setPoints(FlotPointOptions points) {
        this.points = points;
    }
}
