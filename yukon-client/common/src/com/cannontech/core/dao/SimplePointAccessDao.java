package com.cannontech.core.dao;

import java.util.Date;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.PointState;
import com.cannontech.message.dispatch.message.PointData;

public interface SimplePointAccessDao {

    public double getPointValue(LitePoint point) throws PointDataException;

    public void setPointValue(LitePoint point, double value);
    public void setPointValue(LitePoint point, PointState pointState);
    
    public void setPointValue(LitePoint point, Date time, double value);
    public void setPointValue(LitePoint point,  Date time, PointState pointState);
    
    public void writePointData(PointData pointData);

}