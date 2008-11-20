package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.stars.hardware.LMThermostatSchedule;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.LMProgramWebPublishingDao;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCallReport;

/**
 * @author yao
 *
 */
public class LiteStarsCustAccountInformation extends LiteBase {
    private final int energyCompanyId;
	private LiteCustomerAccount customerAccount = null;
	private LiteCustomer customer = null;
	private LiteAccountSite accountSite = null;
	private LiteSiteInformation siteInformation = null;
	private LiteCustomerResidence customerResidence = null;
	private List<LiteStarsLMProgram> programs = null;		// List of LiteStarsLMProgram
	private List<LiteStarsAppliance> appliances = null;	// List of LiteStarsAppliance
	private List<Integer> inventories = null;	// List of IDs of LiteInventoryBase
	private List<LiteLMProgramEvent> programHistory = null;	// List of LiteLMProgramEvent
	private List<StarsCallReport> callReportHistory = null;	// List of StarsCallReport
	private List<Integer> serviceRequestHistory = null;	// List of IDs of LiteWorkOrderBase
	private List<LiteLMThermostatSchedule> thermostatSchedules = null;	// List of LiteLMThermostatSchedule
	
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
	
	public boolean hasTwoWayThermostat(LiteStarsEnergyCompany energyCompany) {
		
		StarsInventoryBaseDao starsInventoryBaseDao = YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
		
		for (int i = 0; i < getInventories().size(); i++) {
			int invID = getInventories().get(i).intValue();
			LiteInventoryBase liteInv = starsInventoryBaseDao.getById(invID);
			
			if (liteInv instanceof LiteStarsLMHardware &&
				((LiteStarsLMHardware) liteInv).isTwoWayThermostat())
				return true;
		}
		
		return false;
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
		if (appliances == null) {
            if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
                StarsApplianceDao starsApplianceDao = YukonSpringHook.getBean("starsApplianceDao", StarsApplianceDao.class);
                appliances = starsApplianceDao.getByAccountId(getAccountID(), energyCompanyId);
            }
        }
		return appliances;
	}

	/**
	 * Returns the callReportHistory.
	 * @return ArrayList
	 */
	public synchronized List<StarsCallReport> getCallReportHistory() {
	    if (callReportHistory == null) {
	        if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
	            StarsCallReport[] calls = StarsFactory.getStarsCallReports(getAccountID());
	            if (calls != null) callReportHistory = Arrays.asList(calls);  
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
	        List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
	        List<Integer> energyCompanyIds = ECUtils.toIdList(allAscendants);
		
		    LMProgramWebPublishingDao lmProgramWebPublishingDao =
		        YukonSpringHook.getBean("lmProgramWebPublishingDao", LMProgramWebPublishingDao.class);
			programs = lmProgramWebPublishingDao.getPrograms(this, energyCompanyIds);
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
	 * Sets the appliances.
	 * @param appliances The appliances to set
	 */
	public void setAppliances(List<LiteStarsAppliance> appliances) {
		this.appliances = appliances;
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
            List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
            List<Integer> energyCompanyIds = ECUtils.toIdList(allAscendants);

		    LMProgramWebPublishingDao lmProgramWebPublishingDao =
		        YukonSpringHook.getBean("lmProgramWebPublishingDao", LMProgramWebPublishingDao.class);
			programHistory = lmProgramWebPublishingDao.getProgramHistory(getAccountID(), energyCompanyIds);
		}	
		return programHistory;
	}

	/**
	 * @param list
	 */
	public void setProgramHistory(List<LiteLMProgramEvent> list) {
		programHistory = list;
	}

	/**
	 * @return
	 */
	public synchronized List<LiteLMThermostatSchedule> getThermostatSchedules() {
		if (thermostatSchedules == null) {
			thermostatSchedules = new ArrayList<LiteLMThermostatSchedule>();
            com.cannontech.database.db.stars.hardware.LMThermostatSchedule[] schedules =
                com.cannontech.database.db.stars.hardware.LMThermostatSchedule.getAllThermostatSchedules(getAccountID());
            if (schedules != null) {
                for (int i = 0; i < schedules.length; i++) {
                    if (schedules[i].getInventoryID().intValue() == 0) {
                        try {
                            LMThermostatSchedule schedule = new LMThermostatSchedule();
                            schedule.setScheduleID( schedules[i].getScheduleID() );
                            schedule = Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();

                            LiteLMThermostatSchedule liteSchedule = StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
                            thermostatSchedules.add( liteSchedule );
                        } catch (TransactionException e) {
                            YukonLogManager.getLogger(getClass()).error(e);
                        }
                    }
                }
            }
		}	
		return thermostatSchedules;
	}

	/**
	 * @param list
	 */
	public void setThermostatSchedules(ArrayList<LiteLMThermostatSchedule> list) {
		thermostatSchedules = list;
	}
    
    public LMHardwareConfiguration getStaticLoadGroupMapping(LiteStarsLMHardware lmHw, LiteStarsEnergyCompany energyCompany) {
        if(!VersionTools.staticLoadGroupMappingExists()) {
            return null;
        }
        LMHardwareConfiguration lmHwConfig = null;        
        StarsStaticLoadGroupMapping criteria = new StarsStaticLoadGroupMapping();
        criteria.setSwitchTypeID(lmHw.getLmHardwareTypeID());

        //get the recently added unassigned appliance on the account
        LiteStarsAppliance unassignedAppliance = null;
        if (getCustomerAccount() != null) { //Must already have at least the base objects loaded
            StarsApplianceDao starsApplianceDao = YukonSpringHook.getBean("starsApplianceDao", StarsApplianceDao.class);
            List<LiteStarsAppliance> unassignedAppliances = starsApplianceDao.getUnassignedAppliances(getAccountID(), energyCompanyId);
            if (unassignedAppliances.size() > 0){
                unassignedAppliance = unassignedAppliances.get(0);
                criteria.setApplianceCategoryID(unassignedAppliance.getApplianceCategoryID());
            }
        }
        if (unassignedAppliance == null){
            return null;
        }
        //get zipCode
        String zip = energyCompany.getAddress(getAccountSite().getStreetAddressID()).getZipCode();
        if(zip.length() > 5) {
            zip = zip.substring(0, 5);
        }
        criteria.setZipCode(zip);
        
        //get ConsumptionType
        LiteCustomer cust = getCustomer();
        int consumptionType = -1;
        if(cust instanceof LiteCICustomer && cust.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI){
            consumptionType = ((LiteCICustomer)cust).getCICustType();
        }
        criteria.setConsumptionTypeID(consumptionType);
        
        StaticLoadGroupMappingDao staticLoadGroupMappingDao =
            YukonSpringHook.getBean("staticLoadGroupMappingDao", StaticLoadGroupMappingDao.class);
        StarsStaticLoadGroupMapping loadGroup = staticLoadGroupMappingDao.getStaticLoadGroupMapping(criteria);
        
        if (loadGroup == null) {
            CTILogger.error("A static mapping could not be determined for serial number=[" + lmHw.getManufacturerSerialNumber() 
                            + "], applianceCategoryID=[" + unassignedAppliance.getApplianceCategoryID() + "], ZipCode=[" + zip + "], ConsumptionTypeID=[" 
                            + consumptionType + "],SwitchTypeID=[" + lmHw.getLmHardwareTypeID() + "]");            
        } else {
            lmHwConfig = new LMHardwareConfiguration();
            lmHwConfig.setInventoryId(lmHw.getInventoryID());
            lmHwConfig.setApplianceId(unassignedAppliance.getApplianceID());
            lmHwConfig.setAddressingGroupId(loadGroup.getLoadGroupID());
        }
        return lmHwConfig;        
    }
}
