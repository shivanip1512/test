/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsCustAccount.java,v 1.1 2002/07/16 19:50:03 Yao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:03 $
**/
public abstract class StarsCustAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _accountNumber;

    private java.lang.String _accountNotes;

    private java.lang.String _propertyNumber;

    private java.lang.String _propertyNotes;

    private StreetAddress _streetAddress;

    private StarsSiteInformation _starsSiteInformation;

    private BillingAddress _billingAddress;

    private PrimaryContact _primaryContact;

    private java.util.Vector _additionalContactList;

    private boolean _isCommercial;

    /**
     * keeps track of state for field: _isCommercial
    **/
    private boolean _has_isCommercial;

    private java.lang.String _company;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustAccount() {
        super();
        _additionalContactList = new Vector();
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
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
    **/
    public java.lang.String getAccountNotes()
    {
        return this._accountNotes;
    } //-- java.lang.String getAccountNotes() 

    /**
    **/
    public java.lang.String getAccountNumber()
    {
        return this._accountNumber;
    } //-- java.lang.String getAccountNumber() 

    /**
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
    **/
    public BillingAddress getBillingAddress()
    {
        return this._billingAddress;
    } //-- BillingAddress getBillingAddress() 

    /**
    **/
    public java.lang.String getCompany()
    {
        return this._company;
    } //-- java.lang.String getCompany() 

    /**
    **/
    public boolean getIsCommercial()
    {
        return this._isCommercial;
    } //-- boolean getIsCommercial() 

    /**
    **/
    public PrimaryContact getPrimaryContact()
    {
        return this._primaryContact;
    } //-- PrimaryContact getPrimaryContact() 

    /**
    **/
    public java.lang.String getPropertyNotes()
    {
        return this._propertyNotes;
    } //-- java.lang.String getPropertyNotes() 

    /**
    **/
    public java.lang.String getPropertyNumber()
    {
        return this._propertyNumber;
    } //-- java.lang.String getPropertyNumber() 

    /**
    **/
    public StarsSiteInformation getStarsSiteInformation()
    {
        return this._starsSiteInformation;
    } //-- StarsSiteInformation getStarsSiteInformation() 

    /**
    **/
    public StreetAddress getStreetAddress()
    {
        return this._streetAddress;
    } //-- StreetAddress getStreetAddress() 

    /**
    **/
    public boolean hasIsCommercial()
    {
        return this._has_isCommercial;
    } //-- boolean hasIsCommercial() 

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
     * @param index
    **/
    public AdditionalContact removeAdditionalContact(int index)
    {
        Object obj = _additionalContactList.elementAt(index);
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
     * @param accountNotes
    **/
    public void setAccountNotes(java.lang.String accountNotes)
    {
        this._accountNotes = accountNotes;
    } //-- void setAccountNotes(java.lang.String) 

    /**
     * 
     * @param accountNumber
    **/
    public void setAccountNumber(java.lang.String accountNumber)
    {
        this._accountNumber = accountNumber;
    } //-- void setAccountNumber(java.lang.String) 

    /**
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
     * 
     * @param billingAddress
    **/
    public void setBillingAddress(BillingAddress billingAddress)
    {
        this._billingAddress = billingAddress;
    } //-- void setBillingAddress(BillingAddress) 

    /**
     * 
     * @param company
    **/
    public void setCompany(java.lang.String company)
    {
        this._company = company;
    } //-- void setCompany(java.lang.String) 

    /**
     * 
     * @param isCommercial
    **/
    public void setIsCommercial(boolean isCommercial)
    {
        this._isCommercial = isCommercial;
        this._has_isCommercial = true;
    } //-- void setIsCommercial(boolean) 

    /**
     * 
     * @param primaryContact
    **/
    public void setPrimaryContact(PrimaryContact primaryContact)
    {
        this._primaryContact = primaryContact;
    } //-- void setPrimaryContact(PrimaryContact) 

    /**
     * 
     * @param propertyNotes
    **/
    public void setPropertyNotes(java.lang.String propertyNotes)
    {
        this._propertyNotes = propertyNotes;
    } //-- void setPropertyNotes(java.lang.String) 

    /**
     * 
     * @param propertyNumber
    **/
    public void setPropertyNumber(java.lang.String propertyNumber)
    {
        this._propertyNumber = propertyNumber;
    } //-- void setPropertyNumber(java.lang.String) 

    /**
     * 
     * @param starsSiteInformation
    **/
    public void setStarsSiteInformation(StarsSiteInformation starsSiteInformation)
    {
        this._starsSiteInformation = starsSiteInformation;
    } //-- void setStarsSiteInformation(StarsSiteInformation) 

    /**
     * 
     * @param streetAddress
    **/
    public void setStreetAddress(StreetAddress streetAddress)
    {
        this._streetAddress = streetAddress;
    } //-- void setStreetAddress(StreetAddress) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
