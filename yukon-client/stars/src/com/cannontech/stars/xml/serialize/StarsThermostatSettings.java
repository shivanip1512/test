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
public class StarsThermostatSettings implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsThermostatProgram _starsThermostatProgram;

    private java.util.Vector _starsThermostatManualEventList;

    private StarsThermostatDynamicData _starsThermostatDynamicData;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatSettings() {
        super();
        _starsThermostatManualEventList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSettings()


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
    **/
    public java.util.Enumeration enumerateStarsThermostatManualEvent()
    {
        return _starsThermostatManualEventList.elements();
    } //-- java.util.Enumeration enumerateStarsThermostatManualEvent() 

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
     * Returns the value of field 'starsThermostatProgram'.
     * 
     * @return the value of field 'starsThermostatProgram'.
    **/
    public StarsThermostatProgram getStarsThermostatProgram()
    {
        return this._starsThermostatProgram;
    } //-- StarsThermostatProgram getStarsThermostatProgram() 

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
    public void removeAllStarsThermostatManualEvent()
    {
        _starsThermostatManualEventList.removeAllElements();
    } //-- void removeAllStarsThermostatManualEvent() 

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
     * Sets the value of field 'starsThermostatProgram'.
     * 
     * @param starsThermostatProgram the value of field
     * 'starsThermostatProgram'.
    **/
    public void setStarsThermostatProgram(StarsThermostatProgram starsThermostatProgram)
    {
        this._starsThermostatProgram = starsThermostatProgram;
    } //-- void setStarsThermostatProgram(StarsThermostatProgram) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsThermostatSettings unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsThermostatSettings) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsThermostatSettings.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatSettings unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
