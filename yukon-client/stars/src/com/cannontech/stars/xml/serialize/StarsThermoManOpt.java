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

import com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public abstract class StarsThermoManOpt implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _temperature;

    /**
     * keeps track of state for field: _temperature
    **/
    private boolean _has_temperature;

    private boolean _hold;

    /**
     * keeps track of state for field: _hold
    **/
    private boolean _has_hold;

    private com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings _mode;

    private com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings _fan;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermoManOpt() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsThermoManOpt()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'hold'.
     * 
     * @return the value of field 'hold'.
    **/
    public boolean getHold()
    {
        return this._hold;
    } //-- boolean getHold() 

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
     * Returns the value of field 'temperature'.
     * 
     * @return the value of field 'temperature'.
    **/
    public int getTemperature()
    {
        return this._temperature;
    } //-- int getTemperature() 

    /**
    **/
    public boolean hasHold()
    {
        return this._has_hold;
    } //-- boolean hasHold() 

    /**
    **/
    public boolean hasTemperature()
    {
        return this._has_temperature;
    } //-- boolean hasTemperature() 

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
     * Sets the value of field 'fan'.
     * 
     * @param fan the value of field 'fan'.
    **/
    public void setFan(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings fan)
    {
        this._fan = fan;
    } //-- void setFan(com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings) 

    /**
     * Sets the value of field 'hold'.
     * 
     * @param hold the value of field 'hold'.
    **/
    public void setHold(boolean hold)
    {
        this._hold = hold;
        this._has_hold = true;
    } //-- void setHold(boolean) 

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
     * Sets the value of field 'temperature'.
     * 
     * @param temperature the value of field 'temperature'.
    **/
    public void setTemperature(int temperature)
    {
        this._temperature = temperature;
        this._has_temperature = true;
    } //-- void setTemperature(int) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
