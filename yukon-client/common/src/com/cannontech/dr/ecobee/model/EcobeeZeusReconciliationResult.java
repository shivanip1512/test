package com.cannontech.dr.ecobee.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;

public class EcobeeZeusReconciliationResult {
    private final boolean success;
    private final ErrorType errorType;
    private final EcobeeZeusDiscrepancy originalDiscrepancy;
    
    private EcobeeZeusReconciliationResult(EcobeeZeusDiscrepancy originalDiscrepancy, boolean success, ErrorType errorType) {
        this.originalDiscrepancy = originalDiscrepancy;
        this.success = success;
        this.errorType = errorType;
    }
    
    public static EcobeeZeusReconciliationResult newSuccess(EcobeeZeusDiscrepancy originalDiscrepancy) {
        return new EcobeeZeusReconciliationResult(originalDiscrepancy, true, ErrorType.NONE);
    }
    
    public static EcobeeZeusReconciliationResult newFailure(EcobeeZeusDiscrepancy originalDiscrepancy, ErrorType errorType) {
        return new EcobeeZeusReconciliationResult(originalDiscrepancy, false, errorType);
    }
    
    public int getErrorId() {
        return originalDiscrepancy.getErrorId();
    }
    
    public EcobeeZeusDiscrepancy getOriginalDiscrepancy() {
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
        NOT_FIXABLE,;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.ecobee.reconciliationErrorType." + name();
        }
    }
}
