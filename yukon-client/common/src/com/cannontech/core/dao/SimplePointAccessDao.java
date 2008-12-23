package com.cannontech.core.dao;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;

public interface SimplePointAccessDao {

    public double getPointValue(LitePoint point) throws PointDataException;

    public void setPointValue(LitePoint point, double value);
    
    public void writePointData(PointData pointData);

}