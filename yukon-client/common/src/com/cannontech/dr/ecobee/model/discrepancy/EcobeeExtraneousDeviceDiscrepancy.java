package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is extraneous.
 */
public class EcobeeExtraneousDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String currentPath;
    
    public EcobeeExtraneousDeviceDiscrepancy(String serialNumber, String currentPath) {
        super(EcobeeDiscrepancyType.EXTRANEOUS_DEVICE);
        this.serialNumber = serialNumber;
        this.currentPath = currentPath;
    }
    
    public EcobeeExtraneousDeviceDiscrepancy(int errorId, String serialNumber, String currentPath) {
        super(errorId, EcobeeDiscrepancyType.EXTRANEOUS_DEVICE);
        this.serialNumber = serialNumber;
        this.currentPath = currentPath;
    }
    
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }
    
    @Override
    public String getCurrentPath() {
        return currentPath;
    }
}
