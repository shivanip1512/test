package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee management set is missing.
 */
public class EcobeeMissingSetDiscrepancy extends EcobeeDiscrepancy {
    private final String correctPath;
    
    public EcobeeMissingSetDiscrepancy(String correctPath) {
        super(EcobeeDiscrepancyType.MISSING_MANAGEMENT_SET);
        this.correctPath = correctPath;
    }
    
    public EcobeeMissingSetDiscrepancy(int errorId, String correctPath) {
        super(errorId, EcobeeDiscrepancyType.MISSING_MANAGEMENT_SET);
        this.correctPath = correctPath;
    }
    
    @Override
    public String getCorrectPath() {
        return correctPath;
    }
}
