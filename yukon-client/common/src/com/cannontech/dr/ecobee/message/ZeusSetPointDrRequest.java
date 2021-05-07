package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZeusSetPointDrRequest {
    private ZeusEvent event;

    public ZeusSetPointDrRequest(ZeusEvent event) {
        this.event = event;
    }

    public ZeusSetPointDrRequest() {
    }

    public ZeusEvent getEvent() {
        return event;
    }

    public void setEvent(ZeusEvent event) {
        this.event = event;
    }

}
