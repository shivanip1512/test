package com.cannontech.common.trend.model;

import java.util.List;

public class TrendModel {
    private Integer trendId;
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
        this.name = name;
    }

    public List<TrendSeries> getTrendSeries() {
        return trendSeries;
    }

    public void setTrendSeries(List<TrendSeries> trendSeries) {
        this.trendSeries = trendSeries;
    }

}
