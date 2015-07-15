package com.cannontech.core.dynamic;

import java.util.Map;
import java.util.Set;

import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

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
    
    public void putValue(PointData pointData) throws DynamicDataAccessException;
    
    public void putValue(int pointId, double value) throws DynamicDataAccessException;
    
    public void putValues(Iterable<PointData> pointDatas) throws DynamicDataAccessException;
    
    /**
     * Please use getPointValue to avoid creating a dependency on the actual PointData message
     * @param pointId
     * @return
     * @throws DynamicDataAccessException
     * @Deprecated use getPointValue
     */
    @Deprecated
    public PointData getPointData(int pointId) throws DynamicDataAccessException;
    /**
     * Please use getPointValue to avoid creating a dependency on the actual PointData message
     * @param pointId
     * @return
     * @throws DynamicDataAccessException
     * @Deprecated use getPointValue
     */
    @Deprecated
    public Set<PointData> getPointData(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public PointValueQualityHolder getPointValue(int pointId) throws DynamicDataAccessException;
    
    public Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public Set<Signal> getSignals(int pointId) throws DynamicDataAccessException;
    
    /**
     * Returns a map of point id to set of signals.  If no signals exist the map value will be
     * an empty set.
     * @throws DynamicDataAccessException
     */
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) throws DynamicDataAccessException;
    
    public Integer getTags(int pointId) throws DynamicDataAccessException;
    
    public Set<Integer> getTags(Set<Integer> pointIds) throws DynamicDataAccessException;

    /**
     * If no signals exist empty set will be returned.
     */
    Set<Signal> getCachedSignalsByCategory(int alarmCategoryId);

    /**
     * If no signals exist empty set will be returned.
     */
    Set<Signal> getCachedSignals(int pointId);
}
