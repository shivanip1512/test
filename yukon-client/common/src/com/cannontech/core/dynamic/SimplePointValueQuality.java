package com.cannontech.core.dynamic;

import java.util.Date;

import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.point.PointType;

/**
 * A simple, immutable implementation of PointValueQualityHolder.
 */
public final class SimplePointValueQuality implements PointValueQualityHolder {
    private final int pointId;
    private final Date pointDataTimestamp;
    private final PointType pointType;
    private final PointQuality quality;
    private final double value;
    
    public SimplePointValueQuality(int id, Date pointDataTimestamp, PointType type, PointQuality quality, double value) {
        pointId = id;
        this.pointDataTimestamp = pointDataTimestamp;
        pointType = type;
        this.quality = quality;
        this.value = value;
    }
    
    @Override
    public int getId() {
        return pointId;
    }

    @Override
    public Date getPointDataTimeStamp() {
        return pointDataTimestamp;
    }

    @Override
    public int getType() {
        return pointType.getPointTypeId();
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public PointQuality getPointQuality() {
        return quality;
    }

    @Override
    public PointType getPointType() {
        return pointType;
    }
}
