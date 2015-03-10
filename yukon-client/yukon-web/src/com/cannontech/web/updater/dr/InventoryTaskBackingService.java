package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class InventoryTaskBackingService extends RecentResultUpdateBackingService {
    
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    private enum DataType {
        NEW_OPERATION_FOR_SUCCESS,
        NEW_OPERATION_FOR_FAILED,
        NEW_OPERATION_FOR_UNSUPPORTED,
        SUCCESS_COUNT,
        FAILED_COUNT,
        UNSUPPORTED_COUNT,
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
        } else if (type == DataType.SUCCESS_COUNT) {
            return result.getSuccessCount();
        } else if (type == DataType.FAILED_COUNT) {
            return result.getFailedCount();
        } else if (type == DataType.UNSUPPORTED_COUNT) {
            return result.getUnsupportedCount();
        } else if (type == DataType.NEW_OPERATION_FOR_SUCCESS) {
            return result.isComplete() && result.getSuccessCount() > 0 ? "" : "dn";
        } else if (type == DataType.NEW_OPERATION_FOR_FAILED) {
            return result.isComplete() && result.getFailedCount() > 0 ? "" : "dn";
        } else if (type == DataType.NEW_OPERATION_FOR_UNSUPPORTED) {
            return result.isComplete() && result.getUnsupportedCount() > 0 ? "" : "dn";
        } else {
            return result.isComplete();
        }
    }
    
    @Required
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}