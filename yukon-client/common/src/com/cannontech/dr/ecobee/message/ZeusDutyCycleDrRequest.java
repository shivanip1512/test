package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusDutyCycleDrRequest {
    private ZeusEvent event;

    public ZeusDutyCycleDrRequest(ZeusEvent event) {
        this.event = event;
    }

    public ZeusDutyCycleDrRequest() {
    }

    public ZeusEvent getEvent() {
        return event;
    }

    public void setEvent(ZeusEvent event) {
        this.event = event;
    }
}