package com.cannontech.yukon;

/**
 * Interface to Yukon data and services.
 * @author alauinger
 */
public interface IYukon
{
   
   public IDBPersistent createIDBPersistent();

   public ISQLStatement createISQLStatement();
	
   public IDBPersistent getDBPersistent();

   public IDatabaseCache getDBCache();
   
   public ITimedDatabaseCache getTimedDBCache();

   public ISQLStatement getSQLStatement();
}
