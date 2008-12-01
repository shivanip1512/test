package com.cannontech.stars.dr.hardware.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsDeviceAlreadyAssignedException extends
        StarsClientRequestException {

    //Can create exception with the reqAccountNumber, serialNumber, energyCompany
    private String reqAccountNumber = "";
    private String serialNumber = "";

    //Can create exception with the reqAccountId, inventoryId, energyCompany    
    private int reqAccountId;
    private int inventoryId;

    private String energyCompany = "";

    public StarsDeviceAlreadyAssignedException() {
        super("Device already assigned to another account");
    }

    public StarsDeviceAlreadyAssignedException(String message) {
        super(message);
    }

    public StarsDeviceAlreadyAssignedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsDeviceAlreadyAssignedException(String reqAccountNumber,
            String serialNumber, String energyCompany) {
        super("Device already assigned to another account: requested accountNumber=[" + reqAccountNumber + "], serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]");
        this.reqAccountNumber = reqAccountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(int reqAccountId,
            int inventoryId, String energyCompany) {
        super("Device already assigned to another account: requested accountId=[" + reqAccountId + "], inventoryId=[" + inventoryId + "], energyCompany=[" + energyCompany + "]");
        this.reqAccountId = reqAccountId;
        this.inventoryId = inventoryId;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(String reqAccountNumber,
            String serialNumber, String energyCompany, String message) {
        super(message);
        this.reqAccountNumber = reqAccountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(int reqAccountId,
            int inventoryId, String energyCompany, String message) {
        super(message);
        this.reqAccountId = reqAccountId;
        this.inventoryId = inventoryId;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(String reqAccountNumber,
            String serialNumber, String energyCompany, Throwable cause) {
        super("Device already assigned to another account: requested accountNumber=[" + reqAccountNumber + "], serialNumber=[" + serialNumber + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.reqAccountNumber = reqAccountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(int reqAccountId,
            int inventoryId, String energyCompany, Throwable cause) {
        super("Device already assigned to another account: requested accountId=[" + reqAccountId + "], inventoryId=[" + inventoryId + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.reqAccountId = reqAccountId;
        this.inventoryId = inventoryId;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(String reqAccountNumber,
            String serialNumber, String energyCompany, String message,
            Throwable cause) {
        super(message, cause);
        this.reqAccountNumber = reqAccountNumber;
        this.serialNumber = serialNumber;
        this.energyCompany = energyCompany;
    }

    public StarsDeviceAlreadyAssignedException(int reqAccountId,
            int inventoryId, String energyCompany, String message,
            Throwable cause) {
        super(message, cause);
        this.reqAccountId = reqAccountId;
        this.inventoryId = inventoryId;
        this.energyCompany = energyCompany;
    }

    public String getReqAccountNumber() {
        return reqAccountNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getReqAccountId() {
        return reqAccountId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public String getEnergyCompany() {
        return energyCompany;
    }
}
