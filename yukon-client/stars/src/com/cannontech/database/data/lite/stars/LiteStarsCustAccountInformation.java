package com.cannontech.database.data.lite.stars;

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
	private LiteCustomerBase customerBase = null;
	private LiteAccountSite accountSite = null;
	private LiteSiteInformation siteInformation = null;
	private java.util.ArrayList lmPrograms = null;	// List of LiteStarsLMProgram
	private java.util.ArrayList appliances = null;	// List of StarsAppliance
	private java.util.ArrayList inventories = null;	// List of IDs of LiteLMHardware
	private java.util.ArrayList callReportHistory = null;	// List of StarsCallReport
	private java.util.ArrayList serviceRequestHistory = null;	// List of IDs of LiteWorkOrderBase
	
	public LiteStarsCustAccountInformation() {
	}
	
	/**
	 * Returns the appliances.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getAppliances() {
		return appliances;
	}

	/**
	 * Returns the callReportHistory.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getCallReportHistory() {
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
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getInventories() {
		return inventories;
	}

	/**
	 * Returns the lmPrograms.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getLmPrograms() {
		return lmPrograms;
	}

	/**
	 * Returns the serviceRequestHistory.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getServiceRequestHistory() {
		return serviceRequestHistory;
	}

	/**
	 * Sets the appliances.
	 * @param appliances The appliances to set
	 */
	public void setAppliances(java.util.ArrayList appliances) {
		this.appliances = appliances;
	}

	/**
	 * Sets the callReportHistory.
	 * @param callReportHistory The callReportHistory to set
	 */
	public void setCallReportHistory(java.util.ArrayList callReportHistory) {
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
	public void setInventories(java.util.ArrayList inventories) {
		this.inventories = inventories;
	}

	/**
	 * Sets the lmPrograms.
	 * @param lmPrograms The lmPrograms to set
	 */
	public void setLmPrograms(java.util.ArrayList lmPrograms) {
		this.lmPrograms = lmPrograms;
	}

	/**
	 * Sets the serviceRequestHistory.
	 * @param serviceRequestHistory The serviceRequestHistory to set
	 */
	public void setServiceRequestHistory(
		java.util.ArrayList serviceRequestHistory) {
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
	public LiteCustomerBase getCustomerBase() {
		return customerBase;
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
	public void setCustomerBase(LiteCustomerBase customerBase) {
		this.customerBase = customerBase;
	}

	/**
	 * Sets the siteInformation.
	 * @param siteInformation The siteInformation to set
	 */
	public void setSiteInformation(LiteSiteInformation siteInformation) {
		this.siteInformation = siteInformation;
	}

}
