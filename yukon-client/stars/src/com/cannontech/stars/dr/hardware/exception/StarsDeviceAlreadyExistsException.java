package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsDeviceAlreadyExistsException extends
        StarsClientRequestException {

    private String accountNumber = "";
    private String serialNumber = "";
    private String energyCompany = "";

    public StarsDeviceAlreadyExistsException() {
        super("Device already exists on the account");
    }

    public StarsDeviceAlreadyExistsException(String message) {
        super(message);
    }

    public StarsDeviceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsDeviceAlreadyExistsException(String accountNumber,
            String serialNumber, String energyCompany) {
        super("Device already exists on the account: accountNumber=[" + accountNumber + "], serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]");
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyExistsException(String accountNumber,
            String serialNumber, String energyCompany, String message) {
        super(message);
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyExistsException(String accountNumber,
            String serialNumber, String energyCompany, Throwable cause) {
        super("Device already exists on the account: accountNumber=[" + accountNumber + "], serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyExistsException(String accountNumber,
            String serialNumber, String energyCompany, String message,
            Throwable cause) {
        super(message, cause);
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getEnergyCompany() {
        return energyCompany;
    }
}
