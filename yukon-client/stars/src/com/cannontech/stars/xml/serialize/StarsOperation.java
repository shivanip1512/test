/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsOperation.java,v 1.5 2002/09/06 22:37:26 zyao Exp $
 */

package com.cannontech.stars.xml.serialize;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Root element
 * 
 * @version $Revision: 1.5 $ $Date: 2002/09/06 22:37:26 $
**/
public class StarsOperation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StarsLogin _starsLogin;

    private StarsLogoff _starsLogoff;

    private StarsSuccess _starsSuccess;

    private StarsFailure _starsFailure;

    /**
     * Create new customer account
    **/
    private StarsNewCustomerAccount _starsNewCustomerAccount;

    private StarsSearchCustomerAccount _starsSearchCustomerAccount;

    private StarsSearchCustomerAccountResponse _starsSearchCustomerAccountResponse;

    /**
     * Update a customer account
    **/
    private StarsUpdateCustomerAccount _starsUpdateCustomerAccount;

    /**
     * Update the LM programs for a customer account
    **/
    private StarsUpdateLMPrograms _starsUpdateLMPrograms;

    private StarsSwitchCommand _starsSwitchCommand;

    private StarsSwitchCommandResponse _starsSwitchCommandResponse;

    /**
     * Get LM control history of a LM program
    **/
    private StarsGetLMControlHistory _starsGetLMControlHistory;

    private StarsGetLMControlHistoryResponse _starsGetLMControlHistoryResponse;

    private StarsCreateCallReport _starsCreateCallReport;

    private StarsGetCallReportHistory _starsGetCallReportHistory;

    private StarsGetCallReportHistoryResponse _starsGetCallReportHistoryResponse;

    private StarsCreateServiceRequest _starsCreateServiceRequest;

    private StarsGetServiceRequestHistory _starsGetServiceRequestHistory;

    private StarsGetServiceRequestHistoryResponse _starsGetServiceRequestHistoryResponse;

    private StarsGetEnrollmentPrograms _starsGetEnrollmentPrograms;

    private StarsGetEnrollmentProgramsResponse _starsGetEnrollmentProgramsResponse;


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
     * Returns the value of field 'starsCreateCallReport'.
     * 
     * @return the value of field 'starsCreateCallReport'.
    **/
    public StarsCreateCallReport getStarsCreateCallReport()
    {
        return this._starsCreateCallReport;
    } //-- StarsCreateCallReport getStarsCreateCallReport() 

    /**
     * Returns the value of field 'starsCreateServiceRequest'.
     * 
     * @return the value of field 'starsCreateServiceRequest'.
    **/
    public StarsCreateServiceRequest getStarsCreateServiceRequest()
    {
        return this._starsCreateServiceRequest;
    } //-- StarsCreateServiceRequest getStarsCreateServiceRequest() 

    /**
     * Returns the value of field 'starsFailure'.
     * 
     * @return the value of field 'starsFailure'.
    **/
    public StarsFailure getStarsFailure()
    {
        return this._starsFailure;
    } //-- StarsFailure getStarsFailure() 

    /**
     * Returns the value of field 'starsGetCallReportHistory'.
     * 
     * @return the value of field 'starsGetCallReportHistory'.
    **/
    public StarsGetCallReportHistory getStarsGetCallReportHistory()
    {
        return this._starsGetCallReportHistory;
    } //-- StarsGetCallReportHistory getStarsGetCallReportHistory() 

    /**
     * Returns the value of field
     * 'starsGetCallReportHistoryResponse'.
     * 
     * @return the value of field
     * 'starsGetCallReportHistoryResponse'.
    **/
    public StarsGetCallReportHistoryResponse getStarsGetCallReportHistoryResponse()
    {
        return this._starsGetCallReportHistoryResponse;
    } //-- StarsGetCallReportHistoryResponse getStarsGetCallReportHistoryResponse() 

    /**
     * Returns the value of field 'starsGetEnrollmentPrograms'.
     * 
     * @return the value of field 'starsGetEnrollmentPrograms'.
    **/
    public StarsGetEnrollmentPrograms getStarsGetEnrollmentPrograms()
    {
        return this._starsGetEnrollmentPrograms;
    } //-- StarsGetEnrollmentPrograms getStarsGetEnrollmentPrograms() 

    /**
     * Returns the value of field
     * 'starsGetEnrollmentProgramsResponse'.
     * 
     * @return the value of field
     * 'starsGetEnrollmentProgramsResponse'.
    **/
    public StarsGetEnrollmentProgramsResponse getStarsGetEnrollmentProgramsResponse()
    {
        return this._starsGetEnrollmentProgramsResponse;
    } //-- StarsGetEnrollmentProgramsResponse getStarsGetEnrollmentProgramsResponse() 

    /**
     * Returns the value of field 'starsGetLMControlHistory'. The
     * field 'starsGetLMControlHistory' has the following
     * description: Get LM control history of a LM program
     * 
     * @return the value of field 'starsGetLMControlHistory'.
    **/
    public StarsGetLMControlHistory getStarsGetLMControlHistory()
    {
        return this._starsGetLMControlHistory;
    } //-- StarsGetLMControlHistory getStarsGetLMControlHistory() 

    /**
     * Returns the value of field
     * 'starsGetLMControlHistoryResponse'.
     * 
     * @return the value of field 'starsGetLMControlHistoryResponse'
    **/
    public StarsGetLMControlHistoryResponse getStarsGetLMControlHistoryResponse()
    {
        return this._starsGetLMControlHistoryResponse;
    } //-- StarsGetLMControlHistoryResponse getStarsGetLMControlHistoryResponse() 

    /**
     * Returns the value of field 'starsGetServiceRequestHistory'.
     * 
     * @return the value of field 'starsGetServiceRequestHistory'.
    **/
    public StarsGetServiceRequestHistory getStarsGetServiceRequestHistory()
    {
        return this._starsGetServiceRequestHistory;
    } //-- StarsGetServiceRequestHistory getStarsGetServiceRequestHistory() 

    /**
     * Returns the value of field
     * 'starsGetServiceRequestHistoryResponse'.
     * 
     * @return the value of field
     * 'starsGetServiceRequestHistoryResponse'.
    **/
    public StarsGetServiceRequestHistoryResponse getStarsGetServiceRequestHistoryResponse()
    {
        return this._starsGetServiceRequestHistoryResponse;
    } //-- StarsGetServiceRequestHistoryResponse getStarsGetServiceRequestHistoryResponse() 

    /**
     * Returns the value of field 'starsLogin'.
     * 
     * @return the value of field 'starsLogin'.
    **/
    public StarsLogin getStarsLogin()
    {
        return this._starsLogin;
    } //-- StarsLogin getStarsLogin() 

    /**
     * Returns the value of field 'starsLogoff'.
     * 
     * @return the value of field 'starsLogoff'.
    **/
    public StarsLogoff getStarsLogoff()
    {
        return this._starsLogoff;
    } //-- StarsLogoff getStarsLogoff() 

    /**
     * Returns the value of field 'starsNewCustomerAccount'. The
     * field 'starsNewCustomerAccount' has the following
     * description: Create new customer account
     * 
     * @return the value of field 'starsNewCustomerAccount'.
    **/
    public StarsNewCustomerAccount getStarsNewCustomerAccount()
    {
        return this._starsNewCustomerAccount;
    } //-- StarsNewCustomerAccount getStarsNewCustomerAccount() 

    /**
     * Returns the value of field 'starsSearchCustomerAccount'.
     * 
     * @return the value of field 'starsSearchCustomerAccount'.
    **/
    public StarsSearchCustomerAccount getStarsSearchCustomerAccount()
    {
        return this._starsSearchCustomerAccount;
    } //-- StarsSearchCustomerAccount getStarsSearchCustomerAccount() 

    /**
     * Returns the value of field
     * 'starsSearchCustomerAccountResponse'.
     * 
     * @return the value of field
     * 'starsSearchCustomerAccountResponse'.
    **/
    public StarsSearchCustomerAccountResponse getStarsSearchCustomerAccountResponse()
    {
        return this._starsSearchCustomerAccountResponse;
    } //-- StarsSearchCustomerAccountResponse getStarsSearchCustomerAccountResponse() 

    /**
     * Returns the value of field 'starsSuccess'.
     * 
     * @return the value of field 'starsSuccess'.
    **/
    public StarsSuccess getStarsSuccess()
    {
        return this._starsSuccess;
    } //-- StarsSuccess getStarsSuccess() 

    /**
     * Returns the value of field 'starsSwitchCommand'.
     * 
     * @return the value of field 'starsSwitchCommand'.
    **/
    public StarsSwitchCommand getStarsSwitchCommand()
    {
        return this._starsSwitchCommand;
    } //-- StarsSwitchCommand getStarsSwitchCommand() 

    /**
     * Returns the value of field 'starsSwitchCommandResponse'.
     * 
     * @return the value of field 'starsSwitchCommandResponse'.
    **/
    public StarsSwitchCommandResponse getStarsSwitchCommandResponse()
    {
        return this._starsSwitchCommandResponse;
    } //-- StarsSwitchCommandResponse getStarsSwitchCommandResponse() 

    /**
     * Returns the value of field 'starsUpdateCustomerAccount'. The
     * field 'starsUpdateCustomerAccount' has the following
     * description: Update a customer account
     * 
     * @return the value of field 'starsUpdateCustomerAccount'.
    **/
    public StarsUpdateCustomerAccount getStarsUpdateCustomerAccount()
    {
        return this._starsUpdateCustomerAccount;
    } //-- StarsUpdateCustomerAccount getStarsUpdateCustomerAccount() 

    /**
     * Returns the value of field 'starsUpdateLMPrograms'. The
     * field 'starsUpdateLMPrograms' has the following description:
     * Update the LM programs for a customer account
     * 
     * @return the value of field 'starsUpdateLMPrograms'.
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
     * Sets the value of field 'starsCreateCallReport'.
     * 
     * @param starsCreateCallReport the value of field
     * 'starsCreateCallReport'.
    **/
    public void setStarsCreateCallReport(StarsCreateCallReport starsCreateCallReport)
    {
        this._starsCreateCallReport = starsCreateCallReport;
    } //-- void setStarsCreateCallReport(StarsCreateCallReport) 

    /**
     * Sets the value of field 'starsCreateServiceRequest'.
     * 
     * @param starsCreateServiceRequest the value of field
     * 'starsCreateServiceRequest'.
    **/
    public void setStarsCreateServiceRequest(StarsCreateServiceRequest starsCreateServiceRequest)
    {
        this._starsCreateServiceRequest = starsCreateServiceRequest;
    } //-- void setStarsCreateServiceRequest(StarsCreateServiceRequest) 

    /**
     * Sets the value of field 'starsFailure'.
     * 
     * @param starsFailure the value of field 'starsFailure'.
    **/
    public void setStarsFailure(StarsFailure starsFailure)
    {
        this._starsFailure = starsFailure;
    } //-- void setStarsFailure(StarsFailure) 

    /**
     * Sets the value of field 'starsGetCallReportHistory'.
     * 
     * @param starsGetCallReportHistory the value of field
     * 'starsGetCallReportHistory'.
    **/
    public void setStarsGetCallReportHistory(StarsGetCallReportHistory starsGetCallReportHistory)
    {
        this._starsGetCallReportHistory = starsGetCallReportHistory;
    } //-- void setStarsGetCallReportHistory(StarsGetCallReportHistory) 

    /**
     * Sets the value of field 'starsGetCallReportHistoryResponse'.
     * 
     * @param starsGetCallReportHistoryResponse the value of field
     * 'starsGetCallReportHistoryResponse'.
    **/
    public void setStarsGetCallReportHistoryResponse(StarsGetCallReportHistoryResponse starsGetCallReportHistoryResponse)
    {
        this._starsGetCallReportHistoryResponse = starsGetCallReportHistoryResponse;
    } //-- void setStarsGetCallReportHistoryResponse(StarsGetCallReportHistoryResponse) 

    /**
     * Sets the value of field 'starsGetEnrollmentPrograms'.
     * 
     * @param starsGetEnrollmentPrograms the value of field
     * 'starsGetEnrollmentPrograms'.
    **/
    public void setStarsGetEnrollmentPrograms(StarsGetEnrollmentPrograms starsGetEnrollmentPrograms)
    {
        this._starsGetEnrollmentPrograms = starsGetEnrollmentPrograms;
    } //-- void setStarsGetEnrollmentPrograms(StarsGetEnrollmentPrograms) 

    /**
     * Sets the value of field
     * 'starsGetEnrollmentProgramsResponse'.
     * 
     * @param starsGetEnrollmentProgramsResponse the value of field
     * 'starsGetEnrollmentProgramsResponse'.
    **/
    public void setStarsGetEnrollmentProgramsResponse(StarsGetEnrollmentProgramsResponse starsGetEnrollmentProgramsResponse)
    {
        this._starsGetEnrollmentProgramsResponse = starsGetEnrollmentProgramsResponse;
    } //-- void setStarsGetEnrollmentProgramsResponse(StarsGetEnrollmentProgramsResponse) 

    /**
     * Sets the value of field 'starsGetLMControlHistory'. The
     * field 'starsGetLMControlHistory' has the following
     * description: Get LM control history of a LM program
     * 
     * @param starsGetLMControlHistory the value of field
     * 'starsGetLMControlHistory'.
    **/
    public void setStarsGetLMControlHistory(StarsGetLMControlHistory starsGetLMControlHistory)
    {
        this._starsGetLMControlHistory = starsGetLMControlHistory;
    } //-- void setStarsGetLMControlHistory(StarsGetLMControlHistory) 

    /**
     * Sets the value of field 'starsGetLMControlHistoryResponse'.
     * 
     * @param starsGetLMControlHistoryResponse the value of field
     * 'starsGetLMControlHistoryResponse'.
    **/
    public void setStarsGetLMControlHistoryResponse(StarsGetLMControlHistoryResponse starsGetLMControlHistoryResponse)
    {
        this._starsGetLMControlHistoryResponse = starsGetLMControlHistoryResponse;
    } //-- void setStarsGetLMControlHistoryResponse(StarsGetLMControlHistoryResponse) 

    /**
     * Sets the value of field 'starsGetServiceRequestHistory'.
     * 
     * @param starsGetServiceRequestHistory the value of field
     * 'starsGetServiceRequestHistory'.
    **/
    public void setStarsGetServiceRequestHistory(StarsGetServiceRequestHistory starsGetServiceRequestHistory)
    {
        this._starsGetServiceRequestHistory = starsGetServiceRequestHistory;
    } //-- void setStarsGetServiceRequestHistory(StarsGetServiceRequestHistory) 

    /**
     * Sets the value of field
     * 'starsGetServiceRequestHistoryResponse'.
     * 
     * @param starsGetServiceRequestHistoryResponse the value of
     * field 'starsGetServiceRequestHistoryResponse'.
    **/
    public void setStarsGetServiceRequestHistoryResponse(StarsGetServiceRequestHistoryResponse starsGetServiceRequestHistoryResponse)
    {
        this._starsGetServiceRequestHistoryResponse = starsGetServiceRequestHistoryResponse;
    } //-- void setStarsGetServiceRequestHistoryResponse(StarsGetServiceRequestHistoryResponse) 

    /**
     * Sets the value of field 'starsLogin'.
     * 
     * @param starsLogin the value of field 'starsLogin'.
    **/
    public void setStarsLogin(StarsLogin starsLogin)
    {
        this._starsLogin = starsLogin;
    } //-- void setStarsLogin(StarsLogin) 

    /**
     * Sets the value of field 'starsLogoff'.
     * 
     * @param starsLogoff the value of field 'starsLogoff'.
    **/
    public void setStarsLogoff(StarsLogoff starsLogoff)
    {
        this._starsLogoff = starsLogoff;
    } //-- void setStarsLogoff(StarsLogoff) 

    /**
     * Sets the value of field 'starsNewCustomerAccount'. The field
     * 'starsNewCustomerAccount' has the following description:
     * Create new customer account
     * 
     * @param starsNewCustomerAccount the value of field
     * 'starsNewCustomerAccount'.
    **/
    public void setStarsNewCustomerAccount(StarsNewCustomerAccount starsNewCustomerAccount)
    {
        this._starsNewCustomerAccount = starsNewCustomerAccount;
    } //-- void setStarsNewCustomerAccount(StarsNewCustomerAccount) 

    /**
     * Sets the value of field 'starsSearchCustomerAccount'.
     * 
     * @param starsSearchCustomerAccount the value of field
     * 'starsSearchCustomerAccount'.
    **/
    public void setStarsSearchCustomerAccount(StarsSearchCustomerAccount starsSearchCustomerAccount)
    {
        this._starsSearchCustomerAccount = starsSearchCustomerAccount;
    } //-- void setStarsSearchCustomerAccount(StarsSearchCustomerAccount) 

    /**
     * Sets the value of field
     * 'starsSearchCustomerAccountResponse'.
     * 
     * @param starsSearchCustomerAccountResponse the value of field
     * 'starsSearchCustomerAccountResponse'.
    **/
    public void setStarsSearchCustomerAccountResponse(StarsSearchCustomerAccountResponse starsSearchCustomerAccountResponse)
    {
        this._starsSearchCustomerAccountResponse = starsSearchCustomerAccountResponse;
    } //-- void setStarsSearchCustomerAccountResponse(StarsSearchCustomerAccountResponse) 

    /**
     * Sets the value of field 'starsSuccess'.
     * 
     * @param starsSuccess the value of field 'starsSuccess'.
    **/
    public void setStarsSuccess(StarsSuccess starsSuccess)
    {
        this._starsSuccess = starsSuccess;
    } //-- void setStarsSuccess(StarsSuccess) 

    /**
     * Sets the value of field 'starsSwitchCommand'.
     * 
     * @param starsSwitchCommand the value of field
     * 'starsSwitchCommand'.
    **/
    public void setStarsSwitchCommand(StarsSwitchCommand starsSwitchCommand)
    {
        this._starsSwitchCommand = starsSwitchCommand;
    } //-- void setStarsSwitchCommand(StarsSwitchCommand) 

    /**
     * Sets the value of field 'starsSwitchCommandResponse'.
     * 
     * @param starsSwitchCommandResponse the value of field
     * 'starsSwitchCommandResponse'.
    **/
    public void setStarsSwitchCommandResponse(StarsSwitchCommandResponse starsSwitchCommandResponse)
    {
        this._starsSwitchCommandResponse = starsSwitchCommandResponse;
    } //-- void setStarsSwitchCommandResponse(StarsSwitchCommandResponse) 

    /**
     * Sets the value of field 'starsUpdateCustomerAccount'. The
     * field 'starsUpdateCustomerAccount' has the following
     * description: Update a customer account
     * 
     * @param starsUpdateCustomerAccount the value of field
     * 'starsUpdateCustomerAccount'.
    **/
    public void setStarsUpdateCustomerAccount(StarsUpdateCustomerAccount starsUpdateCustomerAccount)
    {
        this._starsUpdateCustomerAccount = starsUpdateCustomerAccount;
    } //-- void setStarsUpdateCustomerAccount(StarsUpdateCustomerAccount) 

    /**
     * Sets the value of field 'starsUpdateLMPrograms'. The field
     * 'starsUpdateLMPrograms' has the following description:
     * Update the LM programs for a customer account
     * 
     * @param starsUpdateLMPrograms the value of field
     * 'starsUpdateLMPrograms'.
    **/
    public void setStarsUpdateLMPrograms(StarsUpdateLMPrograms starsUpdateLMPrograms)
    {
        this._starsUpdateLMPrograms = starsUpdateLMPrograms;
    } //-- void setStarsUpdateLMPrograms(StarsUpdateLMPrograms) 

    /**
     * 
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
