package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.Date;
import java.util.List;

import com.cannontech.web.stars.dr.operator.hardware.service.impl.HardwareServiceImpl.HardwareHistory;

public class HardwareDto {
    private int inventoryId;
    private String deviceType;
    private String serialNumber;
    private String deviceLabel;
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
    private Integer deviceId = 0;
    private boolean twoWayLcr = false;
    private List<HardwareHistory> hardwareHistory;
    private String deviceName;
    private boolean isMct;
    private int energyCompanyId;
    
    public Integer getDeviceStatusEntryId() {
        return deviceStatusEntryId;
    }
    
    public void setDeviceStatusEntryId(Integer deviceStatusEntryId) {
        this.deviceStatusEntryId = deviceStatusEntryId;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getDeviceLabel() {
        return deviceLabel;
    }
    
    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
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

    public boolean isTwoWayLcr() {
        return twoWayLcr;
    }
    
    public void setIsTwoWayLcr(boolean twoWayLcr) {
        this.twoWayLcr = twoWayLcr;
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
        return getDeviceType().startsWith("MCT") || getDeviceType().equalsIgnoreCase("LCR-3102");
    }

    public String getDeviceName(){
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public boolean getIsMct() {
        return isMct;
    }

    public void setIsMct(boolean isMtc) {
        this.isMct = isMtc;
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
}