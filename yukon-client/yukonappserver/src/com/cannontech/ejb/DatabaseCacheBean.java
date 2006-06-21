package com.cannontech.ejb;

import java.util.List;
import java.util.Map;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @ejb:bean name="DatabaseCache"
 * jndi-name="jndi/DatabaseCacheBean"
 * type="Stateful"
**/
public class DatabaseCacheBean implements IDatabaseCache
{  
    private IDatabaseCache dbCache = null;
	
    static
    {
      //Add the DBChange listener connection for Dispatch to our Cache
      //ServerDatabaseCache.getInstance().addDBChangeListener( 
           // new CacheChangeListener() );
    }
   
   private synchronized com.cannontech.yukon.IDatabaseCache getCache()
   {  
   	if( dbCache == null )
   	{
   		dbCache = ServerDatabaseCache.getInstance();
   	}
   	
   	return dbCache;      
   }

   public DatabaseCacheBean()
   {
      super();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void addDBChangeListener(DBChangeListener listener) 
   {
      getCache().addDBChangeListener( listener );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
   {
      return getCache().createDBChangeMessages( newItem, changeType );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllAlarmCategories()
   {
      return getCache().getAllAlarmCategories();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonImages()
   {
      return getCache().getAllYukonImages();   
   }
  
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void releaseAllCache()
   {
       getCache().releaseAllCache();   
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlFeeders() 
   {
      return getCache().getAllCapControlFeeders();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlSubBuses() 
   {
      return getCache().getAllCapControlSubBuses();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContacts()
   {
      return getCache().getAllContacts();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDeviceMeterGroups()
   {
      return getCache().getAllDeviceMeterGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDevices() 
   {
      return getCache().getAllDevices();
   }
   
   public synchronized List getAllDeviceTypeCommands()
   {
	   return getCache().getAllDeviceTypeCommands();
   }
   
   /**
	* @ejb:interface-method
	* tview-type:"remote"
	*/
   public synchronized java.util.List getAllMCTs() {
	  return getCache().getAllMCTs();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllGraphDefinitions()
   {
      return getCache().getAllGraphDefinitions();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllHolidaySchedules()
   {
      return getCache().getAllHolidaySchedules();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllBaselines()
   {
	  return getCache().getAllBaselines();
   }
   
   public synchronized java.util.List getAllSeasonSchedules()
   {
	  return getCache().getAllSeasonSchedules();
   }
   
   public synchronized List getAllCommands()
   {
	   return getCache().getAllCommands();
   }
   
   public synchronized Map getAllCommandsMap()
   {
	   return getCache().getAllCommandsMap();
   }
   
   public synchronized java.util.List getAllConfigs()
   {
	  return getCache().getAllConfigs();
   }
   
   public synchronized java.util.List getAllTOUSchedules()
   {
	  return getCache().getAllTOUSchedules();
   }
   
   public synchronized java.util.List getAllTOUDays()
   {
	  return getCache().getAllTOUDays();
   }
   
   public synchronized java.util.List getAllLMProgramConstraints()
   {
	  return getCache().getAllLMProgramConstraints();
   }
   
   public synchronized java.util.List getAllLMScenarios()
   {
	  return getCache().getAllLMScenarios();
   }

	public synchronized java.util.List getAllLMScenarioProgs()
	{
		return getCache().getAllLMScenarioProgs();
	}

	public synchronized java.util.List getAllLMPAOExclusions()
	{
		return getCache().getAllLMPAOExclusions();
	}
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized java.util.List getAllCICustomers()
	{
		return getCache().getAllCICustomers();
	}
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	 public synchronized java.util.List getAllCustomers()
	 {
		 return getCache().getAllCustomers();
	 }
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllCustomersMap()
	{
		return getCache().getAllCustomersMap();		
	}
	 
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLMPrograms()
   {
      return getCache().getAllLMPrograms();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllLMControlAreas()
   {
	  return getCache().getAllLMControlAreas();
   }

   public synchronized List getAllLMGroups()
   {
       return getCache().getAllLMGroups();
   }   
   public synchronized java.util.List getAllGears()
   {
	  return getCache().getAllGears();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLoadManagement() 
   {
      return getCache().getAllLoadManagement();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContactNotificationGroups()
   {
      return getCache().getAllContactNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPoints()
   {
      return getCache().getAllPoints();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllSystemPoints()
   {
      return getCache().getAllSystemPoints();
   }

   
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllPointsMap()
	{
		return getCache().getAllPointsMap();		
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPointsUnits()
   {
      return getCache().getAllPointsUnits();
   }
   
	
   /**
	* @ejb:interface-method
	* tview-type="remote"
   **/
	public synchronized List getAllPointLimits() 
	{
		return getCache().getAllPointLimits();
	}	
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.HashMap getAllPointidMultiplierHashMap()
   {
      return getCache().getAllPointidMultiplierHashMap();
   }
   
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.Map getAllPointIDOffsetMap()
   {
	 return getCache().getAllPointIDOffsetMap();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPorts() 
   {
      return getCache().getAllPorts();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllRoutes() 
   {
      return getCache().getAllRoutes();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.Map getAllStateGroupMap()
   {
      return getCache().getAllStateGroupMap();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllTags() {
   		return getCache().getAllTags();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnitMeasures()
   {
      return getCache().getAllUnitMeasures();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnusedCCDevices()
   {
      return getCache().getAllUnusedCCDevices();
   }
   
   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonPAObjects()
   {
      return getCache().getAllYukonPAObjects();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllPAOsMap()
	{
		return getCache().getAllPAOsMap();
	}

    /**
     * @ejb:interface-method
     * tview-type="remote" 
    **/
    public synchronized java.util.Map getAllContactsMap()
    {
        return getCache().getAllContactsMap();
    }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllUsersMap()
	{
		return getCache().getAllUsersMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupRoleMap()
	 */
	public synchronized Map getYukonGroupRolePropertyMap() {
		return getCache().getYukonGroupRolePropertyMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized List getAllYukonGroups() {
		return getCache().getAllYukonGroups();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized Map getYukonGroupUserMap() {
		return getCache().getYukonGroupUserMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized List getAllYukonRoles() {
		return getCache().getAllYukonRoles();
	}
	
	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized List getAllYukonRoleProperties() {
		return getCache().getAllYukonRoleProperties();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized Map getYukonUserGroupMap() {
		return getCache().getYukonUserGroupMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized Map getYukonUserRolePropertyMap() {
		return getCache().getYukonUserRolePropertyMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public synchronized List getAllYukonUsers() {
		return getCache().getAllYukonUsers();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized List getAllEnergyCompanies() {
		return getCache().getAllEnergyCompanies();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized Map getAllUserEnergyCompanies() {
		return getCache().getAllUserEnergyCompanies();
	}
		
	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized Map getAllContactNotifsMap() {
		return getCache().getAllContactNotifsMap();
	}
	
		
//   /**
//    * @ejb:interface-method
//    * tview-type="remote" 
//   **/
//   public IDatabaseCache getInstance()
//   {
//      return this;
//   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg)
   {
      return getCache().handleDBChangeMessage( dbChangeMsg );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllAlarmCategories()
   {
      getCache().releaseAllAlarmCategories();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized void releaseAllYukonUsers()
	{
		getCache().releaseAllYukonUsers();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllContacts()
   {
      getCache().releaseAllContacts();
   }
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized void releaseAllCustomers()
   {
	  getCache().releaseAllCustomers();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllDeviceMeterGroups()
   {
      getCache().releaseAllDeviceMeterGroups();
   }

   public synchronized void releaseAllDeviceTypeCommands()
   {
	   getCache().releaseAllDeviceTypeCommands();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonImages()
   {
      getCache().releaseAllYukonImages();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllGraphDefinitions()
   {
      getCache().releaseAllGraphDefinitions();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllHolidaySchedules()
   {
      getCache().releaseAllHolidaySchedules();
   }

   public synchronized void releaseAllBaselines()
   {
	  getCache().releaseAllBaselines();
   }
   
   public synchronized void releaseAllSeasonSchedules()
   {
	  getCache().releaseAllSeasonSchedules();
   }

   public synchronized void releaseAllCommands()
   {
	   getCache().releaseAllCommands();
   }   
   
   public synchronized void releaseAllConfigs()
   {
	  getCache().releaseAllConfigs();
   }
   
   public synchronized void releaseAllTOUSchedules()
   {
	  getCache().releaseAllTOUSchedules();
   }
   
   public synchronized void releaseAllTOUDays()
   {
	  getCache().releaseAllTOUDays();
   }
   
   public synchronized void releaseAllTags()
   {   
	  getCache().releaseAllTags();
   }
   
   public synchronized void releaseAllLMProgramConstraints()
   {   
	  getCache().releaseAllLMProgramConstraints();
   }
   
   public synchronized void releaseAllLMScenarios()
   {   
	  getCache().releaseAllLMScenarios();
   }
   
   public synchronized void releaseAllLMPAOExclusions()
   {
   	  getCache().releaseAllLMPAOExclusions();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllNotificationGroups()
   {
      getCache().releaseAllNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllPoints()
   {   
      getCache().releaseAllPoints();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllStateGroups()
   {   
      getCache().releaseAllStateGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllUnitMeasures()
   {   
      getCache().releaseAllUnitMeasures();
   }

   /**
    * @see com.cannontech.yukon.IDatabaseCache#getYukonUserPaoOwners()
    */
   public synchronized Map getYukonUserPaoOwners() {
       return getCache().getYukonUserPaoOwners();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonPAObjects()
   {
      getCache().releaseAllYukonPAObjects();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized  void removeDBChangeListener(DBChangeListener listener) 
   {
      getCache().removeDBChangeListener( listener );
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void setDatabaseAlias(String newAlias)
   {
      getCache().setDatabaseAlias( newAlias );
   }	
	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized List getAllDMG_CollectionGroups()
	{
		return getCache().getAllDMG_CollectionGroups();
	}

	public synchronized  List getAllDMG_AlternateGroups()
	{
		return getCache().getAllDMG_AlternateGroups();
	}

	public synchronized List getAllDMG_BillingGroups()
	{
		return getCache().getAllDMG_BillingGroups();
	}
	/**
	 * @return
	 */
	public LiteYukonRole getARole(LiteYukonUser user, int roleID) {
		return getCache().getARole(user, roleID);
	}

	/**
	 * @return
	 */
	public String getARolePropertyValue(LiteYukonUser user, int rolePropertyID) {
		return getCache().getARolePropertyValue(user, rolePropertyID);
	}

	/**
	 * 
	 */
	public void releaseUserRoleMap() {
		getCache().releaseUserRoleMap();
	}

	/**
	 * 
	 */
	public void releaseUserRolePropertyValueMap() {
		getCache().releaseUserRolePropertyValueMap();
	}
    
    public LiteContact getAContactByUserID(int userID) {
        return getCache().getAContactByUserID(userID);
    }
    
    public LiteContact getAContactByContactID(int contactID) {
        return getCache().getAContactByContactID(contactID);
    }
    
    public LiteContact[] getContactsByLastName(String lastName, boolean partialMatch) {
        return getCache().getContactsByLastName(lastName, partialMatch);
    }
    
    public LiteContact[] getContactsByFirstName(String firstName, boolean partialMatch) {
        return getCache().getContactsByFirstName(firstName, partialMatch);
    }
    
    public LiteContact[] getContactsByPhoneNumber(String phone, boolean partialMatch) {
        return getCache().getContactsByPhoneNumber(phone, partialMatch);
    }
    
    public LiteContact getContactsByEmail(String email) {
        return getCache().getContactsByEmail(email);
    }
    
    public LiteContactNotification getAContactNotifByNotifID(int contNotifyID) {
        return getCache().getAContactNotifByNotifID(contNotifyID);
    }

    public LiteCustomer getACustomerByContactID(int contactID) {
        return getCache().getACustomerByContactID(contactID);
    }
    
    public LiteCustomer getACustomerByCustomerID(int customerID) {
        return getCache().getACustomerByCustomerID(customerID);
    }
    
    public void releaseUserContactMap() {
        getCache().releaseUserContactMap();
    }

	public synchronized List getAllSettlementConfigs()
	{
		return getCache().getAllSettlementConfigs();	  
	}

	public synchronized void releaseAllSettlementConfigs()
	{
		getCache().releaseAllSettlementConfigs();
	}
	public synchronized Map getAllSettlementConfigsMap()
	{
		return getCache().getAllSettlementConfigsMap();
	}

    public List getDevicesByCommPort(int portId) {
      
         return getCache().getDevicesByCommPort(portId);
    }

    public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        return getCache().getDevicesByDeviceAddress(masterAddress, slaveAddress);
    }
}
