package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is missing.
 */
public class EcobeeMissingDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String correctLocation;
    
    public EcobeeMissingDeviceDiscrepancy(String serialNumber, String correctLocation) {
        super(EcobeeDiscrepancyType.MISSING_DEVICE);
        this.serialNumber = serialNumber;
        this.correctLocation = correctLocation;
    }
    
    public EcobeeMissingDeviceDiscrepancy(int errorId, String serialNumber, String correctLocation) {
        super(errorId, EcobeeDiscrepancyType.MISSING_DEVICE);
        this.serialNumber = serialNumber;
        this.correctLocation = correctLocation;
    }
    
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }
    
    @Override
    public String getCorrectLocation() {
        return correctLocation;
    }
}
