package com.cannontech.ejb;

import com.cannontech.yukon.ITimedDatabaseCache;;

/**
 * @ejb:bean name="TimedDatabaseCache"
 * jndi-name="jndi/TimedDatabaseCacheBean"
 * type="Stateful"
**/
public class TimedDatabaseCacheBean implements com.cannontech.yukon.ITimedDatabaseCache
{  

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
