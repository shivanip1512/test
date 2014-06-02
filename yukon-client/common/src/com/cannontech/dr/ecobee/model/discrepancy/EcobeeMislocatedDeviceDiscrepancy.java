package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is mislocated.
 */
public final class EcobeeMislocatedDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String correctLocation;
    private final String currentLocation;
    
    public EcobeeMislocatedDeviceDiscrepancy(String serialNumber, String currentLocation, String correctLocation) {
        super(EcobeeDiscrepancyType.MISLOCATED_DEVICE);
        this.correctLocation = correctLocation;
        this.currentLocation = currentLocation;
        this.serialNumber = serialNumber;
    }
    
    public EcobeeMislocatedDeviceDiscrepancy(int errorId, String serialNumber, String currentLocation, 
                                             String correctLocation) {
        super(errorId, EcobeeDiscrepancyType.MISLOCATED_DEVICE);
        this.correctLocation = correctLocation;
        this.currentLocation = currentLocation;
        this.serialNumber = serialNumber;
    }
    
    @Override
    public String getCorrectLocation() {
        return correctLocation;
    }
    
    @Override
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }
}
