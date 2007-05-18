package com.cannontech.core.dynamic;


/**
 * Implement this interface to receive PointData from Yukon.
 * @see com.cannontech.core.dynamic.AsyncDynamicDataSource 
 * @author alauinger
 */
public interface PointDataListener {
    public void pointDataReceived(PointValueHolder pointData);
}
