package com.cannontech.dr.ecobee.model;

import com.cannontech.common.i18n.DisplayableEnum;

public class EcobeeReconciliationResult {
    private final int errorId;
    private final boolean success;
    private final ErrorType errorType;
    
    private EcobeeReconciliationResult(int errorId, boolean success, ErrorType errorType) {
        this.errorId = errorId;
        this.success = success;
        this.errorType = errorType;
    }
    
    public static EcobeeReconciliationResult newSuccess(int errorId) {
        return new EcobeeReconciliationResult(errorId, true, ErrorType.NONE);
    }
    
    public static EcobeeReconciliationResult newFailure(int errorId, ErrorType errorType) {
        return new EcobeeReconciliationResult(errorId, false, errorType);
    }
    
    public int getErrorId() {
        return errorId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
    
    public enum ErrorType implements DisplayableEnum {
        NONE,
        COMMUNICATION,
        NO_SET,
        NO_DEVICE,
        NOT_FIXABLE,;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.ecobee.reconciliationErrorType." + name();
        }
    }
}
