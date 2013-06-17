package com.cannontech.core.dao;

import org.joda.time.Instant;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.PointState;
import com.cannontech.message.dispatch.message.PointData;

public interface SimplePointAccessDao {

    public double getPointValue(LitePoint point) throws PointDataException;

    public void setPointValue(LitePoint point, double value);
    public void setPointValue(int pointId, double value);
    public void setPointValue(LitePoint point, PointState pointState);
    public void setPointValue(int pointId, PointState pointState);
    
    public void setPointValue(LitePoint point, Instant time, double value);
    public void setPointValue(int pointId, Instant time, double value);
    public void setPointValue(LitePoint point,  Instant time, PointState pointState);
    public void setPointValue(int pointId,  Instant time, PointState pointState);
    
    public void writePointData(PointData pointData);

}