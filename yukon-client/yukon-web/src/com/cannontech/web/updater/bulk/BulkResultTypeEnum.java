package com.cannontech.web.updater.bulk;

import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.util.ResolvableTemplate;


public enum BulkResultTypeEnum {

    SUCCESS_COUNT(new BulkResultValueAccessor() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.getSuccessCount();
        }
    }),
    
    PROCESSING_EXCEPTION_COUNT(new BulkResultValueAccessor() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            return bulkOperationCallbackResults.getProcessingExceptionCount();
        }
    }),
    
    COMPLETED_LINES(new BulkResultValueAccessor() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            
            return (bulkOperationCallbackResults.getSuccessCount() + bulkOperationCallbackResults.getProcessingExceptionCount());
        }
    }),
    
    STOP_TIME(new BulkResultValueAccessor() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.STOP_TIME");
            resolvableTemplate.addData("stopTime", bulkOperationCallbackResults.getStopTime());
            
            boolean finished = false;
            if (bulkOperationCallbackResults.isComplete()) {
                finished = true;
            }
            resolvableTemplate.addData("finished", finished);
            
            return resolvableTemplate;
        }
    }),
    
    IS_COMPLETE(new BulkResultValueAccessor() {
        public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_COMPLETE");
            resolvableTemplate.addData("isComplete", bulkOperationCallbackResults.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    ;
    
    private BulkResultValueAccessor bulkResultValueAccessor;
    
    BulkResultTypeEnum(BulkResultValueAccessor bulkResultValueAccessor) {

        this.bulkResultValueAccessor = bulkResultValueAccessor;
    }
    
    public Object getValue(BulkOperationCallbackResults<?> bulkOperationCallbackResults) {
        return this.bulkResultValueAccessor.getValue(bulkOperationCallbackResults);
    }
}
