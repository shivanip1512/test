package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee management set is missing or extraneous.
 */
public class EcobeeSetDiscrepancy extends EcobeeDiscrepancy {
    private final String currentLocation;
    
    public EcobeeSetDiscrepancy(EcobeeDiscrepancyType errorType, String currentLocation) {
        super(errorType);
        this.currentLocation = currentLocation;
    }
    
    public EcobeeSetDiscrepancy(int errorId, EcobeeDiscrepancyType errorType, String currentLocation) {
        super(errorId, errorType);
        this.currentLocation = currentLocation;
    }
    
    @Override
    public String getCurrentLocation() {
        return currentLocation;
    }
}
