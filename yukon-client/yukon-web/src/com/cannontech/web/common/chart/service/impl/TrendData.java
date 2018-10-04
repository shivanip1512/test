package com.cannontech.web.common.chart.service.impl;

import java.util.List;

public class TrendData {
    public TrendData(List<Object> data, int yaxis) {
        super();
        this.data = data;
        this.yaxis = yaxis;
    }

    private List<Object> data;
    private int yaxis;

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
}
