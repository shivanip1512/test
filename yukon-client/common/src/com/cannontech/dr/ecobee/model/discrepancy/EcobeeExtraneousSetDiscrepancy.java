package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates a management set exists in ecobee that does not correlate to a Yukon group.
 */
public class EcobeeExtraneousSetDiscrepancy extends EcobeeDiscrepancy {
    private final String currentLocation;
    
    public EcobeeExtraneousSetDiscrepancy(String currentLocation) {
        super(EcobeeDiscrepancyType.EXTRANEOUS_MANAGEMENT_SET);
        this.currentLocation = currentLocation;
    }
    
    public EcobeeExtraneousSetDiscrepancy(int errorId, String currentLocation) {
        super(errorId, EcobeeDiscrepancyType.EXTRANEOUS_MANAGEMENT_SET);
        this.currentLocation = currentLocation;
    }
    
    @Override
    public String getCurrentLocation() {
        return currentLocation;
    }
}
