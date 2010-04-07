package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.Date;
import java.util.List;

import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.LMHardwareClass;
import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareServiceImpl.HardwareHistory;

public class HardwareDto {
    private Integer inventoryId;
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
    private LMHardwareClass hardwareClass;
    private HardwareType hardwareType;
    private int hardwareTypeEntryId;
    private String meterNumber;
    
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
    
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public void setHardwareHistory(List<HardwareHistory> hardwareHistory) {
        this.hardwareHistory = hardwareHistory;
    }

    public List<HardwareHistory> getHardwareHistory() {
        return hardwareHistory;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
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

    public void setHardwareClass(LMHardwareClass lmHardwareClass) {
        this.hardwareClass = lmHardwareClass;
    }
    
    public LMHardwareClass getHardwareClass() {
        return hardwareClass;
    }

    public void setHardwareType(HardwareType hardwareType) {
        this.hardwareType = hardwareType;
    }
    
    public HardwareType getHardwareType() {
        return hardwareType;
    }

    public int getHardwareTypeEntryId() {
        return hardwareTypeEntryId;
    }

    public void setHardwareTypeEntryId(int hardwareTypeEntryId) {
        this.hardwareTypeEntryId = hardwareTypeEntryId;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
}