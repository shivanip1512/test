package com.cannontech.dr.ecobee.model;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

public final class EcobeeDeviceReading {
    private final Float outdoorTempInF;
    private final Float indoorTempInF;
    private final Float setCoolTempInF;
    private final Float setHeatTempInF;
    private final Integer runtimeSeconds;
    private final String eventActivity;
    private final Instant date;

    public EcobeeDeviceReading(Float outdoorTempInF, Float indoorTempInF, Float setCoolTempInF,
                               Float setHeatTempInF, Integer runtimeSeconds, String eventActivity, Instant date) {
        this.outdoorTempInF = outdoorTempInF;
        this.indoorTempInF = indoorTempInF;
        this.setCoolTempInF = setCoolTempInF;
        this.setHeatTempInF = setHeatTempInF;
        this.runtimeSeconds = runtimeSeconds;
        this.eventActivity = eventActivity;
        this.date = date;
    }

    public Float getOutdoorTempInF() {
        return outdoorTempInF;
    }

    public Float getIndoorTempInF() {
        return indoorTempInF;
    }

    public Float getSetCoolTempInF() {
        return setCoolTempInF;
    }

    public Float getSetHeatTempInF() {
        return setHeatTempInF;
    }

    public Integer getRuntimeSeconds() {
        return runtimeSeconds;
    }

    public String getEventActivity() {
        return eventActivity;
    }

    public Instant getDate() {
        return date;
    }
    
    /**
     * Sometimes ecobee provides outdoor temp, but no data directly from the thermostat. This method checks to see
     * if this reading contains at least some data direct from the thermostat.
     */
    public boolean isComplete() {
        return indoorTempInF != null || 
               setCoolTempInF != null || 
               setHeatTempInF != null ||
               StringUtils.isNotBlank(eventActivity);
    }

}
