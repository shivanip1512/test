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
public class StarsGetCustomerAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _accountID;

    /**
     * keeps track of state for field: _accountID
    **/
    private boolean _has_accountID;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsGetCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsGetCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

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
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsGetCustomerAccount unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsGetCustomerAccount) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsGetCustomerAccount.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsGetCustomerAccount unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
