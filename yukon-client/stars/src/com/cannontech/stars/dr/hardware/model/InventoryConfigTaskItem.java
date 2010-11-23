package com.cannontech.stars.dr.hardware.model;

public class InventoryConfigTaskItem {
    public static enum Status {
        UNPROCESSED, SUCCESS, FAIL
    }

    private int inventoryConfigTaskId;
    private int inventoryId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
