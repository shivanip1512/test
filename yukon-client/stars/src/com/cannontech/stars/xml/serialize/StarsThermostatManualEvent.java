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
public class StarsThermostatManualEvent extends com.cannontech.stars.xml.serialize.StarsLMCustomerEvent 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ThermostatManualOption _thermostatManualOption;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsThermostatManualEvent() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatManualEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'thermostatManualOption'.
     * 
     * @return the value of field 'thermostatManualOption'.
    **/
    public ThermostatManualOption getThermostatManualOption()
    {
        return this._thermostatManualOption;
    } //-- ThermostatManualOption getThermostatManualOption() 

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
     * Sets the value of field 'thermostatManualOption'.
     * 
     * @param thermostatManualOption the value of field
     * 'thermostatManualOption'.
    **/
    public void setThermostatManualOption(ThermostatManualOption thermostatManualOption)
    {
        this._thermostatManualOption = thermostatManualOption;
    } //-- void setThermostatManualOption(ThermostatManualOption) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsThermostatManualEvent unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsThermostatManualEvent) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsThermostatManualEvent.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsThermostatManualEvent unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
