package com.cannontech.yukon.concrete;

import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IDBPersistent;
import com.cannontech.yukon.ISQLStatement;
import com.cannontech.yukon.IYukon;

// ---------------------------------------------------------------------------------
//  Imports for IDatabase implementation
// ---------------------------------------------------------------------------------   
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;

// ---------------------------------------------------------------------------------
//  Imports for IDBPersistent implementation
// ---------------------------------------------------------------------------------   
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

// ---------------------------------------------------------------------------------
//  Imports for ISQLStatement implementation
// ---------------------------------------------------------------------------------   
import com.cannontech.common.util.CommandExecutionException;
import java.sql.Connection;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class YukonResourceBase implements IYukon 
{	
   protected static IDatabaseCache dbCache = null;
   protected static IDBPersistent dbPersistent = null;
   protected static ISQLStatement sqlStatement = null;


   public abstract IDatabaseCache getDBCache();
   public abstract ISQLStatement getSQLStatement();
   public abstract IDBPersistent getDBPersistent();
   
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



   // ---------------------------------------------------------------------------------
   //  START of the IDatabase implementation
   // ---------------------------------------------------------------------------------   
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

    public Map getAllYukonUserRoleMap() {
		return null;
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


   // ---------------------------------------------------------------------------------
   //  START of the ISQLStatement implementation
   // ---------------------------------------------------------------------------------   
   public void setSQLString( String sql )
   {
   	getSQLStatement().setSQLString( sql );
   }
   
   public void setDBConnection( Connection conn )
   {
   	getSQLStatement().setDBConnection( conn );
   }
   
	public void execute() throws CommandExecutionException
	{
		getSQLStatement().execute();
	}
	
	public Object[] getRow( int row )
	{
		return getSQLStatement().getRow(row);
	}
	
	public int getRowCount()
	{
		return getSQLStatement().getRowCount();
	}
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupRoleMap()
	 */
	public Map getAllYukonGroupRoleMap() {
		return getDBCache().getAllYukonGroupRoleMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroups()
	 */
	public List getAllYukonGroups() {
		return getDBCache().getAllYukonGroups();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroupUserMap()
	 */
	public Map getAllYukonGroupUserMap() {
		return getDBCache().getAllYukonGroupUserMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonRoles()
	 */
	public List getAllYukonRoles() {
		return getDBCache().getAllYukonRoles();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserGroupMap()
	 */
	public Map getAllYukonUserGroupMap() {
		return getDBCache().getAllYukonUserGroupMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUserRoleLookupMap()
	 */
	public Map getAllYukonUserRoleLookupMap() {
		return getDBCache().getAllYukonUserRoleLookupMap();
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUsers()
	 */
	public List getAllYukonUsers() {
		return getDBCache().getAllYukonUsers();
	}

}
