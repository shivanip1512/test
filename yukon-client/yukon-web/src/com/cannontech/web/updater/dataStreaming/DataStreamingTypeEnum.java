package com.cannontech.web.updater.dataStreaming;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallbackResult;

public enum DataStreamingTypeEnum {
    
    SUCCESS_COUNT(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return 15;
            //return dataStreamingConfigCallbackResults.getSuccessCount();
        }
    }),
    
    FAILURE_COUNT(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return 3;
            //return dataStreamingConfigCallbackResults.getFailureCount();
        }
    }),
    
    UNSUPPORTED_COUNT(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return 2;
            //return dataStreamingConfigCallbackResults.getUnsupportedCount();
        }
    }),
    
    CANCELLED_COUNT(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return 0;
            //return dataStreamingConfigCallbackResults.getCancelledCount();
        }
    }),
    
    PROCESSING_EXCEPTION_COUNT(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return dataStreamingConfigCallbackResults.getProcessingExceptionCount();
        }
    }),
    
    COMPLETED_LINES(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            //return (dataStreamingConfigCallbackResults.getSuccessCount() + dataStreamingConfigCallbackResults.getProcessingExceptionCount());
            return 20;
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return dataStreamingConfigCallbackResults.isComplete();
        }
    }),
    
    IS_COMPLETE_WITH_SUCCESSES(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return dataStreamingConfigCallbackResults.isComplete() && dataStreamingConfigCallbackResults.getSuccessCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_FAILURES(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return dataStreamingConfigCallbackResults.isComplete() && dataStreamingConfigCallbackResults.getProcessingExceptionCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_UNSUPPORTED(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return dataStreamingConfigCallbackResults.isComplete() && dataStreamingConfigCallbackResults.getProcessingExceptionCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_CANCELLED(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            return dataStreamingConfigCallbackResults.isComplete() && dataStreamingConfigCallbackResults.getProcessingExceptionCount() > 0;
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        public Object getValue(DataStreamingConfigCallbackResult dataStreamingConfigCallbackResults) {
            
            ResolvableTemplate resolvableTemplate = null;
            
            if (dataStreamingConfigCallbackResults.isComplete()) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_COMPLETE_TEXT");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_IN_PROGRESS_TEXT");
            }
            
            return resolvableTemplate;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<DataStreamingConfigCallbackResult>() {
        @Override
        public Object getValue(DataStreamingConfigCallbackResult result) {
            //return result.isExceptionOccured() ? "error" : "success";
            return "success";
        }
    }),

    ;
    
    private ResultAccessor<DataStreamingConfigCallbackResult> configResultValueAccessor;
    
    private DataStreamingTypeEnum(ResultAccessor<DataStreamingConfigCallbackResult> configResultValueAccessor) {
        this.configResultValueAccessor = configResultValueAccessor;
    }
    
    public Object getValue(DataStreamingConfigCallbackResult callback) {
        return configResultValueAccessor.getValue(callback);
    }
}
