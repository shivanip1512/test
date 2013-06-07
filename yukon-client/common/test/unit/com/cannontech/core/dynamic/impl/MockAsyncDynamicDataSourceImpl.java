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
import com.cannontech.dispatch.DbChangeCategory;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public class MockAsyncDynamicDataSourceImpl implements AsyncDynamicDataSource {

    @Override
    public PointValueQualityHolder getAndRegisterForPointData(PointDataListener l, int pointId) {
        throw new MethodNotImplementedException();
    }

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
    public void registerForSignals(SignalListener l, Set<Integer> pointIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void registerForAllAlarms(SignalListener listener) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void unRegisterForSignals(SignalListener l, Set<Integer> pointIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void unRegisterForSignals(SignalListener l) {
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
    public void publishDbChange(DBChangeMessage dbChange) {
        throw new MethodNotImplementedException();
    }

    @Override
    @Deprecated
    public void addDBChangeLiteListener(DBChangeLiteListener l) {
        throw new MethodNotImplementedException();
    }

    @Override
    @Deprecated
    public void removeDBChangeLiteListener(DBChangeLiteListener l) {
        throw new MethodNotImplementedException();
    }
    

}
