/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsThermostatProgram.java,v 1.2 2004/05/18 18:33:59 zyao Exp $
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
 * @version $Revision: 1.2 $ $Date: 2004/05/18 18:33:59 $
**/
public class StarsThermostatProgram implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _scheduleID;

    /**
     * keeps track of state for field: _scheduleID
    **/
    private boolean _has_scheduleID;

    private java.lang.String _scheduleName;

    private com.cannontech.stars.xml.serialize.types.StarsThermostatTypes _thermostatType;

    private java.util.Vector _starsThermostatSeasonList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatProgram() {
        super();
        _starsThermostatSeasonList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatProgram()


      //-----------/
     //- Methods -/
    //-----------/

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
    public void deleteScheduleID()
    {
        this._has_scheduleID= false;
    } //-- void deleteScheduleID() 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermostatSeason()
    {
        return _starsThermostatSeasonList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatSeason() 

    /**
     * Returns the value of field 'scheduleID'.
     * 
     * @return the value of field 'scheduleID'.
    **/
    public int getScheduleID()
    {
        return this._scheduleID;
    } //-- int getScheduleID() 

    /**
     * Returns the value of field 'scheduleName'.
     * 
     * @return the value of field 'scheduleName'.
    **/
    public java.lang.String getScheduleName()
    {
        return this._scheduleName;
    } //-- java.lang.String getScheduleName() 

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
    public boolean hasScheduleID()
    {
        return this._has_scheduleID;
    } //-- boolean hasScheduleID() 

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
    public void removeAllStarsThermostatSeason()
    {
        _starsThermostatSeasonList.removeAllElements();
    } //-- void removeAllStarsThermostatSeason() 

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
     * Sets the value of field 'scheduleID'.
     * 
     * @param scheduleID the value of field 'scheduleID'.
    **/
    public void setScheduleID(int scheduleID)
    {
        this._scheduleID = scheduleID;
        this._has_scheduleID = true;
    } //-- void setScheduleID(int) 

    /**
     * Sets the value of field 'scheduleName'.
     * 
     * @param scheduleName the value of field 'scheduleName'.
    **/
    public void setScheduleName(java.lang.String scheduleName)
    {
        this._scheduleName = scheduleName;
    } //-- void setScheduleName(java.lang.String) 

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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsThermostatProgram unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsThermostatProgram) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsThermostatProgram.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatProgram unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
