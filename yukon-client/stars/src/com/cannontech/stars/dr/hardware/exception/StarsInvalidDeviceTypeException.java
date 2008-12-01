package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsInvalidDeviceTypeException extends
        StarsClientRequestException {

    private String deviceType = "";
    private String energyCompany = "";

    public StarsInvalidDeviceTypeException() {
        super("Invalid Device type");
    }

    public StarsInvalidDeviceTypeException(String message) {
        super(message);
    }

    public StarsInvalidDeviceTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsInvalidDeviceTypeException(String deviceType,
            String energyCompany) {
        super("Invalid Device type: deviceType=[" + deviceType + "], energyCompany=[" + energyCompany + "]");
        this.deviceType = deviceType;
        this.energyCompany = energyCompany;
    }

    public StarsInvalidDeviceTypeException(String deviceType,
            String energyCompany, String message) {
        super(message);
        this.deviceType = deviceType;
        this.energyCompany = energyCompany;
    }

    public StarsInvalidDeviceTypeException(String deviceType,
            String energyCompany, Throwable cause) {
        super("Invalid Device type: deviceType=[" + deviceType + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.deviceType = deviceType;
        this.energyCompany = energyCompany;
    }

    public StarsInvalidDeviceTypeException(String deviceType,
            String energyCompany, String message, Throwable cause) {
        super(message, cause);
        this.deviceType = deviceType;
        this.energyCompany = energyCompany;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getEnergyCompany() {
        return energyCompany;
    }
}
