package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusDutyCycleDrRequest {
    private ZeusDutyCycleEvent event;

    public ZeusDutyCycleDrRequest(ZeusDutyCycleEvent event) {
        this.event = event;
    }

    public ZeusDutyCycleDrRequest() {
    }

    public ZeusDutyCycleEvent getEvent() {
        return event;
    }

    public void setEvent(ZeusDutyCycleEvent event) {
        this.event = event;
    }
}