package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates a group exists in ecobee that does not correlate to a Yukon group.
 */
public class EcobeeZeusExtraneousGroupDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String currentPath;
    
    public EcobeeZeusExtraneousGroupDiscrepancy(String currentPath) {
        super(EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP);
        this.currentPath = currentPath;
    }
    
    public EcobeeZeusExtraneousGroupDiscrepancy(int errorId, String currentPath) {
        super(errorId, EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP);
        this.currentPath = currentPath;
    }
    
    @Override
    public String getCurrentPath() {
        return currentPath;
    }
}
