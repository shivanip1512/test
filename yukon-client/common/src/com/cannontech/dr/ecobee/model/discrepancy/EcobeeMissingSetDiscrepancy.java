package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee management set is missing.
 */
public class EcobeeMissingSetDiscrepancy extends EcobeeDiscrepancy {
    private final String correctLocation;
    
    public EcobeeMissingSetDiscrepancy( String correctLocation) {
        super(EcobeeDiscrepancyType.MISSING_MANAGEMENT_SET);
        this.correctLocation = correctLocation;
    }
    
    public EcobeeMissingSetDiscrepancy(int errorId, String correctLocation) {
        super(errorId, EcobeeDiscrepancyType.MISSING_MANAGEMENT_SET);
        this.correctLocation = correctLocation;
    }
    
    @Override
    public String getCorrectLocation() {
        return correctLocation;
    }
}
