package com.cannontech.message.dispatch.message;

import java.util.Date;

import org.joda.time.Instant;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.point.PointType;

public class LitePointData extends LiteBase implements PointValueQualityTagHolder {
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Date getPointDataTimeStamp() {
        return time;
    }

    @Override
    public PointQuality getPointQuality() {
        return pointQuality;
    }

    @Override
    public long getTags() {
        return tags;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public PointType getPointType() {
        return PointType.getForId(type);
    }

    @Override
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
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + id;
        result = prime * result + ((pointQuality == null) ? 0 : pointQuality.hashCode());
        result = prime * result + (int) (tags ^ (tags >>> 32));
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + type;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LitePointData other = (LitePointData) obj;
        if (id != other.id) {
            return false;
        }
        if (pointQuality != other.pointQuality) {
            return false;
        }
        if (tags != other.tags) {
            return false;
        }
        if (time == null) {
            if (other.time != null) {
                return false;
            }
        } else if (!time.equals(other.time)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
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
