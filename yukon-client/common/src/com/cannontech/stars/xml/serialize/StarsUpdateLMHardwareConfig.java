package com.cannontech.stars.xml.serialize;

import java.util.Vector;

public class StarsUpdateLMHardwareConfig {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private boolean _saveToBatch;

    /**
     * keeps track of state for field: _saveToBatch
    **/
    private boolean _has_saveToBatch;

    private boolean _saveConfigOnly;

    /**
     * keeps track of state for field: _saveConfigOnly
    **/
    private boolean _has_saveConfigOnly;

    private java.util.Vector<StarsLMHardwareConfig> _starsLMHardwareConfigList;

    private StarsLMConfiguration _starsLMConfiguration;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateLMHardwareConfig() {
        super();
        _starsLMHardwareConfigList = new Vector<StarsLMHardwareConfig>();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsLMHardwareConfig
    **/
    public void addStarsLMHardwareConfig(StarsLMHardwareConfig vStarsLMHardwareConfig)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareConfigList.addElement(vStarsLMHardwareConfig);
    } //-- void addStarsLMHardwareConfig(StarsLMHardwareConfig) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareConfig
    **/
    public void addStarsLMHardwareConfig(int index, StarsLMHardwareConfig vStarsLMHardwareConfig)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsLMHardwareConfigList.insertElementAt(vStarsLMHardwareConfig, index);
    } //-- void addStarsLMHardwareConfig(int, StarsLMHardwareConfig) 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public void deleteSaveConfigOnly()
    {
        this._has_saveConfigOnly= false;
    } //-- void deleteSaveConfigOnly() 

    /**
    **/
    public void deleteSaveToBatch()
    {
        this._has_saveToBatch= false;
    } //-- void deleteSaveToBatch() 

    /**
    **/
    public java.util.Enumeration<StarsLMHardwareConfig> enumerateStarsLMHardwareConfig()
    {
        return _starsLMHardwareConfigList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareConfig() 

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
     * Returns the value of field 'saveConfigOnly'.
     * 
     * @return the value of field 'saveConfigOnly'.
    **/
    public boolean getSaveConfigOnly()
    {
        return this._saveConfigOnly;
    } //-- boolean getSaveConfigOnly() 

    /**
     * Returns the value of field 'saveToBatch'.
     * 
     * @return the value of field 'saveToBatch'.
    **/
    public boolean getSaveToBatch()
    {
        return this._saveToBatch;
    } //-- boolean getSaveToBatch() 

    /**
     * Returns the value of field 'starsLMConfiguration'.
     * 
     * @return the value of field 'starsLMConfiguration'.
    **/
    public StarsLMConfiguration getStarsLMConfiguration()
    {
        return this._starsLMConfiguration;
    } //-- StarsLMConfiguration getStarsLMConfiguration() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareConfig getStarsLMHardwareConfig(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareConfigList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsLMHardwareConfig) _starsLMHardwareConfigList.elementAt(index);
    } //-- StarsLMHardwareConfig getStarsLMHardwareConfig(int) 

    /**
    **/
    public StarsLMHardwareConfig[] getStarsLMHardwareConfig()
    {
        int size = _starsLMHardwareConfigList.size();
        StarsLMHardwareConfig[] mArray = new StarsLMHardwareConfig[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsLMHardwareConfig) _starsLMHardwareConfigList.elementAt(index);
        }
        return mArray;
    } //-- StarsLMHardwareConfig[] getStarsLMHardwareConfig() 

    /**
    **/
    public int getStarsLMHardwareConfigCount()
    {
        return _starsLMHardwareConfigList.size();
    } //-- int getStarsLMHardwareConfigCount() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

    /**
    **/
    public boolean hasSaveConfigOnly()
    {
        return this._has_saveConfigOnly;
    } //-- boolean hasSaveConfigOnly() 

    /**
    **/
    public boolean hasSaveToBatch()
    {
        return this._has_saveToBatch;
    } //-- boolean hasSaveToBatch() 

    /**
    **/
    public void removeAllStarsLMHardwareConfig()
    {
        _starsLMHardwareConfigList.removeAllElements();
    } //-- void removeAllStarsLMHardwareConfig() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsLMHardwareConfig removeStarsLMHardwareConfig(int index)
    {
        java.lang.Object obj = _starsLMHardwareConfigList.elementAt(index);
        _starsLMHardwareConfigList.removeElementAt(index);
        return (StarsLMHardwareConfig) obj;
    } //-- StarsLMHardwareConfig removeStarsLMHardwareConfig(int) 

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
     * Sets the value of field 'saveConfigOnly'.
     * 
     * @param saveConfigOnly the value of field 'saveConfigOnly'.
    **/
    public void setSaveConfigOnly(boolean saveConfigOnly)
    {
        this._saveConfigOnly = saveConfigOnly;
        this._has_saveConfigOnly = true;
    } //-- void setSaveConfigOnly(boolean) 

    /**
     * Sets the value of field 'saveToBatch'.
     * 
     * @param saveToBatch the value of field 'saveToBatch'.
    **/
    public void setSaveToBatch(boolean saveToBatch)
    {
        this._saveToBatch = saveToBatch;
        this._has_saveToBatch = true;
    } //-- void setSaveToBatch(boolean) 

    /**
     * Sets the value of field 'starsLMConfiguration'.
     * 
     * @param starsLMConfiguration the value of field
     * 'starsLMConfiguration'.
    **/
    public void setStarsLMConfiguration(StarsLMConfiguration starsLMConfiguration)
    {
        this._starsLMConfiguration = starsLMConfiguration;
    } //-- void setStarsLMConfiguration(StarsLMConfiguration) 

    /**
     * 
     * 
     * @param index
     * @param vStarsLMHardwareConfig
    **/
    public void setStarsLMHardwareConfig(int index, StarsLMHardwareConfig vStarsLMHardwareConfig)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsLMHardwareConfigList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsLMHardwareConfigList.setElementAt(vStarsLMHardwareConfig, index);
    } //-- void setStarsLMHardwareConfig(int, StarsLMHardwareConfig) 

    /**
     * 
     * 
     * @param starsLMHardwareConfigArray
    **/
    public void setStarsLMHardwareConfig(StarsLMHardwareConfig[] starsLMHardwareConfigArray)
    {
        //-- copy array
        _starsLMHardwareConfigList.removeAllElements();
        for (int i = 0; i < starsLMHardwareConfigArray.length; i++) {
            _starsLMHardwareConfigList.addElement(starsLMHardwareConfigArray[i]);
        }
    } //-- void setStarsLMHardwareConfig(StarsLMHardwareConfig) 

}
