package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusDutyCycleEvent {

    private String id;
    private String name;
    @JsonProperty("tstatgroup_id")
    private String tstatGroupId;
    private DrEventState state;
    @JsonProperty("start_timestamp_local")
    private String eventStartTime;
    @JsonProperty("duration_minutes")
    private int durationInMinutes;
    @JsonProperty("ecoplus_selector")
    private EcoplusSelector ecoplusSelector;
    @JsonProperty("random_time_seconds")
    private int randomTimeSeconds;
    @JsonProperty("is_mandatory")
    private boolean isMandatory;
    @JsonProperty("duty_cycle")
    private int dutyCyclePercentage;
    @JsonProperty("send_email")
    private boolean sendEmail;
    @JsonProperty("show_web")
    private boolean showWeb;
    @JsonProperty("show_thermostat")
    private boolean showThermostat;
    private String message;

    public ZeusDutyCycleEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTstatGroupId() {
        return tstatGroupId;
    }

    public void setTstatGroupId(String tstatGroupId) {
        this.tstatGroupId = tstatGroupId;
    }

    public DrEventState getState() {
        return state;
    }

    public void setState(DrEventState state) {
        this.state = state;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public EcoplusSelector getEcoplusSelector() {
        return ecoplusSelector;
    }

    public void setEcoplusSelector(EcoplusSelector ecoplusSelector) {
        this.ecoplusSelector = ecoplusSelector;
    }

    public int getRandomTimeSeconds() {
        return randomTimeSeconds;
    }

    public void setRandomTimeSeconds(int randomTimeSeconds) {
        this.randomTimeSeconds = randomTimeSeconds;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public int getDutyCyclePercentage() {
        return dutyCyclePercentage;
    }

    public void setDutyCyclePercentage(int dutyCyclePercentage) {
        this.dutyCyclePercentage = dutyCyclePercentage;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isShowWeb() {
        return showWeb;
    }

    public void setShowWeb(boolean showWeb) {
        this.showWeb = showWeb;
    }

    public boolean isShowThermostat() {
        return showThermostat;
    }

    public void setShowThermostat(boolean showThermostat) {
        this.showThermostat = showThermostat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
