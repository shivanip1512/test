package com.cannontech.stars.dr.thermostat.model;

import java.util.Date;

import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;

/**
 * Base model object which represents a customer thermostat event
 */
public class CustomerThermostatEventBase implements CustomerThermostatEvent {

    private Integer eventId;
    private Integer thermostatId;
    private CustomerAction action;
    private CustomerEventType eventType;
    private Date date;
    private String notes = "";
    private String authorizedBy = "";

    @Override
    public Integer getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    @Override
    public Integer getThermostatId() {
        return thermostatId;
    }

    public void setThermostatId(Integer thermostatId) {
        this.thermostatId = thermostatId;
    }

    @Override
    public CustomerAction getAction() {
        return action;
    }

    public void setAction(CustomerAction action) {
        this.action = action;
    }

    @Override
    public CustomerEventType getEventType() {
        return eventType;
    }

    public void setEventType(CustomerEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

}
