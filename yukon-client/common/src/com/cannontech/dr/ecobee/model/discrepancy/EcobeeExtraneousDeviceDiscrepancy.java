package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is extraneous.
 */
public class EcobeeExtraneousDeviceDiscrepancy extends EcobeeDiscrepancy {
    private final String serialNumber;
    private final String currentLocation;
    
    public EcobeeExtraneousDeviceDiscrepancy(String serialNumber, String currentLocation) {
        super(EcobeeDiscrepancyType.EXTRANEOUS_DEVICE);
        this.serialNumber = serialNumber;
        this.currentLocation = currentLocation;
    }
    
    public EcobeeExtraneousDeviceDiscrepancy(int errorId, String serialNumber, String currentLocation) {
        super(errorId, EcobeeDiscrepancyType.EXTRANEOUS_DEVICE);
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
