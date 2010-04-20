package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

public class HardwareSummary {

    private Integer inventoryId;
    private String deviceLabel;
    private String serialNumber;
    private HardwareType hardwareType;

    public HardwareSummary(Integer inventoryId, String deviceLabel,
            String serialNumber, HardwareType hardwareType) {
        this.inventoryId = inventoryId;
        this.deviceLabel = deviceLabel;
        this.serialNumber = serialNumber;
        this.hardwareType = hardwareType;
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

    public HardwareType getHardwareType() {
        return hardwareType;
    }

    public int getNumRelays() {
        return hardwareType.getNumRelays();
    }
}
