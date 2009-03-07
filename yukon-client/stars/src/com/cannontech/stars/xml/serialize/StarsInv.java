package com.cannontech.stars.xml.serialize;

import java.util.Date;

public abstract class StarsInv {
    private int inventoryID;
    private boolean hasInventoryID;
    private int deviceID;
    private boolean hasDeviceID;
    private String category;
    private DeviceType deviceType;
    private String deviceLabel;
    private InstallationCompany installationCompany;
    private Date receiveDate;
    private Date installDate;
    private Date removeDate;
    private String altTrackingNumber;
    private Voltage voltage;
    private String notes;
    private DeviceStatus deviceStatus;
    private String installationNotes;
    private StarsLMHardwareHistory starsLMHardwareHistory;
    private LMHardware LMHardware;
    private MCT _MCT;
    private String meterNumber = "";
    private TwoWayLcrSetupInfoDto twoWayLcrSetupInfoDto;

    public StarsInv() {
        
    } 
    
    public void setTwoWayLcrSetupInfoDto(TwoWayLcrSetupInfoDto twoWayLcrSetupInfoDto) {
		this.twoWayLcrSetupInfoDto = twoWayLcrSetupInfoDto;
	}
    public TwoWayLcrSetupInfoDto getTwoWayLcrSetupInfoDto() {
		return twoWayLcrSetupInfoDto;
	}

    public void deleteDeviceID() {
        this.hasDeviceID = false;
    } 

    public void deleteInventoryID() {
        this.hasInventoryID = false;
    } 

    public String getAltTrackingNumber() {
        return this.altTrackingNumber;
    } 

    public String getCategory() {
        return this.category;
    } 

    public int getDeviceID() {
        return this.deviceID;
    }

    public String getDeviceLabel() {
        return this.deviceLabel;
    }

    public DeviceStatus getDeviceStatus() {
        return this.deviceStatus;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public Date getInstallDate() {
        return this.installDate;
    }

    public InstallationCompany getInstallationCompany() {
        return this.installationCompany;
    }

    public String getInstallationNotes() {
        return this.installationNotes;
    } 

    public int getInventoryID() {
        return this.inventoryID;
    }

    public LMHardware getLMHardware() {
        return this.LMHardware;
    }

    public MCT getMCT() {
        return this._MCT;
    }

    public String getNotes() {
        return this.notes;
    }

    public Date getReceiveDate() {
        return this.receiveDate;
    }

    public Date getRemoveDate() {
        return this.removeDate;
    }

    public StarsLMHardwareHistory getStarsLMHardwareHistory() {
        return this.starsLMHardwareHistory;
    }

    public Voltage getVoltage() {
        return this.voltage;
    }

    public boolean hasDeviceID() {
        return this.hasDeviceID;
    }

    public boolean hasInventoryID() {
        return this.hasInventoryID;
    }

    public void setAltTrackingNumber(String altTrackingNumber) {
        this.altTrackingNumber = altTrackingNumber;
    } 

    public void setCategory(java.lang.String category) {
        this.category = category;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
        this.hasDeviceID = true;
    }

    public void setDeviceLabel(java.lang.String deviceLabel) {
        this.deviceLabel = deviceLabel;
    } 

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    } 

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public void setInstallationCompany(InstallationCompany installationCompany) {
        this.installationCompany = installationCompany;
    }

    public void setInstallationNotes(String installationNotes) {
        this.installationNotes = installationNotes;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
        this.hasInventoryID = true;
    }

    public void setLMHardware(LMHardware LMHardware) {
        this.LMHardware = LMHardware;
    } 

    public void setMCT(MCT MCT) {
        this._MCT = MCT;
    } 

    public void setNotes(String notes) {
        this.notes = notes;
    } 

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public void setRemoveDate(Date removeDate) {
        this.removeDate = removeDate;
    }

    public void setStarsLMHardwareHistory(StarsLMHardwareHistory starsLMHardwareHistory) {
        this.starsLMHardwareHistory = starsLMHardwareHistory;
    }

    public void setVoltage(Voltage voltage) {
        this.voltage = voltage;
    } 

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

}
