package com.cannontech.ejb;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.cannontech.yukon.ITimedDatabaseCache;;

/* Add this to DatabaseCacheHome class */
//public com.cannontech.ejb.DatabaseCache create() throws javax.ejb.CreateException, java.rmi.RemoteException;

/**
 * @ejb:bean name="TimedDatabaseCache"
 * jndi-name="jndi/TimedDatabaseCacheBean"
 * type="Stateful"
**/
public class TimedDatabaseCacheBean implements SessionBean, com.cannontech.yukon.ITimedDatabaseCache
{  
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
   
   public void ejbRemove() throws EJBException, java.rmi.RemoteException 
   {
   }
   
   public void ejbActivate() throws EJBException, java.rmi.RemoteException {}
   public void ejbPassivate() throws EJBException, java.rmi.RemoteException{}
   public void ejbCreate() throws javax.ejb.CreateException {}

   private com.cannontech.yukon.ITimedDatabaseCache getCache()
   {      
      return com.cannontech.yukon.server.cache.TimedServerDatabaseCache.getInstance();
   }

   public TimedDatabaseCacheBean()
   {
      super();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPeakPointHistory()
   {
      return getCache().getAllPeakPointHistory();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void loadAllTimedCache()
   {
      getCache().loadAllTimedCache();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllPeakPointHistory()
   {   
      getCache().releaseAllPeakPointHistory();
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
   **/
   public void setUpdateTimeInMillis(long millis)
   {
		getCache().setUpdateTimeInMillis(millis);
   }
}
