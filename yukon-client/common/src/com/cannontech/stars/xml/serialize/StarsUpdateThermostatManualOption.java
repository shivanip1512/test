package com.cannontech.stars.xml.serialize;

public class StarsUpdateThermostatManualOption extends com.cannontech.stars.xml.serialize.StarsThermoManOpt {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private java.lang.String _inventoryIDs;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateThermostatManualOption() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOption()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

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
     * Returns the value of field 'inventoryIDs'.
     * 
     * @return the value of field 'inventoryIDs'.
    **/
    public java.lang.String getInventoryIDs()
    {
        return this._inventoryIDs;
    } //-- java.lang.String getInventoryIDs() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

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
     * Sets the value of field 'inventoryIDs'.
     * 
     * @param inventoryIDs the value of field 'inventoryIDs'.
    **/
    public void setInventoryIDs(java.lang.String inventoryIDs)
    {
        this._inventoryIDs = inventoryIDs;
    } //-- void setInventoryIDs(java.lang.String) 

}
