/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsOperation.java,v 1.17 2002/12/09 23:11:40 zyao Exp $
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
 * Root element
 * 
 * @version $Revision: 1.17 $ $Date: 2002/12/09 23:11:40 $
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

    private StarsNewCustomerAccountResponse _starsNewCustomerAccountResponse;

    private StarsProgramSignUp _starsProgramSignUp;

    private StarsSearchCustomerAccount _starsSearchCustomerAccount;

    private StarsSearchCustomerAccountResponse _starsSearchCustomerAccountResponse;

    private StarsGetCustomerAccount _starsGetCustomerAccount;

    private StarsGetCustomerAccountResponse _starsGetCustomerAccountResponse;

    /**
     * Update a customer account
    **/
    private StarsUpdateCustomerAccount _starsUpdateCustomerAccount;

    private StarsUpdateCustomerAccountResponse _starsUpdateCustomerAccountResponse;

    /**
     * Update the LM programs for a customer account
    **/
    private StarsUpdateLMPrograms _starsUpdateLMPrograms;

    private StarsProgramOptOut _starsProgramOptOut;

    private StarsProgramOptOutResponse _starsProgramOptOutResponse;

    private StarsProgramReenable _starsProgramReenable;

    private StarsProgramReenableResponse _starsProgramReenableResponse;

    private StarsYukonSwitchCommand _starsYukonSwitchCommand;

    private StarsYukonSwitchCommandResponse _starsYukonSwitchCommandResponse;

    /**
     * Get LM control history of a LM program
    **/
    private StarsGetLMControlHistory _starsGetLMControlHistory;

    private StarsGetLMControlHistoryResponse _starsGetLMControlHistoryResponse;

    private StarsCreateCallReport _starsCreateCallReport;

    private StarsCreateCallReportResponse _starsCreateCallReportResponse;

    private StarsGetCallReportHistory _starsGetCallReportHistory;

    private StarsGetCallReportHistoryResponse _starsGetCallReportHistoryResponse;

    private StarsCreateServiceRequest _starsCreateServiceRequest;

    private StarsCreateServiceRequestResponse _starsCreateServiceRequestResponse;

    private StarsGetServiceRequestHistory _starsGetServiceRequestHistory;

    private StarsGetServiceRequestHistoryResponse _starsGetServiceRequestHistoryResponse;

    private StarsGetEnrollmentPrograms _starsGetEnrollmentPrograms;

    private StarsGetEnrollmentProgramsResponse _starsGetEnrollmentProgramsResponse;

    private StarsGetCustSelectionLists _starsGetCustSelectionLists;

    private StarsGetCustSelectionListsResponse _starsGetCustSelectionListsResponse;

    private StarsCreateAppliance _starsCreateAppliance;

    private StarsCreateApplianceResponse _starsCreateApplianceResponse;

    private StarsCreateLMHardware _starsCreateLMHardware;

    private StarsCreateLMHardwareResponse _starsCreateLMHardwareResponse;

    private StarsDefaultThermostatSettings _starsDefaultThermostatSettings;

    private StarsUpdateLMHardware _starsUpdateLMHardware;

    private StarsUpdateLMHardwareResponse _starsUpdateLMHardwareResponse;

    private StarsUpdateAppliance _starsUpdateAppliance;

    private StarsUpdateApplianceResponse _starsUpdateApplianceResponse;

    private StarsUpdateLogin _starsUpdateLogin;

    private StarsUpdateLoginResponse _starsUpdateLoginResponse;


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
     * Returns the value of field 'starsCreateAppliance'.
     * 
     * @return the value of field 'starsCreateAppliance'.
    **/
    public StarsCreateAppliance getStarsCreateAppliance()
    {
        return this._starsCreateAppliance;
    } //-- StarsCreateAppliance getStarsCreateAppliance() 

    /**
     * Returns the value of field 'starsCreateApplianceResponse'.
     * 
     * @return the value of field 'starsCreateApplianceResponse'.
    **/
    public StarsCreateApplianceResponse getStarsCreateApplianceResponse()
    {
        return this._starsCreateApplianceResponse;
    } //-- StarsCreateApplianceResponse getStarsCreateApplianceResponse() 

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
     * Returns the value of field 'starsCreateCallReportResponse'.
     * 
     * @return the value of field 'starsCreateCallReportResponse'.
    **/
    public StarsCreateCallReportResponse getStarsCreateCallReportResponse()
    {
        return this._starsCreateCallReportResponse;
    } //-- StarsCreateCallReportResponse getStarsCreateCallReportResponse() 

    /**
     * Returns the value of field 'starsCreateLMHardware'.
     * 
     * @return the value of field 'starsCreateLMHardware'.
    **/
    public StarsCreateLMHardware getStarsCreateLMHardware()
    {
        return this._starsCreateLMHardware;
    } //-- StarsCreateLMHardware getStarsCreateLMHardware() 

    /**
     * Returns the value of field 'starsCreateLMHardwareResponse'.
     * 
     * @return the value of field 'starsCreateLMHardwareResponse'.
    **/
    public StarsCreateLMHardwareResponse getStarsCreateLMHardwareResponse()
    {
        return this._starsCreateLMHardwareResponse;
    } //-- StarsCreateLMHardwareResponse getStarsCreateLMHardwareResponse() 

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
     * Returns the value of field
     * 'starsCreateServiceRequestResponse'.
     * 
     * @return the value of field
     * 'starsCreateServiceRequestResponse'.
    **/
    public StarsCreateServiceRequestResponse getStarsCreateServiceRequestResponse()
    {
        return this._starsCreateServiceRequestResponse;
    } //-- StarsCreateServiceRequestResponse getStarsCreateServiceRequestResponse() 

    /**
     * Returns the value of field 'starsDefaultThermostatSettings'.
     * 
     * @return the value of field 'starsDefaultThermostatSettings'.
    **/
    public StarsDefaultThermostatSettings getStarsDefaultThermostatSettings()
    {
        return this._starsDefaultThermostatSettings;
    } //-- StarsDefaultThermostatSettings getStarsDefaultThermostatSettings() 

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
     * Returns the value of field 'starsGetCustSelectionLists'.
     * 
     * @return the value of field 'starsGetCustSelectionLists'.
    **/
    public StarsGetCustSelectionLists getStarsGetCustSelectionLists()
    {
        return this._starsGetCustSelectionLists;
    } //-- StarsGetCustSelectionLists getStarsGetCustSelectionLists() 

    /**
     * Returns the value of field
     * 'starsGetCustSelectionListsResponse'.
     * 
     * @return the value of field
     * 'starsGetCustSelectionListsResponse'.
    **/
    public StarsGetCustSelectionListsResponse getStarsGetCustSelectionListsResponse()
    {
        return this._starsGetCustSelectionListsResponse;
    } //-- StarsGetCustSelectionListsResponse getStarsGetCustSelectionListsResponse() 

    /**
     * Returns the value of field 'starsGetCustomerAccount'.
     * 
     * @return the value of field 'starsGetCustomerAccount'.
    **/
    public StarsGetCustomerAccount getStarsGetCustomerAccount()
    {
        return this._starsGetCustomerAccount;
    } //-- StarsGetCustomerAccount getStarsGetCustomerAccount() 

    /**
     * Returns the value of field
     * 'starsGetCustomerAccountResponse'.
     * 
     * @return the value of field 'starsGetCustomerAccountResponse'.
    **/
    public StarsGetCustomerAccountResponse getStarsGetCustomerAccountResponse()
    {
        return this._starsGetCustomerAccountResponse;
    } //-- StarsGetCustomerAccountResponse getStarsGetCustomerAccountResponse() 

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
     * Returns the value of field
     * 'starsNewCustomerAccountResponse'.
     * 
     * @return the value of field 'starsNewCustomerAccountResponse'.
    **/
    public StarsNewCustomerAccountResponse getStarsNewCustomerAccountResponse()
    {
        return this._starsNewCustomerAccountResponse;
    } //-- StarsNewCustomerAccountResponse getStarsNewCustomerAccountResponse() 

    /**
     * Returns the value of field 'starsProgramOptOut'.
     * 
     * @return the value of field 'starsProgramOptOut'.
    **/
    public StarsProgramOptOut getStarsProgramOptOut()
    {
        return this._starsProgramOptOut;
    } //-- StarsProgramOptOut getStarsProgramOptOut() 

    /**
     * Returns the value of field 'starsProgramOptOutResponse'.
     * 
     * @return the value of field 'starsProgramOptOutResponse'.
    **/
    public StarsProgramOptOutResponse getStarsProgramOptOutResponse()
    {
        return this._starsProgramOptOutResponse;
    } //-- StarsProgramOptOutResponse getStarsProgramOptOutResponse() 

    /**
     * Returns the value of field 'starsProgramReenable'.
     * 
     * @return the value of field 'starsProgramReenable'.
    **/
    public StarsProgramReenable getStarsProgramReenable()
    {
        return this._starsProgramReenable;
    } //-- StarsProgramReenable getStarsProgramReenable() 

    /**
     * Returns the value of field 'starsProgramReenableResponse'.
     * 
     * @return the value of field 'starsProgramReenableResponse'.
    **/
    public StarsProgramReenableResponse getStarsProgramReenableResponse()
    {
        return this._starsProgramReenableResponse;
    } //-- StarsProgramReenableResponse getStarsProgramReenableResponse() 

    /**
     * Returns the value of field 'starsProgramSignUp'.
     * 
     * @return the value of field 'starsProgramSignUp'.
    **/
    public StarsProgramSignUp getStarsProgramSignUp()
    {
        return this._starsProgramSignUp;
    } //-- StarsProgramSignUp getStarsProgramSignUp() 

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
     * Returns the value of field 'starsUpdateAppliance'.
     * 
     * @return the value of field 'starsUpdateAppliance'.
    **/
    public StarsUpdateAppliance getStarsUpdateAppliance()
    {
        return this._starsUpdateAppliance;
    } //-- StarsUpdateAppliance getStarsUpdateAppliance() 

    /**
     * Returns the value of field 'starsUpdateApplianceResponse'.
     * 
     * @return the value of field 'starsUpdateApplianceResponse'.
    **/
    public StarsUpdateApplianceResponse getStarsUpdateApplianceResponse()
    {
        return this._starsUpdateApplianceResponse;
    } //-- StarsUpdateApplianceResponse getStarsUpdateApplianceResponse() 

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
     * Returns the value of field
     * 'starsUpdateCustomerAccountResponse'.
     * 
     * @return the value of field
     * 'starsUpdateCustomerAccountResponse'.
    **/
    public StarsUpdateCustomerAccountResponse getStarsUpdateCustomerAccountResponse()
    {
        return this._starsUpdateCustomerAccountResponse;
    } //-- StarsUpdateCustomerAccountResponse getStarsUpdateCustomerAccountResponse() 

    /**
     * Returns the value of field 'starsUpdateLMHardware'.
     * 
     * @return the value of field 'starsUpdateLMHardware'.
    **/
    public StarsUpdateLMHardware getStarsUpdateLMHardware()
    {
        return this._starsUpdateLMHardware;
    } //-- StarsUpdateLMHardware getStarsUpdateLMHardware() 

    /**
     * Returns the value of field 'starsUpdateLMHardwareResponse'.
     * 
     * @return the value of field 'starsUpdateLMHardwareResponse'.
    **/
    public StarsUpdateLMHardwareResponse getStarsUpdateLMHardwareResponse()
    {
        return this._starsUpdateLMHardwareResponse;
    } //-- StarsUpdateLMHardwareResponse getStarsUpdateLMHardwareResponse() 

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
     * Returns the value of field 'starsUpdateLogin'.
     * 
     * @return the value of field 'starsUpdateLogin'.
    **/
    public StarsUpdateLogin getStarsUpdateLogin()
    {
        return this._starsUpdateLogin;
    } //-- StarsUpdateLogin getStarsUpdateLogin() 

    /**
     * Returns the value of field 'starsUpdateLoginResponse'.
     * 
     * @return the value of field 'starsUpdateLoginResponse'.
    **/
    public StarsUpdateLoginResponse getStarsUpdateLoginResponse()
    {
        return this._starsUpdateLoginResponse;
    } //-- StarsUpdateLoginResponse getStarsUpdateLoginResponse() 

    /**
     * Returns the value of field 'starsYukonSwitchCommand'.
     * 
     * @return the value of field 'starsYukonSwitchCommand'.
    **/
    public StarsYukonSwitchCommand getStarsYukonSwitchCommand()
    {
        return this._starsYukonSwitchCommand;
    } //-- StarsYukonSwitchCommand getStarsYukonSwitchCommand() 

    /**
     * Returns the value of field
     * 'starsYukonSwitchCommandResponse'.
     * 
     * @return the value of field 'starsYukonSwitchCommandResponse'.
    **/
    public StarsYukonSwitchCommandResponse getStarsYukonSwitchCommandResponse()
    {
        return this._starsYukonSwitchCommandResponse;
    } //-- StarsYukonSwitchCommandResponse getStarsYukonSwitchCommandResponse() 

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
     * Sets the value of field 'starsCreateAppliance'.
     * 
     * @param starsCreateAppliance the value of field
     * 'starsCreateAppliance'.
    **/
    public void setStarsCreateAppliance(StarsCreateAppliance starsCreateAppliance)
    {
        this._starsCreateAppliance = starsCreateAppliance;
    } //-- void setStarsCreateAppliance(StarsCreateAppliance) 

    /**
     * Sets the value of field 'starsCreateApplianceResponse'.
     * 
     * @param starsCreateApplianceResponse the value of field
     * 'starsCreateApplianceResponse'.
    **/
    public void setStarsCreateApplianceResponse(StarsCreateApplianceResponse starsCreateApplianceResponse)
    {
        this._starsCreateApplianceResponse = starsCreateApplianceResponse;
    } //-- void setStarsCreateApplianceResponse(StarsCreateApplianceResponse) 

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
     * Sets the value of field 'starsCreateCallReportResponse'.
     * 
     * @param starsCreateCallReportResponse the value of field
     * 'starsCreateCallReportResponse'.
    **/
    public void setStarsCreateCallReportResponse(StarsCreateCallReportResponse starsCreateCallReportResponse)
    {
        this._starsCreateCallReportResponse = starsCreateCallReportResponse;
    } //-- void setStarsCreateCallReportResponse(StarsCreateCallReportResponse) 

    /**
     * Sets the value of field 'starsCreateLMHardware'.
     * 
     * @param starsCreateLMHardware the value of field
     * 'starsCreateLMHardware'.
    **/
    public void setStarsCreateLMHardware(StarsCreateLMHardware starsCreateLMHardware)
    {
        this._starsCreateLMHardware = starsCreateLMHardware;
    } //-- void setStarsCreateLMHardware(StarsCreateLMHardware) 

    /**
     * Sets the value of field 'starsCreateLMHardwareResponse'.
     * 
     * @param starsCreateLMHardwareResponse the value of field
     * 'starsCreateLMHardwareResponse'.
    **/
    public void setStarsCreateLMHardwareResponse(StarsCreateLMHardwareResponse starsCreateLMHardwareResponse)
    {
        this._starsCreateLMHardwareResponse = starsCreateLMHardwareResponse;
    } //-- void setStarsCreateLMHardwareResponse(StarsCreateLMHardwareResponse) 

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
     * Sets the value of field 'starsCreateServiceRequestResponse'.
     * 
     * @param starsCreateServiceRequestResponse the value of field
     * 'starsCreateServiceRequestResponse'.
    **/
    public void setStarsCreateServiceRequestResponse(StarsCreateServiceRequestResponse starsCreateServiceRequestResponse)
    {
        this._starsCreateServiceRequestResponse = starsCreateServiceRequestResponse;
    } //-- void setStarsCreateServiceRequestResponse(StarsCreateServiceRequestResponse) 

    /**
     * Sets the value of field 'starsDefaultThermostatSettings'.
     * 
     * @param starsDefaultThermostatSettings the value of field
     * 'starsDefaultThermostatSettings'.
    **/
    public void setStarsDefaultThermostatSettings(StarsDefaultThermostatSettings starsDefaultThermostatSettings)
    {
        this._starsDefaultThermostatSettings = starsDefaultThermostatSettings;
    } //-- void setStarsDefaultThermostatSettings(StarsDefaultThermostatSettings) 

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
     * Sets the value of field 'starsGetCustSelectionLists'.
     * 
     * @param starsGetCustSelectionLists the value of field
     * 'starsGetCustSelectionLists'.
    **/
    public void setStarsGetCustSelectionLists(StarsGetCustSelectionLists starsGetCustSelectionLists)
    {
        this._starsGetCustSelectionLists = starsGetCustSelectionLists;
    } //-- void setStarsGetCustSelectionLists(StarsGetCustSelectionLists) 

    /**
     * Sets the value of field
     * 'starsGetCustSelectionListsResponse'.
     * 
     * @param starsGetCustSelectionListsResponse the value of field
     * 'starsGetCustSelectionListsResponse'.
    **/
    public void setStarsGetCustSelectionListsResponse(StarsGetCustSelectionListsResponse starsGetCustSelectionListsResponse)
    {
        this._starsGetCustSelectionListsResponse = starsGetCustSelectionListsResponse;
    } //-- void setStarsGetCustSelectionListsResponse(StarsGetCustSelectionListsResponse) 

    /**
     * Sets the value of field 'starsGetCustomerAccount'.
     * 
     * @param starsGetCustomerAccount the value of field
     * 'starsGetCustomerAccount'.
    **/
    public void setStarsGetCustomerAccount(StarsGetCustomerAccount starsGetCustomerAccount)
    {
        this._starsGetCustomerAccount = starsGetCustomerAccount;
    } //-- void setStarsGetCustomerAccount(StarsGetCustomerAccount) 

    /**
     * Sets the value of field 'starsGetCustomerAccountResponse'.
     * 
     * @param starsGetCustomerAccountResponse the value of field
     * 'starsGetCustomerAccountResponse'.
    **/
    public void setStarsGetCustomerAccountResponse(StarsGetCustomerAccountResponse starsGetCustomerAccountResponse)
    {
        this._starsGetCustomerAccountResponse = starsGetCustomerAccountResponse;
    } //-- void setStarsGetCustomerAccountResponse(StarsGetCustomerAccountResponse) 

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
     * Sets the value of field 'starsNewCustomerAccountResponse'.
     * 
     * @param starsNewCustomerAccountResponse the value of field
     * 'starsNewCustomerAccountResponse'.
    **/
    public void setStarsNewCustomerAccountResponse(StarsNewCustomerAccountResponse starsNewCustomerAccountResponse)
    {
        this._starsNewCustomerAccountResponse = starsNewCustomerAccountResponse;
    } //-- void setStarsNewCustomerAccountResponse(StarsNewCustomerAccountResponse) 

    /**
     * Sets the value of field 'starsProgramOptOut'.
     * 
     * @param starsProgramOptOut the value of field
     * 'starsProgramOptOut'.
    **/
    public void setStarsProgramOptOut(StarsProgramOptOut starsProgramOptOut)
    {
        this._starsProgramOptOut = starsProgramOptOut;
    } //-- void setStarsProgramOptOut(StarsProgramOptOut) 

    /**
     * Sets the value of field 'starsProgramOptOutResponse'.
     * 
     * @param starsProgramOptOutResponse the value of field
     * 'starsProgramOptOutResponse'.
    **/
    public void setStarsProgramOptOutResponse(StarsProgramOptOutResponse starsProgramOptOutResponse)
    {
        this._starsProgramOptOutResponse = starsProgramOptOutResponse;
    } //-- void setStarsProgramOptOutResponse(StarsProgramOptOutResponse) 

    /**
     * Sets the value of field 'starsProgramReenable'.
     * 
     * @param starsProgramReenable the value of field
     * 'starsProgramReenable'.
    **/
    public void setStarsProgramReenable(StarsProgramReenable starsProgramReenable)
    {
        this._starsProgramReenable = starsProgramReenable;
    } //-- void setStarsProgramReenable(StarsProgramReenable) 

    /**
     * Sets the value of field 'starsProgramReenableResponse'.
     * 
     * @param starsProgramReenableResponse the value of field
     * 'starsProgramReenableResponse'.
    **/
    public void setStarsProgramReenableResponse(StarsProgramReenableResponse starsProgramReenableResponse)
    {
        this._starsProgramReenableResponse = starsProgramReenableResponse;
    } //-- void setStarsProgramReenableResponse(StarsProgramReenableResponse) 

    /**
     * Sets the value of field 'starsProgramSignUp'.
     * 
     * @param starsProgramSignUp the value of field
     * 'starsProgramSignUp'.
    **/
    public void setStarsProgramSignUp(StarsProgramSignUp starsProgramSignUp)
    {
        this._starsProgramSignUp = starsProgramSignUp;
    } //-- void setStarsProgramSignUp(StarsProgramSignUp) 

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
     * Sets the value of field 'starsUpdateAppliance'.
     * 
     * @param starsUpdateAppliance the value of field
     * 'starsUpdateAppliance'.
    **/
    public void setStarsUpdateAppliance(StarsUpdateAppliance starsUpdateAppliance)
    {
        this._starsUpdateAppliance = starsUpdateAppliance;
    } //-- void setStarsUpdateAppliance(StarsUpdateAppliance) 

    /**
     * Sets the value of field 'starsUpdateApplianceResponse'.
     * 
     * @param starsUpdateApplianceResponse the value of field
     * 'starsUpdateApplianceResponse'.
    **/
    public void setStarsUpdateApplianceResponse(StarsUpdateApplianceResponse starsUpdateApplianceResponse)
    {
        this._starsUpdateApplianceResponse = starsUpdateApplianceResponse;
    } //-- void setStarsUpdateApplianceResponse(StarsUpdateApplianceResponse) 

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
     * Sets the value of field
     * 'starsUpdateCustomerAccountResponse'.
     * 
     * @param starsUpdateCustomerAccountResponse the value of field
     * 'starsUpdateCustomerAccountResponse'.
    **/
    public void setStarsUpdateCustomerAccountResponse(StarsUpdateCustomerAccountResponse starsUpdateCustomerAccountResponse)
    {
        this._starsUpdateCustomerAccountResponse = starsUpdateCustomerAccountResponse;
    } //-- void setStarsUpdateCustomerAccountResponse(StarsUpdateCustomerAccountResponse) 

    /**
     * Sets the value of field 'starsUpdateLMHardware'.
     * 
     * @param starsUpdateLMHardware the value of field
     * 'starsUpdateLMHardware'.
    **/
    public void setStarsUpdateLMHardware(StarsUpdateLMHardware starsUpdateLMHardware)
    {
        this._starsUpdateLMHardware = starsUpdateLMHardware;
    } //-- void setStarsUpdateLMHardware(StarsUpdateLMHardware) 

    /**
     * Sets the value of field 'starsUpdateLMHardwareResponse'.
     * 
     * @param starsUpdateLMHardwareResponse the value of field
     * 'starsUpdateLMHardwareResponse'.
    **/
    public void setStarsUpdateLMHardwareResponse(StarsUpdateLMHardwareResponse starsUpdateLMHardwareResponse)
    {
        this._starsUpdateLMHardwareResponse = starsUpdateLMHardwareResponse;
    } //-- void setStarsUpdateLMHardwareResponse(StarsUpdateLMHardwareResponse) 

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
     * Sets the value of field 'starsUpdateLogin'.
     * 
     * @param starsUpdateLogin the value of field 'starsUpdateLogin'
    **/
    public void setStarsUpdateLogin(StarsUpdateLogin starsUpdateLogin)
    {
        this._starsUpdateLogin = starsUpdateLogin;
    } //-- void setStarsUpdateLogin(StarsUpdateLogin) 

    /**
     * Sets the value of field 'starsUpdateLoginResponse'.
     * 
     * @param starsUpdateLoginResponse the value of field
     * 'starsUpdateLoginResponse'.
    **/
    public void setStarsUpdateLoginResponse(StarsUpdateLoginResponse starsUpdateLoginResponse)
    {
        this._starsUpdateLoginResponse = starsUpdateLoginResponse;
    } //-- void setStarsUpdateLoginResponse(StarsUpdateLoginResponse) 

    /**
     * Sets the value of field 'starsYukonSwitchCommand'.
     * 
     * @param starsYukonSwitchCommand the value of field
     * 'starsYukonSwitchCommand'.
    **/
    public void setStarsYukonSwitchCommand(StarsYukonSwitchCommand starsYukonSwitchCommand)
    {
        this._starsYukonSwitchCommand = starsYukonSwitchCommand;
    } //-- void setStarsYukonSwitchCommand(StarsYukonSwitchCommand) 

    /**
     * Sets the value of field 'starsYukonSwitchCommandResponse'.
     * 
     * @param starsYukonSwitchCommandResponse the value of field
     * 'starsYukonSwitchCommandResponse'.
    **/
    public void setStarsYukonSwitchCommandResponse(StarsYukonSwitchCommandResponse starsYukonSwitchCommandResponse)
    {
        this._starsYukonSwitchCommandResponse = starsYukonSwitchCommandResponse;
    } //-- void setStarsYukonSwitchCommandResponse(StarsYukonSwitchCommandResponse) 

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
