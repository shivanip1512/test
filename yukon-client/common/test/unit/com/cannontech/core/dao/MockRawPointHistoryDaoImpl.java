package com.cannontech.core.dao;

import static com.cannontech.common.point.PointQuality.*;
import static com.cannontech.database.data.point.PointType.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.ListUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.impl.RawPointHistoryDaoImpl;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class MockRawPointHistoryDaoImpl extends RawPointHistoryDaoImpl {

    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(centralTimeZone);

    private Object[][] rawPointHistoryData = 
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

    private static final Ordering<PointValueQualityHolder> ORDERING_BY_TIMESTAMP = new Ordering<PointValueQualityHolder>(){
        @Override
        public int compare(PointValueQualityHolder left, PointValueQualityHolder right) {
            return left.getPointDataTimeStamp().compareTo(right.getPointDataTimeStamp());
        }
    };

    private static final Ordering<PointValueQualityHolder> ORDERING_BY_VALUE = new Ordering<PointValueQualityHolder>(){
        @Override
        public int compare(PointValueQualityHolder left, PointValueQualityHolder right) {
            return Double.compare(left.getValue(), right.getValue());
        }
    };

    private List<PointValueQualityHolder> pointValueQualityHolderData;
    private Map<Long, PointValueQualityHolder> pointValueQualityHolderDataByRPHId;
    private ListMultimap<Instant, PointValueQualityHolder> pointValueQualityHolderDataByDate;

    public MockRawPointHistoryDaoImpl() {
        init();
    }
    public MockRawPointHistoryDaoImpl(Object[][] rawPointHistoryData) {
        this.rawPointHistoryData = rawPointHistoryData;
        init();
    }
    
    public void init() {
        // Converting the array data into point data.
        pointValueQualityHolderData =  new ArrayList<>();
        for (Object[] objectArray : rawPointHistoryData) {
            PointValueBuilder pvBuilder = PointValueBuilder.create();
            pvBuilder.withPointId((Integer)objectArray[0]);
            pvBuilder.withType((PointType) objectArray[1]);
            pvBuilder.withPointQuality((PointQuality) objectArray[2]);
            pvBuilder.withValue((Double)objectArray[3]);
            pvBuilder.withTimeStamp(dateTimeFormatter.parseDateTime((String) objectArray[4]).toDate());
            
            pointValueQualityHolderData.add(pvBuilder.build()); 
        }
        
        // Building up the helper maps that are used for the mock dao method calls.
        pointValueQualityHolderDataByRPHId = new HashMap<>();
        pointValueQualityHolderDataByDate = ArrayListMultimap.create();
        for (int rphId = 0;  rphId < pointValueQualityHolderData.size(); rphId++) {
            PointValueQualityHolder pvqHolder = pointValueQualityHolderData.get(rphId);
            pointValueQualityHolderDataByRPHId.put((long)rphId, pvqHolder);
            pointValueQualityHolderDataByDate.put(new Instant(pvqHolder.getPointDataTimeStamp()), pvqHolder);
        }
    }
    
    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getLimitedAttributeData(Iterable<? extends YukonPao> yukonPaos, Attribute attribute, Range<Instant> dateRange,
                                                                                        Range<Long> changeIdRange, int maxRows, boolean excludeDisabledPaos, Order order, OrderBy orderBy) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> results = ArrayListMultimap.create();

        for (YukonPao yukonPao : yukonPaos) {
            
            List<PointValueQualityHolder> rphPointValueQualityHolders = intersectingPointValueQualityHoldersByRawPointHistoryIds(changeIdRange);
            List<PointValueQualityHolder> dateRangePointValueQualityHolders = intersectingPointValueQualityHoldersByDateRange(dateRange);
            @SuppressWarnings("unchecked")
            List<PointValueQualityHolder> pointValueQualityHolders = ListUtils.intersection(rphPointValueQualityHolders, dateRangePointValueQualityHolders);
            
            List<PointValueQualityHolder> pvqHolders = getPointValueQualityHoldersForAttribute(attribute, pointValueQualityHolders);
            List<PointValueQualityHolder> orderedPVQHolders = orderPointValueData(pvqHolders, order, orderBy);
            results.putAll(yukonPao.getPaoIdentifier(), orderedPVQHolders.subList(0, maxRows));
        } 
        
        return results;
    }
    
    @Override
    public ListMultimap<PaoIdentifier, PointValueQualityHolder> getAttributeData(
            Iterable<? extends YukonPao> yukonPaos, Attribute attribute, final Range<Instant> dateRange,
            final Range<Long> changeIdRange, final boolean excludeDisabledPaos, final Order order) {

        ListMultimap<PaoIdentifier, PointValueQualityHolder> paoIdToPointValueQualityHolder = ArrayListMultimap.create();

        for (YukonPao yukonPao : yukonPaos) {
            
            List<PointValueQualityHolder> rphPointValueQualityHolders = intersectingPointValueQualityHoldersByRawPointHistoryIds(changeIdRange);
            List<PointValueQualityHolder> dateRangePointValueQualityHolders = intersectingPointValueQualityHoldersByDateRange(dateRange);
            @SuppressWarnings("unchecked")
            List<PointValueQualityHolder> pointValueQualityHolders = ListUtils.intersection(rphPointValueQualityHolders, dateRangePointValueQualityHolders);
            
            List<PointValueQualityHolder> pvqHolders = getPointValueQualityHoldersForAttribute(attribute, pointValueQualityHolders);
            List<PointValueQualityHolder> orderedPVQHolders = orderPointValueData(pvqHolders, order);
            paoIdToPointValueQualityHolder.putAll(yukonPao.getPaoIdentifier(), orderedPVQHolders);
        } 
        
        return paoIdToPointValueQualityHolder;
    }

    /**
     * Orders the point data supplied in the point value quality holders by timestamp. 
     */
    private List<PointValueQualityHolder> orderPointValueData(List<PointValueQualityHolder> pvqHolders, Order order) {
        return orderPointValueData(pvqHolders, order, OrderBy.TIMESTAMP);
    }

    /**
     * Orders the point data supplied in the point value quality holders by the supplied order by type.. 
     */
    private List<PointValueQualityHolder> orderPointValueData(List<PointValueQualityHolder> pvqHolders, Order order, OrderBy orderBy) {
        Set<PointValueQualityHolder> orderedPointValueQualityHolders = null;
        if (orderBy == OrderBy.TIMESTAMP) {
            orderedPointValueQualityHolders = new TreeSet<>(ORDERING_BY_TIMESTAMP);
            if (order == Order.REVERSE) {
                orderedPointValueQualityHolders = new TreeSet<>(ORDERING_BY_TIMESTAMP.reverse());
            }
        }
        
        if (orderBy == OrderBy.VALUE) {
            orderedPointValueQualityHolders = new TreeSet<>(ORDERING_BY_VALUE);
            if (order == Order.REVERSE) {
                orderedPointValueQualityHolders = new TreeSet<>(ORDERING_BY_VALUE.reverse());
            }
        }
        
        orderedPointValueQualityHolders.addAll(pvqHolders);
        return Lists.newArrayList(orderedPointValueQualityHolders);
    }

    /**
     * This method takes the attributes supplied and uses the pointType to make a rough idea of which point data should be returned.
     */
    private List<PointValueQualityHolder> getPointValueQualityHoldersForAttribute(Attribute attribute, List<PointValueQualityHolder> pointValueQualityHolders) {
        List<PointValueQualityHolder> results = new ArrayList<>();

        for (PointValueQualityHolder pvqHolder : pointValueQualityHolders) {
            if ((attribute == BuiltInAttribute.USAGE) && pvqHolder.getPointType() == PulseAccumulator) {
                results.add(pvqHolder);
            } else if ((attribute == BuiltInAttribute.DEMAND || attribute == BuiltInAttribute.PEAK_DEMAND) && pvqHolder.getPointType() == DemandAccumulator ) {
                results.add(pvqHolder);
            }
        }

        return results;
    }

    
    /**
     * Returns a list of pointValueQualityHolders that intersect with the changeIdRange supplied.  This method will return
     * all of the pointValueQualityHolders if they are supplied.
     */
    private List<PointValueQualityHolder> intersectingPointValueQualityHoldersByRawPointHistoryIds(Range<Long> changeIdRange) {
        if (changeIdRange == null) {
            return pointValueQualityHolderData;
        }
        
        List<PointValueQualityHolder> results = new ArrayList<>();
        for(Entry<Long, PointValueQualityHolder> entry : pointValueQualityHolderDataByRPHId.entrySet()) {
            if (changeIdRange.intersects(entry.getKey())) {
                results.add(entry.getValue());
            }
        }
        return results;
    }
    
    /**
     * Returns a list of pointValueQualityHolders that intersect with the dateRange supplied.  This method will return
     * all of the pointValueQualityHolders if they are supplied.
     */
    private List<PointValueQualityHolder> intersectingPointValueQualityHoldersByDateRange(Range<Instant> dateRange) {
        if (dateRange == null) {
            return pointValueQualityHolderData;
        }
        
        List<PointValueQualityHolder> results = new ArrayList<>();
        if (dateRange != null) {
            for(Entry<Instant, Collection<PointValueQualityHolder>> entry : pointValueQualityHolderDataByDate.asMap().entrySet()) {
                if (dateRange.intersects(entry.getKey())) {
                    results.addAll(entry.getValue());
                }
            }
        }
        return results;
    }
}