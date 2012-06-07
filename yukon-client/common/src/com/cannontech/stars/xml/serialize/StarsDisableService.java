package com.cannontech.stars.xml.serialize;

public class StarsDisableService {
    private int _inventoryID;
    private boolean _has_inventoryID;

    public StarsDisableService() {
        
    }

    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } 

    public int getInventoryID()
    {
        return this._inventoryID;
    } 

    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } 

    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    }

}
