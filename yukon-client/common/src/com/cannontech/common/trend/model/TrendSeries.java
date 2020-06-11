package com.cannontech.common.trend.model;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TrendSeries {
    private TrendType.GraphType type;
    private Integer pointId;
    private String label;
    private Color color;
    private TrendAxis axis;
    private Double multiplier;
    private RenderType style;
    private LocalDate date;

    public TrendType.GraphType getType() {
        return type;
    }

    public void setType(TrendType.GraphType type) {
        this.type = type;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TrendAxis getAxis() {
        return axis;
    }

    public void setAxis(TrendAxis axis) {
        this.axis = axis;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public RenderType getStyle() {
        return style;
    }

    public void setStyle(RenderType style) {
        this.style = style;
    }

    @JsonSerialize(using=DateSerializer.class)
    @JsonDeserialize(using=DateDeserializer.class)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
