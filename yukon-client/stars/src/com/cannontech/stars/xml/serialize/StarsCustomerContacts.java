/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsCustomerContacts.java,v 1.15 2004/04/22 17:07:36 zyao Exp $
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
 * @version $Revision: 1.15 $ $Date: 2004/04/22 17:07:36 $
**/
public abstract class StarsCustomerContacts implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private PrimaryContact _primaryContact;

    private java.util.Vector _additionalContactList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustomerContacts() {
        super();
        _additionalContactList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustomerContacts()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAdditionalContact
    **/
    public void addAdditionalContact(AdditionalContact vAdditionalContact)
        throws java.lang.IndexOutOfBoundsException
    {
        _additionalContactList.addElement(vAdditionalContact);
    } //-- void addAdditionalContact(AdditionalContact) 

    /**
     * 
     * 
     * @param index
     * @param vAdditionalContact
    **/
    public void addAdditionalContact(int index, AdditionalContact vAdditionalContact)
        throws java.lang.IndexOutOfBoundsException
    {
        _additionalContactList.insertElementAt(vAdditionalContact, index);
    } //-- void addAdditionalContact(int, AdditionalContact) 

    /**
    **/
    public java.util.Enumeration enumerateAdditionalContact()
    {
        return _additionalContactList.elements();
    } //-- java.util.Enumeration enumerateAdditionalContact() 

    /**
     * 
     * 
     * @param index
    **/
    public AdditionalContact getAdditionalContact(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _additionalContactList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (AdditionalContact) _additionalContactList.elementAt(index);
    } //-- AdditionalContact getAdditionalContact(int) 

    /**
    **/
    public AdditionalContact[] getAdditionalContact()
    {
        int size = _additionalContactList.size();
        AdditionalContact[] mArray = new AdditionalContact[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (AdditionalContact) _additionalContactList.elementAt(index);
        }
        return mArray;
    } //-- AdditionalContact[] getAdditionalContact() 

    /**
    **/
    public int getAdditionalContactCount()
    {
        return _additionalContactList.size();
    } //-- int getAdditionalContactCount() 

    /**
     * Returns the value of field 'primaryContact'.
     * 
     * @return the value of field 'primaryContact'.
    **/
    public PrimaryContact getPrimaryContact()
    {
        return this._primaryContact;
    } //-- PrimaryContact getPrimaryContact() 

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
     * 
     * 
     * @param index
    **/
    public AdditionalContact removeAdditionalContact(int index)
    {
        java.lang.Object obj = _additionalContactList.elementAt(index);
        _additionalContactList.removeElementAt(index);
        return (AdditionalContact) obj;
    } //-- AdditionalContact removeAdditionalContact(int) 

    /**
    **/
    public void removeAllAdditionalContact()
    {
        _additionalContactList.removeAllElements();
    } //-- void removeAllAdditionalContact() 

    /**
     * 
     * 
     * @param index
     * @param vAdditionalContact
    **/
    public void setAdditionalContact(int index, AdditionalContact vAdditionalContact)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _additionalContactList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _additionalContactList.setElementAt(vAdditionalContact, index);
    } //-- void setAdditionalContact(int, AdditionalContact) 

    /**
     * 
     * 
     * @param additionalContactArray
    **/
    public void setAdditionalContact(AdditionalContact[] additionalContactArray)
    {
        //-- copy array
        _additionalContactList.removeAllElements();
        for (int i = 0; i < additionalContactArray.length; i++) {
            _additionalContactList.addElement(additionalContactArray[i]);
        }
    } //-- void setAdditionalContact(AdditionalContact) 

    /**
     * Sets the value of field 'primaryContact'.
     * 
     * @param primaryContact the value of field 'primaryContact'.
    **/
    public void setPrimaryContact(PrimaryContact primaryContact)
    {
        this._primaryContact = primaryContact;
    } //-- void setPrimaryContact(PrimaryContact) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
