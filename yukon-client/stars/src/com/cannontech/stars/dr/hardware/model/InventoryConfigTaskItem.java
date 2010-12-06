package com.cannontech.stars.dr.hardware.model;

public class InventoryConfigTaskItem {
    public static enum Status {
        UNPROCESSED, SUCCESS, FAIL
    }

    private int inventoryConfigTaskId;
    private int inventoryId;
    private boolean sendInService;
    private Status status;

    public int getInventoryConfigTaskId() {
        return inventoryConfigTaskId;
    }

    public void setInventoryConfigTaskId(int inventoryConfigTaskId) {
        this.inventoryConfigTaskId = inventoryConfigTaskId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public boolean isSendInService() {
        return sendInService;
    }

    public void setSendInService(boolean sendInService) {
        this.sendInService = sendInService;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InventoryConfigTaskItem [inventoryConfigTaskId=" + inventoryConfigTaskId +
        ", inventoryId=" + inventoryId +", sendInService=" + sendInService + ", status=" + status + "]";
    }
}
