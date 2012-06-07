package com.cannontech.stars.xml.serialize;

public class StarsDeleteLMHardware {
    private int _inventoryID;
    private boolean _has_inventoryID;
    private boolean _deleteFromInventory;
    private boolean _has_deleteFromInventory;
    private boolean _deleteFromYukon;
    private boolean _has_deleteFromYukon;
    private java.util.Date _removeDate;


    public StarsDeleteLMHardware() {

    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteDeleteFromInventory()
    {
        this._has_deleteFromInventory= false;
    } //-- void deleteDeleteFromInventory() 

    /**
    **/
    public void deleteDeleteFromYukon()
    {
        this._has_deleteFromYukon= false;
    } //-- void deleteDeleteFromYukon() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
     * Returns the value of field 'deleteFromInventory'.
     * 
     * @return the value of field 'deleteFromInventory'.
    **/
    public boolean getDeleteFromInventory()
    {
        return this._deleteFromInventory;
    } //-- boolean getDeleteFromInventory() 

    /**
     * Returns the value of field 'deleteFromYukon'.
     * 
     * @return the value of field 'deleteFromYukon'.
    **/
    public boolean getDeleteFromYukon()
    {
        return this._deleteFromYukon;
    } //-- boolean getDeleteFromYukon() 

    /**
     * Returns the value of field 'inventoryID'.
     * 
     * @return the value of field 'inventoryID'.
    **/
    public int getInventoryID()
    {
        return this._inventoryID;
    } //-- int getInventoryID() 

    /**
     * Returns the value of field 'removeDate'.
     * 
     * @return the value of field 'removeDate'.
    **/
    public java.util.Date getRemoveDate()
    {
        return this._removeDate;
    } //-- java.util.Date getRemoveDate() 

    /**
    **/
    public boolean hasDeleteFromInventory()
    {
        return this._has_deleteFromInventory;
    } //-- boolean hasDeleteFromInventory() 

    /**
    **/
    public boolean hasDeleteFromYukon()
    {
        return this._has_deleteFromYukon;
    } //-- boolean hasDeleteFromYukon() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
     * Sets the value of field 'deleteFromInventory'.
     * 
     * @param deleteFromInventory the value of field
     * 'deleteFromInventory'.
    **/
    public void setDeleteFromInventory(boolean deleteFromInventory)
    {
        this._deleteFromInventory = deleteFromInventory;
        this._has_deleteFromInventory = true;
    } //-- void setDeleteFromInventory(boolean) 

    /**
     * Sets the value of field 'deleteFromYukon'.
     * 
     * @param deleteFromYukon the value of field 'deleteFromYukon'.
    **/
    public void setDeleteFromYukon(boolean deleteFromYukon)
    {
        this._deleteFromYukon = deleteFromYukon;
        this._has_deleteFromYukon = true;
    } //-- void setDeleteFromYukon(boolean) 

    /**
     * Sets the value of field 'inventoryID'.
     * 
     * @param inventoryID the value of field 'inventoryID'.
    **/
    public void setInventoryID(int inventoryID)
    {
        this._inventoryID = inventoryID;
        this._has_inventoryID = true;
    } //-- void setInventoryID(int) 

    /**
     * Sets the value of field 'removeDate'.
     * 
     * @param removeDate the value of field 'removeDate'.
    **/
    public void setRemoveDate(java.util.Date removeDate)
    {
        this._removeDate = removeDate;
    } //-- void setRemoveDate(java.util.Date) 

}
