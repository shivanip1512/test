/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsLMHw extends com.cannontech.stars.xml.serialize.StarsInventory 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _manufactureSerialNumber;

    private LMDeviceType _LMDeviceType;

    private DeviceStatus _deviceStatus;

    private java.lang.String _installationNotes;

    private StarsLMHardwareHistory _starsLMHardwareHistory;

    private java.util.Vector _starsLMHardwareConfigList;

    private StarsThermostatSettings _starsThermostatSettings;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsLMHw() {
        super();
        _starsLMHardwareConfigList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsLMHw()


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
    public java.util.Enumeration enumerateStarsLMHardwareConfig()
    {
        return _starsLMHardwareConfigList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareConfig() 

    /**
     * Returns the value of field 'deviceStatus'.
     * 
     * @return the value of field 'deviceStatus'.
    **/
    public DeviceStatus getDeviceStatus()
    {
        return this._deviceStatus;
    } //-- DeviceStatus getDeviceStatus() 

    /**
     * Returns the value of field 'installationNotes'.
     * 
     * @return the value of field 'installationNotes'.
    **/
    public java.lang.String getInstallationNotes()
    {
        return this._installationNotes;
    } //-- java.lang.String getInstallationNotes() 

    /**
     * Returns the value of field 'LMDeviceType'.
     * 
     * @return the value of field 'LMDeviceType'.
    **/
    public LMDeviceType getLMDeviceType()
    {
        return this._LMDeviceType;
    } //-- LMDeviceType getLMDeviceType() 

    /**
     * Returns the value of field 'manufactureSerialNumber'.
     * 
     * @return the value of field 'manufactureSerialNumber'.
    **/
    public java.lang.String getManufactureSerialNumber()
    {
        return this._manufactureSerialNumber;
    } //-- java.lang.String getManufactureSerialNumber() 

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
     * Returns the value of field 'starsLMHardwareHistory'.
     * 
     * @return the value of field 'starsLMHardwareHistory'.
    **/
    public StarsLMHardwareHistory getStarsLMHardwareHistory()
    {
        return this._starsLMHardwareHistory;
    } //-- StarsLMHardwareHistory getStarsLMHardwareHistory() 

    /**
     * Returns the value of field 'starsThermostatSettings'.
     * 
     * @return the value of field 'starsThermostatSettings'.
    **/
    public StarsThermostatSettings getStarsThermostatSettings()
    {
        return this._starsThermostatSettings;
    } //-- StarsThermostatSettings getStarsThermostatSettings() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * 
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

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
     * Sets the value of field 'deviceStatus'.
     * 
     * @param deviceStatus the value of field 'deviceStatus'.
    **/
    public void setDeviceStatus(DeviceStatus deviceStatus)
    {
        this._deviceStatus = deviceStatus;
    } //-- void setDeviceStatus(DeviceStatus) 

    /**
     * Sets the value of field 'installationNotes'.
     * 
     * @param installationNotes the value of field
     * 'installationNotes'.
    **/
    public void setInstallationNotes(java.lang.String installationNotes)
    {
        this._installationNotes = installationNotes;
    } //-- void setInstallationNotes(java.lang.String) 

    /**
     * Sets the value of field 'LMDeviceType'.
     * 
     * @param LMDeviceType the value of field 'LMDeviceType'.
    **/
    public void setLMDeviceType(LMDeviceType LMDeviceType)
    {
        this._LMDeviceType = LMDeviceType;
    } //-- void setLMDeviceType(LMDeviceType) 

    /**
     * Sets the value of field 'manufactureSerialNumber'.
     * 
     * @param manufactureSerialNumber the value of field
     * 'manufactureSerialNumber'.
    **/
    public void setManufactureSerialNumber(java.lang.String manufactureSerialNumber)
    {
        this._manufactureSerialNumber = manufactureSerialNumber;
    } //-- void setManufactureSerialNumber(java.lang.String) 

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

    /**
     * Sets the value of field 'starsLMHardwareHistory'.
     * 
     * @param starsLMHardwareHistory the value of field
     * 'starsLMHardwareHistory'.
    **/
    public void setStarsLMHardwareHistory(StarsLMHardwareHistory starsLMHardwareHistory)
    {
        this._starsLMHardwareHistory = starsLMHardwareHistory;
    } //-- void setStarsLMHardwareHistory(StarsLMHardwareHistory) 

    /**
     * Sets the value of field 'starsThermostatSettings'.
     * 
     * @param starsThermostatSettings the value of field
     * 'starsThermostatSettings'.
    **/
    public void setStarsThermostatSettings(StarsThermostatSettings starsThermostatSettings)
    {
        this._starsThermostatSettings = starsThermostatSettings;
    } //-- void setStarsThermostatSettings(StarsThermostatSettings) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
