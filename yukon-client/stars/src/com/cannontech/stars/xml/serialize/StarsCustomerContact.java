/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsCustomerContact.java,v 1.1 2002/07/16 19:50:04 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:04 $
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
    **/
    public java.lang.String getFirstName()
    {
        return this._firstName;
    } //-- java.lang.String getFirstName() 

    /**
    **/
    public java.lang.String getHomePhone()
    {
        return this._homePhone;
    } //-- java.lang.String getHomePhone() 

    /**
    **/
    public java.lang.String getLastName()
    {
        return this._lastName;
    } //-- java.lang.String getLastName() 

    /**
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
     * @param out
    **/
    public abstract void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param handler
    **/
    public abstract void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException;

    /**
     * 
     * @param firstName
    **/
    public void setFirstName(java.lang.String firstName)
    {
        this._firstName = firstName;
    } //-- void setFirstName(java.lang.String) 

    /**
     * 
     * @param homePhone
    **/
    public void setHomePhone(java.lang.String homePhone)
    {
        this._homePhone = homePhone;
    } //-- void setHomePhone(java.lang.String) 

    /**
     * 
     * @param lastName
    **/
    public void setLastName(java.lang.String lastName)
    {
        this._lastName = lastName;
    } //-- void setLastName(java.lang.String) 

    /**
     * 
     * @param workPhone
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
