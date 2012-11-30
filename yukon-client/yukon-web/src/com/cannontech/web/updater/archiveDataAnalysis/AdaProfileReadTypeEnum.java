package com.cannontech.web.updater.archiveDataAnalysis;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.updater.ResultAccessor;

public enum AdaProfileReadTypeEnum {
    COMPLETED_COUNT(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            return adaProfileReadResult.getCompletedCount();
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            return adaProfileReadResult.isComplete();
        }
    }),
    
    SUCCESS_COUNT(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            return adaProfileReadResult.getSucceededCount();
        }
    }),
    
    FAILED_COUNT(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            return adaProfileReadResult.getFailedCount();
        }
    }),
    
    IS_EXCEPTION_OCCURRED(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            return adaProfileReadResult.isErrorOccurred();
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            MessageSourceResolvable resolvable;
            
            if(adaProfileReadResult.isComplete()) {
                resolvable = YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.amr.analysis.readResults.isCompleteText");
            } else if(adaProfileReadResult.isErrorOccurred()) {
                resolvable = YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.amr.analysis.readResults.isErrorOccurredText");
            } else {
                resolvable = YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.amr.analysis.readResults.inProgressText");
            }
            
            return resolvable;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            String className = "";
            if(adaProfileReadResult.isErrorOccurred()) {
                className = "error";
            }
            return className;
        }
    }),
    ;
    
    private ResultAccessor<ArchiveAnalysisProfileReadResult> adaProfileReadValueAccessor;
    
    AdaProfileReadTypeEnum(ResultAccessor<ArchiveAnalysisProfileReadResult> adaProfileReadValueAccessor) {
        this.adaProfileReadValueAccessor = adaProfileReadValueAccessor;
    }
    
    public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
        return this.adaProfileReadValueAccessor.getValue(adaProfileReadResult);
    }
}
