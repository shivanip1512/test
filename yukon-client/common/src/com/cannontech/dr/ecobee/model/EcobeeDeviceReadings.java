package com.cannontech.dr.ecobee.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class EcobeeDeviceReadings {
    private final String serialNumber;
    private final List<EcobeeDeviceReading> readings;

    public EcobeeDeviceReadings(String serialNumber, List<EcobeeDeviceReading> readings) {
        this.serialNumber = serialNumber;
        this.readings = ImmutableList.copyOf(readings);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public List<EcobeeDeviceReading> getReadings() {
        return readings;
    }
}
