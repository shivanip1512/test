package com.cannontech.core.dynamic.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataTagsListener;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.dynamic.RichPointDataService;

public class RichPointDataServiceImpl implements RichPointDataService, PointDataTagsListener {
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private PointDao pointDao;
    
    private List<RichPointDataListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void registerForAll(RichPointDataListener listener) {
        if (listeners.isEmpty()) {
            asyncDynamicDataSource.registerForAllPointData(this);
        }
        listeners.add(listener);
    }

    @Override
    public void pointDataReceived(PointValueQualityTagHolder pointData) {
        PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointData.getId());
        RichPointData richPointData = new RichPointData(pointData, paoPointIdentifier);
        
        for (RichPointDataListener listener : listeners) {
            listener.pointDataReceived(richPointData);
        }
    }
    
    @Autowired
    public void setAsyncDynamicDataSource(
            AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
}
