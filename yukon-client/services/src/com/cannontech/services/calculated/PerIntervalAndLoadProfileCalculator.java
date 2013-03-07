package com.cannontech.services.calculated;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Calculator class for calculators the produce the per interval value and
 * load profile value based on some {@link CalculationData} consisting of a current interval value.
 * 
 * Point calcuations can be done for attribute based data for now since
 * these attributes will only be mapping to one point definition, 
 * unlike {@link BuiltInAttribute#USAGE} which sometimes maps to a 'Sum kWh' points
 * and sometimes maps to a 'Delivered kWh' point.
 * 
 * If this changes, these calculators must referece the point definitions they 
 * calculate for, not the attributes.
 */
public class PerIntervalAndLoadProfileCalculator implements PointCalculator {

    @Autowired private DynamicDataSource dds;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private final Logger log = YukonLogManager.getLogger(this.getClass());
    private Set<PaoTypePointIdentifier> supported;
    private BuiltInAttribute basedOn;
    private BuiltInAttribute perInterval;
    private BuiltInAttribute loadProfile;
    
    /**
     * The attribute representing the per interval data this calculator produces.
     * Will never be null.
     */
    public void setPerInterval(BuiltInAttribute perInterval) {
        this.perInterval = perInterval;
    }
    
    /**
     * The attribute representing the load profile data this calculator produces.
     * Will be null if load profile calcuation is not supported (like Water Usage).
     */
    public void setLoadProfile(BuiltInAttribute loadProfile) {
        this.loadProfile = loadProfile;
    }
    
    /**
     * The attribute representing the point value used to calculate per interval and load profile values.
     * This may need to be a set of attributes and operations if calculations become more complicated.
     */
    public void setBasedOn(BuiltInAttribute basedOn) {
        this.basedOn = basedOn;
    }
    
    /**
     * Attempts to calculate values for the current interval and the next interval for both 'per interval'
     * and 'load profile' values. If successful, adds a new {@link PointData} to {@code pointData} list.
     * 
     * This method utilizes a cache to avoid querying the database, therefore it is this methods job
     * to clean out the cache when cached values are no longer needed.
     * 
     * NOTE: The calculation is only attempted if the point to calculate for actually exists.
     * 
     * @param data the point data to base this calculation on.
     * @param pointData the list of point data update messages sent to dispatch.
     * @param recentReadings the cache representing recent interval values, used to avoid querying the database. 
     */
    @Override
    public void calculate(Cache<CacheKey, CacheValue> recentReadings, CalculationData data, List<? super PointData> pointData) {
        
        PaoIdentifier pao = data.getPaoPointValue().getPaoIdentifier();
        PointValueQualityHolder pvqh = data.getPaoPointValue().getPointValueQualityHolder();
        Date timestamp = pvqh.getPointDataTimeStamp();
        CacheKey currentKey = CacheKey.of(pvqh.getId(), timestamp.getTime());
        
        LitePoint perIntervalPoint = findPoint(perInterval, pao);
        LitePoint loadProfilePoint = findPoint(loadProfile, pao);
        if (perIntervalPoint == null && loadProfilePoint == null) {
            /* Clean cache and bail out */
            recentReadings.invalidate(currentKey);
            return;
        }
        
        double previous = 0;
        double previousPerIntervalValue = 0;
        double nextPerIntervalValue = 0;
        boolean foundPrevious = false;
        boolean foundNext = false;
        
        int interval = data.getInterval(); // in seconds
        Instant previousInterval = new Instant(timestamp).minus(Duration.standardSeconds(interval));
        Instant nextInterval = new Instant(timestamp).plus(Duration.standardSeconds(interval));
        
        CacheKey previousKey = CacheKey.of(pvqh.getId(), previousInterval.getMillis());
        CacheValue previousValue = recentReadings.getIfPresent(previousKey);
        if (previousValue != null && previousValue.getInterval() == interval) {
            previous = previousValue.getValue();
            foundPrevious = true;
            log.debug(String.format("Found previous interval from Cache: %s", previousValue));
        } else {
            // Didn't cache it, ask dispatch, this is really just a hope that it hasn't gotten blown away yet by the archiving thread
            PointValueQualityHolder previousDispatch = dds.getPointValue(pvqh.getId());
            if (previousDispatch.getPointDataTimeStamp().equals(previousInterval.toDate())) {
                previous = previousDispatch.getValue();
                foundPrevious = true;
                log.debug(String.format("Found previous interval from Dispatch: %s", previousDispatch));
            } else {
                // try getting it from rph?
                PointValueQualityHolder previousRph = rphDao.getSpecificValue(pvqh.getId(), previousInterval.getMillis());
                if (previousRph != null) {
                    previous = previousRph.getValue();
                    foundPrevious = true;
                    log.debug(String.format("Found previous interval from RPH: %s", previousRph));
                }
            }
        }
        
        /** Handle 'per interval' value */
        if (perIntervalPoint != null) {
            
            /* previous interval's per interval value */
            if (foundPrevious) {
                
                previousPerIntervalValue = pvqh.getValue() - previous;
                addPointData(perIntervalPoint, previousPerIntervalValue, timestamp, pointData);
                
                if (previousValue != null) {
                    if (previousValue.isPrevious()) {
                        // handled both intervals around the previous one, it's no longer needed.
                        recentReadings.invalidate(previousKey);
                    } else {
                        previousValue.setNext(true);
                    }
                }
            }
            
            /* next interval's per interval value */
            CacheKey nextKey = CacheKey.of(pvqh.getId(), nextInterval.getMillis());
            CacheValue nextValue = recentReadings.getIfPresent(nextKey);
            
            if (nextValue != null) {
                foundNext = true;
                nextPerIntervalValue = nextValue.getValue() - pvqh.getValue();
                addPointData(perIntervalPoint, nextPerIntervalValue, nextInterval.toDate(), pointData);
                
                // previous and next intervals of the current interval have been handled
                recentReadings.invalidate(currentKey);
                
                if (nextValue.isNext()) {
                    // previous and next intervals of the next interval have been handled
                    recentReadings.invalidate(nextKey);
                } else {
                    // set the previous interval of the next interval as handled
                    nextValue.setPrevious(true);
                }
                
            } else {
                // set the current intervals 'previous' as handled
                recentReadings.put(currentKey, CacheValue.of(pvqh.getValue(), interval, true, false));
            }
        }
        
        /** Handle load profile value */
        if (loadProfile != null && loadProfilePoint != null) {
            double intervalsPerHour = 3600.0d / interval;
            if (foundPrevious) {
                double previousLoadProfileValue = previousPerIntervalValue * intervalsPerHour;
                addPointData(loadProfilePoint, previousLoadProfileValue, timestamp, pointData);
            }
            if (foundNext) {
                double nextLoadProfileValue = nextPerIntervalValue * intervalsPerHour;
                addPointData(loadProfilePoint, nextLoadProfileValue, nextInterval.toDate(), pointData);
            }
        }
    }
    
    private void addPointData(LitePoint lp, double value, Date timestamp, List<? super PointData> pointData) {
        PointData pd = new PointData();
        pd.setId(lp.getPointID());
        pd.setPointQuality(PointQuality.Normal);
        pd.setValue(value);
        pd.setTime(timestamp);
        pd.setType(lp.getPointType());
        pd.setTagsPointMustArchive(true);
        pointData.add(pd);
        LogHelper.debug(log, "Calculated point value: %s", pd);
    }
    
    private LitePoint findPoint (BuiltInAttribute attribute, PaoIdentifier pao) {
        LitePoint point = null;
        if (attribute != null) {
            try {
                point = attributeService.getPointForAttribute(pao, attribute);
            } catch (IllegalUseOfAttribute e) {
                LogHelper.debug(log, "Point for attribute %s does not exist for device %s, skipping.", attribute, pao);
            }
        }
        return point;
    }
    
    @PostConstruct
    private void init() {
        Builder<PaoTypePointIdentifier> b = ImmutableSet.builder();
        Set<PaoType> types = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.RFN_POINT_CALCULATION);
        for (PaoType type : types) {
            try {
                PaoTypePointIdentifier ptpi = attributeService.getPaoTypePointIdentifierForAttribute(type, basedOn);
                b.add(ptpi);
            } catch (IllegalUseOfAttribute e) {/* Ignore */}
        }
        supported = b.build();
    }
    
    @Override
    public boolean supports(PaoTypePointIdentifier ptpi) {
        return supported.contains(ptpi);
    }

}