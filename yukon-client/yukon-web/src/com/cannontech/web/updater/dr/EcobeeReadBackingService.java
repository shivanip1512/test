package com.cannontech.web.updater.dr;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EcobeeReadBackingService extends RecentResultUpdateBackingService {
    private static final Logger log = YukonLogManager.getLogger(RecentResultUpdateBackingService.class);
    private static final int recentDownloadsToReturn = 5;
    @Autowired @Qualifier("ecobeeReads") private RecentResultsCache<EcobeeReadResult> resultsCache;
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        if (resultTypeStr == "RECENT_DOWNLOADS") {
            return getRecentDownloads();
        }
        
        EcobeeReadResult result = resultsCache.getResult(resultId);
        if (result == null) {
            return "";
        }
        
        EcobeeReadUpdateType updateType = EcobeeReadUpdateType.valueOf(resultTypeStr);
        return updateType.getValue(result);
    }
    
    private String getRecentDownloads() {
        List<String> recentDownloads = new ArrayList<>();
        
        List<String> pendingKeys = resultsCache.getPendingKeys();
        //TODO sort keys
        if (pendingKeys.size() > recentDownloadsToReturn) {
            recentDownloads = pendingKeys.subList(0, recentDownloadsToReturn);
        } else if (pendingKeys.size() == recentDownloadsToReturn) {
            recentDownloads = pendingKeys;
        } else {
            recentDownloads.addAll(pendingKeys);
            int remainingSlots = recentDownloadsToReturn - pendingKeys.size();
            
            List<String> completedKeys = resultsCache.getCompletedKeys();
            //TODO sort keys
            if (completedKeys.size() > remainingSlots) {
                List<String> subList = completedKeys.subList(0, remainingSlots);
                recentDownloads.addAll(subList);
            } else {
                recentDownloads.addAll(completedKeys);
            }
        }
        
        try {
            return JsonUtils.toJson(recentDownloads);
        } catch (JsonProcessingException e) {
            log.warn("Error converting recent download ids to json.", e);
            return "";
        }
    }
}
