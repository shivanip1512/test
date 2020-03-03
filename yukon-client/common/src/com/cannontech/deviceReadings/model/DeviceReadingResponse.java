package com.cannontech.deviceReadings.model;

import java.util.Date;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DeviceReadingResponse {

    private int pointId;
    private int type;
    private PointQuality pointQuality;
    private double value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ") 
    private Date time = new Date();

    private String meterNumber;
    private BuiltInAttribute attribute;

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
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

    public void setTime(Date date) {
        this.time = date;
    }

}
