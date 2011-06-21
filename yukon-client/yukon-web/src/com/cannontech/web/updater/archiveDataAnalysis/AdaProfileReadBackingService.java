package com.cannontech.web.updater.archiveDataAnalysis;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class AdaProfileReadBackingService extends RecentResultUpdateBackingService {
    RecentResultsCache<ArchiveAnalysisProfileReadResult> resultsCache;    
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        ArchiveAnalysisProfileReadResult result = resultsCache.getResult(resultId);
        if(result==null) {
            return "";
        }
        
        AdaProfileReadTypeEnum adaProfileReadTypeEnum = AdaProfileReadTypeEnum.valueOf(resultTypeStr);
        return adaProfileReadTypeEnum.getValue(result);
    }
    
    @Resource(name="adaProfileReadRecentResultsCache")
    public void setResultsCache(RecentResultsCache<ArchiveAnalysisProfileReadResult> resultsCache) {
        this.resultsCache = resultsCache;
    }
}