/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id: StarsOperation.java,v 1.1 2002/07/16 19:50:08 Yao Exp $
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
 * Root element
 * @version $Revision: 1.1 $ $Date: 2002/07/16 19:50:08 $
**/
public class StarsOperation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Login to the STARS server
    **/
    private StarsLogin _starsLogin;

    /**
     * Indicate that the requested operation is successful
    **/
    private StarsSuccess _starsSuccess;

    /**
     * Indicate that the requested operation is failed
    **/
    private StarsFailure _starsFailure;

    /**
     * Logoff from the STARS server
    **/
    private java.lang.String _starsLogoff;

    /**
     * Create new customer account
    **/
    private StarsNewCustomerAccount _starsNewCustomerAccount;

    /**
     * Search for existing customer account
    **/
    private StarsSearchCustomerAccount _starsSearchCustomerAccount;

    /**
     * Return information about a customer account
    **/
    private StarsCustomerAccountInformation _starsCustomerAccountInformation;

    /**
     * Update a customer account
    **/
    private StarsUpdateCustomerAccount _starsUpdateCustomerAccount;

    /**
     * Update the LM programs for a customer account
    **/
    private StarsUpdateLMPrograms _starsUpdateLMPrograms;

    /**
     * Enable programs of a customer account
    **/
    private StarsEnableService _starsEnableService;

    /**
     * Disable programs of a customer account
    **/
    private StarsDisableService _starsDisableService;

    /**
     * Get LM control history of a LM program
    **/
    private StarsGetLMControlHistory _starsGetLMControlHistory;

    private StarsLMControlHistory _starsLMControlHistory;


      //----------------/
     //- Constructors -/
    //----------------/

    public StarsOperation() {
        super();
    } //-- com.cannontech.stars.xml.serialize.StarsOperation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public StarsCustomerAccountInformation getStarsCustomerAccountInformation()
    {
        return this._starsCustomerAccountInformation;
    } //-- StarsCustomerAccountInformation getStarsCustomerAccountInformation() 

    /**
    **/
    public StarsDisableService getStarsDisableService()
    {
        return this._starsDisableService;
    } //-- StarsDisableService getStarsDisableService() 

    /**
    **/
    public StarsEnableService getStarsEnableService()
    {
        return this._starsEnableService;
    } //-- StarsEnableService getStarsEnableService() 

    /**
    **/
    public StarsFailure getStarsFailure()
    {
        return this._starsFailure;
    } //-- StarsFailure getStarsFailure() 

    /**
    **/
    public StarsGetLMControlHistory getStarsGetLMControlHistory()
    {
        return this._starsGetLMControlHistory;
    } //-- StarsGetLMControlHistory getStarsGetLMControlHistory() 

    /**
    **/
    public StarsLMControlHistory getStarsLMControlHistory()
    {
        return this._starsLMControlHistory;
    } //-- StarsLMControlHistory getStarsLMControlHistory() 

    /**
    **/
    public StarsLogin getStarsLogin()
    {
        return this._starsLogin;
    } //-- StarsLogin getStarsLogin() 

    /**
    **/
    public java.lang.String getStarsLogoff()
    {
        return this._starsLogoff;
    } //-- java.lang.String getStarsLogoff() 

    /**
    **/
    public StarsNewCustomerAccount getStarsNewCustomerAccount()
    {
        return this._starsNewCustomerAccount;
    } //-- StarsNewCustomerAccount getStarsNewCustomerAccount() 

    /**
    **/
    public StarsSearchCustomerAccount getStarsSearchCustomerAccount()
    {
        return this._starsSearchCustomerAccount;
    } //-- StarsSearchCustomerAccount getStarsSearchCustomerAccount() 

    /**
    **/
    public StarsSuccess getStarsSuccess()
    {
        return this._starsSuccess;
    } //-- StarsSuccess getStarsSuccess() 

    /**
    **/
    public StarsUpdateCustomerAccount getStarsUpdateCustomerAccount()
    {
        return this._starsUpdateCustomerAccount;
    } //-- StarsUpdateCustomerAccount getStarsUpdateCustomerAccount() 

    /**
    **/
    public StarsUpdateLMPrograms getStarsUpdateLMPrograms()
    {
        return this._starsUpdateLMPrograms;
    } //-- StarsUpdateLMPrograms getStarsUpdateLMPrograms() 

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
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler) 

    /**
     * 
     * @param starsCustomerAccountInformation
    **/
    public void setStarsCustomerAccountInformation(StarsCustomerAccountInformation starsCustomerAccountInformation)
    {
        this._starsCustomerAccountInformation = starsCustomerAccountInformation;
    } //-- void setStarsCustomerAccountInformation(StarsCustomerAccountInformation) 

    /**
     * 
     * @param starsDisableService
    **/
    public void setStarsDisableService(StarsDisableService starsDisableService)
    {
        this._starsDisableService = starsDisableService;
    } //-- void setStarsDisableService(StarsDisableService) 

    /**
     * 
     * @param starsEnableService
    **/
    public void setStarsEnableService(StarsEnableService starsEnableService)
    {
        this._starsEnableService = starsEnableService;
    } //-- void setStarsEnableService(StarsEnableService) 

    /**
     * 
     * @param starsFailure
    **/
    public void setStarsFailure(StarsFailure starsFailure)
    {
        this._starsFailure = starsFailure;
    } //-- void setStarsFailure(StarsFailure) 

    /**
     * 
     * @param starsGetLMControlHistory
    **/
    public void setStarsGetLMControlHistory(StarsGetLMControlHistory starsGetLMControlHistory)
    {
        this._starsGetLMControlHistory = starsGetLMControlHistory;
    } //-- void setStarsGetLMControlHistory(StarsGetLMControlHistory) 

    /**
     * 
     * @param starsLMControlHistory
    **/
    public void setStarsLMControlHistory(StarsLMControlHistory starsLMControlHistory)
    {
        this._starsLMControlHistory = starsLMControlHistory;
    } //-- void setStarsLMControlHistory(StarsLMControlHistory) 

    /**
     * 
     * @param starsLogin
    **/
    public void setStarsLogin(StarsLogin starsLogin)
    {
        this._starsLogin = starsLogin;
    } //-- void setStarsLogin(StarsLogin) 

    /**
     * 
     * @param starsLogoff
    **/
    public void setStarsLogoff(java.lang.String starsLogoff)
    {
        this._starsLogoff = starsLogoff;
    } //-- void setStarsLogoff(java.lang.String) 

    /**
     * 
     * @param starsNewCustomerAccount
    **/
    public void setStarsNewCustomerAccount(StarsNewCustomerAccount starsNewCustomerAccount)
    {
        this._starsNewCustomerAccount = starsNewCustomerAccount;
    } //-- void setStarsNewCustomerAccount(StarsNewCustomerAccount) 

    /**
     * 
     * @param starsSearchCustomerAccount
    **/
    public void setStarsSearchCustomerAccount(StarsSearchCustomerAccount starsSearchCustomerAccount)
    {
        this._starsSearchCustomerAccount = starsSearchCustomerAccount;
    } //-- void setStarsSearchCustomerAccount(StarsSearchCustomerAccount) 

    /**
     * 
     * @param starsSuccess
    **/
    public void setStarsSuccess(StarsSuccess starsSuccess)
    {
        this._starsSuccess = starsSuccess;
    } //-- void setStarsSuccess(StarsSuccess) 

    /**
     * 
     * @param starsUpdateCustomerAccount
    **/
    public void setStarsUpdateCustomerAccount(StarsUpdateCustomerAccount starsUpdateCustomerAccount)
    {
        this._starsUpdateCustomerAccount = starsUpdateCustomerAccount;
    } //-- void setStarsUpdateCustomerAccount(StarsUpdateCustomerAccount) 

    /**
     * 
     * @param starsUpdateLMPrograms
    **/
    public void setStarsUpdateLMPrograms(StarsUpdateLMPrograms starsUpdateLMPrograms)
    {
        this._starsUpdateLMPrograms = starsUpdateLMPrograms;
    } //-- void setStarsUpdateLMPrograms(StarsUpdateLMPrograms) 

    /**
     * 
     * @param reader
    **/
    public static com.cannontech.stars.xml.serialize.StarsOperation unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.cannontech.stars.xml.serialize.StarsOperation) Unmarshaller.unmarshal(com.cannontech.stars.xml.serialize.StarsOperation.class, reader);
    } //-- com.cannontech.stars.xml.serialize.StarsOperation unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
