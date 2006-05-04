package com.cannontech.database.cache.functions;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;

public class SimplePointAccess {
    private PointChangeCache cache;

    public SimplePointAccess() {
        this(PointChangeCache.getPointChangeCache());
    }
    
    public SimplePointAccess(PointChangeCache pointChangeCache) {
        cache = pointChangeCache;
    }
    
    public double getPointValue(LitePoint point) {
        PointData pointData = cache.getValue(point.getPointID());
        //Validate.notNull(pointData, "No PointData in cache for pointId " + point.getPointID());
        if (pointData != null) {
            return pointData.getValue();
        }
        // okay, try something else
        throw new IllegalStateException("No point data found for point: " + point);
    }
    
    public void setPointValue(LitePoint point, double value) {
        PointData pointData = new PointData();
        pointData.setId(point.getPointID());
        pointData.setValue(value);
        pointData.setType(PointTypes.ANALOG_POINT);
        pointData.setQuality(PointQualities.NORMAL_QUALITY);
        cache.putValue(pointData);
    }

}
