package com.cannontech.core.dao.impl;

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
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.SimplePointAccessDao#getPointValue(com.cannontech.database.data.lite.LitePoint)
     */
    public double getPointValue(LitePoint point) throws PointDataException {
        PointValueHolder pointData = dynamicDataSource.getPointValue(point.getPointID());
        //Validate.notNull(pointData, "No PointData in cache for pointId " + point.getPointID());
        if (pointData != null) {
            return pointData.getValue();
        }
        // okay, try something else
        throw new PointDataException(point);
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.SimplePointAccessDao#setPointValue(com.cannontech.database.data.lite.LitePoint, double)
     */
    public void setPointValue(LitePoint point, double value) {
        PointData pointData = new PointData();
        pointData.setId(point.getPointID());
        pointData.setValue(value);
        pointData.setType(PointTypes.ANALOG_POINT);
        pointData.setPointQuality(PointQuality.Normal);
        writePointData(pointData);
    }
    
    public void setPointValue(LitePoint point, PointState pointState) {
        setPointValue(point,pointState.getRawState());
    }
    
    @Override
    public void writePointData(PointData pointData) {
    	dynamicDataSource.putValue(pointData);
    }
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
}
