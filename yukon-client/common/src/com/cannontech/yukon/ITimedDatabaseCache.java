package com.cannontech.yukon;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.data.lite.LiteBase;

import com.cannontech.database.cache.DBChangeListener;

/**
 * Interface to Yukon data.
 *
 * @author alauinger
 */
public interface ITimedDatabaseCache {

//   public void addDBChangeListener(DBChangeListener listener);
//   public DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType );

   public java.util.List getAllPeakPointHistory();
   
  
//   public IDatabaseCache getInstance();

   /**
    *  Returns the LiteBase object that was added,deleted or updated, 
    *    else null is returned.
    */
//   public LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg);

   public void loadAllTimedCache();
   public void releaseAllPeakPointHistory();
//   public void removeDBChangeListener(DBChangeListener listener);
   public void setDatabaseAlias(String newAlias);
   public void setUpdateTimeInMillis(long millis);
}
