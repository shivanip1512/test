package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is extraneous.
 */
public class EcobeeZeusExtraneousDeviceDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String serialNumber;
    private final String currentPath;
    
    public EcobeeZeusExtraneousDeviceDiscrepancy(String serialNumber, String currentPath) {
        super(EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE);
        this.serialNumber = serialNumber;
        this.currentPath = currentPath;
    }
    
    public EcobeeZeusExtraneousDeviceDiscrepancy(int errorId, String serialNumber, String currentPath) {
        super(errorId, EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE);
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
