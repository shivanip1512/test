package com.cannontech.yukon.server;

import java.util.List;
import java.util.Map;

import com.cannontech.ejb.SqlStatementBean;
import com.cannontech.yukon.IDBPersistent;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.ISQLStatement;
import com.cannontech.yukon.concrete.YukonResourceBase;

/**
 * @author rneuharth
 * Sep 12, 2002 at 11:06:50 AM
 * 
 * A undefined generated comment
 */
public class YukonServerResource extends YukonResourceBase
{

   // ---------------------------------------------------------------------------------
   //  START of the IDBPersistent implementation
   // ---------------------------------------------------------------------------------   

   public com.cannontech.yukon.IDBPersistent createIDBPersistent()
   {
      return new com.cannontech.ejb.DBPersistentBean();
   }
   
   public synchronized IDBPersistent getDBPersistent()
   {
      if( dbPersistent == null )
         dbPersistent = new com.cannontech.ejb.DBPersistentBean();
      
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
         dbCache = new com.cannontech.ejb.DatabaseCacheBean();
      }
      
      
      return dbCache;
   }


   // ---------------------------------------------------------------------------------
   //  START of the ISqlStatement implementation
   // ---------------------------------------------------------------------------------   
   public synchronized ISQLStatement getSQLStatement()
   {
      if( sqlStatement == null )
         sqlStatement = new com.cannontech.ejb.SqlStatementBean();
      
      return sqlStatement;
   }

   public ISQLStatement createISQLStatement()
   {
      return new SqlStatementBean();
   }
}
