package com.cannontech.yukon;

/**
 * Interface to Yukon data and services.
 * @author alauinger
 */
public interface IYukon extends IDatabaseCache, IDBPersistent, IPorterConnection, IDispatchConnection, IMACSConnection, ILMConnection  
{
   
   public com.cannontech.yukon.IDBPersistent createIDBPersistent();
   
}
