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
   public synchronized java.util.List getAllCustomerContacts()
   {
      return getCache().getAllCustomerContacts();
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
   public synchronized java.util.List getAllNotificationGroups()
   {
      return getCache().getAllNotificationGroups();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllNotificationRecipients()
   {
      return getCache().getAllNotificationRecipients();
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
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupRoleMap()
	 */
	public Map getAllYukonGroupRoleMap() {
		return getCache().getAllYukonGroupRoleMap();
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
	public Map getAllYukonGroupUserMap() {
		return getCache().getAllYukonGroupUserMap();
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
	public Map getAllYukonUserGroupMap() {
		return getCache().getAllYukonUserGroupMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Map getAllYukonUserRoleLookupMap() {
		return getCache().getAllYukonUserRoleLookupMap();
	}

	/**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Map getAllYukonUserRoleMap() {
		return getCache().getAllYukonUserRoleMap();
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
   public synchronized void loadAllCache()
   {
      getCache().loadAllCache();
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
   public synchronized void releaseAllNotificationRecipients()
   {   
      getCache().releaseAllNotificationRecipients();
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
	public Map getAllYukonUserRoleIDLookupMap() {
		return getCache().getAllYukonUserRoleIDLookupMap();
	}

}
