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

import com.cannontech.stars.xml.serialize.types.StarsThermostatTypes;
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
public abstract class StarsThermoSettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _inventoryID;

    /**
     * keeps track of state for field: _inventoryID
    **/
    private boolean _has_inventoryID;

    private com.cannontech.stars.xml.serialize.types.StarsThermostatTypes _thermostatType;

    private java.util.Vector _starsThermostatSeasonList;

    private java.util.Vector _starsThermostatManualEventList;

    private StarsThermostatDynamicData _starsThermostatDynamicData;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermoSettings() {
        super();
        _starsThermostatSeasonList = new Vector();
        _starsThermostatManualEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermoSettings()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsThermostatManualEvent
    **/
    public void addStarsThermostatManualEvent(StarsThermostatManualEvent vStarsThermostatManualEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsThermostatManualEventList.addElement(vStarsThermostatManualEvent);
    } //-- void addStarsThermostatManualEvent(StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatManualEvent
    **/
    public void addStarsThermostatManualEvent(int index, StarsThermostatManualEvent vStarsThermostatManualEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _starsThermostatManualEventList.insertElementAt(vStarsThermostatManualEvent, index);
    } //-- void addStarsThermostatManualEvent(int, StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param vStarsThermostatSeason
    **/
    public void addStarsThermostatSeason(StarsThermostatSeason vStarsThermostatSeason)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermostatSeasonList.size() < 2)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatSeasonList.addElement(vStarsThermostatSeason);
    } //-- void addStarsThermostatSeason(StarsThermostatSeason) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatSeason
    **/
    public void addStarsThermostatSeason(int index, StarsThermostatSeason vStarsThermostatSeason)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermostatSeasonList.size() < 2)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatSeasonList.insertElementAt(vStarsThermostatSeason, index);
    } //-- void addStarsThermostatSeason(int, StarsThermostatSeason) 

    /**
    **/
    public void deleteInventoryID()
    {
        this._has_inventoryID= false;
    } //-- void deleteInventoryID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermostatManualEvent()
    {
        return _starsThermostatManualEventList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatManualEvent() 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermostatSeason()
    {
        return _starsThermostatSeasonList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatSeason() 

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
     * Returns the value of field 'starsThermostatDynamicData'.
     * 
     * @return the value of field 'starsThermostatDynamicData'.
    **/
    public StarsThermostatDynamicData getStarsThermostatDynamicData()
    {
        return this._starsThermostatDynamicData;
    } //-- StarsThermostatDynamicData getStarsThermostatDynamicData() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatManualEvent getStarsThermostatManualEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatManualEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsThermostatManualEvent) _starsThermostatManualEventList.elementAt(index);
    } //-- StarsThermostatManualEvent getStarsThermostatManualEvent(int) 

    /**
    **/
    public StarsThermostatManualEvent[] getStarsThermostatManualEvent()
    {
        int size = _starsThermostatManualEventList.size();
        StarsThermostatManualEvent[] mArray = new StarsThermostatManualEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsThermostatManualEvent) _starsThermostatManualEventList.elementAt(index);
        }
        return mArray;
    } //-- StarsThermostatManualEvent[] getStarsThermostatManualEvent() 

    /**
    **/
    public int getStarsThermostatManualEventCount()
    {
        return _starsThermostatManualEventList.size();
    } //-- int getStarsThermostatManualEventCount() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatSeason getStarsThermostatSeason(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatSeasonList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsThermostatSeason) _starsThermostatSeasonList.elementAt(index);
    } //-- StarsThermostatSeason getStarsThermostatSeason(int) 

    /**
    **/
    public StarsThermostatSeason[] getStarsThermostatSeason()
    {
        int size = _starsThermostatSeasonList.size();
        StarsThermostatSeason[] mArray = new StarsThermostatSeason[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsThermostatSeason) _starsThermostatSeasonList.elementAt(index);
        }
        return mArray;
    } //-- StarsThermostatSeason[] getStarsThermostatSeason() 

    /**
    **/
    public int getStarsThermostatSeasonCount()
    {
        return _starsThermostatSeasonList.size();
    } //-- int getStarsThermostatSeasonCount() 

    /**
     * Returns the value of field 'thermostatType'.
     * 
     * @return the value of field 'thermostatType'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermostatTypes getThermostatType()
    {
        return this._thermostatType;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermostatTypes getThermostatType() 

    /**
    **/
    public boolean hasInventoryID()
    {
        return this._has_inventoryID;
    } //-- boolean hasInventoryID() 

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
    public void removeAllStarsThermostatManualEvent()
    {
        _starsThermostatManualEventList.removeAllElements();
    } //-- void removeAllStarsThermostatManualEvent() 

    /**
    **/
    public void removeAllStarsThermostatSeason()
    {
        _starsThermostatSeasonList.removeAllElements();
    } //-- void removeAllStarsThermostatSeason() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatManualEvent removeStarsThermostatManualEvent(int index)
    {
        java.lang.Object obj = _starsThermostatManualEventList.elementAt(index);
        _starsThermostatManualEventList.removeElementAt(index);
        return (StarsThermostatManualEvent) obj;
    } //-- StarsThermostatManualEvent removeStarsThermostatManualEvent(int) 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatSeason removeStarsThermostatSeason(int index)
    {
        java.lang.Object obj = _starsThermostatSeasonList.elementAt(index);
        _starsThermostatSeasonList.removeElementAt(index);
        return (StarsThermostatSeason) obj;
    } //-- StarsThermostatSeason removeStarsThermostatSeason(int) 

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
     * Sets the value of field 'starsThermostatDynamicData'.
     * 
     * @param starsThermostatDynamicData the value of field
     * 'starsThermostatDynamicData'.
    **/
    public void setStarsThermostatDynamicData(StarsThermostatDynamicData starsThermostatDynamicData)
    {
        this._starsThermostatDynamicData = starsThermostatDynamicData;
    } //-- void setStarsThermostatDynamicData(StarsThermostatDynamicData) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatManualEvent
    **/
    public void setStarsThermostatManualEvent(int index, StarsThermostatManualEvent vStarsThermostatManualEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatManualEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatManualEventList.setElementAt(vStarsThermostatManualEvent, index);
    } //-- void setStarsThermostatManualEvent(int, StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param starsThermostatManualEventArray
    **/
    public void setStarsThermostatManualEvent(StarsThermostatManualEvent[] starsThermostatManualEventArray)
    {
        //-- copy array
        _starsThermostatManualEventList.removeAllElements();
        for (int i = 0; i < starsThermostatManualEventArray.length; i++) {
            _starsThermostatManualEventList.addElement(starsThermostatManualEventArray[i]);
        }
    } //-- void setStarsThermostatManualEvent(StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatSeason
    **/
    public void setStarsThermostatSeason(int index, StarsThermostatSeason vStarsThermostatSeason)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatSeasonList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 2)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatSeasonList.setElementAt(vStarsThermostatSeason, index);
    } //-- void setStarsThermostatSeason(int, StarsThermostatSeason) 

    /**
     * 
     * 
     * @param starsThermostatSeasonArray
    **/
    public void setStarsThermostatSeason(StarsThermostatSeason[] starsThermostatSeasonArray)
    {
        //-- copy array
        _starsThermostatSeasonList.removeAllElements();
        for (int i = 0; i < starsThermostatSeasonArray.length; i++) {
            _starsThermostatSeasonList.addElement(starsThermostatSeasonArray[i]);
        }
    } //-- void setStarsThermostatSeason(StarsThermostatSeason) 

    /**
     * Sets the value of field 'thermostatType'.
     * 
     * @param thermostatType the value of field 'thermostatType'.
    **/
    public void setThermostatType(com.cannontech.stars.xml.serialize.types.StarsThermostatTypes thermostatType)
    {
        this._thermostatType = thermostatType;
    } //-- void setThermostatType(com.cannontech.stars.xml.serialize.types.StarsThermostatTypes) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
