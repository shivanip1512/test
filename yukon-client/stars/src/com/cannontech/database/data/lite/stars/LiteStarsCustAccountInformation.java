package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.LMProgramWebPublishingDao;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCallReport;

/**
 * @author yao
 *
 */
public class LiteStarsCustAccountInformation extends LiteBase {
    private final static long serialVersionUID = 1L;

    private final int energyCompanyId;
	private LiteCustomerAccount customerAccount = null;
	private LiteCustomer customer = null;
	private LiteAccountSite accountSite = null;
	private LiteSiteInformation siteInformation = null;
	private LiteCustomerResidence customerResidence = null;
	private List<LiteStarsLMProgram> programs = null;		// List of LiteStarsLMProgram
    private List<LiteStarsAppliance> unassignedAppliances = null;
	private List<Integer> inventories = null;	// List of IDs of LiteInventoryBase
	private List<LiteLMProgramEvent> programHistory = null;	// List of LiteLMProgramEvent
	private List<StarsCallReport> callReportHistory = null;	// List of StarsCallReport
	private List<Integer> serviceRequestHistory = null;	// List of IDs of LiteWorkOrderBase
	
	public LiteStarsCustAccountInformation(int accountID, int energyCompanyId) {
		setAccountID( accountID );
		setLiteType( LiteTypes.STARS_CUST_ACCOUNT_INFO );
		this.energyCompanyId = energyCompanyId;
	}
	
	public int getAccountID() {
		return getLiteID();
	}
	
	public void setAccountID(int accountID) {
		setLiteID( accountID );
	}
    
    public static String getCompanyName(int customerID)
    {
        return CICustomerBase.getCompanyNameFromDB(customerID);
    }

    /**
     * Returns the appliances.
     * @return ArrayList
     */
    public synchronized List<LiteStarsAppliance> getAppliances() {
        if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
            StarsApplianceDao starsApplianceDao = YukonSpringHook.getBean("starsApplianceDao", StarsApplianceDao.class);
            return starsApplianceDao.getByAccountId(getAccountID(), energyCompanyId);
        }
        return null;
    }

    /**
     * Returns the unassigned appliances.
     * @return ArrayList
     */
    public synchronized List<LiteStarsAppliance> getUnassignedAppliances() {
        if (unassignedAppliances == null) {
            if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
                StarsApplianceDao starsApplianceDao = YukonSpringHook.getBean("starsApplianceDao", StarsApplianceDao.class);
                unassignedAppliances = starsApplianceDao.getUnassignedAppliances(getAccountID(), energyCompanyId);
            }
        }
        return unassignedAppliances;
    }

	/**
	 * Returns the callReportHistory.
	 * @return ArrayList
	 */
	public synchronized List<StarsCallReport> getCallReportHistory() {
	    if (callReportHistory == null) {
	        callReportHistory = new ArrayList<StarsCallReport>();
	        if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
	            StarsCallReport[] calls = StarsFactory.getStarsCallReports(getAccountID());
	            if (calls != null) callReportHistory.addAll(Arrays.asList(calls));  
	        }
	    }
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
	public synchronized List<Integer> getInventories() {
		if (inventories == null) {
            if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
                InventoryBaseDao inventoryBaseDao = YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
                inventories = inventoryBaseDao.getInventoryIdsByAccountId(getAccountID());
            }
		}    
		return inventories;
	}

	/**
	 * Returns the programs.
	 * @return ArrayList
	 */
	public synchronized List<LiteStarsLMProgram> getPrograms() {
		if (programs == null) {
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);
		    LMProgramWebPublishingDao lmProgramWebPublishingDao =
		        YukonSpringHook.getBean("lmProgramWebPublishingDao", LMProgramWebPublishingDao.class);
			programs = lmProgramWebPublishingDao.getPrograms(this, energyCompany);
		}	
		return programs;
	}

	/**
	 * Returns the serviceRequestHistory.
	 * @return ArrayList
	 */
	public synchronized List<Integer> getServiceRequestHistory() {
		if (serviceRequestHistory == null) {
		    if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
	            int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByAccountID(getAccountID());
	            if (orderIDs != null) {
	                serviceRequestHistory = new ArrayList<Integer>();
	                for (int orderId : orderIDs) {
	                    serviceRequestHistory.add(orderId);
	                }
	            }
		    }
		}
		return serviceRequestHistory;
	}

	/**
	 * Sets the callReportHistory.
	 * @param callReportHistory The callReportHistory to set
	 */
	public void setCallReportHistory(List<StarsCallReport> callReportHistory) {
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
	public void setInventories(List<Integer> inventories) {
		this.inventories = inventories;
	}

	/**
	 * Sets the programs.
	 * @param programs The programs to set
	 */
	public void setPrograms(List<LiteStarsLMProgram> programs) {
		this.programs = programs;
	}

	/**
	 * Sets the serviceRequestHistory.
	 * @param serviceRequestHistory The serviceRequestHistory to set
	 */
	public void setServiceRequestHistory(List<Integer> serviceRequestHistory) {
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
	public synchronized LiteCustomer getCustomer() {
		if (customer == null) {
		    CustomerDao customerDao = YukonSpringHook.getBean("customerDao", CustomerDao.class);
		    customer = customerDao.getLiteCustomer(getCustomerAccount().getCustomerID());
		}
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
	public synchronized LiteCustomerResidence getCustomerResidence() {
	    if (customerResidence == null) {
	        if (getCustomerAccount() != null) {   //Must already have at least the base objects loaded
	            com.cannontech.database.db.stars.customer.CustomerResidence residence =
	                com.cannontech.database.db.stars.customer.CustomerResidence.getCustomerResidence(getAccountSite().getAccountSiteID());
	            if (residence != null) {
	                customerResidence = (LiteCustomerResidence) StarsLiteFactory.createLite(residence);
	            }
	        }
	    }
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
	 * @return
	 */
	public synchronized List<LiteLMProgramEvent> getProgramHistory() {
        if (programHistory == null) {
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);
		    LMProgramWebPublishingDao lmProgramWebPublishingDao =
		        YukonSpringHook.getBean("lmProgramWebPublishingDao", LMProgramWebPublishingDao.class);
			programHistory = lmProgramWebPublishingDao.getProgramHistory(getAccountID(), energyCompany);
		}	
		return programHistory;
	}

	/**
	 * @param list
	 */
	public void setProgramHistory(List<LiteLMProgramEvent> list) {
		programHistory = list;
	}

}
