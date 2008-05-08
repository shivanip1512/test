package com.cannontech.web.updater.point;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.TimeSource;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class PointUpdateBackingService implements UpdateBackingService, PointDataListener {
    private TimeSource timeSource;
    private AsyncDynamicDataSource asyncDataSource;
    private PointFormattingService pointFormattingService;
    private int maxCacheSize = 1000;
    private int maxOverSize = maxCacheSize / 20;
    private Map<Integer, DatedPointValue> cache = 
        Collections.synchronizedMap(new LinkedHashMap<Integer, DatedPointValue>(maxCacheSize, .75f, true));
    private Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");

    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        Matcher m = idSplitter.matcher(identifier);
        if (m.matches() && m.groupCount() != 2) {
            throw new RuntimeException("identifier string isn't well formed: " + identifier);
        }
        String idStr = m.group(1);
        String format = m.group(2);

        int pointId = Integer.parseInt(idStr);
        PointValueHolder latestValue = doGetLatestValue(pointId, afterDate);
        if (latestValue == null) return null;
        String valueString;
        try {
            Format formatEnum = Format.valueOf(format);
            valueString = pointFormattingService.getValueString(latestValue, formatEnum, userContext);
        } catch (IllegalArgumentException e) {
            valueString = pointFormattingService.getValueString(latestValue, format, userContext);
        }

        return valueString;
    }

    private PointValueHolder doGetLatestValue(int pointId, long afterDate) {
        DatedPointValue value = cache.get(pointId);
        if (value == null) {
            PointValueHolder pointData = asyncDataSource.getAndRegisterForPointData(this, pointId);
            value = createWrapper(pointData);
            cache.put(pointData.getId(), value);
        }
        
        if (value.receivedTime < afterDate) {
            return null;
        }
        
        return value.value;
    }
    
    private DatedPointValue createWrapper(PointValueHolder pointData) {
        DatedPointValue result = new DatedPointValue();
        result.receivedTime = timeSource.getCurrentMillis();
        result.value = pointData;
        return result;
    }

    private void trimCache() {
        int toTrim = cache.size() - maxCacheSize;
        if (toTrim > maxOverSize) {
            Set<Integer> removedPointIds = new HashSet<Integer>(toTrim);
            Iterator<DatedPointValue> iter = cache.values().iterator();
            while (toTrim-- > 0) {
                DatedPointValue value = iter.next();
                removedPointIds.add(value.value.getId());
                iter.remove();
            }
            asyncDataSource.unRegisterForPointData(this, removedPointIds);
        }
    }
    
    private class DatedPointValue {
        PointValueHolder value;
        long receivedTime;
    }

    public void pointDataReceived(PointValueQualityHolder pointData) {
        trimCache();
        DatedPointValue value = createWrapper(pointData);
        cache.put(pointData.getId(), value);
    }
    
    @Required
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }
    
    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    public void setMaxOverSize(int maxOverSize) {
        this.maxOverSize = maxOverSize;
    }
    
    @Required
    public void setAsyncDataSource(AsyncDynamicDataSource asyncDataSource) {
        this.asyncDataSource = asyncDataSource;
    }
    
    @Required
    public void setPointFormattingService(
            PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }
    
}
