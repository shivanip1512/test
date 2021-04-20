package com.cannontech.dr.ecobee.message;

public class ZeusThermostatRequest {
    String id;
    String state;
    String thermostatId;

    public ZeusThermostatRequest(String id, String state, String thermostatId) {
        super();
        this.id = id;
        this.state = state;
        this.thermostatId = thermostatId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getThermostatId() {
        return thermostatId;
    }

    public void setThermostatId(String thermostatId) {
        this.thermostatId = thermostatId;
    }

}
