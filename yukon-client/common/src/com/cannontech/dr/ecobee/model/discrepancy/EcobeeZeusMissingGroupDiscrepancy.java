package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee group is missing.
 */
public class EcobeeZeusMissingGroupDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String correctPath;
    
    public EcobeeZeusMissingGroupDiscrepancy(String correctPath) {
        super(EcobeeZeusDiscrepancyType.MISSING_GROUP);
        this.correctPath = correctPath;
    }
    
    public EcobeeZeusMissingGroupDiscrepancy(int errorId, String correctPath) {
        super(errorId, EcobeeZeusDiscrepancyType.MISSING_GROUP);
        this.correctPath = correctPath;
    }
    
    @Override
    public String getCorrectPath() {
        return correctPath;
    }
}
