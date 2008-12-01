package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsDeviceSerialNumberAlreadyExistsException extends
        StarsClientRequestException {

    private String serialNumber = "";
    private String energyCompany = "";

    public StarsDeviceSerialNumberAlreadyExistsException() {
        super("Serial number already exists on a device");
    }

    public StarsDeviceSerialNumberAlreadyExistsException(String message) {
        super(message);
    }

    public StarsDeviceSerialNumberAlreadyExistsException(String message,
            Throwable cause) {
        super(message, cause);
    }

    public StarsDeviceSerialNumberAlreadyExistsException(String serialNumber,
            String energyCompany) {
        super("Serial number already exists on a device: serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]");
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceSerialNumberAlreadyExistsException(String serialNumber,
            String energyCompany, String message) {
        super(message);
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceSerialNumberAlreadyExistsException(String serialNumber,
            String energyCompany, Throwable cause) {
        super("Serial number already exists on a device: serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceSerialNumberAlreadyExistsException(String serialNumber,
            String energyCompany, String message, Throwable cause) {
        super(message, cause);
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getEnergyCompany() {
        return energyCompany;
    }
}
