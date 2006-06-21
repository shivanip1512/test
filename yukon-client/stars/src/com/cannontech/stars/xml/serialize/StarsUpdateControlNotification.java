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
public class StarsUpdateControlNotification implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ContactNotification _contactNotification;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsUpdateControlNotification() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateControlNotification()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'contactNotification'.
     * 
     * @return the value of field 'contactNotification'.
    **/
    public ContactNotification getContactNotification()
    {
        return this._contactNotification;
    } //-- ContactNotification getContactNotification() 

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
     * Sets the value of field 'contactNotification'.
     * 
     * @param contactNotification the value of field
     * 'contactNotification'.
    **/
    public void setContactNotification(ContactNotification contactNotification)
    {
        this._contactNotification = contactNotification;
    } //-- void setContactNotification(ContactNotification) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsUpdateControlNotification unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsUpdateControlNotification) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsUpdateControlNotification.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsUpdateControlNotification unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
