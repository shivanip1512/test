/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsCustomerContact.java,v 1.10 2002/09/26 22:26:25 zyao Exp $
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
 * @version $Revision: 1.10 $ $Date: 2002/09/26 22:26:25 $
**/
public abstract class StarsCustomerContact implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _lastName;

    private java.lang.String _firstName;

    private java.lang.String _homePhone;

    private java.lang.String _workPhone;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerContact() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerContact()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'firstName'.
     * 
     * @return the value of field 'firstName'.
    **/
    public java.lang.String getFirstName()
    {
        return this._firstName;
    } //-- java.lang.String getFirstName() 

    /**
     * Returns the value of field 'homePhone'.
     * 
     * @return the value of field 'homePhone'.
    **/
    public java.lang.String getHomePhone()
    {
        return this._homePhone;
    } //-- java.lang.String getHomePhone() 

    /**
     * Returns the value of field 'lastName'.
     * 
     * @return the value of field 'lastName'.
    **/
    public java.lang.String getLastName()
    {
        return this._lastName;
    } //-- java.lang.String getLastName() 

    /**
     * Returns the value of field 'workPhone'.
     * 
     * @return the value of field 'workPhone'.
    **/
    public java.lang.String getWorkPhone()
    {
        return this._workPhone;
    } //-- java.lang.String getWorkPhone() 

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
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * Sets the value of field 'firstName'.
     * 
     * @param firstName the value of field 'firstName'.
    **/
    public void setFirstName(java.lang.String firstName)
    {
        this._firstName = firstName;
    } //-- void setFirstName(java.lang.String) 

    /**
     * Sets the value of field 'homePhone'.
     * 
     * @param homePhone the value of field 'homePhone'.
    **/
    public void setHomePhone(java.lang.String homePhone)
    {
        this._homePhone = homePhone;
    } //-- void setHomePhone(java.lang.String) 

    /**
     * Sets the value of field 'lastName'.
     * 
     * @param lastName the value of field 'lastName'.
    **/
    public void setLastName(java.lang.String lastName)
    {
        this._lastName = lastName;
    } //-- void setLastName(java.lang.String) 

    /**
     * Sets the value of field 'workPhone'.
     * 
     * @param workPhone the value of field 'workPhone'.
    **/
    public void setWorkPhone(java.lang.String workPhone)
    {
        this._workPhone = workPhone;
    } //-- void setWorkPhone(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
