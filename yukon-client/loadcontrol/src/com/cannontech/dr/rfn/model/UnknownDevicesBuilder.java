package com.cannontech.dr.rfn.model;

import java.util.ArrayList;
import java.util.List;

public class UnknownDevicesBuilder {
    private List<UnknownDevice> unknownDevices = new ArrayList<>();
    private int numUnavailable;
    private int numActive;
    private int numInactive;
    private int numUnreportedNew;
    private int numUnreportedOld;
    
    public void addUnknownDevice(UnknownDevice unknownDevice) {
        unknownDevices.add(unknownDevice);
        switch (unknownDevice.getUnknownStatus()) {
            case ACTIVE: numActive++; break;
            case INACTIVE: numInactive++; break;
            case UNAVAILABLE: numUnavailable++; break;
            case UNREPORTED_NEW: numUnreportedNew++; break;
            case UNREPORTED_OLD: numUnreportedOld++; break;
        }
    }
    
    public UnknownDevices build() {
        return new UnknownDevices(unknownDevices, numUnavailable,
                                  numActive, numInactive, numUnreportedNew, numUnreportedOld);
    }
}