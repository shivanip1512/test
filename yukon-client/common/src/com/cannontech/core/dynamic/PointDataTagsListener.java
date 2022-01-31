package com.cannontech.core.dynamic;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;

public interface PointDataTagsListener extends PointDataListener {
    public static final Logger log = YukonLogManager.getLogger(PointDataTagsListener.class);
    
    void pointDataReceived(PointValueQualityTagHolder pointData);
    
    @Override
    default void pointDataReceived(PointValueQualityHolder pointData) {
        if (pointData instanceof PointValueQualityTagHolder) {
            pointDataReceived((PointValueQualityTagHolder) pointData);
        } else {
            //This just eats the pointData with a warning if it isn't a PointValueQualityTagHolder
            log.warn("Point data received with no tags. ID: , date: {}, value: {}", pointData.getId(), pointData.getPointDataTimeStamp(), pointData.getValue());
        }
    }
}
