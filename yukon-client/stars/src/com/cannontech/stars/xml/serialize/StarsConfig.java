package com.cannontech.stars.xml.serialize;

public class StarsConfig {
    private int inventoryID;
    private boolean hasInventoryID;

    public StarsConfig() {
       
    }

    public void deleteInventoryID() {
        this.hasInventoryID = false;
    } 

    public int getInventoryID() {
        return this.inventoryID;
    }

    public boolean hasInventoryID() {
        return this.hasInventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
        this.hasInventoryID = true;
    }

}
