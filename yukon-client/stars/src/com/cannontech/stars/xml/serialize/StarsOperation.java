/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id: StarsOperation.java,v 1.49 2003/08/21 00:18:17 zyao Exp $
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
 * @version $Revision: 1.49 $ $Date: 2003/08/21 00:18:17 $
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

    private StarsGetNextCallNumber _starsGetNextCallNumber;

    private StarsGetNextCallNumberResponse _starsGetNextCallNumberResponse;

    private StarsCreateCallReport _starsCreateCallReport;

    private StarsCreateCallReportResponse _starsCreateCallReportResponse;

    private StarsUpdateCallReport _starsUpdateCallReport;

    private StarsGetCallReportHistory _starsGetCallReportHistory;

    private StarsGetCallReportHistoryResponse _starsGetCallReportHistoryResponse;

    private StarsGetNextOrderNumber _starsGetNextOrderNumber;

    private StarsGetNextOrderNumberResponse _starsGetNextOrderNumberResponse;

    private StarsCreateServiceRequest _starsCreateServiceRequest;

    private StarsCreateServiceRequestResponse _starsCreateServiceRequestResponse;

    private StarsUpdateServiceRequest _starsUpdateServiceRequest;

    private StarsGetServiceRequestHistory _starsGetServiceRequestHistory;

    private StarsGetServiceRequestHistoryResponse _starsGetServiceRequestHistoryResponse;

    private StarsCreateAppliance _starsCreateAppliance;

    private StarsCreateApplianceResponse _starsCreateApplianceResponse;

    private StarsUpdateAppliance _starsUpdateAppliance;

    private StarsDeleteAppliance _starsDeleteAppliance;

    private StarsCreateLMHardware _starsCreateLMHardware;

    private StarsCreateLMHardwareResponse _starsCreateLMHardwareResponse;

    private StarsUpdateLMHardware _starsUpdateLMHardware;

    private StarsUpdateLMHardwareResponse _starsUpdateLMHardwareResponse;

    private StarsDeleteLMHardware _starsDeleteLMHardware;

    private StarsUpdateLogin _starsUpdateLogin;

    private StarsUpdateLoginResponse _starsUpdateLoginResponse;

    private StarsDefaultThermostatSettings _starsDefaultThermostatSettings;

    private StarsGetDefaultThermostatSettings _starsGetDefaultThermostatSettings;

    private StarsGetDefaultThermostatSettingsResponse _starsGetDefaultThermostatSettingsResponse;

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
     * Returns the value of field 'starsDeleteAppliance'.
     * 
     * @return the value of field 'starsDeleteAppliance'.
    **/
    public StarsDeleteAppliance getStarsDeleteAppliance()
    {
        return this._starsDeleteAppliance;
    } //-- StarsDeleteAppliance getStarsDeleteAppliance() 

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
     * Returns the value of field
     * 'starsGetDefaultThermostatSettings'.
     * 
     * @return the value of field
     * 'starsGetDefaultThermostatSettings'.
    **/
    public StarsGetDefaultThermostatSettings getStarsGetDefaultThermostatSettings()
    {
        return this._starsGetDefaultThermostatSettings;
    } //-- StarsGetDefaultThermostatSettings getStarsGetDefaultThermostatSettings() 

    /**
     * Returns the value of field
     * 'starsGetDefaultThermostatSettingsResponse'.
     * 
     * @return the value of field
     * 'starsGetDefaultThermostatSettingsResponse'.
    **/
    public StarsGetDefaultThermostatSettingsResponse getStarsGetDefaultThermostatSettingsResponse()
    {
        return this._starsGetDefaultThermostatSettingsResponse;
    } //-- StarsGetDefaultThermostatSettingsResponse getStarsGetDefaultThermostatSettingsResponse() 

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
     * Returns the value of field 'starsGetNextCallNumber'.
     * 
     * @return the value of field 'starsGetNextCallNumber'.
    **/
    public StarsGetNextCallNumber getStarsGetNextCallNumber()
    {
        return this._starsGetNextCallNumber;
    } //-- StarsGetNextCallNumber getStarsGetNextCallNumber() 

    /**
     * Returns the value of field 'starsGetNextCallNumberResponse'.
     * 
     * @return the value of field 'starsGetNextCallNumberResponse'.
    **/
    public StarsGetNextCallNumberResponse getStarsGetNextCallNumberResponse()
    {
        return this._starsGetNextCallNumberResponse;
    } //-- StarsGetNextCallNumberResponse getStarsGetNextCallNumberResponse() 

    /**
     * Returns the value of field 'starsGetNextOrderNumber'.
     * 
     * @return the value of field 'starsGetNextOrderNumber'.
    **/
    public StarsGetNextOrderNumber getStarsGetNextOrderNumber()
    {
        return this._starsGetNextOrderNumber;
    } //-- StarsGetNextOrderNumber getStarsGetNextOrderNumber() 

    /**
     * Returns the value of field
     * 'starsGetNextOrderNumberResponse'.
     * 
     * @return the value of field 'starsGetNextOrderNumberResponse'.
    **/
    public StarsGetNextOrderNumberResponse getStarsGetNextOrderNumberResponse()
    {
        return this._starsGetNextOrderNumberResponse;
    } //-- StarsGetNextOrderNumberResponse getStarsGetNextOrderNumberResponse() 

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
     * Sets the value of field 'starsGetDefaultThermostatSettings'.
     * 
     * @param starsGetDefaultThermostatSettings the value of field
     * 'starsGetDefaultThermostatSettings'.
    **/
    public void setStarsGetDefaultThermostatSettings(StarsGetDefaultThermostatSettings starsGetDefaultThermostatSettings)
    {
        this._starsGetDefaultThermostatSettings = starsGetDefaultThermostatSettings;
    } //-- void setStarsGetDefaultThermostatSettings(StarsGetDefaultThermostatSettings) 

    /**
     * Sets the value of field
     * 'starsGetDefaultThermostatSettingsResponse'.
     * 
     * @param starsGetDefaultThermostatSettingsResponse the value
     * of field 'starsGetDefaultThermostatSettingsResponse'.
    **/
    public void setStarsGetDefaultThermostatSettingsResponse(StarsGetDefaultThermostatSettingsResponse starsGetDefaultThermostatSettingsResponse)
    {
        this._starsGetDefaultThermostatSettingsResponse = starsGetDefaultThermostatSettingsResponse;
    } //-- void setStarsGetDefaultThermostatSettingsResponse(StarsGetDefaultThermostatSettingsResponse) 

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
     * Sets the value of field 'starsGetNextCallNumber'.
     * 
     * @param starsGetNextCallNumber the value of field
     * 'starsGetNextCallNumber'.
    **/
    public void setStarsGetNextCallNumber(StarsGetNextCallNumber starsGetNextCallNumber)
    {
        this._starsGetNextCallNumber = starsGetNextCallNumber;
    } //-- void setStarsGetNextCallNumber(StarsGetNextCallNumber) 

    /**
     * Sets the value of field 'starsGetNextCallNumberResponse'.
     * 
     * @param starsGetNextCallNumberResponse the value of field
     * 'starsGetNextCallNumberResponse'.
    **/
    public void setStarsGetNextCallNumberResponse(StarsGetNextCallNumberResponse starsGetNextCallNumberResponse)
    {
        this._starsGetNextCallNumberResponse = starsGetNextCallNumberResponse;
    } //-- void setStarsGetNextCallNumberResponse(StarsGetNextCallNumberResponse) 

    /**
     * Sets the value of field 'starsGetNextOrderNumber'.
     * 
     * @param starsGetNextOrderNumber the value of field
     * 'starsGetNextOrderNumber'.
    **/
    public void setStarsGetNextOrderNumber(StarsGetNextOrderNumber starsGetNextOrderNumber)
    {
        this._starsGetNextOrderNumber = starsGetNextOrderNumber;
    } //-- void setStarsGetNextOrderNumber(StarsGetNextOrderNumber) 

    /**
     * Sets the value of field 'starsGetNextOrderNumberResponse'.
     * 
     * @param starsGetNextOrderNumberResponse the value of field
     * 'starsGetNextOrderNumberResponse'.
    **/
    public void setStarsGetNextOrderNumberResponse(StarsGetNextOrderNumberResponse starsGetNextOrderNumberResponse)
    {
        this._starsGetNextOrderNumberResponse = starsGetNextOrderNumberResponse;
    } //-- void setStarsGetNextOrderNumberResponse(StarsGetNextOrderNumberResponse) 

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
