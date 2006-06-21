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

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsUpdateThermostatScheduleResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMHardwareEvent _starsLMHardwareEvent;

    private java.util.Vector _starsThermostatSeasonList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateThermostatScheduleResponse() {
        super();
        _starsThermostatSeasonList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse()


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
    public java.util.Enumeration enumerateStarsThermostatSeason()
    {
        return _starsThermostatSeasonList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatSeason() 

    /**
     * Returns the value of field 'starsLMHardwareEvent'.
     * 
     * @return the value of field 'starsLMHardwareEvent'.
    **/
    public StarsLMHardwareEvent getStarsLMHardwareEvent()
    {
        return this._starsLMHardwareEvent;
    } //-- StarsLMHardwareEvent getStarsLMHardwareEvent() 

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
     * Sets the value of field 'starsLMHardwareEvent'.
     * 
     * @param starsLMHardwareEvent the value of field
     * 'starsLMHardwareEvent'.
    **/
    public void setStarsLMHardwareEvent(StarsLMHardwareEvent starsLMHardwareEvent)
    {
        this._starsLMHardwareEvent = starsLMHardwareEvent;
    } //-- void setStarsLMHardwareEvent(StarsLMHardwareEvent) 

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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
