package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is mislocated.
 */
public final class EcobeeZeusMislocatedDeviceDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String serialNumber;
    private final String correctPath;
    private final String currentPath;
    
    public EcobeeZeusMislocatedDeviceDiscrepancy(String serialNumber, String currentPath, String correctPath) {
        super(EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE);
        this.correctPath = correctPath;
        this.currentPath = currentPath;
        this.serialNumber = serialNumber;
    }
    
    public EcobeeZeusMislocatedDeviceDiscrepancy(int errorId, String serialNumber, String currentPath, 
                                             String correctPath) {
        super(errorId, EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE);
        this.correctPath = correctPath;
        this.currentPath = currentPath;
        this.serialNumber = serialNumber;
    }
    
    @Override
    public String getCorrectPath() {
        return correctPath;
    }
    
    @Override
    public String getCurrentPath() {
        return currentPath;
    }
    
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }
}
