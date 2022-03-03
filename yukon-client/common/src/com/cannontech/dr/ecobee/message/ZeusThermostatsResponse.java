package com.cannontech.dr.ecobee.message;

import java.util.List;

public class ZeusThermostatsResponse {

    private List<ZeusThermostat> thermostats;

    public ZeusThermostatsResponse() {
    }

    public List<ZeusThermostat> getThermostats() {
        return thermostats;
    }

    public void setThermostats(List<ZeusThermostat> thermostats) {
        this.thermostats = thermostats;
    }

}
