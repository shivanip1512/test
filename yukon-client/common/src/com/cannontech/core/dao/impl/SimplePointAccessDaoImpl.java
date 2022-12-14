package com.cannontech.core.dao.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.PointState;
import com.cannontech.message.dispatch.message.PointData;

public class SimplePointAccessDaoImpl implements SimplePointAccessDao {
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

    @Override
    public double getPointValue(LitePoint point) throws PointDataException {
        PointValueHolder pointData = asyncDynamicDataSource.getPointValue(point.getPointID());
        //Validate.notNull(pointData, "No PointData in cache for pointId " + point.getPointID());
        if (pointData != null) {
            return pointData.getValue();
        }
        // okay, try something else
        throw new PointDataException(point);
    }
    
    @Override
    public void setPointValue(LitePoint point, double value) {
        setPointValue(point, new Instant(), value, point.getPointTypeEnum());
    }

    @Override
    public void setPointValue(LitePoint point, PointState pointState) {
        setPointValue(point, new Instant(), pointState.getRawState(), point.getPointTypeEnum());
    }
    
    @Override
    public void setPointValue(LitePoint point, Instant time, double value) {
        setPointValue(point, time, value, point.getPointTypeEnum());
    }

    @Override
    public void setPointValue(LitePoint point,  Instant time, PointState pointState) {
        setPointValue(point, time, pointState.getRawState(), point.getPointTypeEnum());
    }
    
    @Override
    public void setPointValue(LitePoint point, double value, PointQuality quality) {
        setPointValue(point, new Instant(), value, point.getPointTypeEnum(), quality);
    }
    
    private void setPointValue(LitePoint point, Instant time, double value, PointType type) {
        setPointValue(point, time, value, type, PointQuality.Normal);
    }

    private void setPointValue(LitePoint point, Instant time, double value, PointType type, PointQuality quality) {
        PointData pointData = new PointData();
        pointData.setId(point.getPointID());
        pointData.setValue(value);
        pointData.setType(type.getPointTypeId());
        pointData.setPointQuality(quality);
        pointData.setTime(time.toDate());

        writePointData(pointData);
    }
    
    @Override
    public void writePointData(PointData pointData) {
        asyncDynamicDataSource.putValue(pointData);
    }
    
}
