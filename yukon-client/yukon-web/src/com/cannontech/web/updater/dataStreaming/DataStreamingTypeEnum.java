package com.cannontech.web.updater.dataStreaming;

import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;

public enum DataStreamingTypeEnum {
    
    SUCCESS_COUNT(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
           return result.getSuccessCount();
        }
    }),
    
    FAILURE_COUNT(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.getFailureCount();
        }
    }),
    
    UNSUPPORTED_COUNT(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.getUnsupportedCount();
        }
    }),
    
    CANCELLED_COUNT(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.getCanceledCount();
        }
    }),
    
    COMPLETED_LINES(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
           return result.getCompletedCount();
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.isComplete();
        }
    }),
    
    IS_COMPLETE_WITH_SUCCESSES(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.isComplete() && result.getSuccessCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_FAILURES(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.isComplete() && result.getFailureCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_UNSUPPORTED(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.isComplete() && result.getUnsupportedCount() > 0;
        }
    }),
    
    IS_COMPLETE_WITH_CANCELLED(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            return result.isComplete() && result.getCanceledCount() > 0;
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            
            ResolvableTemplate resolvableTemplate = null;
            
            if (result.isComplete()) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_COMPLETE_TEXT");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.bulk.bulkHome.recentBulkOperations.IS_IN_PROGRESS_TEXT");
            }
            
            return resolvableTemplate;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<DataStreamingConfigResult>() {
        @Override
        public Object getValue(DataStreamingConfigResult result) {
            //return result.isExceptionOccured() ? "error" : "success";
            return "success";
        }
    }),

    ;
    
    private ResultAccessor<DataStreamingConfigResult> configResultValueAccessor;
    
    private DataStreamingTypeEnum(ResultAccessor<DataStreamingConfigResult> configResultValueAccessor) {
        this.configResultValueAccessor = configResultValueAccessor;
    }
    
    public Object getValue(DataStreamingConfigResult callback) {
        return configResultValueAccessor.getValue(callback);
    }
}
