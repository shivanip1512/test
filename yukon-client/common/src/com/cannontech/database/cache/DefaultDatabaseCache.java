package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/14/00 3:20:44 PM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.data.lite.LiteBase;

public class DefaultDatabaseCache
{
	//stores a soft reference to the cache
	//private static java.lang.ref.SoftReference cacheReference = null;	
	private static DefaultDatabaseCache cache = null;

	private CacheDBChangeListener dbChangeListener = null;
	private String databaseAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	private java.util.ArrayList allYukonPAObjects = null;
	private java.util.ArrayList allPoints = null;
	private java.util.ArrayList allStateGroups = null;
	private java.util.ArrayList allUnitMeasures = null;
	private java.util.ArrayList allNotificationGroups = null;
	private java.util.ArrayList allUsedNotificationRecipients = null;
	private java.util.ArrayList allNotificationRecipients = null;
	private java.util.ArrayList allAlarmCategories = null;
	private java.util.ArrayList allCustomerContacts = null;
	private java.util.ArrayList allGraphDefinitions = null;
	private java.util.ArrayList allHolidaySchedules = null;
	private java.util.ArrayList allDeviceMeterGroups = null;

	//lists that are created by the joining/parsing of existing lists
	private java.util.ArrayList allGraphTaggedPoints = null;
	private java.util.ArrayList allUnusedCCDevices = null;
	private java.util.ArrayList allCapControlFeeders = null;
	private java.util.ArrayList allCapControlSubBuses = null;	
	private java.util.ArrayList allCustomers = null;
	private java.util.ArrayList allDevices = null;
	private java.util.ArrayList allLMPrograms = null;
	private java.util.ArrayList allLoadManagement = null;
	private java.util.ArrayList allPorts = null;
	private java.util.ArrayList allRoutes = null;
/**
 * DefaultDatabaseCache constructor comment.
 */
protected DefaultDatabaseCache() {
	super();
}
/**
 * DefaultDatabaseCache constructor comment.
 */
protected DefaultDatabaseCache(String databaseAlias) {
	super();
	setDatabaseAlias(databaseAlias);
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:09:04 PM)
 * @param DBChangeListener
 */
public void addDBChangeListener(DBChangeListener listener) 
{
	getDbChangeListener().addDBChangeListener( listener );
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 12:27:17 PM)
 * @param newItem com.cannontech.database.db.DBPersistent
 */
public static DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
{
	return newItem.getDBChangeMsgs( changeType );
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllAlarmCategories(){

	if( allAlarmCategories != null )
		return allAlarmCategories;
	else
	{
		allAlarmCategories = new java.util.ArrayList();
		AlarmCategoryLoader alarmStateLoader = new AlarmCategoryLoader(allAlarmCategories, databaseAlias);
		alarmStateLoader.run();
		return allAlarmCategories;
	}

}
/**
 *
 */
public synchronized java.util.List getAllCapControlFeeders() 
{
	if( allCapControlFeeders == null )
	{
		allCapControlFeeders = new java.util.ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL
				 && ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getType() == com.cannontech.database.data.pao.PAOGroups.CAP_CONTROL_FEEDER )
				allCapControlFeeders.add( getAllYukonPAObjects().get(i) );
		}

		allCapControlFeeders.trimToSize();
	}

	return allCapControlFeeders;
}
/**
 *
 */
public synchronized java.util.List getAllCapControlSubBuses() 
{
	if( allCapControlSubBuses == null )
	{
		allCapControlSubBuses = new java.util.ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL
				 && ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getType() == com.cannontech.database.data.pao.PAOGroups.CAP_CONTROL_SUBBUS )
				allCapControlSubBuses.add( getAllYukonPAObjects().get(i) );
		}

		allCapControlSubBuses.trimToSize();
	}	

	return allCapControlSubBuses;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllCustomerContacts()
{
	if( allCustomerContacts != null )
		return allCustomerContacts;
	else
	{
		allCustomerContacts = new java.util.ArrayList();
		CustomerContactLoader customerContactLoader = new CustomerContactLoader(allCustomerContacts, databaseAlias);
		customerContactLoader.run();
		return allCustomerContacts;
	}
}
/**
 *
 */
public synchronized java.util.List getAllCustomers() 
{
	if( allCustomers == null )
	{
		allCustomers = new java.util.ArrayList( getAllYukonPAObjects().size() );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_CUSTOMER )
				allCustomers.add( getAllYukonPAObjects().get(i) );
		}

		allCustomers.trimToSize();
	}

	return allCustomers;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllDeviceMeterGroups()
{
	if( allDeviceMeterGroups != null )
		return allDeviceMeterGroups;
	else
	{
		allDeviceMeterGroups = new java.util.ArrayList();
		DeviceMeterGroupLoader deviceMeterGroupLoader = new DeviceMeterGroupLoader (allDeviceMeterGroups, databaseAlias);
		deviceMeterGroupLoader.run();
		return allDeviceMeterGroups;
	}

}
/**
 * getAllDevices method comment.
 *
 */
public synchronized java.util.List getAllDevices() 
{
	if( allDevices == null )
	{
		allDevices = new java.util.ArrayList( getAllYukonPAObjects().size() );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() 
				  == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE )
				allDevices.add( getAllYukonPAObjects().get(i) );
		}

		allDevices.trimToSize();		
	}

	return allDevices;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllGraphDefinitions()
{
	if( allGraphDefinitions != null )
		return allGraphDefinitions;
	else
	{
		allGraphDefinitions = new java.util.ArrayList();
		GraphDefinitionLoader graphDefinitionLoader = new GraphDefinitionLoader(allGraphDefinitions, databaseAlias);
		graphDefinitionLoader.run();
		return allGraphDefinitions;
	}
}
// This cache is derive from the point cache
public synchronized java.util.List getAllGraphTaggedPoints()
{
	 if( allGraphTaggedPoints != null )
		  return allGraphTaggedPoints;
	 else
	 {
		  //temp code
		  java.util.Date timerStart = null;
		  java.util.Date timerStop = null;
		  //temp code

		  //temp code
		  timerStart = new java.util.Date();
		  //temp code

		  allGraphTaggedPoints = new java.util.ArrayList();


		  String sqlString = "SELECT PU.POINTID, UM.FORMULA " +
			"FROM POINTUNIT PU , UNITMEASURE UM WHERE PU.UOMID = UM.UOMID";

		  java.sql.Connection conn = null;
		  java.sql.Statement stmt = null;
		  java.sql.ResultSet rset = null;
		  try
		  {
			   conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
			   stmt = conn.createStatement();
			   rset = stmt.executeQuery(sqlString);

			   while (rset.next())
			   {
					int pointID = rset.getInt(1);
					String formula = rset.getString(2);

					for( int i = 0; i < getAllPoints().size(); i++ )
					{
						 com.cannontech.database.data.lite.LitePoint point =((com.cannontech.database.data.lite.LitePoint)getAllPoints().get(i));
						 if( point.getPointID() == pointID )
						 {
							  // tags may need to be changed here if there
										//  are more tags added to this bit field
							  long tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH;      //default value of tags for now.

							  if( formula.equalsIgnoreCase("usage"))
								   tags = com.cannontech.database.data.lite.LitePoint.POINT_UOFM_USAGE;

							  point.setTags( tags );
							  allGraphTaggedPoints.add(  point );
						 }
					}
			   }
			   // Grab all status points too...!
			   for (int i = 0; i < getAllPoints().size(); i++)
			   {
					com.cannontech.database.data.lite.LitePoint point = ((com.cannontech.database.data.lite.LitePoint) getAllPoints().get(i));
					if ( point.getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
					{
						 point.setTags( com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH );
						 allGraphTaggedPoints.add (point);
					}
			   }

		  }
		  catch( java.sql.SQLException e )
		  {
			   e.printStackTrace();
		  }
		  finally
		  {
			   try
			   {
					if( stmt != null )
						 stmt.close();
					if( conn != null )
						 conn.close();
			   }
			   catch( java.sql.SQLException e )
			   {
					e.printStackTrace();
			   }

			   //temp code
			   timerStop = new java.util.Date();
			   System.out.print( (timerStop.getTime() - timerStart.getTime())*.001 );
			   com.cannontech.clientutils.CTILogger.info( " Secs for getAllGraphTaggedPoints()" );
			   //temp code


			   //sort our points by pointoffset
			   java.util.Collections.sort( allGraphTaggedPoints, com.cannontech.database.data.lite.LiteComparators.litePointPointOffsetComparator );

			   return allGraphTaggedPoints;
		  }

	 }

}


/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllHolidaySchedules()
{

	if (allHolidaySchedules != null)
		return allHolidaySchedules;
	else
	{
		allHolidaySchedules = new java.util.ArrayList();
		HolidayScheduleLoader holidayScheduleLoader = new HolidayScheduleLoader(allHolidaySchedules, databaseAlias);
		holidayScheduleLoader.run();
		return allHolidaySchedules;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllLMPrograms()
{
	if( allLMPrograms == null )
	{
		//java.util.List allDevices = getAllLoadManagement();
		allLMPrograms = new java.util.ArrayList( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == com.cannontech.database.data.pao.PAOGroups.LM_CURTAIL_PROGRAM
				 || ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == com.cannontech.database.data.pao.PAOGroups.LM_DIRECT_PROGRAM
				 || ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == com.cannontech.database.data.pao.PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM )
				allLMPrograms.add( getAllLoadManagement().get(i) );				
		}

		allLMPrograms.trimToSize();
	}

	
	
	return allLMPrograms;
}
/**
 * getAllLoadManagement method comment.
 *
 */
public synchronized java.util.List getAllLoadManagement() 
{
	if( allLoadManagement == null )
	{
		allLoadManagement = new java.util.ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.LOADMANAGEMENT ||
					((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.GROUP )
				allLoadManagement.add( getAllYukonPAObjects().get(i) );
		}

		allLoadManagement.trimToSize();		
	}
	

	return allLoadManagement;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllNotificationGroups()
{
	if( allNotificationGroups != null )
		return allNotificationGroups;
	else
	{
		allNotificationGroups = new java.util.ArrayList();
		NotificationGroupLoader notifLoader = new NotificationGroupLoader(allNotificationGroups, databaseAlias);
		notifLoader.run();
		allUsedNotificationRecipients = notifLoader.getAllUsedNotificationRecipients();
		return allNotificationGroups;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllNotificationRecipients()
{
	if( allNotificationRecipients != null )
		return allNotificationRecipients;
	else
	{
		allNotificationRecipients = new java.util.ArrayList();
		NotificationRecipientLoader notifLoader = new NotificationRecipientLoader(allNotificationRecipients, databaseAlias);
		notifLoader.run();
		return allNotificationRecipients;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.List getAllPoints(){

	if( allPoints != null )
		return allPoints;
	else
	{
		allPoints = new java.util.ArrayList();
		PointLoader pointLoader = new PointLoader(allPoints, databaseAlias);
		pointLoader.run();
		return allPoints;
	}
}
/**
 * getAllPorts method comment.
 *
 */
public synchronized java.util.List getAllPorts() 
{
	if( allPorts == null )
	{
		allPorts = new java.util.ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() 
				 == com.cannontech.database.data.pao.PAOGroups.CAT_PORT )
				allPorts.add( getAllYukonPAObjects().get(i) );
		}

		allPorts.trimToSize();		
	}
	

	return allPorts;
}
/**
 * getAllRoutes method comment.
 *
 */
public synchronized java.util.List getAllRoutes() 
{
	if( allRoutes == null )
	{
		allRoutes = new java.util.ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_ROUTE )
				allRoutes.add( getAllYukonPAObjects().get(i) );
		}

		allRoutes.trimToSize();
	}
	
	return allRoutes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.List getAllStateGroups(){

	if( allStateGroups != null )
		return allStateGroups;
	else
	{
		allStateGroups = new java.util.ArrayList();
		StateGroupLoader stateGroupLoader = new StateGroupLoader(allStateGroups, databaseAlias);
		stateGroupLoader.run();
		return allStateGroups;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.List getAllUnitMeasures(){

	if( allUnitMeasures != null )
		return allUnitMeasures;
	else
	{
		allUnitMeasures = new java.util.ArrayList();
		UnitMeasureLoader unitMeasureLoader = new UnitMeasureLoader(allUnitMeasures, databaseAlias);
		unitMeasureLoader.run();
		return allUnitMeasures;
	}
}
// This cache is derive from the Device cache
public synchronized java.util.List getAllUnusedCCDevices()
{

	 if( allUnusedCCDevices != null )
		  return allUnusedCCDevices;
	 else
	 {
	  //temp code
	  java.util.Date timerStart = new java.util.Date();
	  //temp code

		String sqlString =
			"select PAObjectID from " + com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME +
			" where paobjectid > 0 and paobjectid not in ( select controldeviceid from " + com.cannontech.database.db.capcontrol.CapBank.TABLE_NAME + ")";

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			java.util.List allDevices = getAllDevices();
			java.util.Collections.sort(allDevices, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator);
			allUnusedCCDevices = new java.util.ArrayList( (int)(allDevices.size() * 0.7)  );
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while (rset.next())
			{
				int paoID = rset.getInt(1);

				for( int i = 0; i < allDevices.size(); i++ )
				{
					 com.cannontech.database.data.lite.LiteYukonPAObject paObject = (com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i);
					 if( paObject.getYukonID() == paoID )
					 {
						  allUnusedCCDevices.add( paObject );
						  break;
					 }
				}
			}
				
			if( rset != null )
				rset.close();

	   //temp code
	   java.util.Date timerStop = new java.util.Date();
	   System.out.print( (timerStop.getTime() - timerStart.getTime())*.001 );
	   com.cannontech.clientutils.CTILogger.info( " Secs for getAllUnusedCCPaos()" );
	   //temp code

		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}

		return allUnusedCCDevices;
	 }

}


/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllYukonPAObjects()
{
	if( allYukonPAObjects != null )
		return allYukonPAObjects;
	else
	{
		allYukonPAObjects = new java.util.ArrayList();
		YukonPAOLoader yukLoader = new YukonPAOLoader(allYukonPAObjects, databaseAlias);
		yukLoader.run();
		
		return allYukonPAObjects;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 2:01:01 PM)
 * @return com.cannontech.database.cache.CacheDBChangeListener
 */
private CacheDBChangeListener getDbChangeListener() {
	return dbChangeListener;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public static synchronized DefaultDatabaseCache getInstance()
{
	if( cache == null )
	{
		com.cannontech.clientutils.CTILogger.info("CACHE: CREATING NEW CACHE REFERENCE OBJECT");
		cache = new DefaultDatabaseCache();
		cache.setDbChangeListener( new CacheDBChangeListener() );
	}

	return cache;
/*	DefaultDatabaseCache c = null;

	if( cacheReference != null )
	{
		c = (DefaultDatabaseCache)cacheReference.get();
	}

	if( c == null ) //No cache, or we got GC'd
	{
		com.cannontech.clientutils.CTILogger.info("CACHE: CREATING NEW CACHE REFERENCE OBJECT");
		c = new DefaultDatabaseCache();
		//create the DBChange listener
		c.setDbChangeListener( new CacheDBChangeListener() );

		cacheReference = new java.lang.ref.SoftReference(c);
	}

	return c;
*/
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleAlarmCategoryChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allAlarmCategories == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allAlarmCategories.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmCategories.get(i)).getAlarmStateID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allAlarmCategories.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteAlarmCategory la = new com.cannontech.database.data.lite.LiteAlarmCategory(id);
					la.retrieve(databaseAlias);
					allAlarmCategories.add(la);
					lBase = la;
				}				
				break;

		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allAlarmCategories.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmCategories.get(i)).getAlarmStateID() == id )
					{
						((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmCategories.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allAlarmCategories.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allAlarmCategories.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmCategories.get(i)).getAlarmStateID() == id )
					{
						lBase = (LiteBase)allAlarmCategories.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllAlarmCategories();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleCustomerContactChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;
	
	// if the storage is not already loaded, we must not care about it
	if( allCustomerContacts == null )
		return lBase;;

	switch(changeType)
	{
/*		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
		
				if( id == DBChangeMsg.CHANGE_INVALID_ID )
					break;
		
				for(int i=0;i<allCustomerContacts.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteCustomerContact)allCustomerContacts.get(i)).getLiteID() == id )
					{
						alreadyAdded = true;
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteCustomerContact lc = new com.cannontech.database.data.lite.LiteCustomerContact(id);
					lc.retrieve(databaseAlias);
					allCustomerContacts.add(lc);
					lBase = lc;
				}
				break;

		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:

				//if( id == DBChangeMsg.CHANGE_INVALID_ID )
					//break;
		
				for(int i=0;i<allCustomerContacts.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteCustomerContact)allCustomerContacts.get(i)).getLiteID() == id )
					{
						((com.cannontech.database.data.lite.LiteCustomerContact)allCustomerContacts.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allCustomerContacts.get(i);
						break;
					}
				}

				if( lBase == null ) //we did not find the contact, just create a new one
				{
					com.cannontech.database.data.lite.LiteCustomerContact lc = new com.cannontech.database.data.lite.LiteCustomerContact(id);
					lc.retrieve(databaseAlias);
					allCustomerContacts.add(lc);
					lBase = lc;
				}
				
				break;

		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				//special case for this handler!!!!
				if( id == DBChangeMsg.CHANGE_INVALID_ID )
				{
					releaseAllCustomerContacts();
					break;
				}		
				
				for(int i=0;i<allCustomerContacts.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteCustomerContact)allCustomerContacts.get(i)).getLiteID() == id )
					{
						lBase = (LiteBase)allCustomerContacts.remove(i);
						break;
					}
				}
				break;
*/
		default:
				releaseAllCustomerContacts();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 *  Returns the LiteBase object that was added,deleted or updated, 
 *		else null is returned.
 */
public synchronized LiteBase handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg)
{
	String objectType = dbChangeMsg.getObjectType();
	String dbCategory = dbChangeMsg.getCategory();
	int dbType = dbChangeMsg.getTypeOfChange();
	int database = dbChangeMsg.getDatabase();
	int id = dbChangeMsg.getId();
	boolean alreadyAdded = false;
	LiteBase retLBase = null;

	if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_POINT_DB )
	{
		allGraphTaggedPoints = null;
		retLBase = handlePointChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_PAO_DB )
	{
		retLBase = handleYukonPAOChange( dbType, id);

		//if any device changes, 
		// reload all the DeviceMeterGroup data (may be inefficient!!)
		if( dbCategory.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE) )
		{
			allDevices = null;
			allUnusedCCDevices = null;
			
			//we should not have to return the DeviceMeterGroup since
			// the liteObject that was edited/added was actually a Device
			//retLBase = handleDeviceMeterGroupChange( dbType, id);

			handleDeviceMeterGroupChange( dbType, id);
		}
		else if( dbCategory.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAT_LOADMANAGEMENT) )
		{
			allLoadManagement = null;
			allLMPrograms = null;
		}
		else if( dbCategory.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAT_CUSTOMER) )
		{
			allCustomers = null;
		}	
		else if( dbCategory.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAT_CAPCONTROL) )
		{
			allCapControlFeeders = null;
			allCapControlSubBuses = null;	
		}
		else if( dbCategory.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAT_PORT) )
		{
			allPorts = null;
		}
		else if( dbCategory.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAT_ROUTE) )
		{
			allRoutes = null;	
		}

	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_STATE_GROUP_DB )
	{
		retLBase = handleStateGroupChange( dbType, id );		
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_ALARM_CATEGORY_DB )
	{
		retLBase = handleAlarmCategoryChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_NOTIFICATION_GROUP_DB )
	{
		retLBase = handleNotificationGroupChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_NOTIFICATION_RECIPIENT_DB )
	{
		retLBase = handleNotificationRecipientChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB )
	{
		retLBase = handleCustomerContactChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_GRAPH_DB )
	{
		retLBase = handleGraphDefinitionChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_HOLIDAY_SCHEDULE_DB )
	{
		retLBase = handleHolidayScheduleChange( dbType, id );
	}
	else  //let it all go!!
		releaseAllCache();

	return retLBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleDeviceMeterGroupChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allDeviceMeterGroups == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteDeviceMeterNumber )allDeviceMeterGroups.get(i)).getDeviceID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allDeviceMeterGroups.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteDeviceMeterNumber liteDMG = new com.cannontech.database.data.lite.LiteDeviceMeterNumber(id);
					liteDMG.retrieve(databaseAlias);
					allDeviceMeterGroups.add(liteDMG);
					lBase = liteDMG;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteDeviceMeterNumber )allDeviceMeterGroups.get(i)).getDeviceID() == id )
					{
						((com.cannontech.database.data.lite.LiteDeviceMeterNumber )allDeviceMeterGroups.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allDeviceMeterGroups.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteDeviceMeterNumber)allDeviceMeterGroups.get(i)).getDeviceID() == id )
					{
						lBase = (LiteBase)allDeviceMeterGroups.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllDeviceMeterGroups();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleGraphDefinitionChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allGraphDefinitions == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allGraphDefinitions.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteGraphDefinition)allGraphDefinitions.get(i)).getGraphDefinitionID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allGraphDefinitions.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteGraphDefinition lsg = new com.cannontech.database.data.lite.LiteGraphDefinition(id);
					lsg.retrieve(databaseAlias);
					allGraphDefinitions.add(lsg);
					lBase = lsg;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allGraphDefinitions.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteGraphDefinition)allGraphDefinitions.get(i)).getGraphDefinitionID() == id )
					{
						((com.cannontech.database.data.lite.LiteGraphDefinition)allGraphDefinitions.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allGraphDefinitions.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allGraphDefinitions.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteGraphDefinition)allGraphDefinitions.get(i)).getGraphDefinitionID() == id )
					{
						lBase = (LiteBase)allGraphDefinitions.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllGraphDefinitions();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleHolidayScheduleChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allHolidaySchedules == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allHolidaySchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)allHolidaySchedules.get(i)).getHolidayScheduleID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allHolidaySchedules.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteHolidaySchedule lh = new com.cannontech.database.data.lite.LiteHolidaySchedule(id);
					lh.retrieve(databaseAlias);
					allHolidaySchedules.add(lh);
					lBase = lh;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allHolidaySchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)allHolidaySchedules.get(i)).getHolidayScheduleID() == id )
					{
						((com.cannontech.database.data.lite.LiteHolidaySchedule)allHolidaySchedules.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allHolidaySchedules.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allHolidaySchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)allHolidaySchedules.get(i)).getHolidayScheduleID() == id )
					{
						lBase = (LiteBase)allHolidaySchedules.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllHolidaySchedules();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleNotificationGroupChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allNotificationGroups == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allNotificationGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteNotificationGroup)allNotificationGroups.get(i)).getNotificationGroupID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allNotificationGroups.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteNotificationGroup lg = new com.cannontech.database.data.lite.LiteNotificationGroup(id);
					lg.retrieve(databaseAlias);
					allNotificationGroups.add(lg);
					lBase = lg;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allNotificationGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteNotificationGroup)allNotificationGroups.get(i)).getNotificationGroupID() == id )
					{
						((com.cannontech.database.data.lite.LiteNotificationGroup)allNotificationGroups.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allNotificationGroups.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allNotificationGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteNotificationGroup)allNotificationGroups.get(i)).getNotificationGroupID() == id )
					{
						lBase = (LiteBase)allNotificationGroups.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllNotificationGroups();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleNotificationRecipientChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allNotificationRecipients == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allNotificationRecipients.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteNotificationRecipient)allNotificationRecipients.get(i)).getRecipientID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allNotificationRecipients.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteNotificationRecipient lg = new com.cannontech.database.data.lite.LiteNotificationRecipient(id);
					lg.retrieve(databaseAlias);
					allNotificationRecipients.add(lg);
					lBase = lg;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allNotificationRecipients.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteNotificationRecipient)allNotificationRecipients.get(i)).getRecipientID() == id )
					{
						((com.cannontech.database.data.lite.LiteNotificationRecipient)allNotificationRecipients.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allNotificationRecipients.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allNotificationRecipients.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteNotificationRecipient)allNotificationRecipients.get(i)).getRecipientID() == id )
					{
						lBase = (LiteBase)allNotificationRecipients.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllNotificationRecipients();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handlePointChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allPoints == null )
		return lBase;
	
	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allPoints.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).getPointID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allPoints.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LitePoint lp = new com.cannontech.database.data.lite.LitePoint(id);
					lp.retrieve(databaseAlias);
					allPoints.add(lp);
					lBase = lp;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allPoints.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).getPointID() == id )
					{
						((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allPoints.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allPoints.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).getPointID() == id )
					{
						lBase = (LiteBase)allPoints.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllPoints();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleStateGroupChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allStateGroups == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allStateGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allStateGroups.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteStateGroup lsg = new com.cannontech.database.data.lite.LiteStateGroup(id);
					lsg.retrieve(databaseAlias);
					allStateGroups.add(lsg);
					lBase = lsg;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allStateGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == id )
					{
						((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allStateGroups.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allStateGroups.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == id )
					{
						lBase = (LiteBase)allStateGroups.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllStateGroups();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleYukonPAOChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allYukonPAObjects == null )
		return lBase;
		
	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allYukonPAObjects.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allYukonPAObjects.get(i)).getYukonID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allYukonPAObjects.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteYukonPAObject ld = new com.cannontech.database.data.lite.LiteYukonPAObject(id);
					ld.retrieve(databaseAlias);
					allYukonPAObjects.add(ld);
					lBase = ld;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allYukonPAObjects.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allYukonPAObjects.get(i)).getYukonID() == id )
					{
						((com.cannontech.database.data.lite.LiteYukonPAObject)allYukonPAObjects.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allYukonPAObjects.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allYukonPAObjects.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allYukonPAObjects.get(i)).getYukonID() == id )
					{
						lBase = (LiteBase)allYukonPAObjects.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllYukonPAObjects();
				break;
	}

	return lBase;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void loadAllCache()
{
	allPoints = new java.util.ArrayList();
	allStateGroups = new java.util.ArrayList();
	allUnitMeasures = new java.util.ArrayList();
	allNotificationGroups = new java.util.ArrayList();
	allUsedNotificationRecipients = new java.util.ArrayList();
	allNotificationRecipients = new java.util.ArrayList();
	allAlarmCategories = new java.util.ArrayList();
	allCustomerContacts = new java.util.ArrayList();
	allGraphDefinitions = new java.util.ArrayList();
	allHolidaySchedules = new java.util.ArrayList();
	allYukonPAObjects = new java.util.ArrayList();
	allDeviceMeterGroups = new java.util.ArrayList();

	
	//be sure all of our derived storage is cleard
	allGraphTaggedPoints = null;
	allUnusedCCDevices = null;
	allCapControlFeeders = null;
	allCapControlSubBuses = null;	
	allCustomers = null;
	allDevices = null;
	allLMPrograms = null;
	allLoadManagement = null;
	allPorts = null;
	allRoutes = null;	

	
	Runnable[] runners =
	{
		new YukonPAOLoader(allYukonPAObjects, databaseAlias),
		new PointLoader(allPoints, databaseAlias),
		new StateGroupLoader(allStateGroups, databaseAlias),
		new UnitMeasureLoader(allUnitMeasures, databaseAlias),
		new GraphDefinitionLoader(allGraphDefinitions, databaseAlias),
		new NotificationGroupLoader(allNotificationGroups, databaseAlias),
		new NotificationRecipientLoader(allNotificationRecipients, databaseAlias),
		new AlarmCategoryLoader(allAlarmCategories, databaseAlias),
		new CustomerContactLoader(allCustomerContacts, databaseAlias),
		new HolidayScheduleLoader(allHolidaySchedules, databaseAlias),
		new DeviceMeterGroupLoader(allDeviceMeterGroups, databaseAlias)
	};


	//Just use 1 Thread to load the DB for now
	for( int i = 0 ; i < runners.length; i++ )
		runners[i].run();
	
/*
	try
	{
java.util.Date timerStart = new java.util.Date();
		runLoaders( runners );
com.cannontech.clientutils.CTILogger.info( 
	((new java.util.Date().getTime() - timerStart.getTime())*.001) + " Secs for LOADER" );

	}
	catch( Exception e )
	{

		//oops something went wrong, just load all cache in 1 thread
		for( int i = 0 ; i < runners.length; i++ )
			runners[i].run();
	}
*/

}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllAlarmCategories()
{
	allAlarmCategories = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllCache()
{
	allPoints = null;
	allStateGroups = null;
	allNotificationGroups = null;
	allUsedNotificationRecipients = null;
	allNotificationRecipients = null;
	allAlarmCategories = null;
	allCustomerContacts = null;
	allGraphDefinitions = null;
	allYukonPAObjects = null;
	allDeviceMeterGroups = null;

	//be sure all of our derived storage is cleard
	allGraphTaggedPoints = null;
	allUnusedCCDevices = null;
	allCapControlFeeders = null;
	allCapControlSubBuses = null;	
	allCustomers = null;
	allDevices = null;
	allLMPrograms = null;
	allLoadManagement = null;
	allPorts = null;
	allRoutes = null;	
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllCustomerContacts()
{
	allCustomerContacts = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllDeviceMeterGroups()
{
	allDeviceMeterGroups = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllGraphDefinitions()
{
	allGraphDefinitions = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllHolidaySchedules()
{
	allHolidaySchedules = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllNotificationGroups()
{
	allNotificationGroups = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllNotificationRecipients(){

	allNotificationRecipients = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllPoints(){

	allPoints = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllStateGroups(){

	allStateGroups = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllUnitMeasures(){

	allUnitMeasures = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllYukonPAObjects()
{
	allYukonPAObjects = null;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:09:04 PM)
 * @param DBChangeListener
 */
public void removeDBChangeListener(DBChangeListener listener) 
{
	if( getDbChangeListener() != null )
		getDbChangeListener().removeDBChangeListener( listener );

}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 5:36:31 PM)
 * @param loaders java.lang.Runnable[]
 */
private void runLoaders(Runnable[] loaders) throws Exception
{

	// The 2 in the ThreadPool constructor is the max number of db connections
	com.cannontech.common.util.ThreadPool tp = new com.cannontech.common.util.ThreadPool(2);
	for( int i = 0; i < loaders.length; i++ )
		tp.enqueueRunnable( loaders[i] );

	tp.stop();
	tp.join();
	
/*	Thread t1 = new Thread();
	Thread t2 = new Thread();
	int i = 0;
	while( i < loaders.length )
	{
		if( !t1.isAlive() )
		{
			t1 = new Thread(loaders[i++]);
			t1.start();
		}

		if( !t2.isAlive() )
		{
			t2 = new Thread(loaders[i++]);
			t2.start();
		}

		Thread.currentThread().sleep(5);
	}
	t1.join();
	t2.join();
*/
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 * @param newAlias java.lang.String
 */
public void setDatabaseAlias(String newAlias){
	databaseAlias = newAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 2:01:01 PM)
 * @param newDbChangeListener com.cannontech.database.cache.CacheDBChangeListener
 */
protected void setDbChangeListener(CacheDBChangeListener newDbChangeListener) {
	dbChangeListener = newDbChangeListener;
}
}
