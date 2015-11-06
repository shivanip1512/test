package com.cannontech.message.dispatch.message;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Instant;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.point.PointType;

public class LitePointData extends LiteBase implements PointValueQualityHolder {
    private int id;
    private int type;
    private PointQuality pointQuality;
    private long tags;
    private double value;
    private Date time = new Date();

    public static LitePointData of(PointData pointData) {
        LitePointData lpd = new LitePointData();
        lpd.setId(pointData.getId());
        lpd.setType(pointData.getType());
        lpd.setPointQuality(pointData.getPointQuality());
        lpd.setTags(pointData.getTags());
        lpd.setValue(pointData.getValue());
        lpd.setTime(pointData.getPointDataTimeStamp());
        return lpd;
    }

    public int getId() {
        return id;
    }

    public Date getPointDataTimeStamp() {
        return time;
    }

    public PointQuality getPointQuality() {
        return pointQuality;
    }

    public long getTags() {
        return tags;
    }

    public int getType() {
        return type;
    }

    public PointType getPointType() {
        return PointType.getForId(type);
    }

    public double getValue() {
        return value;
    }

    public void setId(int newId) {
        id = newId;
    }

    public void setTags(long newTags) {
        tags = newTags;
    }

    public void setTime(java.util.Date newTime) {
        time = newTime;
    }

    public void setType(int newType) {
        type = newType;
    }

    public void setValue(double newValue) {
        value = newValue;
    }

    public void setPointQuality(PointQuality pointQuality) {
        this.pointQuality = pointQuality;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LitePointData) {
            return ((LitePointData) other).id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(6709, 3511).append(id).toHashCode();
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("id", getId());
        tsc.append("value", getValue());
        tsc.append("timestamp", new Instant(getPointDataTimeStamp()));
        tsc.append("type", getType());
        tsc.append("quality", getPointQuality().getQuality());
        return tsc.toString();
    }
}
