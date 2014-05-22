package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is mislocated.
 */
public final class EcobeeMislocatedDeviceDiscrepancy extends EcobeeDeviceDiscrepancy {
    private final String correctLocation;
    
    public EcobeeMislocatedDeviceDiscrepancy(String serialNumber, String currentLocation, String correctLocation) {
        super(EcobeeDiscrepancyType.MISLOCATED_DEVICE, serialNumber, currentLocation);
        this.correctLocation = correctLocation;
    }
    
    public EcobeeMislocatedDeviceDiscrepancy(int errorId, String serialNumber, String currentLocation, 
                                             String correctLocation) {
        super(errorId, EcobeeDiscrepancyType.MISLOCATED_DEVICE, serialNumber, currentLocation);
        this.correctLocation = correctLocation;
    }
    
    @Override
    public String getCorrectLocation() {
        return correctLocation;
    }
}
