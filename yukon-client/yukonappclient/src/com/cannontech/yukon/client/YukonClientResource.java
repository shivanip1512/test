package com.cannontech.yukon.client;

import com.cannontech.ejb.MACSConnectionBean;
import com.cannontech.yukon.IConnectionBase;
import com.cannontech.yukon.IDBPersistent;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.ITimedDatabaseCache;
import com.cannontech.yukon.ISQLStatement;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.concrete.YukonResourceBase;


/**
 * @author rneuharth
 * Sep 12, 2002 at 11:06:50 AM
 * 
 * A undefined generated comment
 */
public class YukonClientResource extends YukonResourceBase
{
   // ---------------------------------------------------------------------------------
   //  START of the IDBPersistent implementation
   // ---------------------------------------------------------------------------------   

   public com.cannontech.yukon.IDBPersistent createIDBPersistent()
   {
      return new ClientDBPersistent();
   }
   
   public synchronized IDBPersistent getDBPersistent()
   {
      if( dbPersistent == null )
         dbPersistent = new ClientDBPersistent();
      
      return dbPersistent;
   }



   // ---------------------------------------------------------------------------------
   //  START of the IDatabase implementation
   // ---------------------------------------------------------------------------------   
   public synchronized IDatabaseCache getDBCache()
   {
      if( dbCache == null )
      {         
         //db = new com.cannontech.yukonserver.cache.DefaultDatabaseCache();
         dbCache = new ClientCache();
      }
      
      
      return dbCache;
   }

   // ---------------------------------------------------------------------------------
   //  START of the ITimedDatabase implementation
   // ---------------------------------------------------------------------------------   
   public synchronized com.cannontech.yukon.ITimedDatabaseCache getTimedDBCache()
   {
      if( timedDBCache == null )
      {         
         //db = new com.cannontech.yukonserver.cache.DefaultDatabaseCache();
         timedDBCache = new TimedClientCache();
      }
      
      
      return timedDBCache;
   }
   
   // ---------------------------------------------------------------------------------
   //  START of the ISqlStatement implementation
   // ---------------------------------------------------------------------------------   
   public synchronized ISQLStatement getSQLStatement()
   {
      if( sqlStatement == null )
         sqlStatement = new ClientSqlStatement();
      
      return sqlStatement;
   }

   public ISQLStatement createISQLStatement()
   {
      return new ClientSqlStatement();
   }
   
   // ---------------------------------------------------------------------------------
   //  START of the IMACSConnection implementation
   // ---------------------------------------------------------------------------------   
   public synchronized IMACSConnection getMACSConnection()
   {
      if( macsConnection == null )
         macsConnection = null;//new MACSConnectionBean();
      
      return macsConnection;
   }

}
