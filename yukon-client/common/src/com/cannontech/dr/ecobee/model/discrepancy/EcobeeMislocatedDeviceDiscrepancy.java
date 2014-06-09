package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is mislocated.
 */
public final class EcobeeMislocatedDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String correctPath;
    private final String currentPath;
    
    public EcobeeMislocatedDeviceDiscrepancy(String serialNumber, String currentPath, String correctPath) {
        super(EcobeeDiscrepancyType.MISLOCATED_DEVICE);
        this.correctPath = correctPath;
        this.currentPath = currentPath;
        this.serialNumber = serialNumber;
    }
    
    public EcobeeMislocatedDeviceDiscrepancy(int errorId, String serialNumber, String currentPath, 
                                             String correctPath) {
        super(errorId, EcobeeDiscrepancyType.MISLOCATED_DEVICE);
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
