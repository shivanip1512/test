package com.cannontech.yukon.server;

import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IDBPersistent;

// ---------------------------------------------------------------------------------
//  Imports for IDatabase implementation
// ---------------------------------------------------------------------------------   
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.database.data.lite.LiteBase;

// ---------------------------------------------------------------------------------
//  Imports for IDBPersistent implementation
// ---------------------------------------------------------------------------------   
import java.sql.SQLException;
import java.util.List;


/**
 * @author rneuharth
 * Sep 12, 2002 at 11:06:50 AM
 * 
 * A undefined generated comment
 */
public class YukonServerResource implements com.cannontech.yukon.IYukon
{
   private static IDatabaseCache dbCache = null;
   private static IDBPersistent dbPersistent = null;

   // ---------------------------------------------------------------------------------
   //  START of the IDBPersistent implementation
   // ---------------------------------------------------------------------------------   
   public void add( String tableName, Object[] values ) throws SQLException 
   {
      getDBPersistent().add( tableName, values );
   };
   
   public void delete( String tableName, String columnNames[], String columnValues[] ) throws SQLException 
   {
      getDBPersistent().delete( tableName, columnNames, columnValues );
   };
   
	public void setSQLFileName( String fileName )
	{
		getDBPersistent().setSQLFileName( fileName );
	};
	   
   public void delete( String tableName, String columnName, String columnValue ) throws SQLException
   {
      getDBPersistent().delete( tableName, columnName, columnValue );
   };
   
   public Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns, String[] constraintValues, boolean multipleReturn) throws SQLException 
   { 
      return getDBPersistent().retrieve( selectColumns, tableName, constraintColumns, constraintValues, multipleReturn);
   };
   
   public Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[], String keyColumnValues[]) throws SQLException 
   { 
      return getDBPersistent().retrieve( selectColumnNames, tableName, keyColumnNames, keyColumnValues );
   };
   
   public void update( String tableName, String setColumnName[], Object setColumnValue[],
                     String constraintColumnName[], Object constraintColumnValue[]) throws SQLException 
   {
      getDBPersistent().update( tableName, setColumnName, setColumnValue, constraintColumnName, constraintColumnValue );
   };

   public void setDbConnection( java.sql.Connection conn )
   {
      getDBPersistent().setDbConnection( conn );
   };

   public java.sql.Connection getDbConnection() throws SQLException
   {
      return getDBPersistent().getDbConnection();
   };

   public com.cannontech.database.db.DBPersistent
             execute( int operation, com.cannontech.database.db.DBPersistent obj ) throws com.cannontech.database.TransactionException
   {
      return getDBPersistent().execute( operation, obj );
   };

   public com.cannontech.yukon.IDBPersistent createIDBPersistent()
   {
      return new com.cannontech.ejb.DBPersistentBean();
   }
   
   private static synchronized IDBPersistent getDBPersistent()
   {
      if( dbPersistent == null )
         dbPersistent = new com.cannontech.ejb.DBPersistentBean();
      
      return dbPersistent;
   }



   // ---------------------------------------------------------------------------------
   //  START of the IDatabase implementation
   // ---------------------------------------------------------------------------------   
   private static synchronized IDatabaseCache getDBCache()
   {
      if( dbCache == null )
      {         
         //db = new com.cannontech.yukonserver.cache.DefaultDatabaseCache();
         dbCache = new com.cannontech.ejb.DatabaseCacheBean();
      }
      
      
      return dbCache;
   }

   public void addDBChangeListener(DBChangeListener listener)
   {
      getDBCache().addDBChangeListener( listener );
   }
   
   public DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
   {
      return getDBCache().createDBChangeMessages( newItem, changeType );
   }

   public java.util.List getAllAlarmCategories()
   {
      return getDBCache().getAllAlarmCategories();
   }

   public java.util.List getAllYukonImages()
   {
      return getDBCache().getAllYukonImages();
   }

   public java.util.List getAllCapControlFeeders()
   {
      return getDBCache().getAllCapControlFeeders();
   }

   public java.util.List getAllCapControlSubBuses()
   {
      return getDBCache().getAllCapControlSubBuses();
   }

   public java.util.List getAllCustomerContacts()
   {
      return getDBCache().getAllCustomerContacts();
   }

   public java.util.List getAllCustomers()
   {
      return getDBCache().getAllCustomers();
   }

   public java.util.List getAllDeviceMeterGroups()
   {
      return getDBCache().getAllDeviceMeterGroups();
   }

   public java.util.List getAllDevices()
   {
      return getDBCache().getAllDevices();
   }

   public java.util.List getAllGraphDefinitions()
   {
      return getDBCache().getAllGraphDefinitions();
   }

   public java.util.List getAllHolidaySchedules()
   {
      return getDBCache().getAllHolidaySchedules();
   }

   public java.util.List getAllLMPrograms()
   {
      return getDBCache().getAllLMPrograms();
   }

   public java.util.List getAllLoadManagement()
   {
      return getDBCache().getAllLoadManagement();
   }

   public java.util.List getAllNotificationGroups()
   {
      return getDBCache().getAllNotificationGroups();
   }

   public java.util.List getAllNotificationRecipients()
   {
      return getDBCache().getAllNotificationRecipients();
   }

   public java.util.List getAllPoints()
   {
      return getDBCache().getAllPoints();
   }

   public java.util.List getAllPointsUnits()
   {
      return getDBCache().getAllPointsUnits();
   }
   
   public List getAllPointLimits() {
	  return getDBCache().getAllPointLimits();
   }

   public java.util.HashMap getAllPointidMultiplierHashMap()
   {
      return getDBCache().getAllPointidMultiplierHashMap();
   }

   public java.util.List getAllPorts()
   {
      return getDBCache().getAllPorts();
   }

   public java.util.List getAllRoutes()
   {
      return getDBCache().getAllRoutes();
   }

   public java.util.List getAllStateGroups()
   {
      return getDBCache().getAllStateGroups();
   }

   public java.util.List getAllUnitMeasures()
   {
      return getDBCache().getAllUnitMeasures();
   }


   // This cache is derive from the Device cache
   public java.util.List getAllUnusedCCDevices()
   {
      return getDBCache().getAllUnusedCCDevices();
   }


   public java.util.List getAllYukonPAObjects()
   {
      return getDBCache().getAllYukonPAObjects();
   }

   public IDatabaseCache getInstance()
   {
      return getDBCache();
   }


   /**
    *  Returns the LiteBase object that was added,deleted or updated, 
    *    else null is returned.
    */
   public LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg)
   {
      return getDBCache().handleDBChangeMessage( dbChangeMsg );
   }


   public void loadAllCache()
   {
      getDBCache().loadAllCache();
   }

   public void releaseAllCache()
   {
      getDBCache().releaseAllCache();
   }

   public void releaseAllAlarmCategories()
   {
      getDBCache().releaseAllAlarmCategories();
   }

   public void releaseAllCustomerContacts()
   {
      getDBCache().releaseAllCustomerContacts();
   }

   public void releaseAllDeviceMeterGroups()
   {
      getDBCache().releaseAllDeviceMeterGroups();
   }

   public void releaseAllYukonImages()
   {
      getDBCache().releaseAllYukonImages();
   }

   public void releaseAllGraphDefinitions()
   {
      getDBCache().releaseAllGraphDefinitions();
   }

   public void releaseAllHolidaySchedules()
   {
      getDBCache().releaseAllHolidaySchedules();
   }

   public void releaseAllNotificationGroups()
   {
      getDBCache().releaseAllNotificationGroups();
   }

   public void releaseAllNotificationRecipients()
   {
      getDBCache().releaseAllNotificationRecipients();
   }

   public void releaseAllPoints()
   {
      getDBCache().releaseAllPoints();
   }

   public void releaseAllStateGroups()
   {
      getDBCache().releaseAllStateGroups();
   }

   public void releaseAllUnitMeasures()
   {
      getDBCache().releaseAllUnitMeasures();
   }

   public void releaseAllYukonPAObjects()
   {
      getDBCache().releaseAllYukonPAObjects();
   }

   public void removeDBChangeListener(DBChangeListener listener)
   {
      getDBCache().removeDBChangeListener( listener );
   }

   public void setDatabaseAlias(String newAlias)
   {
       getDBCache().setDatabaseAlias( newAlias );
   }


}
