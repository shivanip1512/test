package com.cannontech.web.updater.assetAvailability;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.updater.ResultAccessor;

public enum AssetAvailabilityReadType {
    TOTAL_COUNT(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            return readResult.getTotalCount();
        }
    }),
    
    COMPLETED_COUNT(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            return readResult.getCompletedCount();
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            return readResult.isComplete();
        }
    }),
    
    SUCCESS_COUNT(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            return readResult.getSuccessCount();
        }
    }),
    
    FAILED_COUNT(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            return readResult.getFailedCount();
        }
    }),
    
    IS_EXCEPTION_OCCURRED(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            return readResult.isErrorOccurred();
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            MessageSourceResolvable resolvable;
            if(readResult.isComplete()) {
                resolvable = YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.dr.assetAvailability.pingComplete");
            } else if(readResult.isErrorOccurred()) {
                resolvable = YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.dr.assetAvailability.pingError");
            } else {
                resolvable = YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.dr.assetAvailability.pingInProgress");
            }
            return resolvable;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<AssetAvailabilityReadResult>() {
        public Object getValue(AssetAvailabilityReadResult readResult) {
            String className = "";
            if(readResult.isErrorOccurred()) {
                className = "error";
            }
            return className;
        }
    })
    ;
    
    private ResultAccessor<AssetAvailabilityReadResult> resultAccessor;
    
    AssetAvailabilityReadType(ResultAccessor<AssetAvailabilityReadResult> resultAccessor) {
        this.resultAccessor = resultAccessor;
    }
    
    public Object getValue(AssetAvailabilityReadResult readResult) {
        return this.resultAccessor.getValue(readResult);
    }
}
