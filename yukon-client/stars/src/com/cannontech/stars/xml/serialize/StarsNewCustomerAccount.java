/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsNewCustomerAccount.java,v 1.75 2004/05/18 18:33:56 zyao Exp $
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
 * Create new customer account
 * 
 * @version $Revision: 1.75 $ $Date: 2004/05/18 18:33:56 $
**/
public class StarsNewCustomerAccount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsCustomerAccount _starsCustomerAccount;

    private StarsSULMPrograms _starsSULMPrograms;

    private StarsUpdateLogin _starsUpdateLogin;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsNewCustomerAccount() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsNewCustomerAccount()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'starsCustomerAccount'.
     * 
     * @return the value of field 'starsCustomerAccount'.
    **/
    public StarsCustomerAccount getStarsCustomerAccount()
    {
        return this._starsCustomerAccount;
    } //-- StarsCustomerAccount getStarsCustomerAccount() 

    /**
     * Returns the value of field 'starsSULMPrograms'.
     * 
     * @return the value of field 'starsSULMPrograms'.
    **/
    public StarsSULMPrograms getStarsSULMPrograms()
    {
        return this._starsSULMPrograms;
    } //-- StarsSULMPrograms getStarsSULMPrograms() 

    /**
     * Returns the value of field 'starsUpdateLogin'.
     * 
     * @return the value of field 'starsUpdateLogin'.
    **/
    public StarsUpdateLogin getStarsUpdateLogin()
    {
        return this._starsUpdateLogin;
    } //-- StarsUpdateLogin getStarsUpdateLogin() 

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
     * Sets the value of field 'starsCustomerAccount'.
     * 
     * @param starsCustomerAccount the value of field
     * 'starsCustomerAccount'.
    **/
    public void setStarsCustomerAccount(StarsCustomerAccount starsCustomerAccount)
    {
        this._starsCustomerAccount = starsCustomerAccount;
    } //-- void setStarsCustomerAccount(StarsCustomerAccount) 

    /**
     * Sets the value of field 'starsSULMPrograms'.
     * 
     * @param starsSULMPrograms the value of field
     * 'starsSULMPrograms'.
    **/
    public void setStarsSULMPrograms(StarsSULMPrograms starsSULMPrograms)
    {
        this._starsSULMPrograms = starsSULMPrograms;
    } //-- void setStarsSULMPrograms(StarsSULMPrograms) 

    /**
     * Sets the value of field 'starsUpdateLogin'.
     * 
     * @param starsUpdateLogin the value of field 'starsUpdateLogin'
    **/
    public void setStarsUpdateLogin(StarsUpdateLogin starsUpdateLogin)
    {
        this._starsUpdateLogin = starsUpdateLogin;
    } //-- void setStarsUpdateLogin(StarsUpdateLogin) 

    /**
     * 
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsNewCustomerAccount unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsNewCustomerAccount) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsNewCustomerAccount.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsNewCustomerAccount unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
