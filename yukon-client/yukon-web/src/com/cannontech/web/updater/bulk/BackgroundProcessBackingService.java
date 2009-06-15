package com.cannontech.web.updater.bulk;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class BackgroundProcessBackingService extends RecentResultUpdateBackingService {

    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache = null;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

        BackgroundProcessResultHolder callback = recentResultsCache.getResult(resultId);
       
       if (callback == null) {
           return "";
       }
       BackgroundProcessResultTypeEnum bulkResultTypeEnum = BackgroundProcessResultTypeEnum.valueOf(resultTypeStr);
       return bulkResultTypeEnum.getValue(callback);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
    

    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
}
