package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee management set is mislocated.
 */
public final class EcobeeMislocatedSetDiscrepancy extends EcobeeSetDiscrepancy {
    private final String correctLocation;
    
    public EcobeeMislocatedSetDiscrepancy(String currentLocation, String correctLocation) {
        super(EcobeeDiscrepancyType.MISLOCATED_MANAGEMENT_SET, currentLocation);
        this.correctLocation = correctLocation;
    }
    
    public EcobeeMislocatedSetDiscrepancy(int errorId, String currentLocation, String correctLocation) {
         super(errorId, EcobeeDiscrepancyType.MISLOCATED_MANAGEMENT_SET, currentLocation);
         this.correctLocation = correctLocation;
     }
    
    @Override
    public String getCorrectLocation() {
        return correctLocation;
    }
}
