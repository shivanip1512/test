/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsOperation.java,v 1.77 2004/06/02 16:30:16 zyao Exp $
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
 * @version $Revision: 1.77 $ $Date: 2004/06/02 16:30:16 $
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

    private StarsProgramSignUp _starsProgramSignUp;

    private StarsProgramSignUpResponse _starsProgramSignUpResponse;

    private StarsSearchCustomerAccount _starsSearchCustomerAccount;

    private StarsSearchCustomerAccountResponse _starsSearchCustomerAccountResponse;

    private StarsGetCustomerAccount _starsGetCustomerAccount;

    private StarsGetCustomerAccountResponse _starsGetCustomerAccountResponse;

    private StarsReloadCustomerAccount _starsReloadCustomerAccount;

    private StarsReloadCustomerAccountResponse _starsReloadCustomerAccountResponse;

    private StarsDeleteCustomerAccount _starsDeleteCustomerAccount;

    /**
     * Update a customer account
    **/
    private StarsUpdateCustomerAccount _starsUpdateCustomerAccount;

    private StarsUpdateContacts _starsUpdateContacts;

    private StarsUpdateContactsResponse _starsUpdateContactsResponse;

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

    private StarsUpdateCallReport _starsUpdateCallReport;

    private StarsUpdateCallReportResponse _starsUpdateCallReportResponse;

    private StarsDeleteCallReport _starsDeleteCallReport;

    private StarsCreateServiceRequest _starsCreateServiceRequest;

    private StarsCreateServiceRequestResponse _starsCreateServiceRequestResponse;

    private StarsUpdateServiceRequest _starsUpdateServiceRequest;

    private StarsUpdateServiceRequestResponse _starsUpdateServiceRequestResponse;

    private StarsDeleteServiceRequest _starsDeleteServiceRequest;

    private StarsCreateAppliance _starsCreateAppliance;

    private StarsCreateApplianceResponse _starsCreateApplianceResponse;

    private StarsUpdateAppliance _starsUpdateAppliance;

    private StarsDeleteAppliance _starsDeleteAppliance;

    private StarsDeleteApplianceResponse _starsDeleteApplianceResponse;

    private StarsCreateLMHardware _starsCreateLMHardware;

    private StarsCreateLMHardwareResponse _starsCreateLMHardwareResponse;

    private StarsUpdateLMHardware _starsUpdateLMHardware;

    private StarsUpdateLMHardwareResponse _starsUpdateLMHardwareResponse;

    private StarsDeleteLMHardware _starsDeleteLMHardware;

    private StarsUpdateLogin _starsUpdateLogin;

    private StarsUpdateLoginResponse _starsUpdateLoginResponse;

    private StarsUpdateThermostatSchedule _starsUpdateThermostatSchedule;

    private StarsUpdateThermostatScheduleResponse _starsUpdateThermostatScheduleResponse;

    private StarsUpdateThermostatManualOption _starsUpdateThermostatManualOption;

    private StarsUpdateThermostatManualOptionResponse _starsUpdateThermostatManualOptionResponse;

    private StarsSendExitInterviewAnswers _starsSendExitInterviewAnswers;

    private StarsGetEnergyCompanySettings _starsGetEnergyCompanySettings;

    private StarsGetEnergyCompanySettingsResponse _starsGetEnergyCompanySettingsResponse;

    private StarsUpdateControlNotification _starsUpdateControlNotification;

    private StarsSendOddsForControl _starsSendOddsForControl;

    private StarsUpdateResidenceInformation _starsUpdateResidenceInformation;

    private StarsUpdateLMHardwareConfig _starsUpdateLMHardwareConfig;

    private StarsUpdateLMHardwareConfigResponse _starsUpdateLMHardwareConfigResponse;

    private StarsSaveThermostatSchedule _starsSaveThermostatSchedule;

    private StarsSaveThermostatScheduleResponse _starsSaveThermostatScheduleResponse;

    private StarsApplyThermostatSchedule _starsApplyThermostatSchedule;

    private StarsApplyThermostatScheduleResponse _starsApplyThermostatScheduleResponse;

    private StarsDeleteThermostatSchedule _starsDeleteThermostatSchedule;


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
     * Returns the value of field 'starsApplyThermostatSchedule'.
     * 
     * @return the value of field 'starsApplyThermostatSchedule'.
    **/
    public StarsApplyThermostatSchedule getStarsApplyThermostatSchedule()
    {
        return this._starsApplyThermostatSchedule;
    } //-- StarsApplyThermostatSchedule getStarsApplyThermostatSchedule() 

    /**
     * Returns the value of field
     * 'starsApplyThermostatScheduleResponse'.
     * 
     * @return the value of field
     * 'starsApplyThermostatScheduleResponse'.
    **/
    public StarsApplyThermostatScheduleResponse getStarsApplyThermostatScheduleResponse()
    {
        return this._starsApplyThermostatScheduleResponse;
    } //-- StarsApplyThermostatScheduleResponse getStarsApplyThermostatScheduleResponse() 

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
     * Returns the value of field 'starsDeleteAppliance'.
     * 
     * @return the value of field 'starsDeleteAppliance'.
    **/
    public StarsDeleteAppliance getStarsDeleteAppliance()
    {
        return this._starsDeleteAppliance;
    } //-- StarsDeleteAppliance getStarsDeleteAppliance() 

    /**
     * Returns the value of field 'starsDeleteApplianceResponse'.
     * 
     * @return the value of field 'starsDeleteApplianceResponse'.
    **/
    public StarsDeleteApplianceResponse getStarsDeleteApplianceResponse()
    {
        return this._starsDeleteApplianceResponse;
    } //-- StarsDeleteApplianceResponse getStarsDeleteApplianceResponse() 

    /**
     * Returns the value of field 'starsDeleteCallReport'.
     * 
     * @return the value of field 'starsDeleteCallReport'.
    **/
    public StarsDeleteCallReport getStarsDeleteCallReport()
    {
        return this._starsDeleteCallReport;
    } //-- StarsDeleteCallReport getStarsDeleteCallReport() 

    /**
     * Returns the value of field 'starsDeleteCustomerAccount'.
     * 
     * @return the value of field 'starsDeleteCustomerAccount'.
    **/
    public StarsDeleteCustomerAccount getStarsDeleteCustomerAccount()
    {
        return this._starsDeleteCustomerAccount;
    } //-- StarsDeleteCustomerAccount getStarsDeleteCustomerAccount() 

    /**
     * Returns the value of field 'starsDeleteLMHardware'.
     * 
     * @return the value of field 'starsDeleteLMHardware'.
    **/
    public StarsDeleteLMHardware getStarsDeleteLMHardware()
    {
        return this._starsDeleteLMHardware;
    } //-- StarsDeleteLMHardware getStarsDeleteLMHardware() 

    /**
     * Returns the value of field 'starsDeleteServiceRequest'.
     * 
     * @return the value of field 'starsDeleteServiceRequest'.
    **/
    public StarsDeleteServiceRequest getStarsDeleteServiceRequest()
    {
        return this._starsDeleteServiceRequest;
    } //-- StarsDeleteServiceRequest getStarsDeleteServiceRequest() 

    /**
     * Returns the value of field 'starsDeleteThermostatSchedule'.
     * 
     * @return the value of field 'starsDeleteThermostatSchedule'.
    **/
    public StarsDeleteThermostatSchedule getStarsDeleteThermostatSchedule()
    {
        return this._starsDeleteThermostatSchedule;
    } //-- StarsDeleteThermostatSchedule getStarsDeleteThermostatSchedule() 

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
     * Returns the value of field 'starsGetEnergyCompanySettings'.
     * 
     * @return the value of field 'starsGetEnergyCompanySettings'.
    **/
    public StarsGetEnergyCompanySettings getStarsGetEnergyCompanySettings()
    {
        return this._starsGetEnergyCompanySettings;
    } //-- StarsGetEnergyCompanySettings getStarsGetEnergyCompanySettings() 

    /**
     * Returns the value of field
     * 'starsGetEnergyCompanySettingsResponse'.
     * 
     * @return the value of field
     * 'starsGetEnergyCompanySettingsResponse'.
    **/
    public StarsGetEnergyCompanySettingsResponse getStarsGetEnergyCompanySettingsResponse()
    {
        return this._starsGetEnergyCompanySettingsResponse;
    } //-- StarsGetEnergyCompanySettingsResponse getStarsGetEnergyCompanySettingsResponse() 

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
     * Returns the value of field 'starsProgramSignUpResponse'.
     * 
     * @return the value of field 'starsProgramSignUpResponse'.
    **/
    public StarsProgramSignUpResponse getStarsProgramSignUpResponse()
    {
        return this._starsProgramSignUpResponse;
    } //-- StarsProgramSignUpResponse getStarsProgramSignUpResponse() 

    /**
     * Returns the value of field 'starsReloadCustomerAccount'.
     * 
     * @return the value of field 'starsReloadCustomerAccount'.
    **/
    public StarsReloadCustomerAccount getStarsReloadCustomerAccount()
    {
        return this._starsReloadCustomerAccount;
    } //-- StarsReloadCustomerAccount getStarsReloadCustomerAccount() 

    /**
     * Returns the value of field
     * 'starsReloadCustomerAccountResponse'.
     * 
     * @return the value of field
     * 'starsReloadCustomerAccountResponse'.
    **/
    public StarsReloadCustomerAccountResponse getStarsReloadCustomerAccountResponse()
    {
        return this._starsReloadCustomerAccountResponse;
    } //-- StarsReloadCustomerAccountResponse getStarsReloadCustomerAccountResponse() 

    /**
     * Returns the value of field 'starsSaveThermostatSchedule'.
     * 
     * @return the value of field 'starsSaveThermostatSchedule'.
    **/
    public StarsSaveThermostatSchedule getStarsSaveThermostatSchedule()
    {
        return this._starsSaveThermostatSchedule;
    } //-- StarsSaveThermostatSchedule getStarsSaveThermostatSchedule() 

    /**
     * Returns the value of field
     * 'starsSaveThermostatScheduleResponse'.
     * 
     * @return the value of field
     * 'starsSaveThermostatScheduleResponse'.
    **/
    public StarsSaveThermostatScheduleResponse getStarsSaveThermostatScheduleResponse()
    {
        return this._starsSaveThermostatScheduleResponse;
    } //-- StarsSaveThermostatScheduleResponse getStarsSaveThermostatScheduleResponse() 

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
     * Returns the value of field 'starsSendExitInterviewAnswers'.
     * 
     * @return the value of field 'starsSendExitInterviewAnswers'.
    **/
    public StarsSendExitInterviewAnswers getStarsSendExitInterviewAnswers()
    {
        return this._starsSendExitInterviewAnswers;
    } //-- StarsSendExitInterviewAnswers getStarsSendExitInterviewAnswers() 

    /**
     * Returns the value of field 'starsSendOddsForControl'.
     * 
     * @return the value of field 'starsSendOddsForControl'.
    **/
    public StarsSendOddsForControl getStarsSendOddsForControl()
    {
        return this._starsSendOddsForControl;
    } //-- StarsSendOddsForControl getStarsSendOddsForControl() 

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
     * Returns the value of field 'starsUpdateCallReport'.
     * 
     * @return the value of field 'starsUpdateCallReport'.
    **/
    public StarsUpdateCallReport getStarsUpdateCallReport()
    {
        return this._starsUpdateCallReport;
    } //-- StarsUpdateCallReport getStarsUpdateCallReport() 

    /**
     * Returns the value of field 'starsUpdateCallReportResponse'.
     * 
     * @return the value of field 'starsUpdateCallReportResponse'.
    **/
    public StarsUpdateCallReportResponse getStarsUpdateCallReportResponse()
    {
        return this._starsUpdateCallReportResponse;
    } //-- StarsUpdateCallReportResponse getStarsUpdateCallReportResponse() 

    /**
     * Returns the value of field 'starsUpdateContacts'.
     * 
     * @return the value of field 'starsUpdateContacts'.
    **/
    public StarsUpdateContacts getStarsUpdateContacts()
    {
        return this._starsUpdateContacts;
    } //-- StarsUpdateContacts getStarsUpdateContacts() 

    /**
     * Returns the value of field 'starsUpdateContactsResponse'.
     * 
     * @return the value of field 'starsUpdateContactsResponse'.
    **/
    public StarsUpdateContactsResponse getStarsUpdateContactsResponse()
    {
        return this._starsUpdateContactsResponse;
    } //-- StarsUpdateContactsResponse getStarsUpdateContactsResponse() 

    /**
     * Returns the value of field 'starsUpdateControlNotification'.
     * 
     * @return the value of field 'starsUpdateControlNotification'.
    **/
    public StarsUpdateControlNotification getStarsUpdateControlNotification()
    {
        return this._starsUpdateControlNotification;
    } //-- StarsUpdateControlNotification getStarsUpdateControlNotification() 

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
     * Returns the value of field 'starsUpdateLMHardware'.
     * 
     * @return the value of field 'starsUpdateLMHardware'.
    **/
    public StarsUpdateLMHardware getStarsUpdateLMHardware()
    {
        return this._starsUpdateLMHardware;
    } //-- StarsUpdateLMHardware getStarsUpdateLMHardware() 

    /**
     * Returns the value of field 'starsUpdateLMHardwareConfig'.
     * 
     * @return the value of field 'starsUpdateLMHardwareConfig'.
    **/
    public StarsUpdateLMHardwareConfig getStarsUpdateLMHardwareConfig()
    {
        return this._starsUpdateLMHardwareConfig;
    } //-- StarsUpdateLMHardwareConfig getStarsUpdateLMHardwareConfig() 

    /**
     * Returns the value of field
     * 'starsUpdateLMHardwareConfigResponse'.
     * 
     * @return the value of field
     * 'starsUpdateLMHardwareConfigResponse'.
    **/
    public StarsUpdateLMHardwareConfigResponse getStarsUpdateLMHardwareConfigResponse()
    {
        return this._starsUpdateLMHardwareConfigResponse;
    } //-- StarsUpdateLMHardwareConfigResponse getStarsUpdateLMHardwareConfigResponse() 

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
     * Returns the value of field
     * 'starsUpdateResidenceInformation'.
     * 
     * @return the value of field 'starsUpdateResidenceInformation'.
    **/
    public StarsUpdateResidenceInformation getStarsUpdateResidenceInformation()
    {
        return this._starsUpdateResidenceInformation;
    } //-- StarsUpdateResidenceInformation getStarsUpdateResidenceInformation() 

    /**
     * Returns the value of field 'starsUpdateServiceRequest'.
     * 
     * @return the value of field 'starsUpdateServiceRequest'.
    **/
    public StarsUpdateServiceRequest getStarsUpdateServiceRequest()
    {
        return this._starsUpdateServiceRequest;
    } //-- StarsUpdateServiceRequest getStarsUpdateServiceRequest() 

    /**
     * Returns the value of field
     * 'starsUpdateServiceRequestResponse'.
     * 
     * @return the value of field
     * 'starsUpdateServiceRequestResponse'.
    **/
    public StarsUpdateServiceRequestResponse getStarsUpdateServiceRequestResponse()
    {
        return this._starsUpdateServiceRequestResponse;
    } //-- StarsUpdateServiceRequestResponse getStarsUpdateServiceRequestResponse() 

    /**
     * Returns the value of field
     * 'starsUpdateThermostatManualOption'.
     * 
     * @return the value of field
     * 'starsUpdateThermostatManualOption'.
    **/
    public StarsUpdateThermostatManualOption getStarsUpdateThermostatManualOption()
    {
        return this._starsUpdateThermostatManualOption;
    } //-- StarsUpdateThermostatManualOption getStarsUpdateThermostatManualOption() 

    /**
     * Returns the value of field
     * 'starsUpdateThermostatManualOptionResponse'.
     * 
     * @return the value of field
     * 'starsUpdateThermostatManualOptionResponse'.
    **/
    public StarsUpdateThermostatManualOptionResponse getStarsUpdateThermostatManualOptionResponse()
    {
        return this._starsUpdateThermostatManualOptionResponse;
    } //-- StarsUpdateThermostatManualOptionResponse getStarsUpdateThermostatManualOptionResponse() 

    /**
     * Returns the value of field 'starsUpdateThermostatSchedule'.
     * 
     * @return the value of field 'starsUpdateThermostatSchedule'.
    **/
    public StarsUpdateThermostatSchedule getStarsUpdateThermostatSchedule()
    {
        return this._starsUpdateThermostatSchedule;
    } //-- StarsUpdateThermostatSchedule getStarsUpdateThermostatSchedule() 

    /**
     * Returns the value of field
     * 'starsUpdateThermostatScheduleResponse'.
     * 
     * @return the value of field
     * 'starsUpdateThermostatScheduleResponse'.
    **/
    public StarsUpdateThermostatScheduleResponse getStarsUpdateThermostatScheduleResponse()
    {
        return this._starsUpdateThermostatScheduleResponse;
    } //-- StarsUpdateThermostatScheduleResponse getStarsUpdateThermostatScheduleResponse() 

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
     * Sets the value of field 'starsApplyThermostatSchedule'.
     * 
     * @param starsApplyThermostatSchedule the value of field
     * 'starsApplyThermostatSchedule'.
    **/
    public void setStarsApplyThermostatSchedule(StarsApplyThermostatSchedule starsApplyThermostatSchedule)
    {
        this._starsApplyThermostatSchedule = starsApplyThermostatSchedule;
    } //-- void setStarsApplyThermostatSchedule(StarsApplyThermostatSchedule) 

    /**
     * Sets the value of field
     * 'starsApplyThermostatScheduleResponse'.
     * 
     * @param starsApplyThermostatScheduleResponse the value of
     * field 'starsApplyThermostatScheduleResponse'.
    **/
    public void setStarsApplyThermostatScheduleResponse(StarsApplyThermostatScheduleResponse starsApplyThermostatScheduleResponse)
    {
        this._starsApplyThermostatScheduleResponse = starsApplyThermostatScheduleResponse;
    } //-- void setStarsApplyThermostatScheduleResponse(StarsApplyThermostatScheduleResponse) 

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
     * Sets the value of field 'starsDeleteAppliance'.
     * 
     * @param starsDeleteAppliance the value of field
     * 'starsDeleteAppliance'.
    **/
    public void setStarsDeleteAppliance(StarsDeleteAppliance starsDeleteAppliance)
    {
        this._starsDeleteAppliance = starsDeleteAppliance;
    } //-- void setStarsDeleteAppliance(StarsDeleteAppliance) 

    /**
     * Sets the value of field 'starsDeleteApplianceResponse'.
     * 
     * @param starsDeleteApplianceResponse the value of field
     * 'starsDeleteApplianceResponse'.
    **/
    public void setStarsDeleteApplianceResponse(StarsDeleteApplianceResponse starsDeleteApplianceResponse)
    {
        this._starsDeleteApplianceResponse = starsDeleteApplianceResponse;
    } //-- void setStarsDeleteApplianceResponse(StarsDeleteApplianceResponse) 

    /**
     * Sets the value of field 'starsDeleteCallReport'.
     * 
     * @param starsDeleteCallReport the value of field
     * 'starsDeleteCallReport'.
    **/
    public void setStarsDeleteCallReport(StarsDeleteCallReport starsDeleteCallReport)
    {
        this._starsDeleteCallReport = starsDeleteCallReport;
    } //-- void setStarsDeleteCallReport(StarsDeleteCallReport) 

    /**
     * Sets the value of field 'starsDeleteCustomerAccount'.
     * 
     * @param starsDeleteCustomerAccount the value of field
     * 'starsDeleteCustomerAccount'.
    **/
    public void setStarsDeleteCustomerAccount(StarsDeleteCustomerAccount starsDeleteCustomerAccount)
    {
        this._starsDeleteCustomerAccount = starsDeleteCustomerAccount;
    } //-- void setStarsDeleteCustomerAccount(StarsDeleteCustomerAccount) 

    /**
     * Sets the value of field 'starsDeleteLMHardware'.
     * 
     * @param starsDeleteLMHardware the value of field
     * 'starsDeleteLMHardware'.
    **/
    public void setStarsDeleteLMHardware(StarsDeleteLMHardware starsDeleteLMHardware)
    {
        this._starsDeleteLMHardware = starsDeleteLMHardware;
    } //-- void setStarsDeleteLMHardware(StarsDeleteLMHardware) 

    /**
     * Sets the value of field 'starsDeleteServiceRequest'.
     * 
     * @param starsDeleteServiceRequest the value of field
     * 'starsDeleteServiceRequest'.
    **/
    public void setStarsDeleteServiceRequest(StarsDeleteServiceRequest starsDeleteServiceRequest)
    {
        this._starsDeleteServiceRequest = starsDeleteServiceRequest;
    } //-- void setStarsDeleteServiceRequest(StarsDeleteServiceRequest) 

    /**
     * Sets the value of field 'starsDeleteThermostatSchedule'.
     * 
     * @param starsDeleteThermostatSchedule the value of field
     * 'starsDeleteThermostatSchedule'.
    **/
    public void setStarsDeleteThermostatSchedule(StarsDeleteThermostatSchedule starsDeleteThermostatSchedule)
    {
        this._starsDeleteThermostatSchedule = starsDeleteThermostatSchedule;
    } //-- void setStarsDeleteThermostatSchedule(StarsDeleteThermostatSchedule) 

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
     * Sets the value of field 'starsGetEnergyCompanySettings'.
     * 
     * @param starsGetEnergyCompanySettings the value of field
     * 'starsGetEnergyCompanySettings'.
    **/
    public void setStarsGetEnergyCompanySettings(StarsGetEnergyCompanySettings starsGetEnergyCompanySettings)
    {
        this._starsGetEnergyCompanySettings = starsGetEnergyCompanySettings;
    } //-- void setStarsGetEnergyCompanySettings(StarsGetEnergyCompanySettings) 

    /**
     * Sets the value of field
     * 'starsGetEnergyCompanySettingsResponse'.
     * 
     * @param starsGetEnergyCompanySettingsResponse the value of
     * field 'starsGetEnergyCompanySettingsResponse'.
    **/
    public void setStarsGetEnergyCompanySettingsResponse(StarsGetEnergyCompanySettingsResponse starsGetEnergyCompanySettingsResponse)
    {
        this._starsGetEnergyCompanySettingsResponse = starsGetEnergyCompanySettingsResponse;
    } //-- void setStarsGetEnergyCompanySettingsResponse(StarsGetEnergyCompanySettingsResponse) 

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
     * Sets the value of field 'starsProgramSignUpResponse'.
     * 
     * @param starsProgramSignUpResponse the value of field
     * 'starsProgramSignUpResponse'.
    **/
    public void setStarsProgramSignUpResponse(StarsProgramSignUpResponse starsProgramSignUpResponse)
    {
        this._starsProgramSignUpResponse = starsProgramSignUpResponse;
    } //-- void setStarsProgramSignUpResponse(StarsProgramSignUpResponse) 

    /**
     * Sets the value of field 'starsReloadCustomerAccount'.
     * 
     * @param starsReloadCustomerAccount the value of field
     * 'starsReloadCustomerAccount'.
    **/
    public void setStarsReloadCustomerAccount(StarsReloadCustomerAccount starsReloadCustomerAccount)
    {
        this._starsReloadCustomerAccount = starsReloadCustomerAccount;
    } //-- void setStarsReloadCustomerAccount(StarsReloadCustomerAccount) 

    /**
     * Sets the value of field
     * 'starsReloadCustomerAccountResponse'.
     * 
     * @param starsReloadCustomerAccountResponse the value of field
     * 'starsReloadCustomerAccountResponse'.
    **/
    public void setStarsReloadCustomerAccountResponse(StarsReloadCustomerAccountResponse starsReloadCustomerAccountResponse)
    {
        this._starsReloadCustomerAccountResponse = starsReloadCustomerAccountResponse;
    } //-- void setStarsReloadCustomerAccountResponse(StarsReloadCustomerAccountResponse) 

    /**
     * Sets the value of field 'starsSaveThermostatSchedule'.
     * 
     * @param starsSaveThermostatSchedule the value of field
     * 'starsSaveThermostatSchedule'.
    **/
    public void setStarsSaveThermostatSchedule(StarsSaveThermostatSchedule starsSaveThermostatSchedule)
    {
        this._starsSaveThermostatSchedule = starsSaveThermostatSchedule;
    } //-- void setStarsSaveThermostatSchedule(StarsSaveThermostatSchedule) 

    /**
     * Sets the value of field
     * 'starsSaveThermostatScheduleResponse'.
     * 
     * @param starsSaveThermostatScheduleResponse the value of
     * field 'starsSaveThermostatScheduleResponse'.
    **/
    public void setStarsSaveThermostatScheduleResponse(StarsSaveThermostatScheduleResponse starsSaveThermostatScheduleResponse)
    {
        this._starsSaveThermostatScheduleResponse = starsSaveThermostatScheduleResponse;
    } //-- void setStarsSaveThermostatScheduleResponse(StarsSaveThermostatScheduleResponse) 

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
     * Sets the value of field 'starsSendExitInterviewAnswers'.
     * 
     * @param starsSendExitInterviewAnswers the value of field
     * 'starsSendExitInterviewAnswers'.
    **/
    public void setStarsSendExitInterviewAnswers(StarsSendExitInterviewAnswers starsSendExitInterviewAnswers)
    {
        this._starsSendExitInterviewAnswers = starsSendExitInterviewAnswers;
    } //-- void setStarsSendExitInterviewAnswers(StarsSendExitInterviewAnswers) 

    /**
     * Sets the value of field 'starsSendOddsForControl'.
     * 
     * @param starsSendOddsForControl the value of field
     * 'starsSendOddsForControl'.
    **/
    public void setStarsSendOddsForControl(StarsSendOddsForControl starsSendOddsForControl)
    {
        this._starsSendOddsForControl = starsSendOddsForControl;
    } //-- void setStarsSendOddsForControl(StarsSendOddsForControl) 

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
     * Sets the value of field 'starsUpdateCallReport'.
     * 
     * @param starsUpdateCallReport the value of field
     * 'starsUpdateCallReport'.
    **/
    public void setStarsUpdateCallReport(StarsUpdateCallReport starsUpdateCallReport)
    {
        this._starsUpdateCallReport = starsUpdateCallReport;
    } //-- void setStarsUpdateCallReport(StarsUpdateCallReport) 

    /**
     * Sets the value of field 'starsUpdateCallReportResponse'.
     * 
     * @param starsUpdateCallReportResponse the value of field
     * 'starsUpdateCallReportResponse'.
    **/
    public void setStarsUpdateCallReportResponse(StarsUpdateCallReportResponse starsUpdateCallReportResponse)
    {
        this._starsUpdateCallReportResponse = starsUpdateCallReportResponse;
    } //-- void setStarsUpdateCallReportResponse(StarsUpdateCallReportResponse) 

    /**
     * Sets the value of field 'starsUpdateContacts'.
     * 
     * @param starsUpdateContacts the value of field
     * 'starsUpdateContacts'.
    **/
    public void setStarsUpdateContacts(StarsUpdateContacts starsUpdateContacts)
    {
        this._starsUpdateContacts = starsUpdateContacts;
    } //-- void setStarsUpdateContacts(StarsUpdateContacts) 

    /**
     * Sets the value of field 'starsUpdateContactsResponse'.
     * 
     * @param starsUpdateContactsResponse the value of field
     * 'starsUpdateContactsResponse'.
    **/
    public void setStarsUpdateContactsResponse(StarsUpdateContactsResponse starsUpdateContactsResponse)
    {
        this._starsUpdateContactsResponse = starsUpdateContactsResponse;
    } //-- void setStarsUpdateContactsResponse(StarsUpdateContactsResponse) 

    /**
     * Sets the value of field 'starsUpdateControlNotification'.
     * 
     * @param starsUpdateControlNotification the value of field
     * 'starsUpdateControlNotification'.
    **/
    public void setStarsUpdateControlNotification(StarsUpdateControlNotification starsUpdateControlNotification)
    {
        this._starsUpdateControlNotification = starsUpdateControlNotification;
    } //-- void setStarsUpdateControlNotification(StarsUpdateControlNotification) 

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
     * Sets the value of field 'starsUpdateLMHardwareConfig'.
     * 
     * @param starsUpdateLMHardwareConfig the value of field
     * 'starsUpdateLMHardwareConfig'.
    **/
    public void setStarsUpdateLMHardwareConfig(StarsUpdateLMHardwareConfig starsUpdateLMHardwareConfig)
    {
        this._starsUpdateLMHardwareConfig = starsUpdateLMHardwareConfig;
    } //-- void setStarsUpdateLMHardwareConfig(StarsUpdateLMHardwareConfig) 

    /**
     * Sets the value of field
     * 'starsUpdateLMHardwareConfigResponse'.
     * 
     * @param starsUpdateLMHardwareConfigResponse the value of
     * field 'starsUpdateLMHardwareConfigResponse'.
    **/
    public void setStarsUpdateLMHardwareConfigResponse(StarsUpdateLMHardwareConfigResponse starsUpdateLMHardwareConfigResponse)
    {
        this._starsUpdateLMHardwareConfigResponse = starsUpdateLMHardwareConfigResponse;
    } //-- void setStarsUpdateLMHardwareConfigResponse(StarsUpdateLMHardwareConfigResponse) 

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
     * Sets the value of field 'starsUpdateResidenceInformation'.
     * 
     * @param starsUpdateResidenceInformation the value of field
     * 'starsUpdateResidenceInformation'.
    **/
    public void setStarsUpdateResidenceInformation(StarsUpdateResidenceInformation starsUpdateResidenceInformation)
    {
        this._starsUpdateResidenceInformation = starsUpdateResidenceInformation;
    } //-- void setStarsUpdateResidenceInformation(StarsUpdateResidenceInformation) 

    /**
     * Sets the value of field 'starsUpdateServiceRequest'.
     * 
     * @param starsUpdateServiceRequest the value of field
     * 'starsUpdateServiceRequest'.
    **/
    public void setStarsUpdateServiceRequest(StarsUpdateServiceRequest starsUpdateServiceRequest)
    {
        this._starsUpdateServiceRequest = starsUpdateServiceRequest;
    } //-- void setStarsUpdateServiceRequest(StarsUpdateServiceRequest) 

    /**
     * Sets the value of field 'starsUpdateServiceRequestResponse'.
     * 
     * @param starsUpdateServiceRequestResponse the value of field
     * 'starsUpdateServiceRequestResponse'.
    **/
    public void setStarsUpdateServiceRequestResponse(StarsUpdateServiceRequestResponse starsUpdateServiceRequestResponse)
    {
        this._starsUpdateServiceRequestResponse = starsUpdateServiceRequestResponse;
    } //-- void setStarsUpdateServiceRequestResponse(StarsUpdateServiceRequestResponse) 

    /**
     * Sets the value of field 'starsUpdateThermostatManualOption'.
     * 
     * @param starsUpdateThermostatManualOption the value of field
     * 'starsUpdateThermostatManualOption'.
    **/
    public void setStarsUpdateThermostatManualOption(StarsUpdateThermostatManualOption starsUpdateThermostatManualOption)
    {
        this._starsUpdateThermostatManualOption = starsUpdateThermostatManualOption;
    } //-- void setStarsUpdateThermostatManualOption(StarsUpdateThermostatManualOption) 

    /**
     * Sets the value of field
     * 'starsUpdateThermostatManualOptionResponse'.
     * 
     * @param starsUpdateThermostatManualOptionResponse the value
     * of field 'starsUpdateThermostatManualOptionResponse'.
    **/
    public void setStarsUpdateThermostatManualOptionResponse(StarsUpdateThermostatManualOptionResponse starsUpdateThermostatManualOptionResponse)
    {
        this._starsUpdateThermostatManualOptionResponse = starsUpdateThermostatManualOptionResponse;
    } //-- void setStarsUpdateThermostatManualOptionResponse(StarsUpdateThermostatManualOptionResponse) 

    /**
     * Sets the value of field 'starsUpdateThermostatSchedule'.
     * 
     * @param starsUpdateThermostatSchedule the value of field
     * 'starsUpdateThermostatSchedule'.
    **/
    public void setStarsUpdateThermostatSchedule(StarsUpdateThermostatSchedule starsUpdateThermostatSchedule)
    {
        this._starsUpdateThermostatSchedule = starsUpdateThermostatSchedule;
    } //-- void setStarsUpdateThermostatSchedule(StarsUpdateThermostatSchedule) 

    /**
     * Sets the value of field
     * 'starsUpdateThermostatScheduleResponse'.
     * 
     * @param starsUpdateThermostatScheduleResponse the value of
     * field 'starsUpdateThermostatScheduleResponse'.
    **/
    public void setStarsUpdateThermostatScheduleResponse(StarsUpdateThermostatScheduleResponse starsUpdateThermostatScheduleResponse)
    {
        this._starsUpdateThermostatScheduleResponse = starsUpdateThermostatScheduleResponse;
    } //-- void setStarsUpdateThermostatScheduleResponse(StarsUpdateThermostatScheduleResponse) 

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
