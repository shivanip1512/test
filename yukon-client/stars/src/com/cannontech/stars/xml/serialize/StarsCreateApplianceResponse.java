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
public class StarsCreateApplianceResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsAppliance _starsAppliance;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCreateApplianceResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsAppliance'.
     * 
     * @return the value of field 'starsAppliance'.
    **/
    public StarsAppliance getStarsAppliance()
    {
        return this._starsAppliance;
    } //-- StarsAppliance getStarsAppliance() 

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
     * Sets the value of field 'starsAppliance'.
     * 
     * @param starsAppliance the value of field 'starsAppliance'.
    **/
    public void setStarsAppliance(StarsAppliance starsAppliance)
    {
        this._starsAppliance = starsAppliance;
    } //-- void setStarsAppliance(StarsAppliance) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
