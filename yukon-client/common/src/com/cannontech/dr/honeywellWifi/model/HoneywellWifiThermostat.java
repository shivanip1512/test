package com.cannontech.dr.honeywellWifi.model;

public class HoneywellWifiThermostat {
    private String macAddress;
    private Integer deviceVendorUserId;
    private Integer themostatId;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getDeviceVendorUserId() {
        return deviceVendorUserId;
    }

    public void setDeviceVendorUserId(Integer deviceVendorUserId) {
        this.deviceVendorUserId = deviceVendorUserId;
    }

    @Override
    public String toString() {
        return "HoneywellWifiThermostat [macAddress=" + macAddress + ", deviceVendorUserId=" + deviceVendorUserId + "]";
    }

    public Integer getThemostatId() {
        return themostatId;
    }

    public void setThemostatId(Integer themostatId) {
        this.themostatId = themostatId;
    }
}
