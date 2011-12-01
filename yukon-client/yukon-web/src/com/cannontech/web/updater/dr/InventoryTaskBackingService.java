package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class InventoryTaskBackingService extends RecentResultUpdateBackingService {
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    private enum DataType {
        ITEMS_PROCESSED,
        IS_COMPLETE
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {
        AbstractInventoryTask result = resultsCache.getResult(resultId);
        if (result == null) {
            return "";
        }
        
        DataType type = DataType.valueOf(resultTypeStr);
        if (type == DataType.ITEMS_PROCESSED) {
            return result.getCompletedItems();
        } else {
            return result.isComplete();
        }
    }
    
    @Required
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}