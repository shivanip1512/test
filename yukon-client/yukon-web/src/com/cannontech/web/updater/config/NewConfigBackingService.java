package com.cannontech.web.updater.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.NewLmConfigHelper.NewLmConfigTask;
import com.cannontech.web.updater.UpdateBackingService;

public class NewConfigBackingService implements UpdateBackingService {
    
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private enum DataType {
        ITEMS_PROCESSED,
        SUCCESS_COUNT,
        NEW_OPERATION_FOR_SUCCESS,
        UNSUPPORTED_COUNT,
        NEW_OPERATION_FOR_UNSUPPORTED,
        FAILED_COUNT,
        NEW_OPERATION_FOR_FAILED
    }
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        
        String[] idParts = StringUtils.split(identifier, "/");
        String taskId = idParts[0];
        DataType type = DataType.valueOf(idParts[1]);
        
        return handle(taskId, type, userContext);
    }
    
    private String handle(String taskId, DataType type, YukonUserContext userContext) {
        
        NewLmConfigTask task = (NewLmConfigTask) resultsCache.getResult(taskId);
        String result = "";
        
        switch (type) {
        
        case ITEMS_PROCESSED:
            result = Integer.toString(task.getCompletedItems());
            break;
            
        case SUCCESS_COUNT:
            result = Integer.toString(task.getSuccessCount());
            break;
            
        case FAILED_COUNT:
            result = Integer.toString(task.getFailedCount());
            break;
            
        case UNSUPPORTED_COUNT:
            result = Integer.toString(task.getUnsupportedCount());
            break;
            
        case NEW_OPERATION_FOR_SUCCESS:
            result = task.isComplete() && task.getSuccessCount() > 0 ? "di" : "dn";
            break;
            
        case NEW_OPERATION_FOR_FAILED:
            result = task.isComplete() && task.getFailedCount() > 0 ? "di" : "dn";
            break;
            
        case NEW_OPERATION_FOR_UNSUPPORTED:
            result = task.isComplete() && task.getUnsupportedCount() > 0 ? "di" : "dn";
            break;
        }
        
        return result;
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @Required
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}