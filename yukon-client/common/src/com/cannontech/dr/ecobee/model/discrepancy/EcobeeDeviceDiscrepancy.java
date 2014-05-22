package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is missing or extraneous.
 */
public class EcobeeDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String currentLocation;
    
    public EcobeeDeviceDiscrepancy(EcobeeDiscrepancyType errorType, String serialNumber, String currentLocation) {
        super(errorType);
        this.serialNumber = serialNumber;
        this.currentLocation = currentLocation;
    }
    
    public EcobeeDeviceDiscrepancy(int errorId, EcobeeDiscrepancyType errorType, String serialNumber, String currentLocation) {
        super(errorId, errorType);
        this.serialNumber = serialNumber;
        this.currentLocation = currentLocation;
    }
    
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }
    
    @Override
    public String getCurrentLocation() {
        return currentLocation;
    }
}
