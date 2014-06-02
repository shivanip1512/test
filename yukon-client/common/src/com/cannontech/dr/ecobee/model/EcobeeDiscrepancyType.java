package com.cannontech.dr.ecobee.model;

public enum EcobeeDiscrepancyType {
    MISSING_MANAGEMENT_SET(EcobeeDiscrepancyCategory.GROUP),
    MISLOCATED_MANAGEMENT_SET(EcobeeDiscrepancyCategory.GROUP),
    EXTRANEOUS_MANAGEMENT_SET(EcobeeDiscrepancyCategory.GROUP),
    MISSING_DEVICE(EcobeeDiscrepancyCategory.DEVICE),
    MISLOCATED_DEVICE(EcobeeDiscrepancyCategory.DEVICE),
    EXTRANEOUS_DEVICE(EcobeeDiscrepancyCategory.DEVICE),
    ;
    
    private final EcobeeDiscrepancyCategory category;
    
    private EcobeeDiscrepancyType(EcobeeDiscrepancyCategory category) {
        this.category = category;
    }
    
    public EcobeeDiscrepancyCategory getCategory() {
        return category;
    }
}
