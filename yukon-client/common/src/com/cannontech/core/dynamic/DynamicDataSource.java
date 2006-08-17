package com.cannontech.core.dynamic;

import java.util.Map;
import java.util.Set;

import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

/**
 * DynamicDataSource can be used to obtain dynamic/current point
 * related Yukon data.  
 * @author alauinger
 *
 */
public interface DynamicDataSource {
    
    public void putValue(PointData pointData) throws DynamicDataAccessException;
    public void putValue(int pointId, double value) throws DynamicDataAccessException;
    
    public PointData getPointData(int pointId) throws DynamicDataAccessException;;
    public Set<PointData> getPointData(Set<Integer> pointIds) throws DynamicDataAccessException;;
    
    public Set<Signal> getSignals(int pointId) throws DynamicDataAccessException;
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) throws DynamicDataAccessException;
    
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) throws DynamicDataAccessException;;
    
    public Integer getTags(int pointId) throws DynamicDataAccessException;;
    public Set<Integer> getTags(Set<Integer> pointIds) throws DynamicDataAccessException;
}
