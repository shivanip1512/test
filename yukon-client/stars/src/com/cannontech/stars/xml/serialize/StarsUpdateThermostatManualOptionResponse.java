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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision$ $Date$
**/
public class StarsUpdateThermostatManualOptionResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsThermostatManualEvent _starsThermostatManualEvent;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateThermostatManualOptionResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOptionResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsThermostatManualEvent'.
     * 
     * @return the value of field 'starsThermostatManualEvent'.
    **/
    public StarsThermostatManualEvent getStarsThermostatManualEvent()
    {
        return this._starsThermostatManualEvent;
    } //-- StarsThermostatManualEvent getStarsThermostatManualEvent() 

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
     * Sets the value of field 'starsThermostatManualEvent'.
     * 
     * @param starsThermostatManualEvent the value of field
     * 'starsThermostatManualEvent'.
    **/
    public void setStarsThermostatManualEvent(StarsThermostatManualEvent starsThermostatManualEvent)
    {
        this._starsThermostatManualEvent = starsThermostatManualEvent;
    } //-- void setStarsThermostatManualEvent(StarsThermostatManualEvent) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOptionResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOptionResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOptionResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOptionResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
