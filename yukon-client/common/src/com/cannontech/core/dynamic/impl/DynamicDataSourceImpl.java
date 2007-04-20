package com.cannontech.core.dynamic.impl;

import java.util.*;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

/**
 * Implementation of DynamicDataSource
 * 
 * @author alauinger
 *
 */
public class DynamicDataSourceImpl implements DynamicDataSource {

    DynamicDataCache dynamicDataCache;
    DispatchProxy dispatchProxy;
    PointDao pointDao;
    
    public void putValue(PointData pointData) {
        dispatchProxy.putPointData(pointData);
        dynamicDataCache.handleIncoming(pointData);
    }

    public void putValue(int pointId, double value) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setValue(value);
        putValue(pointData);
    }    
    
    public PointData getPointData(int pointId) {
        PointData pointData = dynamicDataCache.getPointData(pointId);
        if(pointData == null) {
            pointData = dispatchProxy.getPointData(pointId);
        }
        return pointData;
    }

    public Set<PointData> getPointData(Set<Integer> pointIds) {
        Set<Integer> notCachedPointIds = 
            new HashSet<Integer>(pointIds);
        Set<PointData> pointData = 
            new HashSet<PointData>((int) (pointIds.size()/0.75f)+1);
        
        // Get whatever we can out of the cache first
        for (Integer id : pointIds) {
            PointData pd = dynamicDataCache.getPointData(id);
            if(pd != null) {
                pointData.add(pd);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        if(notCachedPointIds.size() > 0) {
            Set<PointData> retrievedPointData = 
                dispatchProxy.getPointData(notCachedPointIds);
            pointData.addAll(retrievedPointData);            
        }
               
        return pointData;       
    }

    public Set<Signal> getSignals(int pointId) {
        Set<Signal> signals = dynamicDataCache.getSignals(pointId);
        if(signals.isEmpty()) {
            signals = dispatchProxy.getSignals(pointId);
        }
        return signals;
    }

    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) {
        Set<Integer> notCachedPointIds =
            new HashSet<Integer>(pointIds);
        Map<Integer, Set<Signal>> signals =
            new HashMap<Integer, Set<Signal>>((int)(pointIds.size()/0.75f)+1);
        
        // Get whatever we can out of the cache first
        for(Integer id : pointIds) {
            Set<Signal> s = dynamicDataCache.getSignals(id);
            if(s != null) {
                signals.put(id, s);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        Map<Integer, Set<Signal>> retrievedSignals = 
            dispatchProxy.getSignals(notCachedPointIds);
        
        signals.putAll(retrievedSignals);
        return signals;
    }
    
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) {
        Set<Signal> signals = dynamicDataCache.getSignalForCategory(alarmCategoryId);
        if(signals == null) {
            Set<Signal> sigSet = dispatchProxy.getSignalsByCategory(alarmCategoryId);
            if(signals == null) {
                signals = new HashSet<Signal>();
                return signals;
            }else {
                signals.addAll(sigSet);
            }
        }
        
        return signals;
    }

    public Integer getTags(int pointId) {
        return (int)getPointData(pointId).getTags();
    }
    
    public Set<Integer> getTags(Set<Integer> pointIds) {
        Set<PointData> pointData = getPointData(pointIds);
        Set<Integer> tags = new HashSet<Integer>((int)(pointIds.size()/0.75f)+1);
        for (PointData pd : pointData) {
            tags.add((int)pd.getTags());
        }
        return tags;
    }
    
    public void setDynamicDataCache(DynamicDataCache dynamicDataCache) {
        this.dynamicDataCache = dynamicDataCache;
    }
    
    public void setDispatchProxy(DispatchProxy dispatchProxy) {
        this.dispatchProxy = dispatchProxy;
    }
    
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
