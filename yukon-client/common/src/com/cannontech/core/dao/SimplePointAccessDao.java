package com.cannontech.core.dao;

import org.joda.time.Instant;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.PointState;
import com.cannontech.message.dispatch.message.PointData;

public interface SimplePointAccessDao {

    double getPointValue(LitePoint point) throws PointDataException;
    
    void setPointValue(LitePoint point, double value);
    void setPointValue(LitePoint point, PointState pointState);
    void setPointValue(LitePoint point, Instant time, double value);
    void setPointValue(LitePoint point, Instant time, PointState pointState);
    void setPointValue(LitePoint point, double value, PointQuality quality);
    
    void writePointData(PointData pointData);
}