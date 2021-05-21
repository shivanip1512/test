package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusDemandResponseRequest {
    private ZeusEvent event;

    public ZeusDemandResponseRequest(ZeusEvent event) {
        this.event = event;
    }

    public ZeusDemandResponseRequest() {
    }

    public ZeusEvent getEvent() {
        return event;
    }

    public void setEvent(ZeusEvent event) {
        this.event = event;
    }
}