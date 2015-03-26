package com.cannontech.stars.database.data.lite;

import com.cannontech.database.data.lite.LiteBase;

public class LiteInventoryBase extends LiteBase {
    
    private int accountID;
    private int categoryID;
    private int installationCompanyID;
    private long receiveDate;
    private long installDate;
    private long removeDate;
    private String alternateTrackingNumber;
    private int voltageID;
    private String notes;
    private int deviceID;
    private String deviceLabel;
    private int currentStateID;
    private String manufacturerSerialNumber;
    private int energyCompanyId;
    private boolean extended = false;
    
    public LiteInventoryBase() { }
    
    public LiteInventoryBase(int inventoryId) {
        setInventoryID(inventoryId);
    }
    
    public int getInventoryID() {
        return getLiteID();
    }
    
    public void setInventoryID(int inventoryId) {
        setLiteID(inventoryId);
    }
    
    public int getAccountID() {
        return accountID;
    }
    
    public String getAlternateTrackingNumber() {
        return alternateTrackingNumber;
    }
    
    public int getCategoryID() {
        return categoryID;
    }
        
    public String getDeviceLabel() {
        return deviceLabel;
    }
    
    public int getInstallationCompanyID() {
        return installationCompanyID;
    }
    
    public long getInstallDate() {
        return installDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public long getReceiveDate() {
        return receiveDate;
    }
    
    public long getRemoveDate() {
        return removeDate;
    }
    
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    
    public void setAlternateTrackingNumber(String alternateTrackingNumber) {
        this.alternateTrackingNumber = alternateTrackingNumber;
    }
    
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    public void setInstallationCompanyID(int installationCompanyID) {
        this.installationCompanyID = installationCompanyID;
    }

    public void setInstallDate(long installDate) {
        this.installDate = installDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setReceiveDate(long receiveDate) {
        this.receiveDate = receiveDate;
    }

    public void setRemoveDate(long removeDate) {
        this.removeDate = removeDate;
    }

    public int getVoltageID() {
        return voltageID;
    }
       
    public void setVoltageID(int voltageID) {
        this.voltageID = voltageID;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public int getCurrentStateID() {
        return currentStateID;
    }

    public void setCurrentStateID(int currentStateID) {
        this.currentStateID = currentStateID;
    }
    
    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }
    
    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }

}