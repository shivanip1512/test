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
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class HeatPump implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private PumpType _pumpType;

    private PumpSize _pumpSize;

    private StandbySource _standbySource;

    private int _restartDelaySeconds;

    /**
     * keeps track of state for field: _restartDelaySeconds
    **/
    private boolean _has_restartDelaySeconds;


      //----------------/
     //- Constructors -/
    //----------------/

    public HeatPump() {
        super();
    } //-- com.cannontech.stars.xml.serialize.HeatPump()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteRestartDelaySeconds()
    {
        this._has_restartDelaySeconds= false;
    } //-- void deleteRestartDelaySeconds() 

    /**
     * Returns the value of field 'pumpSize'.
     * 
     * @return the value of field 'pumpSize'.
    **/
    public PumpSize getPumpSize()
    {
        return this._pumpSize;
    } //-- PumpSize getPumpSize() 

    /**
     * Returns the value of field 'pumpType'.
     * 
     * @return the value of field 'pumpType'.
    **/
    public PumpType getPumpType()
    {
        return this._pumpType;
    } //-- PumpType getPumpType() 

    /**
     * Returns the value of field 'restartDelaySeconds'.
     * 
     * @return the value of field 'restartDelaySeconds'.
    **/
    public int getRestartDelaySeconds()
    {
        return this._restartDelaySeconds;
    } //-- int getRestartDelaySeconds() 

    /**
     * Returns the value of field 'standbySource'.
     * 
     * @return the value of field 'standbySource'.
    **/
    public StandbySource getStandbySource()
    {
        return this._standbySource;
    } //-- StandbySource getStandbySource() 

    /**
    **/
    public boolean hasRestartDelaySeconds()
    {
        return this._has_restartDelaySeconds;
    } //-- boolean hasRestartDelaySeconds() 

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
     * Sets the value of field 'pumpSize'.
     * 
     * @param pumpSize the value of field 'pumpSize'.
    **/
    public void setPumpSize(PumpSize pumpSize)
    {
        this._pumpSize = pumpSize;
    } //-- void setPumpSize(PumpSize) 

    /**
     * Sets the value of field 'pumpType'.
     * 
     * @param pumpType the value of field 'pumpType'.
    **/
    public void setPumpType(PumpType pumpType)
    {
        this._pumpType = pumpType;
    } //-- void setPumpType(PumpType) 

    /**
     * Sets the value of field 'restartDelaySeconds'.
     * 
     * @param restartDelaySeconds the value of field
     * 'restartDelaySeconds'.
    **/
    public void setRestartDelaySeconds(int restartDelaySeconds)
    {
        this._restartDelaySeconds = restartDelaySeconds;
        this._has_restartDelaySeconds = true;
    } //-- void setRestartDelaySeconds(int) 

    /**
     * Sets the value of field 'standbySource'.
     * 
     * @param standbySource the value of field 'standbySource'.
    **/
    public void setStandbySource(StandbySource standbySource)
    {
        this._standbySource = standbySource;
    } //-- void setStandbySource(StandbySource) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.HeatPump unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.HeatPump) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.HeatPump.class, reader);
    } //-- com.cannontech.stars.xml.serialize.HeatPump unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
