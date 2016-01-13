package com.cannontech.core.dynamic.impl;

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
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.Signal;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MockDynamicDataSource implements DynamicDataSource {
    private List<PointValueQualityHolder> pointValueQualityHolderData;
    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(centralTimeZone);
    
    private Object[][] data = 
        {
            // Usage Data
            {1, PulseAccumulator, Normal, 600.2, "07/12/2012 15:13:20"},
            {1, PulseAccumulator, Normal, 620.4, "07/13/2012 15:13:20"},
            {1, PulseAccumulator, Normal, 645.6, "07/14/2012 15:13:20"},
            {1, PulseAccumulator, Normal, 660.8, "07/15/2012 15:13:20"},
            {1, PulseAccumulator, Normal, 670.0, "07/16/2012 15:13:20"},
            {1, PulseAccumulator, Normal, 675.2, "07/17/2012 15:13:20"},
            
            // Demand Data
            {2 ,DemandAccumulator, Normal, 20.0, "07/12/2012 15:13:20"},
            {2, DemandAccumulator, Normal, 20.2, "07/13/2012 15:13:20"},
            {2, DemandAccumulator, Normal, 25.2, "07/14/2012 15:13:20"},
            {2, DemandAccumulator, Normal, 15.2, "07/15/2012 15:13:20"},
            {2, DemandAccumulator, Normal, 10.2, "07/16/2012 15:13:20"},
            {2, DemandAccumulator, Normal, 5.2, "07/17/2012 15:13:20"},
            
        };
    
    public MockDynamicDataSource(Object[][] data) {
        if(data != null) {
            this.data = data;
        }
    }
    
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
    public PointValueQualityHolder getPointValue(int pointId) throws DynamicDataAccessException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<Signal> getSignals(int pointId) throws DynamicDataAccessException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds)
            throws DynamicDataAccessException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<Signal> getSignalsByCategory(int alarmCategoryId) throws DynamicDataAccessException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Integer getTags(int pointId) throws DynamicDataAccessException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<Integer> getTags(Set<Integer> pointIds) throws DynamicDataAccessException {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<Signal> getCachedSignalsByCategory(int alarmCategoryId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<Signal> getCachedSignals(int pointId) {
        throw new MethodNotImplementedException();
    }

}
