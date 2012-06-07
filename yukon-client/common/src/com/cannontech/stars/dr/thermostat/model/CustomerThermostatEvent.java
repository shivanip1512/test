package com.cannontech.stars.dr.thermostat.model;

import java.util.Date;

import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;

/**
 * Iterface which represents a customer thermostat event
 */
public interface CustomerThermostatEvent {

    public Integer getEventId();

    public void setEventId(Integer eventId);

    public Integer getThermostatId();

    public CustomerAction getAction();

    public CustomerEventType getEventType();

    public Date getDate();

    public void setDate(Date date);

    public String getNotes();

    public String getAuthorizedBy();

}