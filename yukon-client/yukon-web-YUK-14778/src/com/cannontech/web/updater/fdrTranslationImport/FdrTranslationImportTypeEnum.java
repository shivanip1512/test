package com.cannontech.web.updater.fdrTranslationImport;

import com.cannontech.common.bulk.callbackResult.TranslationImportCallbackResult;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;

public enum FdrTranslationImportTypeEnum {
    
    SUCCESS_COUNT(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.getSuccessCount();
        }
    }),
    
    PROCESSING_EXCEPTION_COUNT(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.getProcessingExceptionCount();
        }
    }),
    
    COMPLETED_LINES(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            
            return (importCallbackResults.getSuccessCount() + importCallbackResults.getProcessingExceptionCount());
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.isComplete();
        }
    }),
    
    IS_COMPLETE_WITH_SUCCESSES(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.isComplete() && importCallbackResults.getSuccessCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_FAILURES(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.isComplete() && importCallbackResults.getProcessingExceptionCount() > 0;
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            
            ResolvableTemplate resolvableTemplate = null;
            
            if (importCallbackResults.isComplete()) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_COMPLETE_TEXT");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_IN_PROGRESS_TEXT");
            }
            
            return resolvableTemplate;
        }
    }),
    
    RESET_LOG_POSITION(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.resetLog();
        }
    }),
    
    NEXT_LOG_TEXT(new ResultAccessor<TranslationImportCallbackResult>() {
        public Object getValue(TranslationImportCallbackResult importCallbackResults) {
            return importCallbackResults.getNextLogLine();
        }
    }),
    ;
    
    private ResultAccessor<TranslationImportCallbackResult> importResultValueAccessor;
    
    private FdrTranslationImportTypeEnum(ResultAccessor<TranslationImportCallbackResult> importResultValueAccessor) {
        this.importResultValueAccessor = importResultValueAccessor;
    }
    
    public Object getValue(TranslationImportCallbackResult callback) {
        return importResultValueAccessor.getValue(callback);
    }
}
