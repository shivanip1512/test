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

import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;
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
public class StarsThermostatSeason implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings _mode;

    private org.exolab.castor.types.Date _startDate;

    private java.util.Vector _starsThermostatScheduleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatSeason() {
        super();
        _starsThermostatScheduleList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSeason()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStarsThermostatSchedule
    **/
    public void addStarsThermostatSchedule(StarsThermostatSchedule vStarsThermostatSchedule)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermostatScheduleList.size() < 7)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatScheduleList.addElement(vStarsThermostatSchedule);
    } //-- void addStarsThermostatSchedule(StarsThermostatSchedule) 

    /**
     * 
     * 
     * @param index
     * @param vStarsThermostatSchedule
    **/
    public void addStarsThermostatSchedule(int index, StarsThermostatSchedule vStarsThermostatSchedule)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_starsThermostatScheduleList.size() < 7)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatScheduleList.insertElementAt(vStarsThermostatSchedule, index);
    } //-- void addStarsThermostatSchedule(int, StarsThermostatSchedule) 

    /**
    **/
    public java.util.Enumeration enumerateStarsThermostatSchedule()
    {
        return _starsThermostatScheduleList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatSchedule() 

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
     * 
     * 
     * @param index
    **/
    public StarsThermostatSchedule getStarsThermostatSchedule(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatScheduleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (StarsThermostatSchedule) _starsThermostatScheduleList.elementAt(index);
    } //-- StarsThermostatSchedule getStarsThermostatSchedule(int) 

    /**
    **/
    public StarsThermostatSchedule[] getStarsThermostatSchedule()
    {
        int size = _starsThermostatScheduleList.size();
        StarsThermostatSchedule[] mArray = new StarsThermostatSchedule[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (StarsThermostatSchedule) _starsThermostatScheduleList.elementAt(index);
        }
        return mArray;
    } //-- StarsThermostatSchedule[] getStarsThermostatSchedule() 

    /**
    **/
    public int getStarsThermostatScheduleCount()
    {
        return _starsThermostatScheduleList.size();
    } //-- int getStarsThermostatScheduleCount() 

    /**
     * Returns the value of field 'startDate'.
     * 
     * @return the value of field 'startDate'.
    **/
    public org.exolab.castor.types.Date getStartDate()
    {
        return this._startDate;
    } //-- org.exolab.castor.types.Date getStartDate() 

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
    public void removeAllStarsThermostatSchedule()
    {
        _starsThermostatScheduleList.removeAllElements();
    } //-- void removeAllStarsThermostatSchedule() 

    /**
     * 
     * 
     * @param index
    **/
    public StarsThermostatSchedule removeStarsThermostatSchedule(int index)
    {
        java.lang.Object obj = _starsThermostatScheduleList.elementAt(index);
        _starsThermostatScheduleList.removeElementAt(index);
        return (StarsThermostatSchedule) obj;
    } //-- StarsThermostatSchedule removeStarsThermostatSchedule(int) 

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
     * 
     * 
     * @param index
     * @param vStarsThermostatSchedule
    **/
    public void setStarsThermostatSchedule(int index, StarsThermostatSchedule vStarsThermostatSchedule)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _starsThermostatScheduleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 7)) {
            throw new IndexOutOfBoundsException();
        }
        _starsThermostatScheduleList.setElementAt(vStarsThermostatSchedule, index);
    } //-- void setStarsThermostatSchedule(int, StarsThermostatSchedule) 

    /**
     * 
     * 
     * @param starsThermostatScheduleArray
    **/
    public void setStarsThermostatSchedule(StarsThermostatSchedule[] starsThermostatScheduleArray)
    {
        //-- copy array
        _starsThermostatScheduleList.removeAllElements();
        for (int i = 0; i < starsThermostatScheduleArray.length; i++) {
            _starsThermostatScheduleList.addElement(starsThermostatScheduleArray[i]);
        }
    } //-- void setStarsThermostatSchedule(StarsThermostatSchedule) 

    /**
     * Sets the value of field 'startDate'.
     * 
     * @param startDate the value of field 'startDate'.
    **/
    public void setStartDate(org.exolab.castor.types.Date startDate)
    {
        this._startDate = startDate;
    } //-- void setStartDate(org.exolab.castor.types.Date) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsThermostatSeason unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsThermostatSeason) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsThermostatSeason.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSeason unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
