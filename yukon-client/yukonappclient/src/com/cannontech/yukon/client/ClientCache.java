package com.cannontech.yukon.client;

import java.util.List;
import java.util.Map;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Sep 12, 2002 at 4:22:33 PM
 * 
 * A undefined generated comment
 */
public class ClientCache implements IDatabaseCache
{
   private com.cannontech.ejb.DatabaseCache cacheBean = null;

	/**
	 * Constructor for ClientCache.
	 */
	public ClientCache()
	{
		super(); 
      initialize();
	}

   private void initialize()
   {      
      /* THIS NEEDS TO CHANGE AFTER WE GET ROLLING!!! ---RWN */
      java.util.Hashtable props = new java.util.Hashtable();
      //----------------------JBOSS
      props.put(javax.naming.InitialContext.INITIAL_CONTEXT_FACTORY,
               "org.jnp.interfaces.NamingContextFactory");
                  
      props.put(javax.naming.InitialContext.PROVIDER_URL,
               "jnp://127.0.0.1:1099");
      //----------------------JBOSS
      
      try
      {
         javax.naming.InitialContext initialContext = new javax.naming.InitialContext(props);
         
         cacheBean = ((com.cannontech.ejb.DatabaseCacheHome)initialContext.lookup(
                        com.cannontech.ejb.DatabaseCacheHome.JNDI_NAME) ).create();
      }
      catch( Exception e ) 
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
   }

   private com.cannontech.ejb.DatabaseCache getCache()
   {      
      return cacheBean;
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void addDBChangeListener(DBChangeListener listener)
   {
      try
      {
         getCache().addDBChangeListener( listener );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
   {
      try
      {
         return getCache().createDBChangeMessages( newItem, changeType );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new DBChangeMsg[0];
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllAlarmCategories()
   {
      try
      {
         return getCache().getAllAlarmCategories();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
         
   }
   
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContactNotificationGroups()
   {
      try
      {
         return getCache().getAllContactNotificationGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonImages()
   {
      try
      {
         return getCache().getAllYukonImages();   
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
         
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlFeeders() 
   {
      try
      {
         return getCache().getAllCapControlFeeders();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCapControlSubBuses() 
   {
      try
      {
         return getCache().getAllCapControlSubBuses();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllContacts()
   {
      try
      {
         return getCache().getAllContacts();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllCICustomers() 
   {
      try
      {
         return getCache().getAllCICustomers();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDeviceMeterGroups()
   {
      try
      {
         return getCache().getAllDeviceMeterGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
         
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllDevices() 
   {
      try
      {
         return getCache().getAllDevices();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllGraphDefinitions()
   {
      try
      {
         return getCache().getAllGraphDefinitions();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }
  
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllHolidaySchedules()
   {
      try
      {
         return getCache().getAllHolidaySchedules();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
         
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLMPrograms()
   {
      try
      {
         return getCache().getAllLMPrograms();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllLoadManagement() 
   {
      try
      {
         return getCache().getAllLoadManagement();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }


   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPoints()
   {
      try
      {
         return getCache().getAllPoints();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPointsUnits()
   {
      try
      {
         return getCache().getAllPointsUnits();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }
   
	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	**/
	public List getAllPointLimits() {
		try 
		{
			return getCache().getAllPointLimits();
		}
		catch(java.rmi.RemoteException e)
		{
			com.cannontech.clientutils.CTILogger.info(e);
			return new java.util.ArrayList();
		}
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.HashMap getAllPointidMultiplierHashMap()
   {
      try
      {
         return getCache().getAllPointidMultiplierHashMap();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }
   }
   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPorts() 
   {
      try
      {
         return getCache().getAllPorts();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllRoutes() 
   {
      try
      {
         return getCache().getAllRoutes();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllStateGroups()
   {
      try
      {
         return getCache().getAllStateGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnitMeasures()
   {
      try
      {
         return getCache().getAllUnitMeasures();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllUnusedCCDevices()
   {
      try
      {
         return getCache().getAllUnusedCCDevices();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }
   
   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllYukonPAObjects()
   {
      try
      {
         return getCache().getAllYukonPAObjects();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
   }

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupRoleMap()
	 */
	public Map getAllYukonGroupRoleMap() {
	  try
      {
         return getCache().getAllYukonGroupRoleMap();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroups()
	 */
	public List getAllYukonGroups() {
	  try
      {
         return getCache().getAllYukonGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupUserMap()
	 */
	public Map getAllYukonGroupUserMap() {
	  try
      {
         return getCache().getAllYukonGroupUserMap();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonRoles()
	 */
	public List getAllYukonRoles() {
	  try
      {
         return getCache().getAllYukonRoles();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserGroupMap()
	 */
	public Map getAllYukonUserGroupMap() {
	  try
      {
         return getCache().getAllYukonUserGroupMap();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserRoleLookupMap()
	 */
	public Map getAllYukonUserRoleLookupMap() {
	  try
      {
         return getCache().getAllYukonUserRoleLookupMap();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserRoleIDLookupMap()
	 */
	public Map getAllYukonUserRoleIDLookupMap() {
		try 
		{			
			return getCache().getAllYukonUserRoleIDLookupMap();
		}
		catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }	
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserRoleMap()
	 */
	public Map getAllYukonUserRoleMap() {
	  try
      {
         return getCache().getAllYukonUserRoleMap();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.HashMap();
      }
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUsers()
	 */
	public List getAllYukonUsers() {
	  try
      {
         return getCache().getAllYukonUsers();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
	}


		/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public List getAllEnergyCompanies() {
		try
      {
         return getCache().getAllEnergyCompanies();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return new java.util.ArrayList();
      }
		
	}

	/**
	 * @ejb:interface-method
	 * tview-type="remote"
	 */
	public Map getAllUserEnergyCompanies() {
		return null;
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
      try
      {
         return getCache().handleDBChangeMessage( dbChangeMsg );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
         return null;
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void loadAllCache()
   {
      try
      {
         getCache().loadAllCache();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllAlarmCategories()
   {
      try
      {
         getCache().releaseAllAlarmCategories();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllCache()
   {
      try
      {
         getCache().releaseAllCache();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllCustomerContacts()
   {
      try
      {
         getCache().releaseAllCustomerContacts();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllDeviceMeterGroups()
   {
      try
      {
         getCache().releaseAllDeviceMeterGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonImages()
   {
      try
      {
         getCache().releaseAllYukonImages();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllGraphDefinitions()
   {
      try
      {
         getCache().releaseAllGraphDefinitions();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllHolidaySchedules()
   {
      try
      {
         getCache().releaseAllHolidaySchedules();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllNotificationGroups()
   {
      try
      {
         getCache().releaseAllNotificationGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllContactNotifications()
   {   
      try
      {
         getCache().releaseAllNotificationRecipients();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllPoints()
   {   
      try
      {
         getCache().releaseAllPoints();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllStateGroups()
   {   
      try
      {
         getCache().releaseAllStateGroups();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllUnitMeasures()
   {   
      try
      {
         getCache().releaseAllUnitMeasures();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllYukonPAObjects()
   {
      try
      {
         getCache().releaseAllYukonPAObjects();
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void removeDBChangeListener(DBChangeListener listener) 
   {
      try
      {
         getCache().removeDBChangeListener( listener );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void setDatabaseAlias(String newAlias)
   {
      try
      {
         getCache().setDatabaseAlias( newAlias );
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }
   }
	

}
