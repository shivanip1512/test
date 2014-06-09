package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates a management set exists in ecobee that does not correlate to a Yukon group.
 */
public class EcobeeExtraneousSetDiscrepancy extends EcobeeDiscrepancy {
    private final String currentPath;
    
    public EcobeeExtraneousSetDiscrepancy(String currentPath) {
        super(EcobeeDiscrepancyType.EXTRANEOUS_MANAGEMENT_SET);
        this.currentPath = currentPath;
    }
    
    public EcobeeExtraneousSetDiscrepancy(int errorId, String currentPath) {
        super(errorId, EcobeeDiscrepancyType.EXTRANEOUS_MANAGEMENT_SET);
        this.currentPath = currentPath;
    }
    
    @Override
    public String getCurrentPath() {
        return currentPath;
    }
}
