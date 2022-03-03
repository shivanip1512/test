package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

import com.cannontech.database.db.point.stategroup.TrueFalse;

public final class EcobeeZeusDeviceReading {
    private String serialNumber;
    private Float outdoorTempInF;
    private Float indoorTempInF;
    private Float setCoolTempInF;
    private Float setHeatTempInF;
    private TrueFalse controlStatus;
    private Instant date;
    private Integer commStatus;
    private Integer stateValue;

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

    public Integer getStateValue() {
        return stateValue;
    }

    public void setStateValue(Integer stateValue) {
        this.stateValue = stateValue;
    }

    public TrueFalse getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(TrueFalse controlStatus) {
        this.controlStatus = controlStatus;
    }
}
