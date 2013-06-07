package com.cannontech.core.dynamic;

import java.util.Map;
import java.util.Set;

import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;

/**
 * DynamicDataSource can be used to obtain dynamic/current point
 * related Yukon data.
 *   
 * If modifying this class, look at PointUpdateBackingService to see if some
 * of that code could be moved into here or into AsyncDynamicDataSource.
 * @author alauinger
 *
 */
public interface DynamicDataSource {
    
    public void putValue(PointDataMessage pointData) throws DynamicDataAccessException;
    public void putValues(Iterable<PointDataMessage> pointDatas) throws DynamicDataAccessException;
    public void putValue(int pointId, double value) throws DynamicDataAccessException;
    
    /**
     * Please use getPointValue to avoid creating a dependency on the actual PointData message
     * @param pointId
     * @return
     * @throws DynamicDataAccessException
     * @Deprecated use getPointValue
     */
    @Deprecated
    public PointDataMessage getPointData(int pointId) throws DynamicDataAccessException;
    /**
     * Please use getPointValue to avoid creating a dependency on the actual PointData message
     * @param pointId
     * @return
     * @throws DynamicDataAccessException
     * @Deprecated use getPointValue
     */
    @Deprecated
    public Set<PointDataMessage> getPointData(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public PointValueQualityHolder getPointValue(int pointId) throws DynamicDataAccessException;
    public Set<? extends PointValueQualityHolder> getPointValue(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public Set<SignalMessage> getSignals(int pointId) throws DynamicDataAccessException;
    public Map<Integer, Set<SignalMessage>> getSignals(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public Set<SignalMessage> getSignalsByCategory(int alarmCategoryId) throws DynamicDataAccessException;
    
    public Integer getTags(int pointId) throws DynamicDataAccessException;
    public Set<Integer> getTags(Set<Integer> pointIds) throws DynamicDataAccessException;
}
