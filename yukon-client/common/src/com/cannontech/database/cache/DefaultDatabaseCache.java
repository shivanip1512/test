package com.cannontech.database.cache;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Sep 25, 2002 at 5:45:26 PM
 * 
 * A undefined generated comment
 */
public class DefaultDatabaseCache implements IDatabaseCache
{      
    private IDatabaseCache databaseCache;
    
	/**
	 * Constructor for DefaultDatabaseCache.
	 */
	public DefaultDatabaseCache()
	{
		super();
	} 

   public synchronized static final IDatabaseCache getInstance()
   {
       return (IDatabaseCache) YukonSpringHook.getBean("databaseCache");
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized  void addDBChangeListener(DBChangeListener listener) 
   {
      databaseCache.addDBChangeListener( listener );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
   {
      return databaseCache.createDBChangeMessages( newItem, changeType );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllAlarmCategories()
   {
      return databaseCache.getAllAlarmCategories();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonImages()
   {
      return databaseCache.getAllYukonImages();   
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void releaseAllCache()
   {
      databaseCache.releaseAllCache();   
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlFeeders() 
   {
      return databaseCache.getAllCapControlFeeders();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContactNotificationGroups() 
   {
      return databaseCache.getAllContactNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlSubBuses() 
   {
      return databaseCache.getAllCapControlSubBuses();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContacts()
   {
      return databaseCache.getAllContacts();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   	public synchronized java.util.List getAllBaselines()
	{
		return databaseCache.getAllBaselines();
	}

	public synchronized java.util.List getAllSeasonSchedules()
	{
		return databaseCache.getAllSeasonSchedules();
	}
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllCommands()
	{
		return databaseCache.getAllCommands();
	}
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllCommandsMap()
	{
		return databaseCache.getAllCommandsMap();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllConfigs()
	{
		return databaseCache.getAllConfigs();
	}
	
	public synchronized java.util.List getAllTOUSchedules()
	{
		return databaseCache.getAllTOUSchedules();
	}
	
	public synchronized java.util.List getAllTOUDays()
	{
		return databaseCache.getAllTOUDays();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllLMProgramConstraints()
	{
		return databaseCache.getAllLMProgramConstraints();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllLMScenarios()
	{
		return databaseCache.getAllLMScenarios();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllLMScenarioProgs()
	{
		return databaseCache.getAllLMScenarioProgs();
	}
	
	public synchronized java.util.List getAllLMPAOExclusions()
	{
		return databaseCache.getAllLMPAOExclusions();
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCICustomers() 
   {
      return databaseCache.getAllCICustomers();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllCustomers() 
   {
	  return databaseCache.getAllCustomers();
   }
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.Map getAllCustomersMap()
   {
	   return databaseCache.getAllCustomersMap();
   }


   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDeviceMeterGroups()
   {
      return databaseCache.getAllDeviceMeterGroups();
   }
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllDMG_CollectionGroups()
   {
	  return databaseCache.getAllDMG_CollectionGroups();
   }
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllDMG_AlternateGroups()
   {
	  return databaseCache.getAllDMG_AlternateGroups();
   }
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllDMG_BillingGroups()
   {
	  return databaseCache.getAllDMG_BillingGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDevices() 
   {
      return databaseCache.getAllDevices();
   }
   
   /**
	* @ejb:interface-method
	* tview-type="remote"
	*/
   public synchronized java.util.List getAllMCTs() {
	  return databaseCache.getAllMCTs();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllGraphDefinitions()
   {
      return databaseCache.getAllGraphDefinitions();
   }
   
   // This cache is derive from the point cache
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   /*
   public synchronized java.util.List getAllGraphTaggedPoints()
   {
      return getDBCache().getAllGraphTaggedPoints();
   }
   */   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllHolidaySchedules()
   {
      return databaseCache.getAllHolidaySchedules();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLMPrograms()
   {
      return databaseCache.getAllLMPrograms();
   }

   public synchronized List getAllLMControlAreas()
   {
	   return databaseCache.getAllLMControlAreas();
   }
   
   public synchronized java.util.List getAllGears()
   {
	  return databaseCache.getAllGears();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLoadManagement() 
   {
      return databaseCache.getAllLoadManagement();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPoints()
   {
      return databaseCache.getAllPoints();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllSystemPoints()
   {
      return databaseCache.getAllSystemPoints();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllPointsMap()
	{
		return databaseCache.getAllPointsMap();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPointsUnits()
   {
      return databaseCache.getAllPointsUnits();
   }
   
    /**
	 * @ejb:interface-method
	 * tview-type="remote"
	 **/
	public synchronized List getAllPointLimits() {
		return databaseCache.getAllPointLimits();
    }

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized Map getAllContactNotifsMap()
	{
		return databaseCache.getAllContactNotifsMap();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.HashMap getAllPointidMultiplierHashMap()
   {
      return databaseCache.getAllPointidMultiplierHashMap();
   }
   
   /**
	* @ejb:interface-method
	* tview-type="remote" 
	**/
   public synchronized java.util.Map getAllPointIDOffsetMap() 
   {
   		return databaseCache.getAllPointIDOffsetMap();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPorts() 
   {
      return databaseCache.getAllPorts();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllRoutes() 
   {
      return databaseCache.getAllRoutes();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.Map getAllStateGroupMap()
   {
      return databaseCache.getAllStateGroupMap();
   }

	public synchronized List getAllTags() 
	{
		return databaseCache.getAllTags();
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnitMeasures()
   {
      return databaseCache.getAllUnitMeasures();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnusedCCDevices()
   {
      return databaseCache.getAllUnusedCCDevices();
   }
   
   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonPAObjects()
   {
      return databaseCache.getAllYukonPAObjects();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllPAOsMap()
	{
		return databaseCache.getAllPAOsMap();
	}
	
    /**
     * @ejb:interface-method
     * tview-type="remote" 
    **/
    public synchronized java.util.Map getAllContactsMap()
    {
        return databaseCache.getAllContactsMap();
    }
    
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.Map getAllUsersMap()
   {
      return databaseCache.getAllUsersMap();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg)
   {
      return databaseCache.handleDBChangeMessage( dbChangeMsg );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllAlarmCategories()
   {
      databaseCache.releaseAllAlarmCategories();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized void releaseAllYukonUsers()
	{
		databaseCache.releaseAllYukonUsers();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllContacts()
   {
      databaseCache.releaseAllContacts();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized void releaseAllCustomers()
	{
	   databaseCache.releaseAllCustomers();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllDeviceMeterGroups()
   {
      databaseCache.releaseAllDeviceMeterGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonImages()
   {
      databaseCache.releaseAllYukonImages();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllGraphDefinitions()
   {
      databaseCache.releaseAllGraphDefinitions();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllHolidaySchedules()
   {
      databaseCache.releaseAllHolidaySchedules();
   }
   public synchronized void releaseAllBaselines()
   {
	  databaseCache.releaseAllBaselines();
   }
   
   public synchronized void releaseAllSeasonSchedules()
   {
	  databaseCache.releaseAllSeasonSchedules();
   }
   public synchronized void releaseAllCommands()
   {
	  databaseCache.releaseAllCommands();
   }

   
   public synchronized void releaseAllTOUSchedules()
   {
	  databaseCache.releaseAllTOUSchedules();
   }
   
   public synchronized void releaseAllTOUDays()
   {
	  databaseCache.releaseAllTOUDays();
   }
   
   public synchronized void releaseAllConfigs()
   {
	  databaseCache.releaseAllConfigs();
   }
   
   public synchronized void releaseAllLMProgramConstraints()
   {
	  databaseCache.releaseAllLMProgramConstraints();
   }
   
   public synchronized void releaseAllLMScenarios()
   {
	  databaseCache.releaseAllLMScenarios();
   }
   
   public synchronized void releaseAllLMPAOExclusions()
   {
   	  databaseCache.releaseAllLMPAOExclusions();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllNotificationGroups()
   {
      databaseCache.releaseAllNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllPoints()
   {   
      databaseCache.releaseAllPoints();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllStateGroups()
   {   
      databaseCache.releaseAllStateGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllUnitMeasures()
   {   
      databaseCache.releaseAllUnitMeasures();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonPAObjects()
   {
      databaseCache.releaseAllYukonPAObjects();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void removeDBChangeListener(DBChangeListener listener) 
   {
      databaseCache.removeDBChangeListener( listener );
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void setDatabaseAlias(String newAlias)
   {
      databaseCache.setDatabaseAlias( newAlias );
   }

	/**
 	* @ejb:interface-method
 	* tview-type="remote"
 	*/
	public synchronized Map getYukonUserRolePropertyMap() {
		return databaseCache.getYukonUserRolePropertyMap();
	}	

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonGroups() {
		return databaseCache.getAllYukonGroups();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonRoles() {
		return databaseCache.getAllYukonRoles();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonRoleProperties() {
		return databaseCache.getAllYukonRoleProperties();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonUsers() {
		return databaseCache.getAllYukonUsers();
	}
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupRolePropertyMap()
	 */
	public synchronized  Map getYukonGroupRolePropertyMap() {
		return databaseCache.getYukonGroupRolePropertyMap();
	}

    /**
     * @see com.cannontech.yukon.IDatabaseCache#getYukonUserPaoOwners()
     */
    public synchronized Map getYukonUserPaoOwners() {
        return databaseCache.getYukonUserPaoOwners();
    }

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserGroupMap()
	 */
	public synchronized  Map getYukonUserGroupMap() {
		return databaseCache.getYukonUserGroupMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupUserMap()
	 */
	public synchronized  Map getYukonGroupUserMap() {
		return databaseCache.getYukonGroupUserMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllEnergyCompanies()
	 */
	public synchronized  List getAllEnergyCompanies() {
		return databaseCache.getAllEnergyCompanies();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllUserEnergyCompanies()
	 */
	public synchronized  Map getAllUserEnergyCompanies() {
		return databaseCache.getAllUserEnergyCompanies();
	}

	public synchronized void releaseAllTags()
	{
	   databaseCache.releaseAllTags();
	}

	public synchronized List getAllDeviceTypeCommands()
	{
		return databaseCache.getAllDeviceTypeCommands();
	}

	public synchronized void releaseAllDeviceTypeCommands()
	{
		databaseCache.releaseAllDeviceTypeCommands();
		
	}

    public synchronized List getAllLMGroups()
    {
        return databaseCache.getAllLMGroups();
    }

	/**
	 * @return
	 */
	public LiteYukonRole getARole(LiteYukonUser user, int roleID) {
		return databaseCache.getARole(user, roleID);
	}

	/**
	 * @return
	 */
	public String getARolePropertyValue(LiteYukonUser user, int rolePropertyID) {
		return databaseCache.getARolePropertyValue(user, rolePropertyID);
	}

	/**
	 * 
	 */
	public void releaseUserRoleMap() {
		databaseCache.releaseUserRoleMap();
	}

	/**
	 * 
	 */
	public void releaseUserRolePropertyValueMap() {
		databaseCache.releaseUserRolePropertyValueMap();
	}

    public LiteContact getAContactByUserID(int userID) {
        return databaseCache.getAContactByUserID(userID);
    }
    
    public LiteContact getAContactByContactID(int contactID) {
        return databaseCache.getAContactByContactID(contactID);
    }
    
    public LiteContact[] getContactsByLastName(String lastName, boolean partialMatch) {
        return databaseCache.getContactsByLastName(lastName, partialMatch);
    }
    
    public LiteContact[] getContactsByFirstName(String firstName, boolean partialMatch) {
        return databaseCache.getContactsByFirstName(firstName, partialMatch);
    }

    public LiteContact[] getContactsByPhoneNumber(String phone, boolean partialMatch) {
        return databaseCache.getContactsByPhoneNumber(phone, partialMatch);
    }
    
    public LiteContact getContactsByEmail(String email) {
        return databaseCache.getContactsByEmail(email);
    }
    

    public LiteContactNotification getAContactNotifByNotifID(int contNotifyID) {
        return databaseCache.getAContactNotifByNotifID(contNotifyID);
    }
    
    public LiteCustomer getACustomerByContactID(int contactID) {
        return databaseCache.getACustomerByContactID(contactID);
    }
    
    public LiteCustomer getACustomerByCustomerID(int customerID) {
        return databaseCache.getACustomerByCustomerID(customerID);
    }
    
    public void releaseUserContactMap() {
        databaseCache.releaseUserContactMap();
    }

	public synchronized List getAllSettlementConfigs()
	{
		return databaseCache.getAllSettlementConfigs();
	}

	public synchronized void releaseAllSettlementConfigs()
	{
		databaseCache.releaseAllSettlementConfigs();		
	}

	public synchronized Map getAllSettlementConfigsMap()
	{
		return databaseCache.getAllSettlementConfigsMap();
	}
    
    public List getDevicesByCommPort(int portId) {
        return databaseCache.getDevicesByCommPort(portId);

    }

    public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        return databaseCache.getDevicesByDeviceAddress(masterAddress, slaveAddress);

    }

}
