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
public class StarsProgramSignUpResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMPrograms _starsLMPrograms;

    private StarsAppliances _starsAppliances;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsProgramSignUpResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsProgramSignUpResponse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsAppliances'.
     * 
     * @return the value of field 'starsAppliances'.
    **/
    public StarsAppliances getStarsAppliances()
    {
        return this._starsAppliances;
    } //-- StarsAppliances getStarsAppliances() 

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
     * Sets the value of field 'starsAppliances'.
     * 
     * @param starsAppliances the value of field 'starsAppliances'.
    **/
    public void setStarsAppliances(StarsAppliances starsAppliances)
    {
        this._starsAppliances = starsAppliances;
    } //-- void setStarsAppliances(StarsAppliances) 

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
    public static com.cannontech.stars.xml.serialize.StarsProgramSignUpResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsProgramSignUpResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsProgramSignUpResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsProgramSignUpResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
