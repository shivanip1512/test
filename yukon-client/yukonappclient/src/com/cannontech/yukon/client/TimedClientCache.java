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
public class TimedClientCache implements com.cannontech.yukon.ITimedDatabaseCache
{
   private com.cannontech.ejb.TimedDatabaseCache cacheBean = null;

	/**
	 * Constructor for ClientCache.
	 */
	public TimedClientCache()
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
         
         cacheBean = ((com.cannontech.ejb.TimedDatabaseCacheHome)initialContext.lookup(
                        com.cannontech.ejb.TimedDatabaseCacheHome.JNDI_NAME) ).create();
      }
      catch( Exception e ) 
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
   }

   private com.cannontech.ejb.TimedDatabaseCache getCache()
   {      
      return cacheBean;
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPeakPointHistory()
   {
      try
      {
         return getCache().getAllPeakPointHistory();
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
   public synchronized void loadAllTimedCache()
   {
      try
      {
         getCache().loadAllTimedCache();
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
   public synchronized void releaseAllPeakPointHistory()
   {   
      try
      {
         getCache().releaseAllPeakPointHistory();
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
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void setUpdateTimeInMillis(long millis)
   {
     try
      {
      	  getCache().setUpdateTimeInMillis(millis);
      }
      catch( java.rmi.RemoteException e )
      {
         com.cannontech.clientutils.CTILogger.info( e );
      }   	
   }
}
