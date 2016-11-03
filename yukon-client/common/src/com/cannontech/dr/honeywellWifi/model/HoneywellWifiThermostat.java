package com.cannontech.dr.honeywellWifi.model;

public class HoneywellWifiThermostat {
    private String macAddress;
    private Integer userId;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "HoneywellWifiThermostat [macAddress=" + macAddress + ", UserID=" + userId + "]";
    }
}
