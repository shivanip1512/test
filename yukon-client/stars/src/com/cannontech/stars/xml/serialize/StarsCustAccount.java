/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsCustAccount.java,v 1.86 2004/08/06 17:35:08 zyao Exp $
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
 * @version $Revision: 1.86 $ $Date: 2004/08/06 17:35:08 $
**/
public abstract class StarsCustAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _accountID;

    /**
     * keeps track of state for field: _accountID
    **/
    private boolean _has_accountID;

    private int _customerID;

    /**
     * keeps track of state for field: _customerID
    **/
    private boolean _has_customerID;

    private java.lang.String _accountNumber;

    private boolean _isCommercial;

    /**
     * keeps track of state for field: _isCommercial
    **/
    private boolean _has_isCommercial;

    private java.lang.String _company;

    private java.lang.String _accountNotes;

    private java.lang.String _propertyNumber;

    private java.lang.String _propertyNotes;

    private StreetAddress _streetAddress;

    private StarsSiteInformation _starsSiteInformation;

    private BillingAddress _billingAddress;

    private PrimaryContact _primaryContact;

    private java.util.Vector _additionalContactList;

    private java.lang.String _timeZone;


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
    public void deleteAccountID()
    {
        this._has_accountID= false;
    } //-- void deleteAccountID() 

    /**
    **/
    public void deleteCustomerID()
    {
        this._has_customerID= false;
    } //-- void deleteCustomerID() 

    /**
    **/
    public void deleteIsCommercial()
    {
        this._has_isCommercial= false;
    } //-- void deleteIsCommercial() 

    /**
    **/
    public java.util.Enumeration enumerateAdditionalContact()
    {
        return _additionalContactList.elements();
    } //-- java.util.Enumeration enumerateAdditionalContact() 

    /**
     * Returns the value of field 'accountID'.
     * 
     * @return the value of field 'accountID'.
    **/
    public int getAccountID()
    {
        return this._accountID;
    } //-- int getAccountID() 

    /**
     * Returns the value of field 'accountNotes'.
     * 
     * @return the value of field 'accountNotes'.
    **/
    public java.lang.String getAccountNotes()
    {
        return this._accountNotes;
    } //-- java.lang.String getAccountNotes() 

    /**
     * Returns the value of field 'accountNumber'.
     * 
     * @return the value of field 'accountNumber'.
    **/
    public java.lang.String getAccountNumber()
    {
        return this._accountNumber;
    } //-- java.lang.String getAccountNumber() 

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
     * Returns the value of field 'billingAddress'.
     * 
     * @return the value of field 'billingAddress'.
    **/
    public BillingAddress getBillingAddress()
    {
        return this._billingAddress;
    } //-- BillingAddress getBillingAddress() 

    /**
     * Returns the value of field 'company'.
     * 
     * @return the value of field 'company'.
    **/
    public java.lang.String getCompany()
    {
        return this._company;
    } //-- java.lang.String getCompany() 

    /**
     * Returns the value of field 'customerID'.
     * 
     * @return the value of field 'customerID'.
    **/
    public int getCustomerID()
    {
        return this._customerID;
    } //-- int getCustomerID() 

    /**
     * Returns the value of field 'isCommercial'.
     * 
     * @return the value of field 'isCommercial'.
    **/
    public boolean getIsCommercial()
    {
        return this._isCommercial;
    } //-- boolean getIsCommercial() 

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
     * Returns the value of field 'propertyNotes'.
     * 
     * @return the value of field 'propertyNotes'.
    **/
    public java.lang.String getPropertyNotes()
    {
        return this._propertyNotes;
    } //-- java.lang.String getPropertyNotes() 

    /**
     * Returns the value of field 'propertyNumber'.
     * 
     * @return the value of field 'propertyNumber'.
    **/
    public java.lang.String getPropertyNumber()
    {
        return this._propertyNumber;
    } //-- java.lang.String getPropertyNumber() 

    /**
     * Returns the value of field 'starsSiteInformation'.
     * 
     * @return the value of field 'starsSiteInformation'.
    **/
    public StarsSiteInformation getStarsSiteInformation()
    {
        return this._starsSiteInformation;
    } //-- StarsSiteInformation getStarsSiteInformation() 

    /**
     * Returns the value of field 'streetAddress'.
     * 
     * @return the value of field 'streetAddress'.
    **/
    public StreetAddress getStreetAddress()
    {
        return this._streetAddress;
    } //-- StreetAddress getStreetAddress() 

    /**
     * Returns the value of field 'timeZone'.
     * 
     * @return the value of field 'timeZone'.
    **/
    public java.lang.String getTimeZone()
    {
        return this._timeZone;
    } //-- java.lang.String getTimeZone() 

    /**
    **/
    public boolean hasAccountID()
    {
        return this._has_accountID;
    } //-- boolean hasAccountID() 

    /**
    **/
    public boolean hasCustomerID()
    {
        return this._has_customerID;
    } //-- boolean hasCustomerID() 

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
     * Sets the value of field 'accountID'.
     * 
     * @param accountID the value of field 'accountID'.
    **/
    public void setAccountID(int accountID)
    {
        this._accountID = accountID;
        this._has_accountID = true;
    } //-- void setAccountID(int) 

    /**
     * Sets the value of field 'accountNotes'.
     * 
     * @param accountNotes the value of field 'accountNotes'.
    **/
    public void setAccountNotes(java.lang.String accountNotes)
    {
        this._accountNotes = accountNotes;
    } //-- void setAccountNotes(java.lang.String) 

    /**
     * Sets the value of field 'accountNumber'.
     * 
     * @param accountNumber the value of field 'accountNumber'.
    **/
    public void setAccountNumber(java.lang.String accountNumber)
    {
        this._accountNumber = accountNumber;
    } //-- void setAccountNumber(java.lang.String) 

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
     * Sets the value of field 'billingAddress'.
     * 
     * @param billingAddress the value of field 'billingAddress'.
    **/
    public void setBillingAddress(BillingAddress billingAddress)
    {
        this._billingAddress = billingAddress;
    } //-- void setBillingAddress(BillingAddress) 

    /**
     * Sets the value of field 'company'.
     * 
     * @param company the value of field 'company'.
    **/
    public void setCompany(java.lang.String company)
    {
        this._company = company;
    } //-- void setCompany(java.lang.String) 

    /**
     * Sets the value of field 'customerID'.
     * 
     * @param customerID the value of field 'customerID'.
    **/
    public void setCustomerID(int customerID)
    {
        this._customerID = customerID;
        this._has_customerID = true;
    } //-- void setCustomerID(int) 

    /**
     * Sets the value of field 'isCommercial'.
     * 
     * @param isCommercial the value of field 'isCommercial'.
    **/
    public void setIsCommercial(boolean isCommercial)
    {
        this._isCommercial = isCommercial;
        this._has_isCommercial = true;
    } //-- void setIsCommercial(boolean) 

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
     * Sets the value of field 'propertyNotes'.
     * 
     * @param propertyNotes the value of field 'propertyNotes'.
    **/
    public void setPropertyNotes(java.lang.String propertyNotes)
    {
        this._propertyNotes = propertyNotes;
    } //-- void setPropertyNotes(java.lang.String) 

    /**
     * Sets the value of field 'propertyNumber'.
     * 
     * @param propertyNumber the value of field 'propertyNumber'.
    **/
    public void setPropertyNumber(java.lang.String propertyNumber)
    {
        this._propertyNumber = propertyNumber;
    } //-- void setPropertyNumber(java.lang.String) 

    /**
     * Sets the value of field 'starsSiteInformation'.
     * 
     * @param starsSiteInformation the value of field
     * 'starsSiteInformation'.
    **/
    public void setStarsSiteInformation(StarsSiteInformation starsSiteInformation)
    {
        this._starsSiteInformation = starsSiteInformation;
    } //-- void setStarsSiteInformation(StarsSiteInformation) 

    /**
     * Sets the value of field 'streetAddress'.
     * 
     * @param streetAddress the value of field 'streetAddress'.
    **/
    public void setStreetAddress(StreetAddress streetAddress)
    {
        this._streetAddress = streetAddress;
    } //-- void setStreetAddress(StreetAddress) 

    /**
     * Sets the value of field 'timeZone'.
     * 
     * @param timeZone the value of field 'timeZone'.
    **/
    public void setTimeZone(java.lang.String timeZone)
    {
        this._timeZone = timeZone;
    } //-- void setTimeZone(java.lang.String) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
