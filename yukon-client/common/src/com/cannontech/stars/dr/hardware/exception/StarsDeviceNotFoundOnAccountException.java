package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsDeviceNotFoundOnAccountException extends
        StarsClientRequestException {

    private String accountNumber = "";
    private String serialNumber = "";
    private String energyCompany = "";

    public StarsDeviceNotFoundOnAccountException() {
        super("Device not found on the account");
    }

    public StarsDeviceNotFoundOnAccountException(String message) {
        super(message);
    }

    public StarsDeviceNotFoundOnAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsDeviceNotFoundOnAccountException(String accountNumber,
            String serialNumber, String energyCompany) {
        super("Device not found on the account: accountNumber=[" + accountNumber + "], serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]");
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceNotFoundOnAccountException(String accountNumber,
            String serialNumber, String energyCompany, String message) {
        super(message);
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceNotFoundOnAccountException(String accountNumber,
            String serialNumber, String energyCompany, Throwable cause) {
        super("Device not found on the account: accountNumber=[" + accountNumber + "], serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.accountNumber = accountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceNotFoundOnAccountException(String accountNumber,
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
