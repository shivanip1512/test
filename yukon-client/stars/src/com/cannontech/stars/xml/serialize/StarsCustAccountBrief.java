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
public class StarsCustAccountBrief implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _accountID;

    /**
     * keeps track of state for field: _accountID
    **/
    private boolean _has_accountID;

    private java.lang.String _accountNumber;

    private java.lang.String _contactName;

    private java.lang.String _contPhoneNumber;

    private java.lang.String _streetAddress;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsCustAccountBrief() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccountBrief()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteAccountID()
    {
        this._has_accountID= false;
    } //-- void deleteAccountID() 

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
     * Returns the value of field 'accountNumber'.
     * 
     * @return the value of field 'accountNumber'.
    **/
    public java.lang.String getAccountNumber()
    {
        return this._accountNumber;
    } //-- java.lang.String getAccountNumber() 

    /**
     * Returns the value of field 'contPhoneNumber'.
     * 
     * @return the value of field 'contPhoneNumber'.
    **/
    public java.lang.String getContPhoneNumber()
    {
        return this._contPhoneNumber;
    } //-- java.lang.String getContPhoneNumber() 

    /**
     * Returns the value of field 'contactName'.
     * 
     * @return the value of field 'contactName'.
    **/
    public java.lang.String getContactName()
    {
        return this._contactName;
    } //-- java.lang.String getContactName() 

    /**
     * Returns the value of field 'streetAddress'.
     * 
     * @return the value of field 'streetAddress'.
    **/
    public java.lang.String getStreetAddress()
    {
        return this._streetAddress;
    } //-- java.lang.String getStreetAddress() 

    /**
    **/
    public boolean hasAccountID()
    {
        return this._has_accountID;
    } //-- boolean hasAccountID() 

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
     * Sets the value of field 'accountNumber'.
     * 
     * @param accountNumber the value of field 'accountNumber'.
    **/
    public void setAccountNumber(java.lang.String accountNumber)
    {
        this._accountNumber = accountNumber;
    } //-- void setAccountNumber(java.lang.String) 

    /**
     * Sets the value of field 'contPhoneNumber'.
     * 
     * @param contPhoneNumber the value of field 'contPhoneNumber'.
    **/
    public void setContPhoneNumber(java.lang.String contPhoneNumber)
    {
        this._contPhoneNumber = contPhoneNumber;
    } //-- void setContPhoneNumber(java.lang.String) 

    /**
     * Sets the value of field 'contactName'.
     * 
     * @param contactName the value of field 'contactName'.
    **/
    public void setContactName(java.lang.String contactName)
    {
        this._contactName = contactName;
    } //-- void setContactName(java.lang.String) 

    /**
     * Sets the value of field 'streetAddress'.
     * 
     * @param streetAddress the value of field 'streetAddress'.
    **/
    public void setStreetAddress(java.lang.String streetAddress)
    {
        this._streetAddress = streetAddress;
    } //-- void setStreetAddress(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsCustAccountBrief unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsCustAccountBrief) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsCustAccountBrief.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsCustAccountBrief unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
