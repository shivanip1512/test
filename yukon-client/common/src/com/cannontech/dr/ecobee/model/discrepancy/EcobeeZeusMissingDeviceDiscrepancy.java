package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is missing.
 */
public class EcobeeZeusMissingDeviceDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String serialNumber;
    private final String correctPath;
    
    public EcobeeZeusMissingDeviceDiscrepancy(String serialNumber, String correctPath) {
        super(EcobeeZeusDiscrepancyType.MISSING_DEVICE);
        this.serialNumber = serialNumber;
        this.correctPath = correctPath;
    }
    
    public EcobeeZeusMissingDeviceDiscrepancy(int errorId, String serialNumber, String correctPath) {
        super(errorId, EcobeeZeusDiscrepancyType.MISSING_DEVICE);
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
