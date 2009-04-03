package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

/**
 * ThermostatSummary data transfer object, used to populate top-level
 * menu-options
 */
public class ThermostatSummary {

    private Integer id;
    private String serialNumber;
    private String deviceLabel;

    public ThermostatSummary(int id, String serialNumber, String deviceLabel) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.deviceLabel = deviceLabel;
    }

    public Integer getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDeviceLabel() {
        return deviceLabel;
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
