/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsCustomerContact.java,v 1.88 2004/10/26 21:15:55 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * 
 * 
 * @version $Revision: 1.88 $ $Date: 2004/10/26 21:15:55 $
**/
public abstract class StarsCustomerContact implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _contactID;

    /**
     * keeps track of state for field: _contactID
    **/
    private boolean _has_contactID;

    private java.lang.String _lastName;

    private java.lang.String _firstName;

    private java.util.Vector _contactNotificationList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerContact() {
        super();
        _contactNotificationList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerContact()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vContactNotification
    **/
    public void addContactNotification(ContactNotification vContactNotification)
        throws java.lang.IndexOutOfBoundsException
    {
        _contactNotificationList.addElement(vContactNotification);
    } //-- void addContactNotification(ContactNotification) 

    /**
     * 
     * 
     * @param index
     * @param vContactNotification
    **/
    public void addContactNotification(int index, ContactNotification vContactNotification)
        throws java.lang.IndexOutOfBoundsException
    {
        _contactNotificationList.insertElementAt(vContactNotification, index);
    } //-- void addContactNotification(int, ContactNotification) 

    /**
    **/
    public void deleteContactID()
    {
        this._has_contactID= false;
    } //-- void deleteContactID() 

    /**
    **/
    public java.util.Enumeration enumerateContactNotification()
    {
        return _contactNotificationList.elements();
    } //-- java.util.Enumeration enumerateContactNotification() 

    /**
     * Returns the value of field 'contactID'.
     * 
     * @return the value of field 'contactID'.
    **/
    public int getContactID()
    {
        return this._contactID;
    } //-- int getContactID() 

    /**
     * 
     * 
     * @param index
    **/
    public ContactNotification getContactNotification(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _contactNotificationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ContactNotification) _contactNotificationList.elementAt(index);
    } //-- ContactNotification getContactNotification(int) 

    /**
    **/
    public ContactNotification[] getContactNotification()
    {
        int size = _contactNotificationList.size();
        ContactNotification[] mArray = new ContactNotification[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ContactNotification) _contactNotificationList.elementAt(index);
        }
        return mArray;
    } //-- ContactNotification[] getContactNotification() 

    /**
    **/
    public int getContactNotificationCount()
    {
        return _contactNotificationList.size();
    } //-- int getContactNotificationCount() 

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
     * Returns the value of field 'lastName'.
     * 
     * @return the value of field 'lastName'.
    **/
    public java.lang.String getLastName()
    {
        return this._lastName;
    } //-- java.lang.String getLastName() 

    /**
    **/
    public boolean hasContactID()
    {
        return this._has_contactID;
    } //-- boolean hasContactID() 

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
    **/
    public void removeAllContactNotification()
    {
        _contactNotificationList.removeAllElements();
    } //-- void removeAllContactNotification() 

    /**
     * 
     * 
     * @param index
    **/
    public ContactNotification removeContactNotification(int index)
    {
        java.lang.Object obj = _contactNotificationList.elementAt(index);
        _contactNotificationList.removeElementAt(index);
        return (ContactNotification) obj;
    } //-- ContactNotification removeContactNotification(int) 

    /**
     * Sets the value of field 'contactID'.
     * 
     * @param contactID the value of field 'contactID'.
    **/
    public void setContactID(int contactID)
    {
        this._contactID = contactID;
        this._has_contactID = true;
    } //-- void setContactID(int) 

    /**
     * 
     * 
     * @param index
     * @param vContactNotification
    **/
    public void setContactNotification(int index, ContactNotification vContactNotification)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _contactNotificationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _contactNotificationList.setElementAt(vContactNotification, index);
    } //-- void setContactNotification(int, ContactNotification) 

    /**
     * 
     * 
     * @param contactNotificationArray
    **/
    public void setContactNotification(ContactNotification[] contactNotificationArray)
    {
        //-- copy array
        _contactNotificationList.removeAllElements();
        for (int i = 0; i < contactNotificationArray.length; i++) {
            _contactNotificationList.addElement(contactNotificationArray[i]);
        }
    } //-- void setContactNotification(ContactNotification) 

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
     * Sets the value of field 'lastName'.
     * 
     * @param lastName the value of field 'lastName'.
    **/
    public void setLastName(java.lang.String lastName)
    {
        this._lastName = lastName;
    } //-- void setLastName(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
