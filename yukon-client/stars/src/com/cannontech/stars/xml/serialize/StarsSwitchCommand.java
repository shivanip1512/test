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
public class StarsSwitchCommand implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Enable programs of a customer account
    **/
    private StarsEnableService _starsEnableService;

    /**
     * Disable programs of a customer account
    **/
    private StarsDisableService _starsDisableService;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsSwitchCommand() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsSwitchCommand()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsDisableService'. The field
     * 'starsDisableService' has the following description: Disable
     * programs of a customer account
     * 
     * @return the value of field 'starsDisableService'.
    **/
    public StarsDisableService getStarsDisableService()
    {
        return this._starsDisableService;
    } //-- StarsDisableService getStarsDisableService() 

    /**
     * Returns the value of field 'starsEnableService'. The field
     * 'starsEnableService' has the following description: Enable
     * programs of a customer account
     * 
     * @return the value of field 'starsEnableService'.
    **/
    public StarsEnableService getStarsEnableService()
    {
        return this._starsEnableService;
    } //-- StarsEnableService getStarsEnableService() 

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
     * Sets the value of field 'starsDisableService'. The field
     * 'starsDisableService' has the following description: Disable
     * programs of a customer account
     * 
     * @param starsDisableService the value of field
     * 'starsDisableService'.
    **/
    public void setStarsDisableService(StarsDisableService starsDisableService)
    {
        this._starsDisableService = starsDisableService;
    } //-- void setStarsDisableService(StarsDisableService) 

    /**
     * Sets the value of field 'starsEnableService'. The field
     * 'starsEnableService' has the following description: Enable
     * programs of a customer account
     * 
     * @param starsEnableService the value of field
     * 'starsEnableService'.
    **/
    public void setStarsEnableService(StarsEnableService starsEnableService)
    {
        this._starsEnableService = starsEnableService;
    } //-- void setStarsEnableService(StarsEnableService) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsSwitchCommand unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsSwitchCommand) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsSwitchCommand.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsSwitchCommand unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
