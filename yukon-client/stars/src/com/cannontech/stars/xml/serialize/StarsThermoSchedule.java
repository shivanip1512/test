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

import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.types.Time;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsThermoSchedule implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings _day;

    private com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings _mode;

    private org.exolab.castor.types.Time _time1;

    private int _temperature1;

    /**
     * keeps track of state for field: _temperature1
    **/
    private boolean _has_temperature1;

    private org.exolab.castor.types.Time _time2;

    private int _temperature2;

    /**
     * keeps track of state for field: _temperature2
    **/
    private boolean _has_temperature2;

    private org.exolab.castor.types.Time _time3;

    private int _temperature3;

    /**
     * keeps track of state for field: _temperature3
    **/
    private boolean _has_temperature3;

    private org.exolab.castor.types.Time _time4;

    private int _temperature4;

    /**
     * keeps track of state for field: _temperature4
    **/
    private boolean _has_temperature4;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermoSchedule() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsThermoSchedule()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'day'.
     * 
     * @return the value of field 'day'.
    **/
    public com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings getDay()
    {
        return this._day;
    } //-- com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings getDay() 

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
     * Returns the value of field 'temperature1'.
     * 
     * @return the value of field 'temperature1'.
    **/
    public int getTemperature1()
    {
        return this._temperature1;
    } //-- int getTemperature1() 

    /**
     * Returns the value of field 'temperature2'.
     * 
     * @return the value of field 'temperature2'.
    **/
    public int getTemperature2()
    {
        return this._temperature2;
    } //-- int getTemperature2() 

    /**
     * Returns the value of field 'temperature3'.
     * 
     * @return the value of field 'temperature3'.
    **/
    public int getTemperature3()
    {
        return this._temperature3;
    } //-- int getTemperature3() 

    /**
     * Returns the value of field 'temperature4'.
     * 
     * @return the value of field 'temperature4'.
    **/
    public int getTemperature4()
    {
        return this._temperature4;
    } //-- int getTemperature4() 

    /**
     * Returns the value of field 'time1'.
     * 
     * @return the value of field 'time1'.
    **/
    public org.exolab.castor.types.Time getTime1()
    {
        return this._time1;
    } //-- org.exolab.castor.types.Time getTime1() 

    /**
     * Returns the value of field 'time2'.
     * 
     * @return the value of field 'time2'.
    **/
    public org.exolab.castor.types.Time getTime2()
    {
        return this._time2;
    } //-- org.exolab.castor.types.Time getTime2() 

    /**
     * Returns the value of field 'time3'.
     * 
     * @return the value of field 'time3'.
    **/
    public org.exolab.castor.types.Time getTime3()
    {
        return this._time3;
    } //-- org.exolab.castor.types.Time getTime3() 

    /**
     * Returns the value of field 'time4'.
     * 
     * @return the value of field 'time4'.
    **/
    public org.exolab.castor.types.Time getTime4()
    {
        return this._time4;
    } //-- org.exolab.castor.types.Time getTime4() 

    /**
    **/
    public boolean hasTemperature1()
    {
        return this._has_temperature1;
    } //-- boolean hasTemperature1() 

    /**
    **/
    public boolean hasTemperature2()
    {
        return this._has_temperature2;
    } //-- boolean hasTemperature2() 

    /**
    **/
    public boolean hasTemperature3()
    {
        return this._has_temperature3;
    } //-- boolean hasTemperature3() 

    /**
    **/
    public boolean hasTemperature4()
    {
        return this._has_temperature4;
    } //-- boolean hasTemperature4() 

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
     * Sets the value of field 'day'.
     * 
     * @param day the value of field 'day'.
    **/
    public void setDay(com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings day)
    {
        this._day = day;
    } //-- void setDay(com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings) 

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
     * Sets the value of field 'temperature1'.
     * 
     * @param temperature1 the value of field 'temperature1'.
    **/
    public void setTemperature1(int temperature1)
    {
        this._temperature1 = temperature1;
        this._has_temperature1 = true;
    } //-- void setTemperature1(int) 

    /**
     * Sets the value of field 'temperature2'.
     * 
     * @param temperature2 the value of field 'temperature2'.
    **/
    public void setTemperature2(int temperature2)
    {
        this._temperature2 = temperature2;
        this._has_temperature2 = true;
    } //-- void setTemperature2(int) 

    /**
     * Sets the value of field 'temperature3'.
     * 
     * @param temperature3 the value of field 'temperature3'.
    **/
    public void setTemperature3(int temperature3)
    {
        this._temperature3 = temperature3;
        this._has_temperature3 = true;
    } //-- void setTemperature3(int) 

    /**
     * Sets the value of field 'temperature4'.
     * 
     * @param temperature4 the value of field 'temperature4'.
    **/
    public void setTemperature4(int temperature4)
    {
        this._temperature4 = temperature4;
        this._has_temperature4 = true;
    } //-- void setTemperature4(int) 

    /**
     * Sets the value of field 'time1'.
     * 
     * @param time1 the value of field 'time1'.
    **/
    public void setTime1(org.exolab.castor.types.Time time1)
    {
        this._time1 = time1;
    } //-- void setTime1(org.exolab.castor.types.Time) 

    /**
     * Sets the value of field 'time2'.
     * 
     * @param time2 the value of field 'time2'.
    **/
    public void setTime2(org.exolab.castor.types.Time time2)
    {
        this._time2 = time2;
    } //-- void setTime2(org.exolab.castor.types.Time) 

    /**
     * Sets the value of field 'time3'.
     * 
     * @param time3 the value of field 'time3'.
    **/
    public void setTime3(org.exolab.castor.types.Time time3)
    {
        this._time3 = time3;
    } //-- void setTime3(org.exolab.castor.types.Time) 

    /**
     * Sets the value of field 'time4'.
     * 
     * @param time4 the value of field 'time4'.
    **/
    public void setTime4(org.exolab.castor.types.Time time4)
    {
        this._time4 = time4;
    } //-- void setTime4(org.exolab.castor.types.Time) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsThermoSchedule unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsThermoSchedule) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsThermoSchedule.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsThermoSchedule unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
