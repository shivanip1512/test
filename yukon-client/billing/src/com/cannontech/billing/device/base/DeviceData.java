package com.cannontech.billing.device.base;

/**
 * Class which holds basic data for a device.
 */
public class DeviceData {
    private String meterNumber = null;
    private String meterPositionNumber = null;
    private String address = null;
    private String accountNumber = null;
    private String paoName = null;

    public DeviceData() {
    }

    public DeviceData(String meterNumber, String meterPositionNumber, String address,
            String accountNumber, String paoName) {
        this.meterNumber = meterNumber;
        this.meterPositionNumber = meterPositionNumber;
        this.address = address;
        this.accountNumber = accountNumber;
        this.paoName = paoName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getMeterPositionNumber() {
        return meterPositionNumber;
    }

    public void setMeterPositionNumber(String meterPositionNumber) {
        this.meterPositionNumber = meterPositionNumber;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

}
