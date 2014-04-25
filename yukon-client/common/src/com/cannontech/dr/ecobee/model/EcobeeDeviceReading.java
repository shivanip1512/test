package com.cannontech.dr.ecobee.model;



public class EcobeeDeviceReading {
    private final Float outdoorTempInF;
    private final Float indoorTempInF;
    private final Float setCoolTempInF;
    private final Float setHeatTempInF;
    private final Integer runtimeSeconds;
    private final String eventActivity;

    public EcobeeDeviceReading(Float outdoorTempInF, Float indoorTempInF, Float setCoolTempInF,
                               Float setHeatTempInF, Integer runtimeSeconds, String eventActivity) {
        this.outdoorTempInF = outdoorTempInF;
        this.indoorTempInF = indoorTempInF;
        this.setCoolTempInF = setCoolTempInF;
        this.setHeatTempInF = setHeatTempInF;
        this.runtimeSeconds = runtimeSeconds;
        this.eventActivity = eventActivity;
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
}
