package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee management set is mislocated.
 */
public final class EcobeeMislocatedSetDiscrepancy extends EcobeeDiscrepancy {
    private final String currentPath;
    private final String correctPath;
    
    public EcobeeMislocatedSetDiscrepancy(String currentPath, String correctPath) {
        super(EcobeeDiscrepancyType.MISLOCATED_MANAGEMENT_SET);
        this.correctPath = correctPath;
        this.currentPath = currentPath;
    }
    
    public EcobeeMislocatedSetDiscrepancy(int errorId, String currentPath, String correctPath) {
         super(errorId, EcobeeDiscrepancyType.MISLOCATED_MANAGEMENT_SET);
         this.correctPath = correctPath;
         this.currentPath = currentPath;
     }
    
    @Override
    public String getCurrentPath() {
        return currentPath;
    }
    
    @Override
    public String getCorrectPath() {
        return correctPath;
    }
}
