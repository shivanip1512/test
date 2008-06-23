package com.cannontech.stars.xml.serialize;

public class SULMProgram {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _programID;

    /**
     * keeps track of state for field: _programID
    **/
    private boolean _has_programID;

    private int _applianceCategoryID;

    /**
     * keeps track of state for field: _applianceCategoryID
    **/
    private boolean _has_applianceCategoryID;

    private int _addressingGroupID;

    /**
     * keeps track of state for field: _addressingGroupID
    **/
    private boolean _has_addressingGroupID;

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private int _loadNumber;

    /**
     * keeps track of state for field: _loadNumber
    **/
    private boolean _has_loadNumber;


      //----------------/
     //- Constructors -/
    //----------------/

    public SULMProgram() {
        super();
    } //-- com.cannontech.stars.xml.serialize.SULMProgram()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteAddressingGroupID()
    {
        this._has_addressingGroupID= false;
    } //-- void deleteAddressingGroupID() 

    /**
    **/
    public void deleteApplianceCategoryID()
    {
        this._has_applianceCategoryID= false;
    } //-- void deleteApplianceCategoryID() 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public void deleteLoadNumber()
    {
        this._has_loadNumber= false;
    } //-- void deleteLoadNumber() 

    /**
    **/
    public void deleteProgramID()
    {
        this._has_programID= false;
    } //-- void deleteProgramID() 

    /**
     * Returns the value of field 'addressingGroupID'.
     * 
     * @return the value of field 'addressingGroupID'.
    **/
    public int getAddressingGroupID()
    {
        return this._addressingGroupID;
    } //-- int getAddressingGroupID() 

    /**
     * Returns the value of field 'applianceCategoryID'.
     * 
     * @return the value of field 'applianceCategoryID'.
    **/
    public int getApplianceCategoryID()
    {
        return this._applianceCategoryID;
    } //-- int getApplianceCategoryID() 

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
     * Returns the value of field 'loadNumber'.
     * 
     * @return the value of field 'loadNumber'.
    **/
    public int getLoadNumber()
    {
        return this._loadNumber;
    } //-- int getLoadNumber() 

    /**
     * Returns the value of field 'programID'.
     * 
     * @return the value of field 'programID'.
    **/
    public int getProgramID()
    {
        return this._programID;
    } //-- int getProgramID() 

    /**
    **/
    public boolean hasAddressingGroupID()
    {
        return this._has_addressingGroupID;
    } //-- boolean hasAddressingGroupID() 

    /**
    **/
    public boolean hasApplianceCategoryID()
    {
        return this._has_applianceCategoryID;
    } //-- boolean hasApplianceCategoryID() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
    **/
    public boolean hasLoadNumber()
    {
        return this._has_loadNumber;
    } //-- boolean hasLoadNumber() 

    /**
    **/
    public boolean hasProgramID()
    {
        return this._has_programID;
    } //-- boolean hasProgramID() 

    /**
     * Sets the value of field 'addressingGroupID'.
     * 
     * @param addressingGroupID the value of field
     * 'addressingGroupID'.
    **/
    public void setAddressingGroupID(int addressingGroupID)
    {
        this._addressingGroupID = addressingGroupID;
        this._has_addressingGroupID = true;
    } //-- void setAddressingGroupID(int) 

    /**
     * Sets the value of field 'applianceCategoryID'.
     * 
     * @param applianceCategoryID the value of field
     * 'applianceCategoryID'.
    **/
    public void setApplianceCategoryID(int applianceCategoryID)
    {
        this._applianceCategoryID = applianceCategoryID;
        this._has_applianceCategoryID = true;
    } //-- void setApplianceCategoryID(int) 

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
     * Sets the value of field 'loadNumber'.
     * 
     * @param loadNumber the value of field 'loadNumber'.
    **/
    public void setLoadNumber(int loadNumber)
    {
        this._loadNumber = loadNumber;
        this._has_loadNumber = true;
    } //-- void setLoadNumber(int) 

    /**
     * Sets the value of field 'programID'.
     * 
     * @param programID the value of field 'programID'.
    **/
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    } //-- void setProgramID(int) 

}
