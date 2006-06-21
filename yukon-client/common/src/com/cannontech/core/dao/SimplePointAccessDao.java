package com.cannontech.core.dao;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.database.data.lite.LitePoint;

public interface SimplePointAccessDao {

    public double getPointValue(LitePoint point) throws PointDataException;

    public void setPointValue(LitePoint point, double value);

}