package com.cannontech.web.updater.pointImport;

import javax.annotation.Resource;

import com.cannontech.common.bulk.callbackResult.PointImportCallbackResult;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class PointImportBackingService extends RecentResultUpdateBackingService {
    @Resource(name="recentResultsCache")
    RecentResultsCache<PointImportCallbackResult> resultsCache;
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate,
                                               YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        PointImportCallbackResult result = resultsCache.getResult(resultId);
        if(result == null) {
            return "";
        }
        PointImportDataUpdaterType updaterType = PointImportDataUpdaterType.valueOf(resultTypeStr);
        return updaterType.getValue(result);
    }
}
