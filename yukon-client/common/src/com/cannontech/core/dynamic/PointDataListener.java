package com.cannontech.core.dynamic;

import com.cannontech.message.dispatch.message.PointData;

/**
 * Implement this interface to receive PointData from Yukon.
 * @see com.cannontech.core.dynamic.AsyncDynamicDataSource 
 * @author alauinger
 */
public interface PointDataListener {
    public void pointDataReceived(PointData pointData);
}
