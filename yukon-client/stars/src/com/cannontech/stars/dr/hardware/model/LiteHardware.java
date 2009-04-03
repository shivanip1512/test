package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

public class LiteHardware {

    private Integer inventoryId;
    private String deviceLabel;
    private String serialNumber;

    public LiteHardware(Integer inventoryId, String deviceLabel,
            String serialNumber) {
        this.inventoryId = inventoryId;
        this.deviceLabel = deviceLabel;
        this.serialNumber = serialNumber;
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
}
