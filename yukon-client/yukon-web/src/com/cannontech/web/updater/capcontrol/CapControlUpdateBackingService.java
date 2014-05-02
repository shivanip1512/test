package com.cannontech.web.updater.capcontrol;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.WebUpdatedDAO;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.capcontrol.CapControlFormattingService.Format;
import com.cannontech.web.updater.capcontrol.exception.CacheManagementException;

public class CapControlUpdateBackingService implements UpdateBackingService {
    
    private static final Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");
    private CapControlCache capControlCache;
    private CapControlFormattingService<StreamableCapObject> formattingService;
    
    @Override
    public synchronized String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        
        Matcher m = idSplitter.matcher(identifier);
        if (m.matches() && m.groupCount() != 2) {
            throw new RuntimeException("identifier string isn't well formed: " + identifier);
        }
        
        String idStr = m.group(1);
        String format = m.group(2);
        
        int paoId = Integer.parseInt(idStr);
        StreamableCapObject latestValue = doGetLatestValue(paoId, afterDate);
        
        if (latestValue == null) return null;
        
        Format formatEnum = Format.valueOf(format);
        String valueString = formattingService.getValueString(latestValue, formatEnum, userContext);
        
        return valueString;
    }
    
    private StreamableCapObject doGetLatestValue(int paoId, long afterDate) {
        
        boolean updated = updatedInCache(paoId, afterDate);
        if (updated || afterDate == 0) {
            try {
                StreamableCapObject obj = capControlCache.getObject(paoId);
                return obj;
            } catch (NotFoundException nfe) {
                // This can happen if we delete an object and return
                // to a page that that used to have that object before
                // the server was able to update the cache.
                // We are throwing an exception alerting the updater that the page is invalid
                throw new CacheManagementException();
            }
        }
        return null;
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
            long afterDate, 
            YukonUserContext userContext) {
        return true;
    }
    
    private boolean updatedInCache(int areaId, long afterDate) {
        
        WebUpdatedDAO<Integer> updatedObjMap = capControlCache.getUpdatedObjects();
        List<Integer> updatedIds = updatedObjMap.getUpdatedIdsSince(new Date(afterDate), areaId);
        
        boolean result = updatedIds.contains(areaId);
        return result;
    }
    
    public void setFormattingService(CapControlFormattingService<StreamableCapObject> formattingService) {
        this.formattingService = formattingService;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }

}
