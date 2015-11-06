package com.cannontech.core.dynamic.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.message.dispatch.message.LitePointData;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DynamicDataSourceImpl implements DynamicDataSource {

    @Autowired private DynamicDataCache dynamicDataCache;
    @Autowired private DispatchProxy dispatchProxy;
    
    @Override
    public void putValue(PointData pointData) {
        dispatchProxy.putPointData(pointData);
        dynamicDataCache.handleIncoming(pointData);
    }
    
    @Override
    public void putValues(Iterable<PointData> pointDatas) throws DynamicDataAccessException {
        dispatchProxy.putPointData(pointDatas);
        for (PointData pointData : pointDatas) {
            dynamicDataCache.handleIncoming(pointData);
        }
    }

    @Override
    public void putValue(int pointId, double value) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setValue(value);
        putValue(pointData);
    }    
    
    @Override
    public LitePointData getPointData(int pointId) {
        LitePointData pointData = dynamicDataCache.getPointData(pointId);
        if (pointData == null) {
            pointData = dispatchProxy.getPointData(pointId);
        }
        return pointData;
    }

    @Override
    public Set<LitePointData> getPointData(Set<Integer> pointIds) {
        
        Set<Integer> notCachedPointIds = new HashSet<Integer>(pointIds);
        Set<LitePointData> pointData = new HashSet<LitePointData>((int) (pointIds.size() / 0.75f) + 1);
        
        // Get whatever we can out of the cache first
        for (Integer id : pointIds) {
            LitePointData pd = dynamicDataCache.getPointData(id);
            if (pd != null) {
                pointData.add(pd);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        if (notCachedPointIds.size() > 0) {
            // break the request into partitions of 10000 so we reduce the risk of the request timing out
            List<List<Integer>> notCachedPointIdsPartitioned = Lists.partition(Lists.newArrayList(notCachedPointIds), 10000);
            for (List<Integer> notCachedPointIdsPartition: notCachedPointIdsPartitioned) {
                Set<LitePointData> retrievedPointData = dispatchProxy.getPointData(Sets.newHashSet(notCachedPointIdsPartition));
                pointData.addAll(retrievedPointData);
            }
        }
        
        return pointData;
    }

    @Override
    public Set<Signal> getSignals(int pointId) {
        
        Set<Signal> signals = dynamicDataCache.getSignals(pointId);
        if (signals == null) {
            signals = dispatchProxy.getSignals(pointId);
            dynamicDataCache.handleSignals(signals, pointId);
        }
        
        return new HashSet<Signal>(signals);
    }
    
    @Override
    public Set<Signal> getCachedSignals(int pointId) {
        
        Set<Signal> signals = dynamicDataCache.getSignals(pointId);
        if (signals != null) {
           return signals;
        }
        return new HashSet<>();
    }

    @Override
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) {
        
        Set<Integer> notCachedPointIds = new HashSet<Integer>(pointIds);
        Map<Integer, Set<Signal>> signals = new HashMap<Integer, Set<Signal>>((int)(pointIds.size() / 0.75f ) + 1);
        
        // Get whatever we can out of the cache first
        for (Integer id : pointIds) {
            Set<Signal> s = dynamicDataCache.getSignals(id);
            if (s != null) {
                signals.put(id, s);
                notCachedPointIds.remove(id);
            }
        }
        
        // Request to dispatch for the rest
        if (!notCachedPointIds.isEmpty()){
            Map<Integer, Set<Signal>> retrievedSignals = dispatchProxy.getSignals(notCachedPointIds);
            signals.putAll(retrievedSignals);
            for (Integer pointId : notCachedPointIds) {
                Set<Signal> pointSignals = retrievedSignals.get(pointId);
                if (pointSignals == null) pointSignals = Collections.emptySet();
                dynamicDataCache.handleSignals(pointSignals, pointId);
            }
        }
        
        return signals;
    }
    
    @Override
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) {
        Set<Signal> signals = dynamicDataCache.getSignalForCategory(alarmCategoryId);
        if (signals == null) {
            signals = new HashSet<Signal>();
            Set<Signal> sigSet = dispatchProxy.getSignalsByCategory(alarmCategoryId);
            if (sigSet != null && !sigSet.isEmpty()) {
                signals.addAll(sigSet);
            }
        }
        return signals;
    }
    
    @Override
    public Set<Signal> getCachedSignalsByCategory(int alarmCategoryId) {
        Set<Signal> signals = dynamicDataCache.getSignalForCategory(alarmCategoryId);
        if (signals != null) {
            return signals;
        }
        return new HashSet<>();
    }

    @Override
    public Integer getTags(int pointId) {
        return (int) getPointData(pointId).getTags();
    }
    
    @Override
    public Set<Integer> getTags(Set<Integer> pointIds) {
        Set<LitePointData> pointData = getPointData(pointIds);
        Set<Integer> tags = new HashSet<Integer>((int)(pointIds.size() / 0.75f) + 1);
        for (LitePointData pd : pointData) {
            tags.add((int)pd.getTags());
        }
        return tags;
    }
    
    @Override
    public PointValueQualityHolder getPointValue(int pointId) throws DynamicDataAccessException {
        return getPointData(pointId);
    }
    
    @Override
    public Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds) throws DynamicDataAccessException {
        return getPointData(pointIds);
    }
    
}