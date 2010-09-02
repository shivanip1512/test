package com.cannontech.core.dynamic;

/**
 * Implement this interface to receive all PointData from Yukon
 * (including PointDatas that are older than the current value).
 * @see com.cannontech.core.dynamic.AsyncDynamicDataSource 
 */
public interface AllPointDataListener extends PointDataListener {
}
