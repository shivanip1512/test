package com.cannontech.web.updater.bulk;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class BulkResultBackingService extends RecentResultUpdateBackingService {

    private RecentResultsCache<BulkOperationCallbackResults<?>> resultsCache = null;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

        BulkOperationCallbackResults<?> bulkOperationCallbackResults = resultsCache.getResult(resultId);
       
       if (bulkOperationCallbackResults == null) {
           return "";
       }
       BulkResultTypeEnum bulkResultTypeEnum = BulkResultTypeEnum.valueOf(resultTypeStr);
       return bulkResultTypeEnum.getValue(bulkOperationCallbackResults);
    }
    

    @Required
    public void setResultsCache(RecentResultsCache<BulkOperationCallbackResults<?>> resultsCache) {
        this.resultsCache = resultsCache;
    }
}
