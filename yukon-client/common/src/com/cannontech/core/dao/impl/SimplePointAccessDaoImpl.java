package com.cannontech.core.dao.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
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
        setPointValue(point.getPointID(), new Instant(), value, PointTypes.ANALOG_POINT);
    }

    @Override
    public void setPointValue(int pointId, double value) {
        setPointValue(pointId, new Instant(), value, PointTypes.ANALOG_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point, PointState pointState) {
        setPointValue(point.getPointID(), new Instant(), pointState.getRawState(), PointTypes.STATUS_POINT);
    }
    
    @Override
    public void setPointValue(int pointId, PointState pointState) {
        setPointValue(pointId, new Instant(), pointState.getRawState(), PointTypes.STATUS_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point, Instant time, double value) {
        setPointValue(point.getPointID(), time, value, PointTypes.ANALOG_POINT);
    }

    @Override
    public void setPointValue(int pointId, Instant time, double value) {
        setPointValue(pointId, time, value, PointTypes.ANALOG_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point,  Instant time, PointState pointState) {
        setPointValue(point.getPointID(),time,pointState.getRawState(),PointTypes.STATUS_POINT);
    }
    
    @Override
    public void setPointValue(int pointId, Instant time, PointState pointState) {
        setPointValue(pointId,time,pointState.getRawState(),PointTypes.STATUS_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point, double value, PointQuality quality) {
        setPointValue(point.getPointID(), new Instant(), value, PointTypes.ANALOG_POINT, quality);
    }
    
    private void setPointValue(int pointId, Instant time, double value, int pointType) {
        setPointValue(pointId, time, value, pointType, PointQuality.Normal);
    }

    private void setPointValue(int pointId, Instant time, double value, int pointType, PointQuality quality) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setValue(value);
        pointData.setType(pointType);
        pointData.setPointQuality(quality);
        pointData.setTime(time.toDate());

        writePointData(pointData);
    }
    
    @Override
    public void writePointData(PointData pointData) {
        asyncDynamicDataSource.putValue(pointData);
    }
    
}
