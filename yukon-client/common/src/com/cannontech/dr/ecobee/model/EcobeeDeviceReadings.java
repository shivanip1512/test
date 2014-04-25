package com.cannontech.dr.ecobee.model;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.google.common.collect.ImmutableList;

public class EcobeeDeviceReadings {
    private String serialNumber;
    private Range<Instant> dateRange;
    private List<EcobeeDeviceReading> readings;

    public EcobeeDeviceReadings(String serialNumber, Range<Instant> dateRange,
                                List<EcobeeDeviceReading> readings) {
        this.serialNumber = serialNumber;
        this.dateRange = dateRange;
        this.readings = ImmutableList.copyOf(readings);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public Range<Instant> getDateRange() {
        return dateRange;
    }

    public List<EcobeeDeviceReading> getReadings() {
        return readings;
    }
}
