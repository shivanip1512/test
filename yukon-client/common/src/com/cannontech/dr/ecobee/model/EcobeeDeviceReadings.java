package com.cannontech.dr.ecobee.model;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;

public class EcobeeDeviceReadings {
    private long serialNumber;
    private Range<Instant> dateRange;
    private List<Float> outdoorTempInF;
    private List<Float> indoorTempInF;
    private List<Float> setCoolTempInF;
    private List<Float> setHeatTempInF;
    private List<Integer> runtimeSeconds;
    private List<String> eventActivity;
}
