package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsCustAccountInformation {

	private LiteCustomerAccount customerAccount = null;
	private LiteCustomer customer = null;
	private LiteAccountSite accountSite = null;
	private LiteSiteInformation siteInformation = null;
	private ArrayList lmPrograms = null;	// List of LiteStarsLMProgram
	private ArrayList appliances = null;	// List of LiteStarsAppliance
	private ArrayList inventories = null;	// List of IDs of LiteLMHardware
	private ArrayList serviceCompanies = null;	// List of IDs of LiteServiceCompany
	private ArrayList callReportHistory = null;	// List of StarsCallReport
	private ArrayList serviceRequestHistory = null;	// List of IDs of LiteWorkOrderBase
	private LiteStarsThermostatSettings thermostatSettings = null;
	private int loginID = com.cannontech.user.UserUtils.USER_YUKON_ID;
	
	public LiteStarsCustAccountInformation() {
	}
	
	/**
	 * Returns the appliances.
	 * @return ArrayList
	 */
	public ArrayList getAppliances() {
		if (appliances == null)
			appliances = new ArrayList();
		return appliances;
	}

	/**
	 * Returns the callReportHistory.
	 * @return ArrayList
	 */
	public ArrayList getCallReportHistory() {
		if (callReportHistory == null)
			callReportHistory = new ArrayList();
		return callReportHistory;
	}

	/**
	 * Returns the customerAccount.
	 * @return LiteStarsCustomerAccount
	 */
	public LiteCustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * Returns the inventorys.
	 * @return ArrayList
	 */
	public ArrayList getInventories() {
		if (inventories == null)
			inventories = new ArrayList();
		return inventories;
	}

	/**
	 * Returns the lmPrograms.
	 * @return ArrayList
	 */
	public ArrayList getLmPrograms() {
		if (lmPrograms == null)
			lmPrograms = new ArrayList();
		return lmPrograms;
	}

	/**
	 * Returns the serviceRequestHistory.
	 * @return ArrayList
	 */
	public ArrayList getServiceRequestHistory() {
		if (serviceRequestHistory == null)
			serviceRequestHistory = new ArrayList();
		return serviceRequestHistory;
	}

	/**
	 * Sets the appliances.
	 * @param appliances The appliances to set
	 */
	public void setAppliances(ArrayList appliances) {
		this.appliances = appliances;
	}

	/**
	 * Sets the callReportHistory.
	 * @param callReportHistory The callReportHistory to set
	 */
	public void setCallReportHistory(ArrayList callReportHistory) {
		this.callReportHistory = callReportHistory;
	}

	/**
	 * Sets the customerAccount.
	 * @param customerAccount The customerAccount to set
	 */
	public void setCustomerAccount(LiteCustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	/**
	 * Sets the inventorys.
	 * @param inventorys The inventorys to set
	 */
	public void setInventories(ArrayList inventories) {
		this.inventories = inventories;
	}

	/**
	 * Sets the lmPrograms.
	 * @param lmPrograms The lmPrograms to set
	 */
	public void setLmPrograms(ArrayList lmPrograms) {
		this.lmPrograms = lmPrograms;
	}

	/**
	 * Sets the serviceRequestHistory.
	 * @param serviceRequestHistory The serviceRequestHistory to set
	 */
	public void setServiceRequestHistory(
		ArrayList serviceRequestHistory) {
		this.serviceRequestHistory = serviceRequestHistory;
	}

	/**
	 * Returns the accountSite.
	 * @return LiteAccountSite
	 */
	public LiteAccountSite getAccountSite() {
		return accountSite;
	}

	/**
	 * Returns the customerBase.
	 * @return LiteCustomerBase
	 */
	public LiteCustomer getCustomer() {
		return customer;
	}

	/**
	 * Returns the siteInformation.
	 * @return LiteSiteInformation
	 */
	public LiteSiteInformation getSiteInformation() {
		return siteInformation;
	}

	/**
	 * Sets the accountSite.
	 * @param accountSite The accountSite to set
	 */
	public void setAccountSite(LiteAccountSite accountSite) {
		this.accountSite = accountSite;
	}

	/**
	 * Sets the customerBase.
	 * @param customerBase The customerBase to set
	 */
	public void setCustomer(LiteCustomer customer) {
		this.customer = customer;
	}

	/**
	 * Sets the siteInformation.
	 * @param siteInformation The siteInformation to set
	 */
	public void setSiteInformation(LiteSiteInformation siteInformation) {
		this.siteInformation = siteInformation;
	}

	/**
	 * Returns the serviceCompanies.
	 * @return ArrayList
	 */
	public ArrayList getServiceCompanies() {
		if (serviceCompanies == null)
			serviceCompanies = new ArrayList();
		return serviceCompanies;
	}

	/**
	 * Sets the serviceCompanies.
	 * @param serviceCompanies The serviceCompanies to set
	 */
	public void setServiceCompanies(ArrayList serviceCompanies) {
		this.serviceCompanies = serviceCompanies;
	}

	/**
	 * Returns the thermostatSettings.
	 * @return LiteStarsThermostatSettings
	 */
	public LiteStarsThermostatSettings getThermostatSettings() {
		return thermostatSettings;
	}

	/**
	 * Sets the thermostatSettings.
	 * @param thermostatSettings The thermostatSettings to set
	 */
	public void setThermostatSettings(LiteStarsThermostatSettings thermostatSettings) {
		this.thermostatSettings = thermostatSettings;
	}

	/**
	 * Returns the logInID.
	 * @return int
	 */
	public int getLoginID() {
		return loginID;
	}

	/**
	 * Sets the logInID.
	 * @param logInID The logInID to set
	 */
	public void setLoginID(int loginID) {
		this.loginID = loginID;
	}

}
