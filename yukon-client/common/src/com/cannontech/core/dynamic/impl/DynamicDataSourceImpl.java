package com.cannontech.core.dynamic.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
    
    @Override
    public void putValue(PointDataMessage pointData) {
        dispatchProxy.putPointData(pointData);
        dynamicDataCache.handleIncoming(pointData);
    }
    
    @Override
    public void putValues(Iterable<PointDataMessage> pointDatas) throws DynamicDataAccessException {
        dispatchProxy.putPointData(pointDatas);
        for (PointDataMessage pointData : pointDatas) {
            dynamicDataCache.handleIncoming(pointData);
        }
    }

    @Override
    public void putValue(int pointId, double value) {
        PointDataMessage pointData = new PointDataMessage();
        pointData.setId(pointId);
        pointData.setValue(value);
        putValue(pointData);
    }    
    
    @Override
    public PointDataMessage getPointData(int pointId) {
        PointDataMessage pointData = dynamicDataCache.getPointData(pointId);
        if(pointData == null) {
            pointData = dispatchProxy.getPointData(pointId);
        }
        return pointData;
    }

    @Override
    public Set<PointDataMessage> getPointData(Set<Integer> pointIds) {
        Set<Integer> notCachedPointIds = 
            new HashSet<Integer>(pointIds);
        Set<PointDataMessage> pointData = 
            new HashSet<PointDataMessage>((int) (pointIds.size()/0.75f)+1);
        
        // Get whatever we can out of the cache first
        for (Integer id : pointIds) {
            PointDataMessage pd = dynamicDataCache.getPointData(id);
            if(pd != null) {
                pointData.add(pd);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        if(notCachedPointIds.size() > 0) {
            // break the request into partitions of 1000 so we reduce the risk of the request timing out
            List<List<Integer>> notCachedPointIdsPartitioned = Lists.partition(Lists.newArrayList(notCachedPointIds), 1000);
            for (List<Integer> notCachedPointIdsPartition: notCachedPointIdsPartitioned) {
                Set<PointDataMessage> retrievedPointData = dispatchProxy.getPointData(Sets.newHashSet(notCachedPointIdsPartition));
                pointData.addAll(retrievedPointData);
            }
        }
               
        return pointData;       
    }

    @Override
    public Set<SignalMessage> getSignals(int pointId) {
        Set<SignalMessage> signals = dynamicDataCache.getSignals(pointId);
        if(signals == null) {
            signals = dispatchProxy.getSignals(pointId);
            dynamicDataCache.handleSignals(signals, pointId);
        }
        return new HashSet<SignalMessage>(signals);
    }

    @Override
    public Map<Integer, Set<SignalMessage>> getSignals(Set<Integer> pointIds) {
        Set<Integer> notCachedPointIds = new HashSet<Integer>(pointIds);
        Map<Integer, Set<SignalMessage>> signals = new HashMap<Integer, Set<SignalMessage>>((int)(pointIds.size()/0.75f)+1);
        
        // Get whatever we can out of the cache first
        for(Integer id : pointIds) {
            Set<SignalMessage> s = dynamicDataCache.getSignals(id);
            if(s != null) {
                signals.put(id, s);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        if(!notCachedPointIds.isEmpty()){
            Map<Integer, Set<SignalMessage>> retrievedSignals = dispatchProxy.getSignals(notCachedPointIds);
            signals.putAll(retrievedSignals);
            for(Integer pointId : notCachedPointIds) {
                Set<SignalMessage> pointSignals = retrievedSignals.get(pointId);
                if(pointSignals == null) pointSignals = Collections.emptySet();
                dynamicDataCache.handleSignals(pointSignals, pointId);
            }
        }
        return signals;
    }
    
    @Override
    public Set<SignalMessage> getSignalsByCategory(int alarmCategoryId) {
        Set<SignalMessage> signals = dynamicDataCache.getSignalForCategory(alarmCategoryId);
        if(signals == null) {
            signals = new HashSet<SignalMessage>();
            Set<SignalMessage> sigSet = dispatchProxy.getSignalsByCategory(alarmCategoryId);
            if(sigSet != null && !sigSet.isEmpty()) {
                signals.addAll(sigSet);
            }
        }
        return signals;
    }

    @Override
    public Integer getTags(int pointId) {
        return (int)getPointData(pointId).getTags();
    }
    
    @Override
    public Set<Integer> getTags(Set<Integer> pointIds) {
        Set<PointDataMessage> pointData = getPointData(pointIds);
        Set<Integer> tags = new HashSet<Integer>((int)(pointIds.size()/0.75f)+1);
        for (PointDataMessage pd : pointData) {
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

    @Override
    public PointValueQualityHolder getPointValue(int pointId) throws DynamicDataAccessException {
        return getPointData(pointId);
    }
    
    @Override
    public Set<? extends PointValueQualityHolder> getPointValue(Set<Integer> pointIds) throws DynamicDataAccessException {
        return getPointData(pointIds);
    }
}
