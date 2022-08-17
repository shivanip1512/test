package com.cannontech.dr.ecobee.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;

public class EcobeeReconciliationResult {
    private final boolean success;
    private final ErrorType errorType;
    private final EcobeeDiscrepancy originalDiscrepancy;
    
    private EcobeeReconciliationResult(EcobeeDiscrepancy originalDiscrepancy, boolean success, ErrorType errorType) {
        this.originalDiscrepancy = originalDiscrepancy;
        this.success = success;
        this.errorType = errorType;
    }
    
    public static EcobeeReconciliationResult newSuccess(EcobeeDiscrepancy originalDiscrepancy) {
        return new EcobeeReconciliationResult(originalDiscrepancy, true, ErrorType.NONE);
    }
    
    public static EcobeeReconciliationResult newFailure(EcobeeDiscrepancy originalDiscrepancy, ErrorType errorType) {
        return new EcobeeReconciliationResult(originalDiscrepancy, false, errorType);
    }
    
    public int getErrorId() {
        return originalDiscrepancy.getErrorId();
    }
    
    public EcobeeDiscrepancy getOriginalDiscrepancy() {
        return originalDiscrepancy;
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
