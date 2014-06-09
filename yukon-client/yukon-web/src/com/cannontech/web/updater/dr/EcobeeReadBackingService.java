package com.cannontech.web.updater.dr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EcobeeReadBackingService extends RecentResultUpdateBackingService {
    
    @Autowired @Qualifier("ecobeeReads") private RecentResultsCache<EcobeeReadResult> resultsCache;
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        
        if (resultTypeStr.equalsIgnoreCase("STATUS")) {
            
            EcobeeReadResult result = resultsCache.getResult(resultId);
            Map<String, Object> status = new HashMap<>();
            status.put("key", result.getKey());
            status.put("complete", result.isComplete());
            status.put("successful", result.isSuccessful());
            status.put("percentDone", result.getPercentDone());
            
            try {
                return JsonUtils.toJson(status);
            } catch (JsonProcessingException e) {
                return "";
            }
        } else {
            throw new IllegalArgumentException("Unsupported Updater Type: " + resultTypeStr);
        }
        
    }
    
}
