package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsCustAccountInformation extends LiteBase {

	private LiteCustomerAccount customerAccount = null;
	private LiteCustomer customer = null;
	private LiteAccountSite accountSite = null;
	private LiteSiteInformation siteInformation = null;
	private LiteCustomerResidence customerResidence = null;
	private ArrayList lmPrograms = null;	// List of LiteStarsLMProgram
	private ArrayList appliances = null;	// List of LiteStarsAppliance
	private ArrayList inventories = null;	// List of IDs of LiteInventoryBase
	private ArrayList programHistory = null;	// List of LiteLMProgramEvent
	private ArrayList callReportHistory = null;	// List of StarsCallReport
	private ArrayList serviceRequestHistory = null;	// List of IDs of LiteWorkOrderBase
	private ArrayList thermostatSchedules = null;	// List of LiteLMThermostatSchedule
	
	private long lastLoginTime = 0;
	private boolean extended = false;
	
	public LiteStarsCustAccountInformation() {
		super();
		setLiteType( LiteTypes.STARS_CUST_ACCOUNT_INFO );
	}
	
	public LiteStarsCustAccountInformation(int accountID) {
		super();
		setAccountID( accountID );
		setLiteType( LiteTypes.STARS_CUST_ACCOUNT_INFO );
	}
	
	public int getAccountID() {
		return getLiteID();
	}
	
	public void setAccountID(int accountID) {
		setLiteID( accountID );
	}
	
	public boolean hasTwoWayThermostat(LiteStarsEnergyCompany energyCompany) {
		for (int i = 0; i < getInventories().size(); i++) {
			int invID = ((Integer) getInventories().get(i)).intValue();
			LiteInventoryBase liteInv = energyCompany.getInventory( invID, true );
			
			if (liteInv instanceof LiteStarsLMHardware &&
				((LiteStarsLMHardware) liteInv).isTwoWayThermostat())
				return true;
		}
		
		return false;
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
	 * Returns the customer.
	 * @return LiteCustomer
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
	 * Returns the customerResidence.
	 * @return LiteCustomerResidence
	 */
	public LiteCustomerResidence getCustomerResidence() {
		return customerResidence;
	}

	/**
	 * Sets the customerResidence.
	 * @param customerResidence The customerResidence to set
	 */
	public void setCustomerResidence(LiteCustomerResidence customerResidence) {
		this.customerResidence = customerResidence;
	}

	/**
	 * Returns the lastLoginTime.
	 * @return long
	 */
	public long getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * Sets the lastLoginTime.
	 * @param lastLoginTime The lastLoginTime to set
	 */
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * Returns the extended.
	 * @return boolean
	 */
	public boolean isExtended() {
		return extended;
	}

	/**
	 * Sets the extended.
	 * @param extended The extended to set
	 */
	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	/**
	 * @return
	 */
	public ArrayList getProgramHistory() {
		if (programHistory == null)
			programHistory = new ArrayList();
		return programHistory;
	}

	/**
	 * @param list
	 */
	public void setProgramHistory(ArrayList list) {
		programHistory = list;
	}

	/**
	 * @return
	 */
	public ArrayList getThermostatSchedules() {
		if (thermostatSchedules == null)
			thermostatSchedules = new ArrayList();
		return thermostatSchedules;
	}

	/**
	 * @param list
	 */
	public void setThermostatSchedules(ArrayList list) {
		thermostatSchedules = list;
	}

}
