package com.cannontech.stars.dr.hardware.model;

import com.cannontech.stars.dr.hardware.service.HardwareConfigService.Status;


public class InventoryConfigTaskItem {

    private InventoryConfigTask inventoryConfigTask;
    private int inventoryId;
    private Status status;

    public InventoryConfigTask getInventoryConfigTask() {
        return inventoryConfigTask;
    }

    public void setInventoryConfigTask(InventoryConfigTask inventoryConfigTask) {
        this.inventoryConfigTask = inventoryConfigTask;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InventoryConfigTaskItem [inventoryConfigTask=" + inventoryConfigTask +
        ", inventoryId=" + inventoryId + ", status=" + status + "]";
    }
}
