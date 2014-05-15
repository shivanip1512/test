package com.cannontech.dr.ecobee.message.partial;

import org.joda.time.Instant;

import com.cannontech.common.util.JsonSerializers;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using=JsonSerializers.EcobeeRuntimeReportRow.class)
public class RuntimeReportRow {
    private final Instant date;
    private final String eventName;
    private final Float indoorTemp;
    private final Float outdoorTemp;
    private final Float coolSetPoint;
    private final Float heatSetPoint;
    private final Integer coolRuntime;
    private final Integer heatRuntime;

    public RuntimeReportRow(Instant date, String eventName, Float indoorTemp, Float outdoorTemp, Float coolSetPoint,
                            Float heatSetPoint, Integer coolRuntime, Integer heatRuntime) {
        this.date = date;
        this.eventName = eventName;
        this.indoorTemp = indoorTemp;
        this.outdoorTemp = outdoorTemp;
        this.coolSetPoint = coolSetPoint;
        this.heatSetPoint = heatSetPoint;
        this.coolRuntime = coolRuntime;
        this.heatRuntime = heatRuntime;
    }

    public String getEventName() {
        return eventName;
    }

    public Float getIndoorTemp() {
        return indoorTemp;
    }

    public Float getOutdoorTemp() {
        return outdoorTemp;
    }

    public Float getCoolSetPoint() {
        return coolSetPoint;
    }

    public Float getHeatSetPoint() {
        return heatSetPoint;
    }

    public Integer getCoolRuntime() {
        return coolRuntime;
    }

    public Integer getHeatRuntime() {
        return heatRuntime;
    }

    public Instant getDate() {
        return date;
    }
}
