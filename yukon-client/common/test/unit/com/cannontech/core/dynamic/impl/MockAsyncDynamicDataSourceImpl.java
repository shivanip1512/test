package com.cannontech.core.dynamic.impl;

import java.util.Set;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.PointData;

public class MockAsyncDynamicDataSourceImpl implements AsyncDynamicDataSource {

    @Override
    public Set<? extends PointValueQualityHolder> getAndRegisterForPointData(PointDataListener l,
                                                                             Set<Integer> pointIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void registerForAllPointData(PointDataListener l) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void registerForPointData(PointDataListener l, Set<Integer> pointIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void unRegisterForPointData(PointDataListener l) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void registerForAllAlarms(SignalListener listener) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void addDBChangeListener(DBChangeListener l) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void removeDBChangeListener(DBChangeListener l) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void addDatabaseChangeEventListener(DatabaseChangeEventListener listener) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void addDatabaseChangeEventListener(DbChangeCategory changeCategory,
                                               DatabaseChangeEventListener listener) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void addDatabaseChangeEventListener(DbChangeCategory changeCategory,
                                               Set<DbChangeType> types,
                                               DatabaseChangeEventListener listener) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void publishDbChange(DBChangeMsg dbChange) {
        throw new MethodNotImplementedException();
    }

    @Override
    @Deprecated
    public void addDBChangeLiteListener(DBChangeLiteListener l) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void putValue(PointData pointData) {
        throw new MethodNotImplementedException();
        
    }

    @Override
    public void putValues(Iterable<PointData> pointDatas) {
        throw new MethodNotImplementedException();  
    }
}
