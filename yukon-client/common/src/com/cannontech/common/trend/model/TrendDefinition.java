package com.cannontech.common.trend.model;

import java.util.List;

public class TrendDefinition {
    private Integer trendId;
    private String name;
    private Boolean autoScaleLeftAxis;
    private Double scaleLeftMin;
    private Double scaleLeftMax;
    private Boolean autoScaleRightAxis;
    private Double scaleRightMin;
    private Double scaleRightMax;
    private List<TrendSeriesDefinition> trendSeriesDefinition;

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

    public Boolean isAutoScaleLeftAxis() {
        return autoScaleLeftAxis;
    }

    public void setAutoScaleLeftAxis(Boolean autoScaleLeftAxis) {
        this.autoScaleLeftAxis = autoScaleLeftAxis;
    }

    public Double getScaleLeftMin() {
        return scaleLeftMin;
    }

    public void setScaleLeftMin(Double scaleLeftMin) {
        this.scaleLeftMin = scaleLeftMin;
    }

    public Double getScaleLeftMax() {
        return scaleLeftMax;
    }

    public void setScaleLeftMax(Double scaleLeftMax) {
        this.scaleLeftMax = scaleLeftMax;
    }

    public Boolean isAutoScaleRightAxis() {
        return autoScaleRightAxis;
    }

    public void setAutoScaleRightAxis(Boolean autoScaleRightAxis) {
        this.autoScaleRightAxis = autoScaleRightAxis;
    }

    public Double getScaleRightMin() {
        return scaleRightMin;
    }

    public void setScaleRightMin(Double scaleRightMin) {
        this.scaleRightMin = scaleRightMin;
    }

    public Double getScaleRightMax() {
        return scaleRightMax;
    }

    public void setScaleRightMax(Double scaleRightMax) {
        this.scaleRightMax = scaleRightMax;
    }

    public List<TrendSeriesDefinition> getTrendSeriesDefinition() {
        return trendSeriesDefinition;
    }

    public void setTrendSeriesDefinition(List<TrendSeriesDefinition> trendSeriesDefinition) {
        this.trendSeriesDefinition = trendSeriesDefinition;
    }

}
