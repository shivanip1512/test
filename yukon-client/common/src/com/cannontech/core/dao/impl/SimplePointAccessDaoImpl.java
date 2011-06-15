package com.cannontech.core.dao.impl;

import java.util.Date;

import com.cannontech.common.exception.PointDataException;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.stategroup.PointState;
import com.cannontech.message.dispatch.message.PointData;

public class SimplePointAccessDaoImpl implements SimplePointAccessDao {
    private DynamicDataSource dynamicDataSource;

    public SimplePointAccessDaoImpl() {
    }
    
    public double getPointValue(LitePoint point) throws PointDataException {
        PointValueHolder pointData = dynamicDataSource.getPointValue(point.getPointID());
        //Validate.notNull(pointData, "No PointData in cache for pointId " + point.getPointID());
        if (pointData != null) {
            return pointData.getValue();
        }
        // okay, try something else
        throw new PointDataException(point);
    }
    
    @Override
    public void setPointValue(LitePoint point, double value) {
        setPointValue(point, new Date(), value, PointTypes.ANALOG_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point, PointState pointState) {
        setPointValue(point, new Date(), pointState.getRawState(), PointTypes.STATUS_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point, Date time, double value) {
        setPointValue(point, time, value, PointTypes.ANALOG_POINT);
    }
    
    @Override
    public void setPointValue(LitePoint point,  Date time, PointState pointState) {
        setPointValue(point,time,pointState.getRawState(),PointTypes.STATUS_POINT);
    }
    
    private void setPointValue(LitePoint point, Date time, double value, int pointType) {
        PointData pointData = new PointData();
        pointData.setId(point.getPointID());
        pointData.setValue(value);
        pointData.setType(pointType);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTime(time);
        
        writePointData(pointData);
    }
    
    @Override
    public void writePointData(PointData pointData) {
    	dynamicDataSource.putValue(pointData);
    }
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
}
