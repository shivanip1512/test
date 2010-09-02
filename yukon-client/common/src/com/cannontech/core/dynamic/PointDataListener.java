package com.cannontech.core.dynamic;


/**
 * Implement this interface to receive new/fresh PointData from Yukon.
 * @see com.cannontech.core.dynamic.AsyncDynamicDataSource 
 */
public interface PointDataListener {
    public void pointDataReceived(PointValueQualityHolder pointData);
}
