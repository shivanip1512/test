package com.cannontech.stars.dr.hardware.model;

import java.sql.Timestamp;

public class InventoryBase {
    private int inventoryId;
    private int accountId;
    private int installationCompanyId;
    private int categoryId;
    private Timestamp receiveDate; 
    private Timestamp installDate;
    private Timestamp removeDate;
    private String alternateTrackingNumber;
    private int voltageId;
    private String notes;
    private int deviceId;
    private String deviceLabel;
    private int currentStateId;
    
    public int getAccountId() {
        return accountId;
    }
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    public String getAlternateTrackingNumber() {
        return alternateTrackingNumber;
    }
    public void setAlternateTrackingNumber(String alternateTrackingNumber) {
        this.alternateTrackingNumber = alternateTrackingNumber;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getCurrentStateId() {
        return currentStateId;
    }
    public void setCurrentStateId(int currentStateId) {
        this.currentStateId = currentStateId;
    }
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceLabel() {
        return deviceLabel;
    }
    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }
    public int getInstallationCompanyId() {
        return installationCompanyId;
    }
    public void setInstallationCompanyId(int installationCompanyId) {
        this.installationCompanyId = installationCompanyId;
    }
    public Timestamp getInstallDate() {
        return installDate;
    }
    public void setInstallDate(Timestamp installDate) {
        this.installDate = installDate;
    }
    public int getInventoryId() {
        return inventoryId;
    }
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Timestamp getReceiveDate() {
        return receiveDate;
    }
    public void setReceiveDate(Timestamp receiveDate) {
        this.receiveDate = receiveDate;
    }
    public Timestamp getRemoveDate() {
        return removeDate;
    }
    public void setRemoveDate(Timestamp removeDate) {
        this.removeDate = removeDate;
    }
    public int getVoltageId() {
        return voltageId;
    }
    public void setVoltageId(int voltageId) {
        this.voltageId = voltageId;
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + accountId;
        result = PRIME * result + ((alternateTrackingNumber == null) ? 0 : alternateTrackingNumber.hashCode());
        result = PRIME * result + categoryId;
        result = PRIME * result + currentStateId;
        result = PRIME * result + deviceId;
        result = PRIME * result + ((deviceLabel == null) ? 0 : deviceLabel.hashCode());
        result = PRIME * result + ((installDate == null) ? 0 : installDate.hashCode());
        result = PRIME * result + installationCompanyId;
        result = PRIME * result + inventoryId;
        result = PRIME * result + ((notes == null) ? 0 : notes.hashCode());
        result = PRIME * result + ((receiveDate == null) ? 0 : receiveDate.hashCode());
        result = PRIME * result + ((removeDate == null) ? 0 : removeDate.hashCode());
        result = PRIME * result + voltageId;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InventoryBase other = (InventoryBase) obj;
        if (accountId != other.accountId)
            return false;
        if (alternateTrackingNumber == null) {
            if (other.alternateTrackingNumber != null)
                return false;
        } else if (!alternateTrackingNumber.equals(other.alternateTrackingNumber))
            return false;
        if (categoryId != other.categoryId)
            return false;
        if (currentStateId != other.currentStateId)
            return false;
        if (deviceId != other.deviceId)
            return false;
        if (deviceLabel == null) {
            if (other.deviceLabel != null)
                return false;
        } else if (!deviceLabel.equals(other.deviceLabel))
            return false;
        if (installDate == null) {
            if (other.installDate != null)
                return false;
        } else if (!installDate.equals(other.installDate))
            return false;
        if (installationCompanyId != other.installationCompanyId)
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        if (notes == null) {
            if (other.notes != null)
                return false;
        } else if (!notes.equals(other.notes))
            return false;
        if (receiveDate == null) {
            if (other.receiveDate != null)
                return false;
        } else if (!receiveDate.equals(other.receiveDate))
            return false;
        if (removeDate == null) {
            if (other.removeDate != null)
                return false;
        } else if (!removeDate.equals(other.removeDate))
            return false;
        if (voltageId != other.voltageId)
            return false;
        return true;
    }
    
}
