package com.cannontech.ejb;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.ejb.SessionBean;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.Reference;
import javax.rmi.PortableRemoteObject;

import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;


/* Add this to DatabaseCacheHome class */
//public com.cannontech.ejb.DatabaseCache create() throws javax.ejb.CreateException, java.rmi.RemoteException;

/**
 * @ejb:bean name="DatabaseCache"
 * jndi-name="jndi/DatabaseCacheBean"
 * type="Stateful"
**/
public class DatabaseCacheBean implements SessionBean, IDatabaseCache
{  
	private IDatabaseCache dbCache = null;
	
   static
   {
      //Add the DBChange listener connection for Dispatch to our Cache
      //ServerDatabaseCache.getInstance().addDBChangeListener( 
           // new CacheChangeListener() );
   }
   
   private SessionContext cnt = null;   

   public void setSessionContext(SessionContext cntxt) throws EJBException, java.rmi.RemoteException 
   {
      cnt = cntxt;
   }
   
   public void ejbRemove() throws EJBException, java.rmi.RemoteException {}
   public void ejbActivate() throws EJBException, java.rmi.RemoteException {}
   public void ejbPassivate() throws EJBException, java.rmi.RemoteException{}
   public void ejbCreate() throws javax.ejb.CreateException {}

   private com.cannontech.yukon.IDatabaseCache getCache()
   {  
   	if( dbCache == null )
   	{
	   	try
	   	{
		      Hashtable props = new Hashtable();		
		      props.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		      props.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1099");
	   		

	   		//get the context of the current VM (We should be inside an App Server!)
				InitialContext rootCtx = new InitialContext(props);
				
				// Get the Cache instance MBean by using JNDI
				dbCache = (IDatabaseCache)rootCtx.lookup(
										"com/cannontech/DatabaseCache");

				CTILogger.debug( "CACHE: Found DB_Cache instance in AppServer, using it" );
	   	}
	   	catch( Exception e )
	   	{
	   		//we must not be inside of an App Server, just create a new cache by ourself
	   		CTILogger.debug( "CACHE: Unable to find AppServer, creating Cache independently" );
	   		dbCache = ServerDatabaseCache.getInstance();
	   	}
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

   public synchronized java.util.List getAllConfigs()
   {
	  return getCache().getAllConfigs();
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
   public synchronized java.util.List getAllLMPrograms()
   {
      return getCache().getAllLMPrograms();
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
	public List getAllPointLimits() 
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
   public synchronized java.util.List getAllStateGroups()
   {
      return getCache().getAllStateGroups();
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
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupRoleMap()
	 */
	public Map getYukonGroupRolePropertyMap() {
		return getCache().getYukonGroupRolePropertyMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public List getAllYukonGroups() {
		return getCache().getAllYukonGroups();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Map getYukonGroupUserMap() {
		return getCache().getYukonGroupUserMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public List getAllYukonRoles() {
		return getCache().getAllYukonRoles();
	}
	
	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public List getAllYukonRoleProperties() {
		return getCache().getAllYukonRoleProperties();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Map getYukonUserGroupMap() {
		return getCache().getYukonUserGroupMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Map getYukonUserRolePropertyMap() {
		return getCache().getYukonUserRolePropertyMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public List getAllYukonUsers() {
		return getCache().getAllYukonUsers();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public List getAllEnergyCompanies() {
		return getCache().getAllEnergyCompanies();
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public Map getAllUserEnergyCompanies() {
		return getCache().getAllUserEnergyCompanies();
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
   public synchronized void releaseAllCache()
   {
      getCache().releaseAllCache();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllCustomerContacts()
   {
      getCache().releaseAllCustomerContacts();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllDeviceMeterGroups()
   {
      getCache().releaseAllDeviceMeterGroups();
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
   
   public synchronized void releaseAllConfigs()
   {
	  getCache().releaseAllConfigs();
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
   public synchronized void releaseAllContactNotifications()
   {   
      getCache().releaseAllContactNotifications();
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
   public void removeDBChangeListener(DBChangeListener listener) 
   {
      getCache().removeDBChangeListener( listener );
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void setDatabaseAlias(String newAlias)
   {
      getCache().setDatabaseAlias( newAlias );
   }	
	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public Map getYukonUserRoleIDLookupMap() {
		return getCache().getYukonUserRoleIDLookupMap();
	}
	
    /**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public Map getYukonUserRolePropertyIDLookupMap() {
		return getCache().getYukonUserRolePropertyIDLookupMap();
	}
	
}
