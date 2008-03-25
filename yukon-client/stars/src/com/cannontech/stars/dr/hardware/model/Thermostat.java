package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

/**
 * Thermostat data transfer object
 */
public class Thermostat {

    private int id = -1;
    private String serialNumber;
    private String deviceLabel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    /**
     * Method to get the thermostat label which is the device label or the
     * serial number
     * @return Label
     */
    public String getLabel() {

        if (!StringUtils.isEmpty(deviceLabel.trim())) {
            return deviceLabel;
        }

        return serialNumber;
    }

}
