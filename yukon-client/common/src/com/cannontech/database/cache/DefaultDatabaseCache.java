package com.cannontech.database.cache;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.concrete.ResourceFactory;

/**
 * @author rneuharth
 * Sep 25, 2002 at 5:45:26 PM
 * 
 * A undefined generated comment
 */
public class DefaultDatabaseCache implements IDatabaseCache
{
   private static DefaultDatabaseCache cache = null;
   
	/**
	 * Constructor for DefaultDatabaseCache.
	 */
	public DefaultDatabaseCache()
	{
		super();
	} 

   public synchronized static final DefaultDatabaseCache getInstance()
   {
      if( cache == null )
      {
         cache = new DefaultDatabaseCache();
      }
      
      return cache;
   }

   private synchronized IDatabaseCache getDBCache()
   {
      return ResourceFactory.getIYukon().getDBCache();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized  void addDBChangeListener(DBChangeListener listener) 
   {
      getDBCache().addDBChangeListener( listener );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
   {
      return getDBCache().createDBChangeMessages( newItem, changeType );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllAlarmCategories()
   {
      return getDBCache().getAllAlarmCategories();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonImages()
   {
      return getDBCache().getAllYukonImages();   
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void releaseAllCache()
   {
      getDBCache().releaseAllCache();   
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlFeeders() 
   {
      return getDBCache().getAllCapControlFeeders();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContactNotificationGroups() 
   {
      return getDBCache().getAllContactNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlSubBuses() 
   {
      return getDBCache().getAllCapControlSubBuses();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContacts()
   {
      return getDBCache().getAllContacts();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   	public synchronized java.util.List getAllBaselines()
	{
		return getDBCache().getAllBaselines();
	}

	public synchronized java.util.List getAllSeasonSchedules()
	{
		return getDBCache().getAllSeasonSchedules();
	}
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllConfigs()
	{
		return getDBCache().getAllConfigs();
	}
	
	public synchronized java.util.List getAllTOUSchedules()
	{
		return getDBCache().getAllTOUSchedules();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllLMProgramConstraints()
	{
		return getDBCache().getAllLMProgramConstraints();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllLMScenarios()
	{
		return getDBCache().getAllLMScenarios();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.List getAllLMScenarioProgs()
	{
		return getDBCache().getAllLMScenarioProgs();
	}
	
	public synchronized java.util.List getAllLMPAOExclusions()
	{
		return getDBCache().getAllLMPAOExclusions();
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCICustomers() 
   {
      return getDBCache().getAllCICustomers();
   }

   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.List getAllCustomers() 
   {
	  return getDBCache().getAllCustomers();
   }
   /**
	* @ejb:interface-method
	* tview-type="remote" 
   **/
   public synchronized java.util.Map getAllCustomersMap()
   {
	   return getDBCache().getAllCustomersMap();
   }


   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDeviceMeterGroups()
   {
      return getDBCache().getAllDeviceMeterGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDevices() 
   {
      return getDBCache().getAllDevices();
   }
   
   /**
	* @ejb:interface-method
	* tview-type="remote"
	*/
   public synchronized java.util.List getAllMCTs() {
	  return getDBCache().getAllMCTs();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllGraphDefinitions()
   {
      return getDBCache().getAllGraphDefinitions();
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
      return getDBCache().getAllHolidaySchedules();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLMPrograms()
   {
      return getDBCache().getAllLMPrograms();
   }

   public synchronized java.util.List getAllGears()
   {
	  return getDBCache().getAllGears();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLoadManagement() 
   {
      return getDBCache().getAllLoadManagement();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPoints()
   {
      return getDBCache().getAllPoints();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllPointsMap()
	{
		return getDBCache().getAllPointsMap();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPointsUnits()
   {
      return getDBCache().getAllPointsUnits();
   }
   
    /**
	 * @ejb:interface-method
	 * tview-type="remote"
	 **/
	public synchronized List getAllPointLimits() {
		return getDBCache().getAllPointLimits();
    }
    
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.HashMap getAllPointidMultiplierHashMap()
   {
      return getDBCache().getAllPointidMultiplierHashMap();
   }
   
   /**
	* @ejb:interface-method
	* tview-type="remote" 
	**/
   public synchronized java.util.Map getAllPointIDOffsetMap() 
   {
   		return getDBCache().getAllPointIDOffsetMap();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPorts() 
   {
      return getDBCache().getAllPorts();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllRoutes() 
   {
      return getDBCache().getAllRoutes();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllStateGroups()
   {
      return getDBCache().getAllStateGroups();
   }

	public synchronized List getAllTags() 
	{
		return getDBCache().getAllTags();
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnitMeasures()
   {
      return getDBCache().getAllUnitMeasures();
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnusedCCDevices()
   {
      return getDBCache().getAllUnusedCCDevices();
   }
   
   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonPAObjects()
   {
      return getDBCache().getAllYukonPAObjects();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized java.util.Map getAllPAOsMap()
	{
		return getDBCache().getAllPAOsMap();
	}
	
    /**
     * @ejb:interface-method
     * tview-type="remote" 
    **/
    public synchronized java.util.Map getAllContactsMap()
    {
        return getDBCache().getAllContactsMap();
    }
    
//   /**
//    * @ejb:interface-method
//    * tview-type="remote" 
//   **/
//   public IDatabaseCache getDBCache()
//   {
//      return this;
//   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg)
   {
      return getDBCache().handleDBChangeMessage( dbChangeMsg );
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllAlarmCategories()
   {
      getDBCache().releaseAllAlarmCategories();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized void releaseAllYukonUsers()
	{
		getDBCache().releaseAllYukonUsers();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllContacts()
   {
      getDBCache().releaseAllContacts();
   }

	/**
	 * @ejb:interface-method
	 * tview-type="remote" 
	**/
	public synchronized void releaseAllCustomers()
	{
	   getDBCache().releaseAllCustomers();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllDeviceMeterGroups()
   {
      getDBCache().releaseAllDeviceMeterGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonImages()
   {
      getDBCache().releaseAllYukonImages();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllGraphDefinitions()
   {
      getDBCache().releaseAllGraphDefinitions();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllHolidaySchedules()
   {
      getDBCache().releaseAllHolidaySchedules();
   }
   public synchronized void releaseAllBaselines()
   {
	  getDBCache().releaseAllBaselines();
   }
   
   public synchronized void releaseAllSeasonSchedules()
   {
	  getDBCache().releaseAllSeasonSchedules();
   }
   
   public synchronized void releaseAllTOUSchedules()
   {
	  getDBCache().releaseAllTOUSchedules();
   }
   
   public synchronized void releaseAllConfigs()
   {
	  getDBCache().releaseAllConfigs();
   }
   
   public synchronized void releaseAllLMProgramConstraints()
   {
	  getDBCache().releaseAllLMProgramConstraints();
   }
   
   public synchronized void releaseAllLMScenarios()
   {
	  getDBCache().releaseAllLMScenarios();
   }
   
   public synchronized void releaseAllLMPAOExclusions()
   {
   	  getDBCache().releaseAllLMPAOExclusions();
   }
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllNotificationGroups()
   {
      getDBCache().releaseAllNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllContactNotifications()
   {   
      getDBCache().releaseAllContactNotifications();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllPoints()
   {   
      getDBCache().releaseAllPoints();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllStateGroups()
   {   
      getDBCache().releaseAllStateGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllUnitMeasures()
   {   
      getDBCache().releaseAllUnitMeasures();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonPAObjects()
   {
      getDBCache().releaseAllYukonPAObjects();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void removeDBChangeListener(DBChangeListener listener) 
   {
      getDBCache().removeDBChangeListener( listener );
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void setDatabaseAlias(String newAlias)
   {
      getDBCache().setDatabaseAlias( newAlias );
   }

	/**
 	* @ejb:interface-method
 	* tview-type="remote"
 	*/
	public synchronized Map getYukonUserRolePropertyMap() {
		return getDBCache().getYukonUserRolePropertyMap();
	}	

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonGroups() {
		return getDBCache().getAllYukonGroups();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonRoles() {
		return getDBCache().getAllYukonRoles();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonRoleProperties() {
		return getDBCache().getAllYukonRoleProperties();
	}
	
	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public synchronized  List getAllYukonUsers() {
		return getDBCache().getAllYukonUsers();
	}
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupRolePropertyMap()
	 */
	public synchronized  Map getYukonGroupRolePropertyMap() {
		return getDBCache().getYukonGroupRolePropertyMap();
	}

    /**
     * @see com.cannontech.yukon.IDatabaseCache#getYukonUserPaoOwners()
     */
    public synchronized Map getYukonUserPaoOwners() {
        return getDBCache().getYukonUserPaoOwners();
    }

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserGroupMap()
	 */
	public synchronized  Map getYukonUserGroupMap() {
		return getDBCache().getYukonUserGroupMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupUserMap()
	 */
	public synchronized  Map getYukonGroupUserMap() {
		return getDBCache().getYukonGroupUserMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllEnergyCompanies()
	 */
	public synchronized  List getAllEnergyCompanies() {
		return getDBCache().getAllEnergyCompanies();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllUserEnergyCompanies()
	 */
	public synchronized  Map getAllUserEnergyCompanies() {
		return getDBCache().getAllUserEnergyCompanies();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserRoleIDLookupMap()
	 */
	public synchronized  Map getYukonUserRoleIDLookupMap() {
		return getDBCache().getYukonUserRoleIDLookupMap();
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserRolePropertyIDLookupMap()
	 */
	public synchronized  Map getYukonUserRolePropertyIDLookupMap() {
		return getDBCache().getYukonUserRolePropertyIDLookupMap();
	}
	
	public synchronized void releaseAllTags()
	{
	   getDBCache().releaseAllTags();
	}

}
