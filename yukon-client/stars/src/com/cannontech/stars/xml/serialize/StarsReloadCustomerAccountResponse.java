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
public class StarsReloadCustomerAccountResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustAccountInformation _starsCustAccountInformation;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsReloadCustomerAccountResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsReloadCustomerAccountResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCustAccountInformation'.
     * 
     * @return the value of field 'starsCustAccountInformation'.
    **/
    public StarsCustAccountInformation getStarsCustAccountInformation()
    {
        return this._starsCustAccountInformation;
    } //-- StarsCustAccountInformation getStarsCustAccountInformation() 

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
     * Sets the value of field 'starsCustAccountInformation'.
     * 
     * @param starsCustAccountInformation the value of field
     * 'starsCustAccountInformation'.
    **/
    public void setStarsCustAccountInformation(StarsCustAccountInformation starsCustAccountInformation)
    {
        this._starsCustAccountInformation = starsCustAccountInformation;
    } //-- void setStarsCustAccountInformation(StarsCustAccountInformation) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsReloadCustomerAccountResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsReloadCustomerAccountResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsReloadCustomerAccountResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsReloadCustomerAccountResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
