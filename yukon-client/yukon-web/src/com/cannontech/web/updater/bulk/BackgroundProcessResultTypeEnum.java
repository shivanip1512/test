package com.cannontech.web.updater.bulk;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;


public enum BackgroundProcessResultTypeEnum {

    SUCCESS_COUNT(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.getSuccessCount();
        }
    }),
    
    PROCESSING_EXCEPTION_COUNT(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.getProcessingExceptionCount();
        }
    }),
    
    COMPLETED_LINES(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            
            return (bulkOperationCallbackResults.getSuccessCount() + bulkOperationCallbackResults.getProcessingExceptionCount());
        }
    }),
    
    STOP_TIME(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.STOP_TIME");
            resolvableTemplate.addData("stopTime", bulkOperationCallbackResults.getStopTime());
            resolvableTemplate.addData("finished", bulkOperationCallbackResults.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.isComplete();
        }
    }),
    
    IS_COMPLETE_WITH_SUCCESSES(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.isComplete() && bulkOperationCallbackResults.getSuccessCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_FAILURES(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.isComplete() && bulkOperationCallbackResults.getProcessingExceptionCount() > 0;
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<BackgroundProcessResultHolder>() {
        public Object getValue(BackgroundProcessResultHolder bulkOperationCallbackResults) {
            
        	ResolvableTemplate resolvableTemplate = null;
        	
        	if (bulkOperationCallbackResults.isComplete()) {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_COMPLETE_TEXT");
        	} else {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_IN_PROGRESS_TEXT");
        	}
        	
            return resolvableTemplate;
        }
    }),
    ;
    
    private ResultAccessor<BackgroundProcessResultHolder> bulkResultValueAccessor;
    
    BackgroundProcessResultTypeEnum(ResultAccessor<BackgroundProcessResultHolder> bulkResultValueAccessor) {
        this.bulkResultValueAccessor = bulkResultValueAccessor;
    }
    
    public Object getValue(BackgroundProcessResultHolder callback) {
        return this.bulkResultValueAccessor.getValue(callback);
    }
}
