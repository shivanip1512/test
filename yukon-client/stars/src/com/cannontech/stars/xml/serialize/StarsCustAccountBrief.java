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

    private java.lang.String _accountNotes;


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
