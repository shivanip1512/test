package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

public class HardwareSummary {

    private Integer inventoryId;
    private String deviceLabel;
    private String serialNumber;
    private int deviceTypeId;

    public HardwareSummary(Integer inventoryId, String deviceLabel,
            String serialNumber, int deviceTypeId) {
        this.inventoryId = inventoryId;
        this.deviceLabel = deviceLabel;
        this.serialNumber = serialNumber;
        this.deviceTypeId = deviceTypeId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDisplayName() {
        if (StringUtils.isBlank(deviceLabel)) {
            return serialNumber;
        }

        return deviceLabel;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }
}
