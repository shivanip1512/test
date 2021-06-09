package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee group is mislocated.
 */
public final class EcobeeZeusMislocatedGroupDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String currentPath;
    private final String correctPath;
    
    public EcobeeZeusMislocatedGroupDiscrepancy(String currentPath, String correctPath) {
        super(EcobeeZeusDiscrepancyType.MISLOCATED_GROUP);
        this.correctPath = correctPath;
        this.currentPath = currentPath;
    }
    
    public EcobeeZeusMislocatedGroupDiscrepancy(int errorId, String currentPath, String correctPath) {
         super(errorId, EcobeeZeusDiscrepancyType.MISLOCATED_GROUP);
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
