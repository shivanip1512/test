package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

public final class EcobeeZeusDeviceReading {
    private String serialNumber;
    private Float outdoorTempInF;
    private Float indoorTempInF;
    private Float setCoolTempInF;
    private Float setHeatTempInF;
    private String drRef;
    private Instant date;
    private Integer commStatus;
    private Integer coolStage1;
    private Integer heatStage1;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Float getOutdoorTempInF() {
        return outdoorTempInF;
    }

    public void setOutdoorTempInF(Float outdoorTempInF) {
        this.outdoorTempInF = outdoorTempInF;
    }

    public Float getIndoorTempInF() {
        return indoorTempInF;
    }

    public void setIndoorTempInF(Float indoorTempInF) {
        this.indoorTempInF = indoorTempInF;
    }

    public Float getSetCoolTempInF() {
        return setCoolTempInF;
    }

    public void setSetCoolTempInF(Float setCoolTempInF) {
        this.setCoolTempInF = setCoolTempInF;
    }

    public Float getSetHeatTempInF() {
        return setHeatTempInF;
    }

    public void setSetHeatTempInF(Float setHeatTempInF) {
        this.setHeatTempInF = setHeatTempInF;
    }

    public String getDrRef() {
        return drRef;
    }

    public void setDrRef(String drRef) {
        this.drRef = drRef;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Integer getCommStatus() {
        return commStatus;
    }

    public void setCommStatus(Integer commStatus) {
        this.commStatus = commStatus;
    }

    public Integer getCoolStage1() {
        return coolStage1;
    }

    public void setCoolStage1(Integer coolStage1) {
        this.coolStage1 = coolStage1;
    }

    public Integer getHeatStage1() {
        return heatStage1;
    }

    public void setHeatStage1(Integer heatStage1) {
        this.heatStage1 = heatStage1;
    }
}
