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
public class StarsYukonSwitchCommandResponse implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLMHardware _starsLMHardware;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsYukonSwitchCommandResponse() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse()


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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
