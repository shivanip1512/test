/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsDeleteApplianceResponse.java,v 1.5 2004/05/18 17:48:46 zyao Exp $
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
 * @version $Revision: 1.5 $ $Date: 2004/05/18 17:48:46 $
**/
public class StarsDeleteApplianceResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMPrograms _starsLMPrograms;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsDeleteApplianceResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteApplianceResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsLMPrograms'.
     * 
     * @return the value of field 'starsLMPrograms'.
    **/
    public StarsLMPrograms getStarsLMPrograms()
    {
        return this._starsLMPrograms;
    } //-- StarsLMPrograms getStarsLMPrograms() 

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
     * Sets the value of field 'starsLMPrograms'.
     * 
     * @param starsLMPrograms the value of field 'starsLMPrograms'.
    **/
    public void setStarsLMPrograms(StarsLMPrograms starsLMPrograms)
    {
        this._starsLMPrograms = starsLMPrograms;
    } //-- void setStarsLMPrograms(StarsLMPrograms) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsDeleteApplianceResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsDeleteApplianceResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsDeleteApplianceResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsDeleteApplianceResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
