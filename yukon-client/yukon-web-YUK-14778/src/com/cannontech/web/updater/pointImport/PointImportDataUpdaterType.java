package com.cannontech.web.updater.pointImport;

import com.cannontech.common.bulk.callbackResult.PointImportCallbackResult;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;

public enum PointImportDataUpdaterType {
    
    SUCCESS_COUNT(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return result.getSuccessCount();
        }
    }),
    
    PROCESSING_EXCEPTION_COUNT(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return result.getProcessingExceptionCount();
        }
    }),
    
    COMPLETED_LINES(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return (result.getSuccessCount() + result.getProcessingExceptionCount());
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return result.isComplete();
        }
    }),
    
    IS_COMPLETE_WITH_SUCCESSES(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return result.isComplete() && result.getSuccessCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_FAILURES(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return result.isComplete() && result.getProcessingExceptionCount() > 0;
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            ResolvableTemplate resolvableTemplate = null;
            if (result.isComplete()) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_COMPLETE_TEXT");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_IN_PROGRESS_TEXT");
            }
            return resolvableTemplate;
        }
    }),
    
    RESET_LOG_POSITION(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult result) {
            return result.resetLog();
        }
    }),
    
    NEXT_LOG_TEXT(new ResultAccessor<PointImportCallbackResult>() {
        public Object getValue(PointImportCallbackResult importCallbackResults) {
            return importCallbackResults.getNextLogLine();
        }
    }),
    ;
    
    private ResultAccessor<PointImportCallbackResult> importResultValueAccessor;
    
    private PointImportDataUpdaterType(ResultAccessor<PointImportCallbackResult> importResultValueAccessor) {
        this.importResultValueAccessor = importResultValueAccessor;
    }
    
    public Object getValue(PointImportCallbackResult result) {
        return importResultValueAccessor.getValue(result);
    }
}
