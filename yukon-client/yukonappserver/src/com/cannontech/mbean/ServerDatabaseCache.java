package com.cannontech.mbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;

import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.server.cache.*;


import org.jboss.naming.NonSerializableFactory;

/**
 * Insert the type's description here.
 * Creation date: (3/14/00 3:20:44 PM)
 * @author: 
 */ 

public class ServerDatabaseCache extends CTIMBeanBase implements IDatabaseCache
{
	//stores a soft reference to the cache
	//private static java.lang.ref.SoftReference cacheReference = null;	
	private static ServerDatabaseCache cache = null;

	private CacheDBChangeListener dbChangeListener = null;
	private String databaseAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	private ArrayList allYukonPAObjects = null;
	private ArrayList allPoints = null;
	private ArrayList allStateGroups = null;
	private ArrayList allUnitMeasures = null;
	private ArrayList allNotificationGroups = null;
	
	//private java.util.ArrayList allUsedContactNotifications = null;
	private ArrayList allContactNotifications = null;
	
	private ArrayList allAlarmCategories = null;
	private ArrayList allContacts = null;
	private ArrayList allGraphDefinitions = null;
	private ArrayList allHolidaySchedules = null;
	private ArrayList allDeviceMeterGroups = null;
	private ArrayList allPointsUnits = null;
	private ArrayList allPointLimits = null;
    private ArrayList allYukonImages = null;
	private ArrayList allCICustomers = null;

	private ArrayList allYukonUsers = null;
	private ArrayList allYukonRoles = null;
	private ArrayList allYukonRoleProperties = null;
	private ArrayList allYukonGroups = null;
	
	private Map allYukonUserRoleProperties = null;
	private Map allYukonGroupRoleProperties = null;
	private Map allYukonUserGroups = null;
	private Map allYukonGroupUsers = null;
		
	//derived from allYukonUsers,allYukonRoles,allYukonGroups
	//see type info in IDatabaseCache
	private Map allYukonUserLookupRoleIDs = null;
	private Map allYukonUserLookupRolePropertyIDs = null;
	
	private ArrayList allEnergyCompanies = null;
	private Map allUserEnergyCompanies = null;
	
	//lists that are created by the joining/parsing of existing lists
	private ArrayList allGraphTaggedPoints = null; //Points
	private ArrayList allUnusedCCDevices = null; //PAO
	private ArrayList allCapControlFeeders = null; //PAO
	private ArrayList allCapControlSubBuses = null; //PAO	
	private ArrayList allDevices = null; //PAO
	private ArrayList allLMPrograms = null; //PAO
	private ArrayList allLoadManagement = null; //PAO
	private ArrayList allPorts = null; //PAO
	private ArrayList allRoutes = null; //PAO
	
	
	private java.util.HashMap allPointidMultiplierHashMap = null;
/**
 * DefaultDatabaseCache constructor comment.
 */
public ServerDatabaseCache() 
{ 
	super();
}

/**
 * DefaultDatabaseCache constructor comment.
 */
protected ServerDatabaseCache(String databaseAlias) {
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
public synchronized DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType )
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
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
/*
public synchronized java.util.List getAllContactNotifications()
{
	if( allContactNotifications != null )
		return allContactNotifications;
	else
	{
		// FIXFIX ---should not return the NotifGroups!!!!!
		allContactNotifications = new java.util.ArrayList();
		ContactNotificationGroupLoader alarmStateLoader = new ContactNotificationGroupLoader(allContactNotifications, databaseAlias);
		alarmStateLoader.run();
		return allContactNotifications;
	}

}
*/
/**
 * Returns a list of all
 * com.cannontech.database.data.lite.LiteYukonImage
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllYukonImages()
{
   if( allYukonImages != null )
      return allYukonImages;
   else
   {
      allYukonImages = new java.util.ArrayList();
      YukonImageLoader imageLoader = new YukonImageLoader(allYukonImages, databaseAlias);
      imageLoader.run();
      return allYukonImages;
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
public synchronized java.util.List getAllContacts()
{
	if( allContacts != null )
		return allContacts;
	else
	{
		allContacts = new java.util.ArrayList();
		ContactLoader customerContactLoader = new ContactLoader(allContacts, databaseAlias);
		customerContactLoader.run();
		return allContacts;
	}
}

/**
 *
 */
public synchronized java.util.List getAllCICustomers() 
{
	if( allCICustomers == null )
	{
		allCICustomers = new java.util.ArrayList();
		CICustomerLoader ciCstLoader = new CICustomerLoader(allCICustomers, databaseAlias);
		ciCstLoader.run();
	}

	return allCICustomers;
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
			   com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
					com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			   }

			   //temp code
			   timerStop = new java.util.Date();
			   com.cannontech.clientutils.CTILogger.info( 
                               (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for getAllGraphTaggedPoints()" );
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
public synchronized java.util.List getAllContactNotificationGroups()
{
	if( allNotificationGroups != null )
		return allNotificationGroups;
	else
	{
		allNotificationGroups = new java.util.ArrayList();
		ContactNotificationGroupLoader notifLoader = new ContactNotificationGroupLoader(allNotificationGroups, databaseAlias);
		notifLoader.run();
		
		//allUsedContactNotifications = notifLoader.getAllUsedContactNotifications();
		return allNotificationGroups;
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
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.List getAllPointsUnits(){

	if( allPointsUnits != null )
		return allPointsUnits;
	else
	{
		allPointsUnits = new java.util.ArrayList();
		PointUnitLoader pointUnitLoader = new PointUnitLoader(allPointsUnits, databaseAlias);
		pointUnitLoader.run();
		return allPointsUnits;
	}
}

public synchronized java.util.List getAllPointLimits() {
	if( allPointLimits != null ) 
		return allPointLimits;
	else
	{
		allPointLimits = new java.util.ArrayList();
		PointLimitLoader pointLimitLoader = new PointLimitLoader(allPointLimits, databaseAlias);
		pointLimitLoader.run();
		return allPointLimits;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.HashMap getAllPointidMultiplierHashMap(){

	if( allPointidMultiplierHashMap != null )
		return allPointidMultiplierHashMap;
	else
	{

//	java.util.Vector returnMultipliers = new java.util.Vector();
	allPointidMultiplierHashMap = new java.util.HashMap(50);	//guess of how many points there may be.
	
	String sql = new String("SELECT ACC.POINTID, ACC.MULTIPLIER FROM POINTACCUMULATOR ACC");

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			System.out.println(":  Error getting database connection.");
			return null;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();

			while( rset.next() )
			{
				Integer pointID = new Integer(rset.getInt(1));
				Double multiplier = new Double(rset.getDouble(2));
				allPointidMultiplierHashMap.put(pointID, multiplier);				
			}
			
			sql = new String("SELECT ANA.POINTID, ANA.MULTIPLIER FROM POINTANALOG ANA");
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();
			
			while( rset.next())
			{
				Integer pointID = new Integer( rset.getInt(1));
				Double multiplier = new Double( rset.getDouble(2));
				allPointidMultiplierHashMap.put(pointID, multiplier);
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
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
			return null;
		}	
	}
		return allPointidMultiplierHashMap;
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
	   com.cannontech.clientutils.CTILogger.info( 
               (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for getAllUnusedCCPaos()" );
	   //temp code

		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroups()
	 */
	public List getAllYukonGroups() {		
		if(allYukonGroups == null) {
			allYukonGroups = new java.util.ArrayList();
			YukonGroupLoader l = new YukonGroupLoader(allYukonGroups, databaseAlias);
			l.run();
		}
		return allYukonGroups;				
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonRoles()
	 */
	public List getAllYukonRoles() {		
		if( allYukonRoles == null) {
			allYukonRoles = new java.util.ArrayList();
			YukonRoleLoader l = new YukonRoleLoader(allYukonRoles, databaseAlias);
			l.run();
		}
		return allYukonRoles;				
	}
		
	public List getAllYukonRoleProperties() { 
		if( allYukonRoleProperties == null) {
			allYukonRoleProperties = new ArrayList();
			final YukonRolePropertyLoader l = new YukonRolePropertyLoader(allYukonRoleProperties, databaseAlias);
			l.run();
		}
		return allYukonRoleProperties;
	}
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUsers()
	 */
	public List getAllYukonUsers() {		
		if(allYukonUsers == null) {
			allYukonUsers = new java.util.ArrayList();
			YukonUserLoader l = new YukonUserLoader(allYukonUsers, databaseAlias);
			l.run();
		}
		return allYukonUsers;		
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserRolePropertyMap()
	 */
	public Map getYukonUserRolePropertyMap() {
		
		if(allYukonUserRoleProperties == null) {
			allYukonUserRoleProperties = new java.util.HashMap();
		    final YukonUserRoleLoader l = 
		    	new YukonUserRoleLoader(allYukonUserRoleProperties, getAllYukonUsers(), getAllYukonRoles(), getAllYukonRoleProperties(), databaseAlias);
		    l.run();
		}									
		return allYukonUserRoleProperties;		
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupRolePropertyMap()
	 */
	public Map getYukonGroupRolePropertyMap() {
		if(allYukonGroupRoleProperties == null) {
			allYukonGroupRoleProperties = new java.util.HashMap();
			final YukonGroupRoleLoader l = 
				new YukonGroupRoleLoader(allYukonGroupRoleProperties, getAllYukonGroups(), getAllYukonRoles(), getAllYukonRoleProperties(), databaseAlias);
			l.run();
		}		
		return allYukonGroupRoleProperties;
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserGroupMap()
	 */
	public Map getYukonUserGroupMap() {
		if(allYukonUserGroups == null) {
			loadUsersAndGroups();
		}
		return allYukonUserGroups;
	}
		
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupUserMap()
	 */
	public Map getYukonGroupUserMap() {
		if(allYukonUserGroups == null) {
			loadUsersAndGroups();
		}
		return allYukonGroupUsers;				
	}

	private void loadUsersAndGroups() {
		allYukonUserGroups = new HashMap();
		allYukonGroupUsers = new HashMap();
		YukonUserGroupLoader l = 
				new YukonUserGroupLoader(allYukonUserGroups, allYukonGroupUsers, getAllYukonUsers(), getAllYukonGroups(), databaseAlias);
		l.run();
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserRoleIDLookupMap()
	 */
	public Map getYukonUserRoleIDLookupMap() {
		if(allYukonUserLookupRoleIDs == null) {
			loadRoleLookupMaps();
		}
		return allYukonUserLookupRoleIDs;
	}
	
	public Map getYukonUserRolePropertyIDLookupMap() {
		if(allYukonUserLookupRolePropertyIDs == null) {
			loadRoleLookupMaps();
		}
		return allYukonUserLookupRolePropertyIDs;
	}
	
	/**
	 * Fill in allYukonUserLookupRoleIDs and allYukonUserLookupRolePropertyIDs
	 */	
	private void loadRoleLookupMaps() 
	{
		allYukonUserLookupRoleIDs = new HashMap();
		allYukonUserLookupRolePropertyIDs = new HashMap();
		
		Iterator iter = getAllYukonUsers().iterator();			
		Map userRoles = getYukonUserRolePropertyMap();
		Map userGroups =  getYukonUserGroupMap();
		Map groupRoles = getYukonGroupRolePropertyMap();
			
		while(iter.hasNext()) {
					
			LiteYukonUser user = (LiteYukonUser) iter.next();
			
			Map userRoleIDsLookupMap = new HashMap();
			Map userRolePropertyIDsLookupMap = new HashMap();
			
			// first consider group roles then user roles
			// then user roles to maintain precedence
			List groups = (List) userGroups.get(user);
			if(groups != null) {
				Iterator groupIter = groups.iterator();
				while(groupIter.hasNext()) {
					LiteYukonGroup group = (LiteYukonGroup) groupIter.next();
					Map groupRoleMap = (Map) groupRoles.get(group);
					if(groupRoleMap != null) {
						addRolesAndPropertiesToLookupMap(groupRoleMap, userRoleIDsLookupMap, userRolePropertyIDsLookupMap);												
					}
				}
			}
			
			//add user roles
			Map userRoleMap = (Map) userRoles.get(user);
			if(userRoleMap != null) {			
				addRolesAndPropertiesToLookupMap(userRoleMap, userRoleIDsLookupMap,userRolePropertyIDsLookupMap);
			}
System.out.println("user: " + user.getUserID() + " size: " + userRoleIDsLookupMap.size());			
			allYukonUserLookupRoleIDs.put(user, userRoleIDsLookupMap);
			allYukonUserLookupRolePropertyIDs.put(user, userRolePropertyIDsLookupMap);
		}
	
	}
	/**
	 * roleMap<LiteYukonRole, Map<LiteYukonRoleProperty,String>>
	 * roleIDMap<Integer,LiteYukonRole>
	 * rolePropertyIDMap<Integer,Pair<LiteYukonRoleProperty,String>>
	 */
	private void addRolesAndPropertiesToLookupMap(final Map roleMap, final Map roleIDMap, final Map rolePropertyIDMap) {
		Iterator roleIter = roleMap.keySet().iterator();
		while(roleIter.hasNext()) {
			LiteYukonRole groupRole = (LiteYukonRole) roleIter.next();
System.out.println("putting: " + groupRole.getRoleID());			
			roleIDMap.put(new Integer(groupRole.getRoleID()), groupRole);						
						
			// add roleproperties for this role
			Map rolePropertyMap = (Map) roleMap.get(groupRole);
			addRolePropertiesToMap(rolePropertyMap, rolePropertyIDMap);						
		}	
	}
	
	/**
	 * Adds the properties and values in rolePropertyMap<LiteYukonRoleProperty,String>
	 * into roleIDMap<Integer,Pair<LiteYukonRoleProperty,String>>
	 * */
	private void addRolePropertiesToMap(final Map rolePropertyMap, final Map rolePropertyIDMap) {
		Iterator propertyIter = rolePropertyMap.keySet().iterator();
		while(propertyIter.hasNext()) {
			LiteYukonRoleProperty property = (LiteYukonRoleProperty) propertyIter.next();
			Pair propPair = new Pair(property, rolePropertyMap.get(property));
			rolePropertyIDMap.put(new Integer(property.getRolePropertyID()), propPair);
		}		
	}
					
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllEnergyCompanies()
	 */
	public List getAllEnergyCompanies() {
		if(allEnergyCompanies == null) {
			allEnergyCompanies = new java.util.ArrayList();
			EnergyCompanyLoader l = new EnergyCompanyLoader(allEnergyCompanies, databaseAlias);
			l.run();
		}
		return allEnergyCompanies;
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllUserEnergyCompanies()
	 */
	public Map getAllUserEnergyCompanies() {
		if(allUserEnergyCompanies == null) {
			allUserEnergyCompanies = new java.util.HashMap();
			UserEnergyCompanyLoader l = new UserEnergyCompanyLoader(allUserEnergyCompanies,getAllYukonUsers(), getAllEnergyCompanies(), databaseAlias);
			l.run();
		}
		return allUserEnergyCompanies;
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
public static synchronized com.cannontech.yukon.IDatabaseCache getInstance()
{
	if( cache == null )
	{
		com.cannontech.clientutils.CTILogger.info("CACHE: CREATING NEW CACHE REFERENCE OBJECT");
		cache = new ServerDatabaseCache();
		cache.setDbChangeListener( new CacheDBChangeListener() );
	}

	return cache;
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
private synchronized LiteBase handleYukonImageChange( int changeType, int id )
{
   boolean alreadyAdded = false;
   LiteBase lBase = null;

   // if the storage is not already loaded, we must not care about it
   if( allAlarmCategories == null )
      return lBase;

   switch(changeType)
   {
      case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
            for(int i=0;i<allYukonImages.size();i++)
            {
               if( ((com.cannontech.database.data.lite.LiteYukonImage)allYukonImages.get(i)).getImageID() == id )
               {
                  alreadyAdded = true;
                  lBase = (LiteBase)allYukonImages.get(i);
                  break;
               }
            }
            if( !alreadyAdded )
            {
               com.cannontech.database.data.lite.LiteYukonImage ls = new com.cannontech.database.data.lite.LiteYukonImage(id);
               ls.retrieve(databaseAlias);
               allYukonImages.add(ls);
               lBase = ls;
            }           
            break;

      case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
            for(int i=0;i<allYukonImages.size();i++)
            {
               if( ((com.cannontech.database.data.lite.LiteYukonImage)allYukonImages.get(i)).getImageID() == id )
               {
                  ((com.cannontech.database.data.lite.LiteYukonImage)allYukonImages.get(i)).retrieve(databaseAlias);
                  lBase = (LiteBase)allYukonImages.get(i);
                  break;
               }
            }
            break;
      case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
            for(int i=0;i<allYukonImages.size();i++)
            {
               if( ((com.cannontech.database.data.lite.LiteYukonImage)allYukonImages.get(i)).getImageID() == id )
               {
                  lBase = (LiteBase)allYukonImages.remove(i);
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
	if( allContacts == null )
		return lBase;;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
		
				if( id == DBChangeMsg.CHANGE_INVALID_ID )
					break;
		
				for(int i=0;i<allContacts.size();i++)
				{
					if( ((LiteContact)allContacts.get(i)).getLiteID() == id )
					{
						alreadyAdded = true;
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteContact lc = new LiteContact(id);
					lc.retrieve(databaseAlias);
					allContacts.add(lc);
					lBase = lc;
				}
				break;

		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:

				//if( id == DBChangeMsg.CHANGE_INVALID_ID )
					//break;
		
				for(int i=0;i<allContacts.size();i++)
				{
					if( ((LiteContact)allContacts.get(i)).getLiteID() == id )
					{
						((LiteContact)allContacts.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allContacts.get(i);
						break;
					}
				}

				if( lBase == null ) //we did not find the contact, just create a new one
				{
					LiteContact lc = new LiteContact(id);
					lc.retrieve(databaseAlias);
					allContacts.add(lc);
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
				
				for(int i=0;i<allContacts.size();i++)
				{
					if( ((LiteContact)allContacts.get(i)).getLiteID() == id )
					{
						lBase = (LiteBase)allContacts.remove(i);
						break;
					}
				}
				break;

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
		allPointsUnits = null;
		allPointidMultiplierHashMap = null;
		allPointLimits = null;
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
			allLoadManagement = null; //PAOGroups are here, oops!
			
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
   else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_STATE_IMAGE_DB )
   {
      retLBase = handleYukonImageChange( dbType, id );
   }
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_NOTIFICATION_GROUP_DB )
	{
		retLBase = handleNotificationGroupChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_NOTIFICATION_RECIPIENT_DB )
	{
		retLBase = handleContactNotificationChange( dbType, id );
	}
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CONTACT_DB )
	{
		//clear out the CICustomers as they may have changed
		allCICustomers = null;

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
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CUSTOMER_DB )
	{
		retLBase = handleCICustomerChange( dbType, id );
	}	
	else if( database == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_YUKON_USER_DB ) 
	{
		// This seems heavy handed!
		//allYukonUsers = null;
		retLBase = handleYukonUserChange( dbType, id );
		
		allYukonRoles = null;
		allYukonGroups = null;
		allYukonUserRoleProperties = null;
		allYukonGroupRoleProperties = null;
		allYukonUserGroups = null;
		allYukonGroupUsers = null;
		allUserEnergyCompanies = null;
	}
	else if( database == DBChangeMsg.CHANGE_ENERGY_COMPANY_DB )
	{
		allEnergyCompanies = null;
		allUserEnergyCompanies = null;
	}
	else if ( database == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB )
	{
		if ( dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CUSTOMER_ACCOUNT) ) {
			allContacts = null;
		}
		retLBase = null;
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
private synchronized LiteBase handleContactNotificationChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allContactNotifications == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allContactNotifications.size();i++)
				{
					if( ((LiteContactNotification)allContactNotifications.get(i)).getContactID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allContactNotifications.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteContactNotification lg = new LiteContactNotification(id);
					lg.retrieve(databaseAlias);
					allContactNotifications.add(lg);
					lBase = lg;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allContactNotifications.size();i++)
				{
					if( ((LiteContactNotification)allContactNotifications.get(i)).getContactID() == id )
					{
						((LiteContactNotification)allContactNotifications.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allContactNotifications.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allContactNotifications.size();i++)
				{
					if( ((LiteContactNotification)allContactNotifications.get(i)).getContactID() == id )
					{
						lBase = (LiteBase)allContactNotifications.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllContactNotifications();
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
private synchronized LiteBase handleCICustomerChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allCICustomers == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allCICustomers.size();i++)
				{
					if( ((LiteCICustomer)allCICustomers.get(i)).getCustomerID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allCICustomers.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteCICustomer lcst = new LiteCICustomer(id);
					lcst.retrieve(databaseAlias);
					allCICustomers.add(lcst);
					lBase = lcst;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allCICustomers.size();i++)
				{
					if( ((LiteCICustomer)allCICustomers.get(i)).getCustomerID() == id )
					{
						((LiteCICustomer)allCICustomers.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allCICustomers.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allCICustomers.size();i++)
				{
					if( ((LiteCICustomer)allCICustomers.get(i)).getCustomerID() == id )
					{
						lBase = (LiteBase)allCICustomers.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllCICustomers();
				break;
	}

	return lBase;
}


/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleYukonUserChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allYukonUsers == null )
		return lBase;

	switch(changeType)
	{
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allYukonUsers.size();i++)
				{
					if( ((LiteYukonUser)allYukonUsers.get(i)).getUserID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allYukonUsers.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteYukonUser lcst = new LiteYukonUser(id);
					lcst.retrieve(databaseAlias);
					allYukonUsers.add(lcst);
					lBase = lcst;
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allYukonUsers.size();i++)
				{
					if( ((LiteYukonUser)allYukonUsers.get(i)).getUserID() == id )
					{
						((LiteYukonUser)allYukonUsers.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allYukonUsers.get(i);
						break;
					}
				}
				break;
		case com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allYukonUsers.size();i++)
				{
					if( ((LiteYukonUser)allYukonUsers.get(i)).getUserID() == id )
					{
						lBase = (LiteBase)allYukonUsers.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllYukonUsers();
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
public synchronized void releaseAllAlarmCategories()
{
	allAlarmCategories = null;
}

/**
 * Drop all the junk we have accumulated.
 * Please be keeping this method in sync
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllCache()
{
	allYukonPAObjects = null;
	allPoints = null;
	allStateGroups = null;
	allUnitMeasures = null;
	allNotificationGroups = null;
	
	allContactNotifications = null;
	
	allAlarmCategories = null;
	allContacts = null;
	allGraphDefinitions = null;
	allHolidaySchedules = null;
	allDeviceMeterGroups = null;
	allPointsUnits = null;
	allPointLimits = null;
    allYukonImages = null;
	allCICustomers = null;

	allYukonUsers = null;
	allYukonRoles = null;
	allYukonGroups = null;
	
	allYukonUserRoleProperties = null;
	allYukonGroupRoleProperties = null;
	allYukonUserGroups = null;
	allYukonGroupUsers = null;
		
	allYukonUserLookupRoleIDs = null;
	allYukonUserLookupRolePropertyIDs = null;
	
	allEnergyCompanies = null;
	allUserEnergyCompanies = null;
	
	allGraphTaggedPoints = null; //Points
	allUnusedCCDevices = null; //PAO
	allCapControlFeeders = null; //PAO
	allCapControlSubBuses = null; //PAO	
	allDevices = null; //PAO
	allLMPrograms = null; //PAO
	allLoadManagement = null; //PAO
	allPorts = null; //PAO
	allRoutes = null; //PAO
	
	allPointidMultiplierHashMap = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllCustomerContacts()
{
	allContacts = null;
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
public synchronized void releaseAllYukonImages()
{
   allYukonImages = null;
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
public synchronized void releaseAllContactNotifications()
{
	allContactNotifications = null;
}

public synchronized void releaseAllCICustomers()
{
	allCICustomers = null;
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
public synchronized void releaseAllYukonUsers(){
	allYukonUsers = null;
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
