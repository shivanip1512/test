package com.cannontech.common.trend.model;

import org.joda.time.DateTime;
import org.springframework.validation.BindingResult;

import com.cannontech.common.trend.model.TrendType.GraphType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonInclude(Include.NON_NULL)
public class TrendSeries {
    private TrendType.GraphType type;
    private Integer pointId;
    private String label;
    private Color color;
    private TrendAxis axis;
    private Double multiplier;
    private RenderType style;
    private DateTime date;
    
    public TrendSeries () {
        this.color = Color.getNextDefaultColor(0);
    }
    
    public TrendSeries (Color color) {
        this.color = color;
        applyDefaults();
    }

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
    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        return String.format("TrendSeries [graphType=%s, pointId=%s, label=%s, color=%s, axis=%s, multiplier=%s, style=%s, date=%s]",
                type, pointId, label, color, axis, multiplier, style, date);
    }
    
    /**
     * Update the TrendSeries with default values when null.
     */
    public void applyDefaults() {
        if (getDate() == null) {
            setDate(DateTime.now());
        }
        setDefaultValues();
    }
    
    public void applyDefaultsIfNoErrors(BindingResult results) {
        if (!results.hasFieldErrors("date")) {
            setDate(DateTime.now());
        }
        setDefaultValues();
    }
    
    private void setDefaultValues() {
        if (getAxis() == null) {
            setAxis(TrendAxis.LEFT);
        }
        if (getStyle() == null) {
            setStyle(RenderType.LINE);
        }
        if (getType() == null) {
            setType(GraphType.BASIC_TYPE);
        }
        if (getMultiplier() == null) {
            setMultiplier(1d);
        }
    }
    
    public void setMarkerDefaults() {
        setType(GraphType.MARKER_TYPE);
        setPointId(-100);
    }
}