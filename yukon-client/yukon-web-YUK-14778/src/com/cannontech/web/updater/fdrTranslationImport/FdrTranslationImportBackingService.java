package com.cannontech.web.updater.fdrTranslationImport;

import javax.annotation.Resource;

import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class FdrTranslationImportBackingService extends RecentResultUpdateBackingService {
    RecentResultsCache<TranslationImportCallbackResult> resultsCache;    
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate,
                                               YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        TranslationImportCallbackResult result = resultsCache.getResult(resultId);
        if(result == null) {
            return "";
        }
        FdrTranslationImportTypeEnum importTypeEnum = FdrTranslationImportTypeEnum.valueOf(resultTypeStr);
        return importTypeEnum.getValue(result);
    }
    
    @Resource(name="recentResultsCache")
    public void setResultsCache(RecentResultsCache<TranslationImportCallbackResult> resultsCache) {
        this.resultsCache = resultsCache;
    }
}
