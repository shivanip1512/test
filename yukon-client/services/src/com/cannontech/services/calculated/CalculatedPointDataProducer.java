package com.cannontech.services.calculated;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * This producer uses existing point values to produce new values for different points
 * that are calculated from the existing point values.  Calculation is only done if a
 * calculator is found that supports calculations based on the existing pao and point type.
 * 
 * This producer uses a concurrent and expiring cache to avoid hitting the database as
 * much as possible since this work is very performance intensive.  The calculator 
 * implementations have the responsibility of cleaning out cache values when they detect
 * that they are no longer needed.
 */
@ManagedResource
public class CalculatedPointDataProducer {

    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AttributeService attributeService;
    @Autowired private ConfigurationSource config;
    @Autowired private List<PointCalculator> calculators;
    
    private static Cache<CacheKey, CacheValue> recentReadings;
    private ImmutableMap<PaoTypePointIdentifier, PointCalculator> calculatorMappings;
    
    private static final Logger log = YukonLogManager.getLogger(CalculatedPointDataProducer.class);
    
    /**
     * The primary method used to pass each {@link CalculationData} on to it's corresponding
     * {@link PointCalculator}.  Any resulting {@link PointData} messages are to be added to {@code toArchive}
     */
    public void calculate(Collection<? extends CalculationData> calculatorFrom, List<PointData> toArchive) {
        for (CalculationData data : calculatorFrom) {
            PaoTypePointIdentifier ptpi = data.getPaoPointValue().getPaoPointIdentifier().getPaoTypePointIdentifier();
            PointCalculator calculator = calculatorMappings.get(ptpi);
            if (calculator != null) {
                calculator.calculate(recentReadings, data, toArchive);
            } else {
                log.debug("No point calculator found for " + ptpi);
            }
        }
    }
    
    /**
     * Values are dumped into the cache as soon as they are received and just prior to queuing up the
     * appropriate calculator thread.
     */
    public void updateCache(List<CalculationData> data) {
        for (CalculationData newValue : data) {
            PointValueQualityHolder pvqh = newValue.getPaoPointValue().getPointValueQualityHolder();
            CacheKey currentKey = CacheKey.of(pvqh.getId(), pvqh.getPointDataTimeStamp().getTime());
            CacheValue value = CacheValue.of(pvqh.getValue(), newValue.getInterval(), false, false);
            recentReadings.put(currentKey, value);
        }
    }
    
    @PostConstruct
    public void init() {
        int expireAfter = config.getInteger("RFN_CALC_CACHE_EXPIRATION", 72);
        recentReadings = CacheBuilder.newBuilder().expireAfterWrite(expireAfter, TimeUnit.HOURS).build();
        
        Set<PaoType> types = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.RFN_POINT_CALCULATION);
        ImmutableSet<BuiltInAttribute> attributes = ImmutableSet.of(BuiltInAttribute.SUM_KWH, 
                                                                    BuiltInAttribute.DELIVERED_KWH, 
                                                                    BuiltInAttribute.RECEIVED_KWH,
                                                                    BuiltInAttribute.NET_KWH, 
                                                                    BuiltInAttribute.SUM_KVARH, 
                                                                    BuiltInAttribute.SUM_KVAH,
                                                                    BuiltInAttribute.KVARH,
                                                                    BuiltInAttribute.USAGE_WATER,
                                                                    BuiltInAttribute.USAGE_GAS,
                                                                    BuiltInAttribute.KVAH);
        
        ImmutableMap.Builder<PaoTypePointIdentifier, PointCalculator> b = ImmutableMap.builder();
        for (PaoType type : types) {
            for (BuiltInAttribute attribute : attributes) {
                try {
                    PaoTypePointIdentifier ptpi = attributeService.getPaoTypePointIdentifierForAttribute(type, attribute);
                    for (PointCalculator calc : calculators) {
                        if (calc.supports(ptpi)) {
                            b.put(ptpi, calc);
                            break;
                        }
                    }
                } catch (IllegalUseOfAttribute e) {/* Ignore */}
            }
        }
        calculatorMappings = b.build();
    }
    
    @ManagedAttribute
    public long getCacheSize() {
        return recentReadings.size();
    }
    
}