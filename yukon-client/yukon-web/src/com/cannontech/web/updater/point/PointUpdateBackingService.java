package com.cannontech.web.updater.point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.BulkUpdateBackingService;
import com.cannontech.web.updater.UpdateIdentifier;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class PointUpdateBackingService implements BulkUpdateBackingService, PointDataListener {
    private final static Logger log = YukonLogManager.getLogger(PointUpdateBackingService.class);

    @Autowired private AsyncDynamicDataSource asyncDataSource;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ConfigurationSource configSource;

    private boolean alwaysRegisterForPoints;
    private final static int maxCacheSize = 5000;
    private final static Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");
    private Cache<Integer, DatedPointValue> cache =
        CacheBuilder.newBuilder().maximumSize(maxCacheSize).removalListener(
            new RemovalListener<Integer, DatedPointValue>() {
                @Override
                public void onRemoval(RemovalNotification<Integer, DatedPointValue> notification) {
                    if (notification.getCause() != RemovalCause.REPLACED) {
                        log.debug("[Unregistering Listener]:PointUpdateBackingService for point id="+notification.getKey());
                        DatedPointValue value = cache.getIfPresent(notification.getKey());
                        if(value != null){
                            log.debug("Point in cache DatedPointValue="+notification.getKey());
                        }else{
                            log.debug("Removed point data from cache point id=" + notification.getKey());
                        }
                        log.debug("Cache size="+cache.size());
                        asyncDataSource.unRegisterForPointData(PointUpdateBackingService.this,
                            Collections.singleton(notification.getKey()));
                    }
                }
        }).build();

    @Override
    public Map<UpdateIdentifier, String> getLatestValues(List<UpdateIdentifier> updateIdentifiers, long afterDate,
                                                         YukonUserContext userContext, boolean canWait) {
        if (log.isDebugEnabled()) {
           log.debug("getLatestValues - handling " + Joiner.on("; ").join(updateIdentifiers));
        }
        ListMultimap<Integer, PointIdentifier> identifiers = ArrayListMultimap.create();
        for (UpdateIdentifier updateIdentifier : updateIdentifiers) {
            PointIdentifier pointIdentifier = new PointIdentifier(updateIdentifier);
            identifiers.put(pointIdentifier.pointId, pointIdentifier);
        }
        List<DatedPointValue> latestValues = getLatestValues(identifiers.keySet(), afterDate, canWait);
        Map<UpdateIdentifier, String> formattedValues = formatValues(identifiers, latestValues, userContext);
        log.debug("getLatestValues - done-"+ formattedValues);
        return formattedValues;
    }
        
    private List<DatedPointValue> getLatestValues(Set<Integer> pointIds, long afterDate, boolean canWait) {
        //YUK-14972
        if(alwaysRegisterForPoints){
            return getLatestValuesAndAlwaysRegisterForPoints(pointIds, afterDate, canWait); 
        }else{
            return getLatestValuesAndRegisterForPointsOnlyOnce(pointIds, afterDate, canWait);
        }
    }
    
    /**
     * Registers for points every time the methods is called.
     */
    private List<DatedPointValue> getLatestValuesAndAlwaysRegisterForPoints(Set<Integer> pointIds, long afterDate,
            boolean canWait) {
        asyncDataSource.registerForPointData(this, pointIds);
        ImmutableMap<Integer, DatedPointValue> cachedValues = cache.getAllPresent(pointIds);
        Map<Integer, DatedPointValue> dispatchValues = Collections.emptyMap();
        if (cachedValues.size() < pointIds.size() && canWait) {
            Set<Integer> pointsNotInCache = Sets.difference(pointIds, cachedValues.keySet());
            dispatchValues = 
                    asyncDataSource.getPointValues(pointsNotInCache).stream()
                            .collect(Collectors.toMap(PointValueQualityHolder::getId, DatedPointValue::new));
            cache.putAll(dispatchValues);
        }
        return Stream.concat(cachedValues.values().stream(), dispatchValues.values().stream())
            .filter(dpv -> dpv.receivedTime >= afterDate)
            .collect(Collectors.toList());
    }
    
    /**
     * Registers for points only when we are trying to get values for the first time.
     */
    private List<DatedPointValue> getLatestValuesAndRegisterForPointsOnlyOnce(Set<Integer> pointIds, long afterDate,
            boolean canWait) {
        Set<Integer> pointsToRegister = new HashSet<>();
        List<DatedPointValue> values = new ArrayList<>();
        for (Integer pointId : pointIds) {
            DatedPointValue value = cache.getIfPresent(pointId);
            if (value == null) {
                if (canWait) {
                    pointsToRegister.add(pointId);
                }
            } else if (value.receivedTime >= afterDate) {
                values.add(value);
            }
        }
        if (!pointsToRegister.isEmpty()) {
            log.debug("Registering points" + pointsToRegister);
            Set<? extends PointValueQualityHolder> points =
                asyncDataSource.getAndRegisterForPointData(this, pointsToRegister);
            for (PointValueQualityHolder point : points) {
                DatedPointValue value = new DatedPointValue(point);
                cache.put(point.getId(), value);
                if (value.receivedTime >= afterDate) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    private Map<UpdateIdentifier, String> formatValues(ListMultimap<Integer, PointIdentifier> identifiers,
                                                       List<DatedPointValue> values, YukonUserContext userContext) {
        Map<UpdateIdentifier, String> formattedValues = new HashMap<>();
        for (DatedPointValue datedPointValue : values) {
            List<PointIdentifier> pointIdentifier = identifiers.get(datedPointValue.value.getId());
            for (PointIdentifier identifier : pointIdentifier) {
                String valueString;
                try {
                    Format formatEnum = Format.valueOf(identifier.format);
                    valueString =
                        pointFormattingService.getValueString(datedPointValue.value, formatEnum, userContext);
                } catch (IllegalArgumentException e) {
                    valueString = pointFormattingService.getValueString(datedPointValue.value,
                                                                        identifier.format,
                                                                        userContext);
                }
                formattedValues.put(identifier.updateIdentifier, valueString);
            }
        }
        return formattedValues;
    }

    private class DatedPointValue {
    	PointValueQualityHolder value;
    	long receivedTime;
        DatedPointValue(PointValueQualityHolder pointData) {
            receivedTime = System.currentTimeMillis();
            value = pointData;
        }
    }

    private static class PointIdentifier {
        int pointId;
        String format;
        UpdateIdentifier updateIdentifier;
        PointIdentifier(UpdateIdentifier identifier) {
            Matcher m = idSplitter.matcher(identifier.getRemainder());
            if (!m.matches() || m.groupCount() != 2) {
                throw new RuntimeException("identifier string isn't well formed: " + identifier);
            }
            pointId = Integer.parseInt(m.group(1));
            format = m.group(2);
            updateIdentifier = identifier;
        }
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        DatedPointValue value = new DatedPointValue(pointData);
        if(log.isDebugEnabled()){
            log.debug("Added Point data to cache "+pointData +" PointDataTimeStamp="+pointData.getPointDataTimeStamp());
        }
        cache.put(pointData.getId(), value);
    }
        
    @PostConstruct
    private void init(){
        alwaysRegisterForPoints = configSource.getBoolean(MasterConfigBoolean.POINT_UPDATE_REGISTRATION, true);
    }
    
    @Override
    public PointValueQualityHolder getCachedValue(int pointId) {
        log.info("PointUpdateBackingService alwaysRegisterForPoints=" + alwaysRegisterForPoints);
        log.info("PointUpdateBackingService Internal cache size=" + cache.size());
        DatedPointValue value = getLatestValues(Collections.singleton(pointId), 0, true).get(0);
        log.info("PointUpdateBackingService received point data for id=" + pointId + " on "
            + new DateTime(value.receivedTime).toString("MM-dd-yyyy HH:mm:ss"));
        return value.value;
    }
}
