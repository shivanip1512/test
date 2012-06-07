package com.cannontech.stars.xml.serialize;

import java.util.Vector;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsThermostatDynamicData {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.Date _lastUpdatedTime;

    private int _displayedTemperature;

    /**
     * keeps track of state for field: _displayedTemperature
    **/
    private boolean _has_displayedTemperature;

    private java.lang.String _displayedTempUnit;

    private com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings _fan;

    private com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings _mode;

    private int _coolSetpoint;

    /**
     * keeps track of state for field: _coolSetpoint
    **/
    private boolean _has_coolSetpoint;

    private int _heatSetpoint;

    /**
     * keeps track of state for field: _heatSetpoint
    **/
    private boolean _has_heatSetpoint;

    private boolean _setpointHold;

    /**
     * keeps track of state for field: _setpointHold
    **/
    private boolean _has_setpointHold;

    private int _lowerCoolSetpointLimit;

    /**
     * keeps track of state for field: _lowerCoolSetpointLimit
    **/
    private boolean _has_lowerCoolSetpointLimit;

    private int _upperHeatSetpointLimit;

    /**
     * keeps track of state for field: _upperHeatSetpointLimit
    **/
    private boolean _has_upperHeatSetpointLimit;

    private java.util.Vector _infoStringList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatDynamicData() {
        super();
        _infoStringList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatDynamicData()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vInfoString
    **/
    public void addInfoString(java.lang.String vInfoString)
        throws java.lang.IndexOutOfBoundsException
    {
        _infoStringList.addElement(vInfoString);
    } //-- void addInfoString(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vInfoString
    **/
    public void addInfoString(int index, java.lang.String vInfoString)
        throws java.lang.IndexOutOfBoundsException
    {
        _infoStringList.insertElementAt(vInfoString, index);
    } //-- void addInfoString(int, java.lang.String) 

    /**
    **/
    public void deleteCoolSetpoint()
    {
        this._has_coolSetpoint= false;
    } //-- void deleteCoolSetpoint() 

    /**
    **/
    public void deleteDisplayedTemperature()
    {
        this._has_displayedTemperature= false;
    } //-- void deleteDisplayedTemperature() 

    /**
    **/
    public void deleteHeatSetpoint()
    {
        this._has_heatSetpoint= false;
    } //-- void deleteHeatSetpoint() 

    /**
    **/
    public void deleteLowerCoolSetpointLimit()
    {
        this._has_lowerCoolSetpointLimit= false;
    } //-- void deleteLowerCoolSetpointLimit() 

    /**
    **/
    public void deleteSetpointHold()
    {
        this._has_setpointHold= false;
    } //-- void deleteSetpointHold() 

    /**
    **/
    public void deleteUpperHeatSetpointLimit()
    {
        this._has_upperHeatSetpointLimit= false;
    } //-- void deleteUpperHeatSetpointLimit() 

    /**
    **/
    public java.util.Enumeration enumerateInfoString()
    {
        return _infoStringList.elements();
    } //-- java.util.Enumeration enumerateInfoString() 

    /**
     * Returns the value of field 'coolSetpoint'.
     * 
     * @return the value of field 'coolSetpoint'.
    **/
    public int getCoolSetpoint()
    {
        return this._coolSetpoint;
    } //-- int getCoolSetpoint() 

    /**
     * Returns the value of field 'displayedTempUnit'.
     * 
     * @return the value of field 'displayedTempUnit'.
    **/
    public java.lang.String getDisplayedTempUnit()
    {
        return this._displayedTempUnit;
    } //-- java.lang.String getDisplayedTempUnit() 

    /**
     * Returns the value of field 'displayedTemperature'.
     * 
     * @return the value of field 'displayedTemperature'.
    **/
    public int getDisplayedTemperature()
    {
        return this._displayedTemperature;
    } //-- int getDisplayedTemperature() 

    /**
     * Returns the value of field 'fan'.
     * 
     * @return the value of field 'fan'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings getFan()
    {
        return this._fan;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings getFan() 

    /**
     * Returns the value of field 'heatSetpoint'.
     * 
     * @return the value of field 'heatSetpoint'.
    **/
    public int getHeatSetpoint()
    {
        return this._heatSetpoint;
    } //-- int getHeatSetpoint() 

    /**
     * 
     * 
     * @param index
    **/
    public java.lang.String getInfoString(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _infoStringList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_infoStringList.elementAt(index);
    } //-- java.lang.String getInfoString(int) 

    /**
    **/
    public java.lang.String[] getInfoString()
    {
        int size = _infoStringList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_infoStringList.elementAt(index);
        }
        return mArray;
    } //-- java.lang.String[] getInfoString() 

    /**
    **/
    public int getInfoStringCount()
    {
        return _infoStringList.size();
    } //-- int getInfoStringCount() 

    /**
     * Returns the value of field 'lastUpdatedTime'.
     * 
     * @return the value of field 'lastUpdatedTime'.
    **/
    public java.util.Date getLastUpdatedTime()
    {
        return this._lastUpdatedTime;
    } //-- java.util.Date getLastUpdatedTime() 

    /**
     * Returns the value of field 'lowerCoolSetpointLimit'.
     * 
     * @return the value of field 'lowerCoolSetpointLimit'.
    **/
    public int getLowerCoolSetpointLimit()
    {
        return this._lowerCoolSetpointLimit;
    } //-- int getLowerCoolSetpointLimit() 

    /**
     * Returns the value of field 'mode'.
     * 
     * @return the value of field 'mode'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings getMode()
    {
        return this._mode;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings getMode() 

    /**
     * Returns the value of field 'setpointHold'.
     * 
     * @return the value of field 'setpointHold'.
    **/
    public boolean getSetpointHold()
    {
        return this._setpointHold;
    } //-- boolean getSetpointHold() 

    /**
     * Returns the value of field 'upperHeatSetpointLimit'.
     * 
     * @return the value of field 'upperHeatSetpointLimit'.
    **/
    public int getUpperHeatSetpointLimit()
    {
        return this._upperHeatSetpointLimit;
    } //-- int getUpperHeatSetpointLimit() 

    /**
    **/
    public boolean hasCoolSetpoint()
    {
        return this._has_coolSetpoint;
    } //-- boolean hasCoolSetpoint() 

    /**
    **/
    public boolean hasDisplayedTemperature()
    {
        return this._has_displayedTemperature;
    } //-- boolean hasDisplayedTemperature() 

    /**
    **/
    public boolean hasHeatSetpoint()
    {
        return this._has_heatSetpoint;
    } //-- boolean hasHeatSetpoint() 

    /**
    **/
    public boolean hasLowerCoolSetpointLimit()
    {
        return this._has_lowerCoolSetpointLimit;
    } //-- boolean hasLowerCoolSetpointLimit() 

    /**
    **/
    public boolean hasSetpointHold()
    {
        return this._has_setpointHold;
    } //-- boolean hasSetpointHold() 

    /**
    **/
    public boolean hasUpperHeatSetpointLimit()
    {
        return this._has_upperHeatSetpointLimit;
    } //-- boolean hasUpperHeatSetpointLimit() 

    /**
    **/
    public void removeAllInfoString()
    {
        _infoStringList.removeAllElements();
    } //-- void removeAllInfoString() 

    /**
     * 
     * 
     * @param index
    **/
    public java.lang.String removeInfoString(int index)
    {
        java.lang.Object obj = _infoStringList.elementAt(index);
        _infoStringList.removeElementAt(index);
        return (String)obj;
    } //-- java.lang.String removeInfoString(int) 

    /**
     * Sets the value of field 'coolSetpoint'.
     * 
     * @param coolSetpoint the value of field 'coolSetpoint'.
    **/
    public void setCoolSetpoint(int coolSetpoint)
    {
        this._coolSetpoint = coolSetpoint;
        this._has_coolSetpoint = true;
    } //-- void setCoolSetpoint(int) 

    /**
     * Sets the value of field 'displayedTempUnit'.
     * 
     * @param displayedTempUnit the value of field
     * 'displayedTempUnit'.
    **/
    public void setDisplayedTempUnit(java.lang.String displayedTempUnit)
    {
        this._displayedTempUnit = displayedTempUnit;
    } //-- void setDisplayedTempUnit(java.lang.String) 

    /**
     * Sets the value of field 'displayedTemperature'.
     * 
     * @param displayedTemperature the value of field
     * 'displayedTemperature'.
    **/
    public void setDisplayedTemperature(int displayedTemperature)
    {
        this._displayedTemperature = displayedTemperature;
        this._has_displayedTemperature = true;
    } //-- void setDisplayedTemperature(int) 

    /**
     * Sets the value of field 'fan'.
     * 
     * @param fan the value of field 'fan'.
    **/
    public void setFan(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings fan)
    {
        this._fan = fan;
    } //-- void setFan(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings) 

    /**
     * Sets the value of field 'heatSetpoint'.
     * 
     * @param heatSetpoint the value of field 'heatSetpoint'.
    **/
    public void setHeatSetpoint(int heatSetpoint)
    {
        this._heatSetpoint = heatSetpoint;
        this._has_heatSetpoint = true;
    } //-- void setHeatSetpoint(int) 

    /**
     * 
     * 
     * @param index
     * @param vInfoString
    **/
    public void setInfoString(int index, java.lang.String vInfoString)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _infoStringList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _infoStringList.setElementAt(vInfoString, index);
    } //-- void setInfoString(int, java.lang.String) 

    /**
     * 
     * 
     * @param infoStringArray
    **/
    public void setInfoString(java.lang.String[] infoStringArray)
    {
        //-- copy array
        _infoStringList.removeAllElements();
        for (int i = 0; i < infoStringArray.length; i++) {
            _infoStringList.addElement(infoStringArray[i]);
        }
    } //-- void setInfoString(java.lang.String) 

    /**
     * Sets the value of field 'lastUpdatedTime'.
     * 
     * @param lastUpdatedTime the value of field 'lastUpdatedTime'.
    **/
    public void setLastUpdatedTime(java.util.Date lastUpdatedTime)
    {
        this._lastUpdatedTime = lastUpdatedTime;
    } //-- void setLastUpdatedTime(java.util.Date) 

    /**
     * Sets the value of field 'lowerCoolSetpointLimit'.
     * 
     * @param lowerCoolSetpointLimit the value of field
     * 'lowerCoolSetpointLimit'.
    **/
    public void setLowerCoolSetpointLimit(int lowerCoolSetpointLimit)
    {
        this._lowerCoolSetpointLimit = lowerCoolSetpointLimit;
        this._has_lowerCoolSetpointLimit = true;
    } //-- void setLowerCoolSetpointLimit(int) 

    /**
     * Sets the value of field 'mode'.
     * 
     * @param mode the value of field 'mode'.
    **/
    public void setMode(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings mode)
    {
        this._mode = mode;
    } //-- void setMode(com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings) 

    /**
     * Sets the value of field 'setpointHold'.
     * 
     * @param setpointHold the value of field 'setpointHold'.
    **/
    public void setSetpointHold(boolean setpointHold)
    {
        this._setpointHold = setpointHold;
        this._has_setpointHold = true;
    } //-- void setSetpointHold(boolean) 

    /**
     * Sets the value of field 'upperHeatSetpointLimit'.
     * 
     * @param upperHeatSetpointLimit the value of field
     * 'upperHeatSetpointLimit'.
    **/
    public void setUpperHeatSetpointLimit(int upperHeatSetpointLimit)
    {
        this._upperHeatSetpointLimit = upperHeatSetpointLimit;
        this._has_upperHeatSetpointLimit = true;
    } //-- void setUpperHeatSetpointLimit(int) 

}
