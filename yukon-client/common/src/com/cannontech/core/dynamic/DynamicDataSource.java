package com.cannontech.core.dynamic;

import java.util.Map;
import java.util.Set;

import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
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
        
    public PointValueQualityHolder getPointValue(int pointId);
    
    public Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds);
    
    public Set<Signal> getSignals(int pointId);
    
    /**
     * Returns a map of point id to set of signals.  If no signals exist the map value will be
     * an empty set.
     * @throws DynamicDataAccessException
     */
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds);
    
    public Set<Signal> getSignalsByCategory(int alarmCategoryId);
    
    public Integer getTags(int pointId);
    
    public Set<Integer> getTags(Set<Integer> pointIds);

    /**
     * If no signals exist empty set will be returned.
     */
    Set<Signal> getCachedSignalsByCategory(int alarmCategoryId);

    /**
     * If no signals exist empty set will be returned.
     */
    Set<Signal> getCachedSignals(int pointId);
}
