package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is missing.
 */
public class EcobeeMissingDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String correctPath;
    
    public EcobeeMissingDeviceDiscrepancy(String serialNumber, String correctPath) {
        super(EcobeeDiscrepancyType.MISSING_DEVICE);
        this.serialNumber = serialNumber;
        this.correctPath = correctPath;
    }
    
    public EcobeeMissingDeviceDiscrepancy(int errorId, String serialNumber, String correctPath) {
        super(errorId, EcobeeDiscrepancyType.MISSING_DEVICE);
        this.serialNumber = serialNumber;
        this.correctPath = correctPath;
    }
    
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }
    
    @Override
    public String getCorrectPath() {
        return correctPath;
    }
}
