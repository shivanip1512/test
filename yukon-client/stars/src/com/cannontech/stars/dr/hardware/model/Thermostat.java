package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

/**
 * Thermostat data transfer object
 */
public class Thermostat {

    private Integer id;
    private String serialNumber;
    private String deviceLabel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

        if (!StringUtils.isBlank(deviceLabel)) {
            return deviceLabel;
        }

        return serialNumber;
    }

}
