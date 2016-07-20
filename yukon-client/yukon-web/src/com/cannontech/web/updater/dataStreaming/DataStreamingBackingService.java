package com.cannontech.web.updater.dataStreaming;

import javax.annotation.Resource;

import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallbackResult;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class DataStreamingBackingService extends RecentResultUpdateBackingService{
    RecentResultsCache<DataStreamingConfigCallbackResult> resultsCache;    
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate,
                                               YukonUserContext userContext) {
        return true;
    }
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        DataStreamingConfigCallbackResult result = resultsCache.getResult(resultId);
        if(result == null) {
            return "";
        }
        DataStreamingTypeEnum importTypeEnum = DataStreamingTypeEnum.valueOf(resultTypeStr);
        return importTypeEnum.getValue(result);
    }
    
    @Resource(name="recentResultsCache")
    public void setResultsCache(RecentResultsCache<DataStreamingConfigCallbackResult> resultsCache) {
        this.resultsCache = resultsCache;
    }
}
