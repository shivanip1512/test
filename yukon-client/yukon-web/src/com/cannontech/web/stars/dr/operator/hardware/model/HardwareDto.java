package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.Date;
import java.util.List;

import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareServiceImpl.HardwareHistory;

public class HardwareDto {
    private int inventoryId;
    private Integer deviceId = 0;
    private int energyCompanyId;
    private String displayName;
    private String displayLabel;
    private String displayType;
    private String categoryName;
    private String serialNumber;
    private String altTrackingNumber;
    private Integer voltageEntryId;
    private Date fieldInstallDate;
    private Date fieldReceiveDate;
    private Date fieldRemoveDate;
    private String deviceNotes;
    private Integer routeId;
    private Integer serviceCompanyId;
    private Integer warehouseId;
    private String installNotes;
    private Integer deviceStatusEntryId;
    private Integer originalDeviceStatusEntryId;
    private String twoWayDeviceName;
    private List<HardwareHistory> hardwareHistory;
    private boolean twoWayLcr = false;
    private boolean mct = false;
    private boolean thermostat = false;
    
    public Integer getDeviceStatusEntryId() {
        return deviceStatusEntryId;
    }
    
    public void setDeviceStatusEntryId(Integer deviceStatusEntryId) {
        this.deviceStatusEntryId = deviceStatusEntryId;
    }
    
    public String getDisplayType() {
        return displayType;
    }
    
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }
    
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getDisplayLabel() {
        return displayLabel;
    }
    
    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }
    
    public String getAltTrackingNumber() {
        return altTrackingNumber;
    }
    
    public void setAltTrackingNumber(String altTrackingNumber) {
        this.altTrackingNumber = altTrackingNumber;
    }
    
    public Integer getVoltageEntryId() {
        return voltageEntryId;
    }
    
    public void setVoltageEntryId(Integer voltageEntryId) {
        this.voltageEntryId = voltageEntryId;
    }
    
    public Date getFieldInstallDate() {
        return fieldInstallDate;
    }
    
    public void setFieldInstallDate(Date fieldInstallDate) {
        this.fieldInstallDate = fieldInstallDate;
    }
    
    public Date getFieldReceiveDate() {
        return fieldReceiveDate;
    }
    
    public void setFieldReceiveDate(Date fieldReceiveDate) {
        this.fieldReceiveDate = fieldReceiveDate;
    }
    
    public Date getFieldRemoveDate() {
        return fieldRemoveDate;
    }
    
    public void setFieldRemoveDate(Date fieldRemoveDate) {
        this.fieldRemoveDate = fieldRemoveDate;
    }
    
    public String getDeviceNotes() {
        return deviceNotes;
    }
    
    public void setDeviceNotes(String deviceNotes) {
        this.deviceNotes = deviceNotes;
    }
    
    public Integer getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    
    public Integer getServiceCompanyId() {
        return serviceCompanyId;
    }
    
    public void setServiceCompanyId(Integer serviceCompanyId) {
        this.serviceCompanyId = serviceCompanyId;
    }
    
    public Integer getWarehouseId() {
        return warehouseId;
    }
    
    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    public String getInstallNotes() {
        return installNotes;
    }
    
    public void setInstallNotes(String installNotes) {
        this.installNotes = installNotes;
    }

    public String getTwoWayDeviceName() {
        return twoWayDeviceName;
    }
    
    public void setTwoWayDeviceName(String twoWayDeviceName) {
        this.twoWayDeviceName = twoWayDeviceName;
    }
    
    public Integer getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Integer deviceId){
        this.deviceId = deviceId;
    }

    public void setHardwareHistory(List<HardwareHistory> hardwareHistory) {
        this.hardwareHistory = hardwareHistory;
    }

    public List<HardwareHistory> getHardwareHistory() {
        return hardwareHistory;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public boolean hasDevice() {
        return getDisplayType().startsWith("MCT") || getDisplayType().equalsIgnoreCase("LCR-3102");
    }

    public String getDisplayName(){
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public Integer getOriginalDeviceStatusEntryId() {
        return originalDeviceStatusEntryId;
    }

    public void setOriginalDeviceStatusEntryId(Integer originalDeviceStatusEntryId) {
        this.originalDeviceStatusEntryId = originalDeviceStatusEntryId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setThermostat(boolean thermostat) {
        this.thermostat = thermostat;
    }
    
    public boolean isThermostat() {
        return thermostat;
    }
    
    public boolean isTwoWayLcr() {
        return twoWayLcr;
    }
    
    public void setTwoWayLcr(boolean twoWayLcr) {
        this.twoWayLcr = twoWayLcr;
    }
    
    public boolean isMct() {
        return mct;
    }

    public void setMct(boolean mct) {
        this.mct = mct;
    }
}