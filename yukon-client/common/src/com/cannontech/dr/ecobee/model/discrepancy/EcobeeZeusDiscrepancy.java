package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * Base discrepancy, which returns null values for all fields. All ecobee reconciliation report discrepancies are
 * children of this class and override the getters for all fields they actually use.
 */
public abstract class EcobeeZeusDiscrepancy {
    private final Integer errorId;
    private final EcobeeZeusDiscrepancyType errorType;
    
    public EcobeeZeusDiscrepancy(EcobeeZeusDiscrepancyType errorType) {
        errorId = null;
        this.errorType = errorType;
    }
    
    public EcobeeZeusDiscrepancy(int errorId, EcobeeZeusDiscrepancyType errorType) {
        this.errorId = errorId;
        this.errorType = errorType;
    }
    
    /**
     * Used to determine what type of discrepancy this is (which will determine which fields are non-null).
     */
    public EcobeeZeusDiscrepancyType getErrorType() {
        return errorType;
    }
    
    public Integer getErrorId() {
        return errorId;
    }
    
    public String getSerialNumber() {
        return null;
    }
    
    public String getCurrentPath() {
        return null;
    }
    
    public String getCorrectPath() {
        return null;
    }
    
    public String[] getArguments() {
        return new String[] {getCorrectPath(), getCurrentPath(), getSerialNumber()};
    }
    
    public boolean isFixable() {
        return errorType.isFixable();
    }
}
