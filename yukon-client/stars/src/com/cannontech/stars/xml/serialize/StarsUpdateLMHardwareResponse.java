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
public class StarsUpdateLMHardwareResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMHardware _starsLMHardware;

    private StarsServiceCompany _starsServiceCompany;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateLMHardwareResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsLMHardware'.
     * 
     * @return the value of field 'starsLMHardware'.
    **/
    public StarsLMHardware getStarsLMHardware()
    {
        return this._starsLMHardware;
    } //-- StarsLMHardware getStarsLMHardware() 

    /**
     * Returns the value of field 'starsServiceCompany'.
     * 
     * @return the value of field 'starsServiceCompany'.
    **/
    public StarsServiceCompany getStarsServiceCompany()
    {
        return this._starsServiceCompany;
    } //-- StarsServiceCompany getStarsServiceCompany() 

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
     * Sets the value of field 'starsLMHardware'.
     * 
     * @param starsLMHardware the value of field 'starsLMHardware'.
    **/
    public void setStarsLMHardware(StarsLMHardware starsLMHardware)
    {
        this._starsLMHardware = starsLMHardware;
    } //-- void setStarsLMHardware(StarsLMHardware) 

    /**
     * Sets the value of field 'starsServiceCompany'.
     * 
     * @param starsServiceCompany the value of field
     * 'starsServiceCompany'.
    **/
    public void setStarsServiceCompany(StarsServiceCompany starsServiceCompany)
    {
        this._starsServiceCompany = starsServiceCompany;
    } //-- void setStarsServiceCompany(StarsServiceCompany) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
