/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: LMHardware.java,v 1.13 2004/05/18 17:48:45 zyao Exp $
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
 * @version $Revision: 1.13 $ $Date: 2004/05/18 17:48:45 $
**/
public class LMHardware implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _routeID;

    /**
     * keeps track of state for field: _routeID
    **/
    private boolean _has_routeID;

    private java.lang.String _manufacturerSerialNumber;

    private java.util.Vector _starsLMHardwareConfigList;

    private StarsThermostatSettings _starsThermostatSettings;


      //----------------/
     //- Constructors -/
    //----------------/

    public LMHardware() {
        super();
        _starsLMHardwareConfigList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.LMHardware()


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
    public void deleteRouteID()
    {
        this._has_routeID= false;
    } //-- void deleteRouteID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsLMHardwareConfig()
    {
        return _starsLMHardwareConfigList.elements();
    } //-- java.util.Enumeration enumerateStarsLMHardwareConfig() 

    /**
     * Returns the value of field 'manufacturerSerialNumber'.
     * 
     * @return the value of field 'manufacturerSerialNumber'.
    **/
    public java.lang.String getManufacturerSerialNumber()
    {
        return this._manufacturerSerialNumber;
    } //-- java.lang.String getManufacturerSerialNumber() 

    /**
     * Returns the value of field 'routeID'.
     * 
     * @return the value of field 'routeID'.
    **/
    public int getRouteID()
    {
        return this._routeID;
    } //-- int getRouteID() 

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
    public boolean hasRouteID()
    {
        return this._has_routeID;
    } //-- boolean hasRouteID() 

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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

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
     * Sets the value of field 'manufacturerSerialNumber'.
     * 
     * @param manufacturerSerialNumber the value of field
     * 'manufacturerSerialNumber'.
    **/
    public void setManufacturerSerialNumber(java.lang.String manufacturerSerialNumber)
    {
        this._manufacturerSerialNumber = manufacturerSerialNumber;
    } //-- void setManufacturerSerialNumber(java.lang.String) 

    /**
     * Sets the value of field 'routeID'.
     * 
     * @param routeID the value of field 'routeID'.
    **/
    public void setRouteID(int routeID)
    {
        this._routeID = routeID;
        this._has_routeID = true;
    } //-- void setRouteID(int) 

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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.LMHardware unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.LMHardware) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.LMHardware.class, reader);
    } //-- com.cannontech.stars.xml.serialize.LMHardware unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
