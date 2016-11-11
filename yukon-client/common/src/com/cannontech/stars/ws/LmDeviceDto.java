package com.cannontech.stars.ws;

import java.util.Date;

import org.springframework.core.style.ToStringCreator;

/**
 * This is a LM device data transfer object, used when adding/updating a
 * device to an account.
 * @author mmalekar
 */
public class LmDeviceDto {

    private String accountNumber;
    private String serialNumber;
    private String deviceType;
    private String deviceLabel;
    private Date fieldInstallDate;
    private String macAddress;
    private Integer deviceVendorUserId;
    private String serviceCompanyName;
    private Date fieldRemoveDate;
    private Throwable throwable;    

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    public Date getFieldInstallDate() {
        return fieldInstallDate;
    }

    public void setFieldInstallDate(Date fieldInstallDate) {
        this.fieldInstallDate = fieldInstallDate;
    }

    public String getServiceCompanyName() {
        return serviceCompanyName;
    }

    public void setServiceCompanyName(String serviceCompanyName) {
        this.serviceCompanyName = serviceCompanyName;
    }

    public Date getFieldRemoveDate() {
        return fieldRemoveDate;
    }

    public void setFieldRemoveDate(Date fieldRemoveDate) {
        this.fieldRemoveDate = fieldRemoveDate;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
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

    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("accountNumber", getAccountNumber());
        tsc.append("serialNumber", getSerialNumber());
        tsc.append("deviceType", getDeviceType());
        tsc.append("fieldInstallDate", getFieldInstallDate());
        tsc.append("serviceCompanyName", getServiceCompanyName());
        tsc.append("deviceLabel", getDeviceLabel());
        tsc.append("fieldRemoveDate", getFieldRemoveDate());
        tsc.append("macAddress", getMacAddress());
        tsc.append("deviceVendorUserId", getDeviceVendorUserId());
        return tsc.toString();
    }

}