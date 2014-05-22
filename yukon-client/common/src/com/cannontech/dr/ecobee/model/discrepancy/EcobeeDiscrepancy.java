package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * Base discrepancy, which returns null values for all fields. All ecobee reconciliation report discrepancies are
 * children of this class and override the getters for all fields they actually use.
 */
public abstract class EcobeeDiscrepancy {
    private final Integer errorId;
    private final EcobeeDiscrepancyType errorType;
    
    public EcobeeDiscrepancy(EcobeeDiscrepancyType errorType) {
        errorId = null;
        this.errorType = errorType;
    }
    
    public EcobeeDiscrepancy(int errorId, EcobeeDiscrepancyType errorType) {
        this.errorId = errorId;
        this.errorType = errorType;
    }
    
    /**
     * Used to determine what type of discrepancy this is (which will determine which fields are non-null).
     */
    public EcobeeDiscrepancyType getErrorType() {
        return errorType;
    }
    
    public Integer getErrorId() {
        return errorId;
    }
    
    public String getSerialNumber() {
        return null;
    }
    
    public String getCurrentLocation() {
        return null;
    }
    
    public String getCorrectLocation() {
        return null;
    }
}
