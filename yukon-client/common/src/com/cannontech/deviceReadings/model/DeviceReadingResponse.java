package com.cannontech.deviceReadings.model;

import java.util.Date;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DeviceReadingResponse {

    private Identifier identifier;
    private Attribute attribute;
    private int pointId;
    private int type;
    private PointQuality pointQuality;
    private double value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
    private Date time = new Date();

    public DeviceReadingResponse(Identifier identifier, Attribute attribute, PointValueQualityHolder pointValue) {
        super();
        this.identifier = identifier;
        this.attribute = attribute;
        this.pointId = pointValue.getId();
        this.type = pointValue.getType();
        this.pointQuality = pointValue.getPointQuality();
        this.value = pointValue.getValue();
        this.time = pointValue.getPointDataTimeStamp();
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PointQuality getPointQuality() {
        return pointQuality;
    }

    public void setPointQuality(PointQuality pointQuality) {
        this.pointQuality = pointQuality;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}
