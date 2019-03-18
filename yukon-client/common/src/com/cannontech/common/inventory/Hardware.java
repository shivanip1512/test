package com.cannontech.common.inventory;

import java.util.Date;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.LazyList;

public class Hardware {
    
    private int accountId;
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
    private int routeId = 0;
    private Integer serviceCompanyId;
    private Integer warehouseId;
    private String installNotes;
    private Integer deviceStatusEntryId;
    private Integer originalDeviceStatusEntryId;
    private List<HardwareHistory> hardwareHistory;
    private HardwareType hardwareType;
    private int hardwareTypeEntryId;
    private int destinationEndPointId;
    private int nodeId;
    
    /* LCR-3102 fields */
    private String twoWayDeviceName;
    private boolean creatingNewTwoWayDevice;
    
    /* Meter fields */
    private String meterNumber;
    private List<SwitchAssignment> switchAssignments = LazyList.ofInstance(SwitchAssignment.class);
    
    /* ZigBee fields */
    private String installCode;
    private Integer gatewayId;
    private String firmwareVersion;
    private int commissionedId;
    
    /* ZigBee/Itron/Honeywell fields */
    private String macAddress;
    
    /* Honeywell field */
    private Integer deviceVendorUserId;
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

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
    
    public int getRouteId() {
        return routeId;
    }
    
    public void setRouteId(int routeId) {
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

    public String getInstallCode() {
        return installCode;
    }
    
    public void setInstallCode(String installCode) {
        this.installCode = installCode;
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

    public List<SwitchAssignment> getSwitchAssignments() {
        return switchAssignments;
    }
    
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public void setCommissionedId(int commissionedId) {
        this.commissionedId = commissionedId;
    }
    
    public int getCommissionedId() {
        return commissionedId;
    }
    
    public YukonPao getYukonPao() {
        PaoType paoType = PaoType.getForDbString(displayType);
        return new PaoIdentifier(deviceId, paoType);
    }

    public void setCreatingNewTwoWayDevice(boolean creatingNewTwoWayDevice) {
        this.creatingNewTwoWayDevice = creatingNewTwoWayDevice;
    }

    public boolean isCreatingNewTwoWayDevice() {
        return creatingNewTwoWayDevice;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Integer getGatewayId() {
        return gatewayId;
    }
    
    public int getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
    
    public void setDestinationEndPointId(int destinationEndPointId) {
        this.destinationEndPointId = destinationEndPointId;
    }
    
    public int getDestinationEndPointId() {
        return destinationEndPointId;
    }

    public Integer getDeviceVendorUserId() {
        return deviceVendorUserId;
    }

    public void setDeviceVendorUserId(Integer deviceVendorUserId) {
        this.deviceVendorUserId = deviceVendorUserId;
    }

}