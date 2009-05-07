package com.cannontech.web.updater.bulk;

import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;


public enum BulkResultTypeEnum {

    SUCCESS_COUNT(new ResultAccessor<BulkOperationCallbackResults<?>>() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.getSuccessCount();
        }
    }),
    
    PROCESSING_EXCEPTION_COUNT(new ResultAccessor<BulkOperationCallbackResults<?>>() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.getProcessingExceptionCount();
        }
    }),
    
    COMPLETED_LINES(new ResultAccessor<BulkOperationCallbackResults<?>>() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            
            return (bulkOperationCallbackResults.getSuccessCount() + bulkOperationCallbackResults.getProcessingExceptionCount());
        }
    }),
    
    STOP_TIME(new ResultAccessor<BulkOperationCallbackResults<?>>() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.STOP_TIME");
            resolvableTemplate.addData("stopTime", bulkOperationCallbackResults.getStopTime());
            resolvableTemplate.addData("finished", bulkOperationCallbackResults.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<BulkOperationCallbackResults<?>>() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.isComplete();
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<BulkOperationCallbackResults<?>>() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            
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
    
    private ResultAccessor<BulkOperationCallbackResults<?>> bulkResultValueAccessor;
    
    BulkResultTypeEnum(ResultAccessor<BulkOperationCallbackResults<?>> bulkResultValueAccessor) {

        this.bulkResultValueAccessor = bulkResultValueAccessor;
    }
    
    public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
        return this.bulkResultValueAccessor.getValue(bulkOperationCallbackResults);
    }
}
