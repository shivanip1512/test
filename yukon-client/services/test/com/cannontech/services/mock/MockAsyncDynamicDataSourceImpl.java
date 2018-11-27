package com.cannontech.services.mock;

import static com.cannontech.common.point.PointQuality.Normal;
import static com.cannontech.database.data.point.PointType.DemandAccumulator;
import static com.cannontech.database.data.point.PointType.PulseAccumulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.LitePointData;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MockAsyncDynamicDataSourceImpl implements AsyncDynamicDataSource {
    
    private List<PointValueQualityHolder> pointValueQualityHolderData;
    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(centralTimeZone);
    
    private Object[][] data = new Object[][] {
        // Usage Data
        { 1, PulseAccumulator, Normal, 600.2, "07/12/2012 15:13:20" },
        { 1, PulseAccumulator, Normal, 620.4, "07/13/2012 15:13:20" },
        { 1, PulseAccumulator, Normal, 645.6, "07/14/2012 15:13:20" },
        { 1, PulseAccumulator, Normal, 660.8, "07/15/2012 15:13:20" },
        { 1, PulseAccumulator, Normal, 670.0, "07/16/2012 15:13:20" },
        { 1, PulseAccumulator, Normal, 675.2, "07/17/2012 15:13:20" },

        // Demand Data
        { 2, DemandAccumulator, Normal, 20.0, "07/12/2012 15:13:20" },
        { 2, DemandAccumulator, Normal, 20.2, "07/13/2012 15:13:20" },
        { 2, DemandAccumulator, Normal, 25.2, "07/14/2012 15:13:20" },
        { 2, DemandAccumulator, Normal, 15.2, "07/15/2012 15:13:20" },
        { 2, DemandAccumulator, Normal, 10.2, "07/16/2012 15:13:20" },
        { 2, DemandAccumulator, Normal, 5.2, "07/17/2012 15:13:20" },

    };

    public void init() {
        // Converting the array data into point data.
        pointValueQualityHolderData =  new ArrayList<>();
        for (Object[] objectArray : data) {
            PointValueBuilder pvBuilder = PointValueBuilder.create();
            pvBuilder.withPointId((Integer)objectArray[0]);
            pvBuilder.withType((PointType) objectArray[1]);
            pvBuilder.withPointQuality((PointQuality) objectArray[2]);
            pvBuilder.withValue((Double)objectArray[3]);
            pvBuilder.withTimeStamp(dateTimeFormatter.parseDateTime((String) objectArray[4]).toDate());
            
            pointValueQualityHolderData.add(pvBuilder.build()); 
        }
    }
    
    @Override
    public Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds) throws DynamicDataAccessException {
        Map<Integer, PointValueQualityHolder> pointToPvqhMap = Maps.newHashMap();
        for(PointValueQualityHolder pvqh : pointValueQualityHolderData) {
            int pointId = pvqh.getId();
            //is this point one we're looking for?
            if(pointIds.contains(pointId)) {
                //is it "newer" than what we already have?
                PointValueQualityHolder oldPvqh = pointToPvqhMap.get(pointId);
                if(oldPvqh == null || pvqh.getPointDataTimeStamp().after(oldPvqh.getPointDataTimeStamp())) {
                    pointToPvqhMap.put(pointId, pvqh);
                }
            }
        }
        return Sets.newHashSet(pointToPvqhMap.values());
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
    public void registerForAllAlarms(SignalListener listener) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void addDBChangeListener(DBChangeListener l) {
        //  Ignore any listeners for now.  However, we will need to add support for this when anyone utilizes  publishDbChange().
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

    @Override
    public PointValueQualityHolder getPointValue(int pointId) {
        throw new MethodNotImplementedException();  
    }

    @Override
    public Set<Signal> getSignals(int pointId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public Set<Signal> getCachedSignals(int pointId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public Set<Signal> getCachedSignalsByCategory(int alarmCategoryId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public void logListenerInfo(int pointId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public Set<? extends PointValueQualityTagHolder> getPointValuesAndTags(Set<Integer> pointIds) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public long getTags(int pointId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public PointValueQualityTagHolder getPointValueAndTags(int pointId) {
        throw new MethodNotImplementedException(); 
    }

    @Override
    public Set<LitePointData> getPointDataOnce(Set<Integer> pointIds) {
        throw new MethodNotImplementedException();
    }
}
