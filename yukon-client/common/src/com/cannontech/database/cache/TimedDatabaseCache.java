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
public class TimedDatabaseCache implements com.cannontech.yukon.ITimedDatabaseCache
{
   private static TimedDatabaseCache cache = null;
   
	/**
	 * Constructor for DefaultDatabaseCache.
	 */
	public TimedDatabaseCache()
	{
		super();
	} 

   public synchronized static final TimedDatabaseCache getInstance()
   {
      if( cache == null )
      {
         cache = new TimedDatabaseCache();
      }
      
      return cache;
   }

   private com.cannontech.yukon.ITimedDatabaseCache getTimedDBCache()
   {
      return ResourceFactory.getIYukon().getTimedDBCache();
   }
   
      /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized java.util.List getAllPeakPointHistory()
   {
      return getTimedDBCache().getAllPeakPointHistory();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void loadAllTimedCache()
   {
      getTimedDBCache().loadAllTimedCache();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
/*
   public synchronized void releaseAllTimedCache()
   {
      getTimedDBCache().releaseAllTimedCache();
   }
*/
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public synchronized void releaseAllPeakPointHistory()
   {   
      getTimedDBCache().releaseAllPeakPointHistory();
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void setDatabaseAlias(String newAlias)
   {
      getTimedDBCache().setDatabaseAlias( newAlias );
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void setUpdateTimeInMillis(long millis)
   {
		getTimedDBCache().setUpdateTimeInMillis(millis);
   }
}
