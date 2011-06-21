package com.cannontech.web.updater.archiveDataAnalysis;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.bulk.model.ArchiveAnalysisProfileReadResult;
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
            ResolvableTemplate resolvableTemplate = null;
            
            if(adaProfileReadResult.isComplete()) {
                resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.analysisReadResults.isCompleteText");
            } else if(adaProfileReadResult.isErrorOccurred()) {
                resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.analysisReadResults.isErrorOccurredText");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.analysisReadResults.inProgressText");
            }
            //cancelled?
            
            return resolvableTemplate;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<ArchiveAnalysisProfileReadResult>() {
        public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
            String className = "";
            if(adaProfileReadResult.isErrorOccurred()) {
                className = "errorRed";
            }
            return className;
        }
    }),
    ;
    
    //TODO: WTF does this do?
    private ResultAccessor<ArchiveAnalysisProfileReadResult> adaProfileReadValueAccessor;
    
    AdaProfileReadTypeEnum(ResultAccessor<ArchiveAnalysisProfileReadResult> adaProfileReadValueAccessor) {
        this.adaProfileReadValueAccessor = adaProfileReadValueAccessor;
    }
    
    public Object getValue(ArchiveAnalysisProfileReadResult adaProfileReadResult) {
        return this.adaProfileReadValueAccessor.getValue(adaProfileReadResult);
    }
}
