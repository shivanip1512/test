package com.cannontech.web.common.chart.service.impl;

import com.cannontech.common.chart.model.ConverterType;

public class GraphDetail {
    private int pointId;
    private ConverterType converterType;
    private String yLabelUnits;
    private int axisIndex;
    private String yAxisPosition;

    public int getAxisIndex() {
        return axisIndex;
    }

    public void setAxisIndex(int axisIndex) {
        this.axisIndex = axisIndex;
    }

    public GraphDetail(int pointId, ConverterType converterType, String yLabelUnits, int axisIndex, String yAxisPosition) {
        super();
        this.pointId = pointId;
        this.converterType = converterType;
        this.yLabelUnits = yLabelUnits;
        this.axisIndex = axisIndex;
        this.yAxisPosition = yAxisPosition;
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
}
