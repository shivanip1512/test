package com.cannontech.yukon.server;

import com.cannontech.yukon.IDBPersistent;
import com.cannontech.yukon.ITimedDatabaseCache;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IMACSConnection;
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
    
    public YukonServerResource()
    {
        super();
        
        /* *
         * 
         * Initialize resources that are independent of other resources and
         * do not use other resources during construction
         * 
         * */
        dbCache = new com.cannontech.ejb.DatabaseCacheBean();
        dbPersistent = new com.cannontech.ejb.DBPersistentBean();
        timedDBCache = new com.cannontech.ejb.TimedDatabaseCacheBean();
        sqlStatement = new com.cannontech.ejb.SqlStatementBean();
    }


   // ---------------------------------------------------------------------------------
   //  START of the IDBPersistent implementation
   // ---------------------------------------------------------------------------------   
   public com.cannontech.yukon.IDBPersistent createIDBPersistent()
   {
      return new com.cannontech.ejb.DBPersistentBean();
   }
   
   public IDBPersistent getDBPersistent()
   {
      return dbPersistent;
   }



   // ---------------------------------------------------------------------------------
   //  START of the IDatabase implementation
   // ---------------------------------------------------------------------------------   
   public IDatabaseCache getDBCache()
   {
      return dbCache;
   }


   // ---------------------------------------------------------------------------------
   //  START of the ITimedDatabase implementation
   // ---------------------------------------------------------------------------------   
   public ITimedDatabaseCache getTimedDBCache()
   {
      return timedDBCache;
   }


   // ---------------------------------------------------------------------------------
   //  START of the ISqlStatement implementation
   // ---------------------------------------------------------------------------------   
   public ISQLStatement getSQLStatement()
   {
      return sqlStatement;
   }

   public ISQLStatement createISQLStatement()
   {
      return new com.cannontech.ejb.SqlStatementBean();
   }


   // ---------------------------------------------------------------------------------
   //  START of the IMACSConnection implementation
   // ---------------------------------------------------------------------------------   
   public IMACSConnection getMACSConnection()
   {
       if( macsConnection == null )
           macsConnection = new com.cannontech.yukon.connections.ServerMACSConnection();

      return macsConnection;
   }


}
