package com.cannontech.web.updater.point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.core.dynamic.AllPointDataListener;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.BulkUpdateBackingService;
import com.cannontech.web.updater.UpdateIdentifier;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class PointUpdateBackingService implements BulkUpdateBackingService, AllPointDataListener {
    private final static Logger log = YukonLogManager.getLogger(PointUpdateBackingService.class);

    private final static int maxCacheSize = 5000;

    private final static Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");

    @Autowired private AsyncDynamicDataSource asyncDataSource;
    @Autowired private PointFormattingService pointFormattingService;

    private Map<Integer, DatedPointValue> cache = new LeastRecentlyUsedCacheMap<>(maxCacheSize);

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
        log.debug("getLatestValues - done");
        return formattedValues;
    }
        
    private List<DatedPointValue> getLatestValues(Set<Integer> pointIds, long afterDate, boolean canWait) {
        Set<Integer> pointsToRegister = new HashSet<>();
        List<DatedPointValue> values = new ArrayList<>();
        for (Integer pointId : pointIds) {
            DatedPointValue value = cache.get(pointId);
            if (value == null) {
                if (canWait) {
                    pointsToRegister.add(pointId);
                }
            } else if (value.receivedTime >= afterDate) {
                values.add(value);
            }
        }
        if (!pointsToRegister.isEmpty()) {
            log.debug("Registering points");
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
            String idStr = m.group(1);
            String format = m.group(2);
            pointId = Integer.parseInt(idStr);
            this.format = format;
            updateIdentifier = identifier;
        }
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        DatedPointValue old = cache.get(pointData.getId());
        if (old == null) {
            usePointData(pointData);
        } else {
            checkBeforeUsing(old, pointData);
        }
    }
    
    /**
     * The point data can actually be older than the most current value but we want to use it instead.
     * This will happen if the point data was recorded before the point was created in Yukon e.g.: peak demand.
     * We want to replace our 'newer' uninitialized value with the a real value recorded before our point was created.
     */
    private void checkBeforeUsing(DatedPointValue old, PointValueQualityHolder pointData) {
        boolean isNewer = pointData.getPointDataTimeStamp().after(old.value.getPointDataTimeStamp());
        boolean previouslyUninitialized = old.value.getPointQuality() == PointQuality.Uninitialized;
        if (isNewer || previouslyUninitialized) {
            usePointData(pointData);
        }
    }
    
    private void usePointData(PointValueQualityHolder pointData) {
        DatedPointValue value = new DatedPointValue(pointData);
        cache.put(pointData.getId(), value);
    }
}
