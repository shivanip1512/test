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
public class StarsCreateServiceRequestResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsServiceRequest _starsServiceRequest;

    private StarsServiceCompany _starsServiceCompany;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCreateServiceRequestResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'starsServiceRequest'.
     * 
     * @return the value of field 'starsServiceRequest'.
    **/
    public StarsServiceRequest getStarsServiceRequest()
    {
        return this._starsServiceRequest;
    } //-- StarsServiceRequest getStarsServiceRequest() 

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
     * Sets the value of field 'starsServiceRequest'.
     * 
     * @param starsServiceRequest the value of field
     * 'starsServiceRequest'.
    **/
    public void setStarsServiceRequest(StarsServiceRequest starsServiceRequest)
    {
        this._starsServiceRequest = starsServiceRequest;
    } //-- void setStarsServiceRequest(StarsServiceRequest) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
