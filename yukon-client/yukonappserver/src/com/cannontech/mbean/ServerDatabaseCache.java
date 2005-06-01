package com.cannontech.mbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.server.cache.*;

/**
 * All the action is here!
 * Creation date: (3/14/00 3:20:44 PM)
 * @author: everyone and their dog
 */ 

public class ServerDatabaseCache extends CTIMBeanBase implements IDatabaseCache
{
	//stores a soft reference to the cache
	//private static java.lang.ref.SoftReference cacheReference = null;	
	private static ServerDatabaseCache cache = null;

	private CacheDBChangeListener dbChangeListener = null;
	private String databaseAlias = CtiUtilities.getDatabaseAlias();

	private ArrayList allYukonPAObjects = null;
	private ArrayList allPoints = null;
	private ArrayList allUnitMeasures = null;
	private ArrayList allNotificationGroups = null;
	
	//private ArrayList allUsedContactNotifications = null;
	private ArrayList allContactNotifications = null;
	
	private ArrayList allAlarmCategories = null;
	private ArrayList allContacts = null;
	private ArrayList allGraphDefinitions = null;
	private ArrayList allMCTs = null;
	private ArrayList allHolidaySchedules = null;
	private ArrayList allBaselines = null;
	private ArrayList allConfigs = null;
	private ArrayList allDeviceMeterGroups = null;
	private ArrayList allDMG_CollectionGroups = null;	//distinct DeviceMeterGroup.collectionGroup
	private ArrayList allDMG_AlternateGroups = null;	//distinct DeviceMeterGroup.alternateGroup
	private ArrayList allDMG_BillingGroups = null;	//distinct DeviceMeterGroup.billingGroup
	private ArrayList allPointsUnits = null;
	private ArrayList allPointLimits = null;
	private ArrayList allYukonImages = null;
	private ArrayList allCICustomers = null;
	private ArrayList allCustomers = null;
	private ArrayList allLMProgramConstraints = null;
	private ArrayList allLMScenarios = null;
	private ArrayList allLMScenarioProgs = null;
	private ArrayList allLMPAOExclusions = null;

	private ArrayList allTags = null;
	private ArrayList allSeasonSchedules = null;
	private ArrayList allGears = null;
	private ArrayList allTOUSchedules = null;
	private ArrayList allTOUDays = null;
	
	private ArrayList allYukonUsers = null;
	private ArrayList allYukonRoles = null;
	private ArrayList allYukonRoleProperties = null;
	private ArrayList allYukonGroups = null;
	
	private Map allYukonUserRolePropertiesMap = null;
	private Map allYukonGroupRolePropertiesMap = null;
	private Map allYukonUserGroupsMap = null;
	private Map allYukonGroupUsersMap = null;
		
	private ArrayList allEnergyCompanies = null;
	
	//lists that are created by the joining/parsing of existing lists
	private ArrayList allGraphTaggedPoints = null; //Points
	private ArrayList allUnusedCCDevices = null; //PAO
	private ArrayList allCapControlFeeders = null; //PAO
	private ArrayList allCapControlSubBuses = null; //PAO	
	private ArrayList allDevices = null; //PAO
	private ArrayList allLMPrograms = null; //PAO
	private ArrayList allLMControlAreas = null;	//PAO
	private ArrayList allLMGroups = null;//PAO
	private ArrayList allLoadManagement = null; //PAO
	private ArrayList allPorts = null; //PAO
	private ArrayList allRoutes = null; //PAO
	
	
	//Maps that are created by the joining/parsing of existing lists
	private HashMap allPointidMultiplierHashMap = null;
	private Map allPointIDOffsetHashMap = null;
	private Map allPointsMap = null;
	private Map allPAOsMap = null;
	private Map allCustomersMap = null;    
	private Map allContactsMap = null;
    
	//derived from allYukonUsers,allYukonRoles,allYukonGroups
	//see type info in IDatabaseCache
	private Map allYukonUserLookupRoleIDsMap = null;
	private Map allYukonUserLookupRolePropertyIDsMap = null;    
	private Map allUserEnergyCompaniesMap = null;
	private Map userPaoOwnersMap = null;
    
	private ArrayList allDeviceTypeCommands = null;
	private ArrayList allCommands = null;
	private Map allCommandsMap = null;
	private Map allStateGroupMap = null;
	private Map allUsersMap = null;
	private Map allContactNotifsMap = null;


/**
 * ServerDatabaseCache constructor comment.
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
public synchronized void addDBChangeListener(DBChangeListener listener) 
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
		allAlarmCategories = new ArrayList();
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
		allContactNotifications = new ArrayList();
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
	  allYukonImages = new ArrayList();
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
		allCapControlFeeders = new ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == PAOGroups.CAT_CAPCONTROL
				 && ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getType() == PAOGroups.CAP_CONTROL_FEEDER )
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
		allCapControlSubBuses = new ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == PAOGroups.CAT_CAPCONTROL
				 && ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getType() == PAOGroups.CAP_CONTROL_SUBBUS )
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
		allContacts = new ArrayList();        
		allContactsMap = new HashMap();

		ContactLoader contactLoader =
			new ContactLoader(allContacts, allContactsMap, databaseAlias);

		contactLoader.run();		
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
		allCICustomers = new ArrayList( getAllCustomers().size());

		for( int i = 0; i < getAllCustomers().size(); i++ )
		{
			if( getAllCustomers().get(i) instanceof LiteCICustomer )
				allCICustomers.add((LiteCICustomer)getAllCustomers().get(i));
		}
		allCICustomers.trimToSize();
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
		allDeviceMeterGroups = new ArrayList();
		DeviceMeterGroupLoader deviceMeterGroupLoader = new DeviceMeterGroupLoader (allDeviceMeterGroups, databaseAlias);
		deviceMeterGroupLoader.run();
		return allDeviceMeterGroups;
	}

}

public synchronized List getAllDMG_CollectionGroups()
{
	if (allDMG_CollectionGroups != null)
		return allDMG_CollectionGroups;
	else
	{
		allDMG_CollectionGroups = new ArrayList();
		List groups = getAllDeviceMeterGroups();
		for (int i = 0; i < groups.size(); i++)
		{
			LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)groups.get(i);
			if( !allDMG_CollectionGroups.contains(ldmn.getCollGroup()))
				allDMG_CollectionGroups.add(ldmn.getCollGroup());
		}

		Collections.sort(allDMG_CollectionGroups);
		return allDMG_CollectionGroups;
	}
}
public synchronized List getAllDMG_AlternateGroups()
{
	if (allDMG_AlternateGroups != null)
		return allDMG_AlternateGroups;
	else
	{
		allDMG_AlternateGroups = new ArrayList();
		List groups = getAllDeviceMeterGroups();
		for (int i = 0; i < groups.size(); i++)
		{
			LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)groups.get(i);
			if( !allDMG_AlternateGroups.contains(ldmn.getTestCollGroup()))
				allDMG_AlternateGroups.add(ldmn.getTestCollGroup());
		}
		Collections.sort(allDMG_AlternateGroups);
		return allDMG_AlternateGroups;
	}
}

public synchronized List getAllDMG_BillingGroups()
{
	if (allDMG_BillingGroups != null)
		return allDMG_BillingGroups;
	else
	{
		allDMG_BillingGroups = new ArrayList();
		List groups = getAllDeviceMeterGroups();
		for (int i = 0; i < groups.size(); i++)
		{
			LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber)groups.get(i);
			if( !allDMG_BillingGroups.contains(ldmn.getBillGroup()))
				allDMG_BillingGroups.add(ldmn.getBillGroup());
		}
		Collections.sort(allDMG_BillingGroups);
		return allDMG_BillingGroups;
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
		allDevices = new ArrayList( getAllYukonPAObjects().size() );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() 
				  == PAOGroups.CAT_DEVICE )
				allDevices.add( getAllYukonPAObjects().get(i) );
		}

		allDevices.trimToSize();		
	}

	return allDevices;
}

/**
 * Get all MCTs
 */
public synchronized java.util.List getAllMCTs() {
	if (allMCTs == null) {
		allMCTs = new ArrayList( getAllDevices().size() / 2 );
		
		for (int i = 0; i < getAllDevices().size(); i++) {
			if (com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(
					((LiteYukonPAObject) getAllDevices().get(i)).getType() ))
				allMCTs.add( getAllDevices().get(i) );
		}
		
		allMCTs.trimToSize();
	}
	
	return allMCTs;
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
		allGraphDefinitions = new ArrayList();
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

		  allGraphTaggedPoints = new ArrayList();


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
					LitePoint point = PointFuncs.getLitePoint( pointID );

					// tags may need to be changed here if there
					//  are more tags added to this bit field
					long tags = LitePoint.POINT_UOFM_GRAPH;      //default value of tags for now.

					if( formula.equalsIgnoreCase("usage"))
						tags = LitePoint.POINT_UOFM_USAGE;

					point.setTags( tags );
					allGraphTaggedPoints.add(  point );

			   }
			   // Grab all status points too...!
			   for (int i = 0; i < getAllPoints().size(); i++)
			   {
					LitePoint point = ((LitePoint) getAllPoints().get(i));
					if ( point.getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
					{
						 point.setTags( LitePoint.POINT_UOFM_GRAPH );
						 allGraphTaggedPoints.add (point);
					}
			   }

		  }
		  catch( java.sql.SQLException e )
		  {
			   CTILogger.error( e.getMessage(), e );
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
					CTILogger.error( e.getMessage(), e );
			   }
		  }
          
		  //temp code
		  timerStop = new java.util.Date();
		  CTILogger.info( 
							  (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for getAllGraphTaggedPoints()" );
            
		  //sort our points by pointoffset
		  java.util.Collections.sort( allGraphTaggedPoints, com.cannontech.database.data.lite.LiteComparators.litePointPointOffsetComparator );
          
		  return allGraphTaggedPoints;
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
		allHolidaySchedules = new ArrayList();
		HolidayScheduleLoader holidayScheduleLoader = new HolidayScheduleLoader(allHolidaySchedules, databaseAlias);
		holidayScheduleLoader.run();
		return allHolidaySchedules;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized java.util.List getAllBaselines()
{

	if (allBaselines != null)
		return allBaselines;
	else
	{
		allBaselines = new ArrayList();
		BaselineLoader baselineLoader = new BaselineLoader(allBaselines, databaseAlias);
		baselineLoader.run();
		return allBaselines;
	}
}

public synchronized java.util.List getAllSeasonSchedules()
{

	if (allSeasonSchedules != null)
		return allSeasonSchedules;
	else
	{
		allSeasonSchedules = new ArrayList();
		SeasonScheduleLoader seasonLoader = new SeasonScheduleLoader(allSeasonSchedules, databaseAlias);
		seasonLoader.run();
		return allSeasonSchedules;
	}
}

public synchronized java.util.List getAllTOUSchedules()
{

	if (allTOUSchedules != null)
		return allTOUSchedules;
	else
	{
		allTOUSchedules = new ArrayList();
		TOUScheduleLoader touLoader = new TOUScheduleLoader(allTOUSchedules, databaseAlias);
		touLoader.run();
		return allTOUSchedules;
	}
}

public synchronized java.util.List getAllTOUDays()
{

	if (allTOUDays != null)
		return allTOUDays;
	else
	{
		allTOUDays = new ArrayList();
		TOUDayLoader dayLoader = new TOUDayLoader(allTOUDays, databaseAlias);
		dayLoader.run();
		return allTOUDays;
	}
}

public synchronized java.util.List getAllGears()
{

	if (allGears != null)
		return allGears;
	else
	{
		allGears = new ArrayList();
		GearLoader gearLoader = new GearLoader(allGears, databaseAlias);
		gearLoader.run();
		return allGears;
	}
}

public synchronized List getAllCommands() {
	if (allCommands== null)
	{
		allCommands = new ArrayList();
		allCommandsMap = new HashMap();
		CommandLoader commandLoader = new CommandLoader(allCommands, allCommandsMap, databaseAlias);
		commandLoader.run();
	}
	return allCommands;
}

public synchronized java.util.Map getAllCommandsMap()
{
	if( allCommandsMap != null )
		return allCommandsMap;
	else
	{
		releaseAllCommands();
		getAllCommands();

		return allCommandsMap;
	}
}

public synchronized java.util.List getAllConfigs()
{

	if (allConfigs != null)
		return allConfigs;
	else
	{
		allConfigs = new ArrayList();
		ConfigLoader configLoader = new ConfigLoader(allConfigs, databaseAlias);
		configLoader.run();
		return allConfigs;
	}
}

public synchronized java.util.List getAllLMProgramConstraints()
{

	if (allLMProgramConstraints != null)
		return allLMProgramConstraints;
	else
	{
		allLMProgramConstraints = new ArrayList();
		LMConstraintLoader lmConstraintsLoader = new LMConstraintLoader(allLMProgramConstraints, databaseAlias);
		lmConstraintsLoader.run();
		return allLMProgramConstraints;
	}
}

public synchronized java.util.List getAllLMScenarioProgs()
{

	if( allLMScenarioProgs == null )
	{
		allLMScenarioProgs = new ArrayList();
		LMScenarioProgramLoader ldr = new LMScenarioProgramLoader(allLMScenarioProgs, databaseAlias);
		ldr.run();
	}
	
	return allLMScenarioProgs;
}

public synchronized java.util.List getAllLMScenarios()
{

	if( allLMScenarios == null )
	{
		allLMScenarios = new ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllLoadManagement().get(i)).getType() 
				  == PAOGroups.LM_SCENARIO )
			allLMScenarios.add( getAllLoadManagement().get(i) );
		}

		allLMScenarios.trimToSize();		
	}

	return allLMScenarios;
}

public synchronized java.util.List getAllLMPAOExclusions()
{

	if( allLMPAOExclusions == null )
	{
		allLMPAOExclusions = new ArrayList();
		LMPAOExclusionLoader ldr = new LMPAOExclusionLoader(allLMPAOExclusions, databaseAlias);
		ldr.run();
	}
	
	return allLMPAOExclusions;
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
		allLMPrograms = new ArrayList( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == PAOGroups.LM_CURTAIL_PROGRAM
				 || ((LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == PAOGroups.LM_DIRECT_PROGRAM
				 || ((LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM )
				allLMPrograms.add( getAllLoadManagement().get(i) );				
		}

		allLMPrograms.trimToSize();
	}
	return allLMPrograms;
}

/* (non-Javadoc)
 * @see com.cannontech.yukon.IDatabaseCache#getAllLMControlAreas()
 */
public List getAllLMControlAreas()
{
	if( allLMControlAreas == null )
	{
		allLMControlAreas = new ArrayList( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllLoadManagement().get(i)).getType() == PAOGroups.LM_CONTROL_AREA )
				allLMControlAreas.add( getAllLoadManagement().get(i) );				
		}

		allLMControlAreas.trimToSize();
	}
	return allLMControlAreas;
}


/* (non-Javadoc)
 * @see com.cannontech.yukon.IDatabaseCache#getAllLMGroups()
 */
public List getAllLMGroups()
{
    if( allLMGroups == null )
	{
		allLMGroups = new ArrayList( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( DeviceTypesFuncs.isLmGroup( ((LiteYukonPAObject)getAllLoadManagement().get(i)).getType()) )
				allLMGroups.add( getAllLoadManagement().get(i) );				
		}

		allLMGroups.trimToSize();
	}
	return allLMGroups;
}
/**
 * getAllLoadManagement method comment.
 *
 */
public synchronized java.util.List getAllLoadManagement() 
{
	if( allLoadManagement == null )
	{
		allLoadManagement = new ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getPaoClass() == DeviceClasses.LOADMANAGEMENT ||
					((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getPaoClass() == DeviceClasses.GROUP )
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
		allNotificationGroups = new ArrayList();
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
		allPoints = new ArrayList();
		allPointsMap = new HashMap();
		PointLoader pointLoader = new PointLoader(allPoints, allPointsMap, databaseAlias);
		pointLoader.run();
		return allPoints;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.Map getAllPointsMap()
{
	if( allPointsMap != null )
		return allPointsMap;
	else
	{
		releaseAllPoints();
		getAllPoints();

		return allPointsMap;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.Map getAllPAOsMap()
{
	if( allPAOsMap != null )
		return allPAOsMap;
	else
	{
		releaseAllYukonPAObjects();
		getAllYukonPAObjects();

		return allPAOsMap;
	}
}

/**
 * A map for all LiteYukonUser objects keyed by userid
 * 
 */
public synchronized java.util.Map getAllUsersMap()
{
	if( allUsersMap != null )
		return allUsersMap;
	else
	{
		releaseAllYukonUsers();
		getAllYukonUsers();

		return allUsersMap;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.Map getAllContactsMap()
{
	if( allContactsMap != null )
		return allContactsMap;
	else
	{
		releaseAllContacts();
		getAllContacts();

		return allContactsMap;
	}
}

public synchronized java.util.Map getAllContactNotifsMap()
{
	if( allContactNotifsMap != null )
		return allContactNotifsMap;
	else
	{
		allContactNotifsMap = new HashMap();

		ContactNotifcationLoader notifLoader =
			new ContactNotifcationLoader(allContactNotifsMap, databaseAlias);

		notifLoader.run();		
		return allContactNotifsMap;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.Map getAllCustomersMap()
{
	if( allCustomersMap != null )
		return allCustomersMap ;
	else
	{
		releaseAllCustomers();
		getAllCustomers();

		return allCustomersMap;
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
		allPointsUnits = new ArrayList();
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
		allPointLimits = new ArrayList();
		PointLimitLoader pointLimitLoader = new PointLimitLoader(allPointLimits, databaseAlias);
		pointLimitLoader.run();
		return allPointLimits;
	}
}
/**
 * Get a map of type HashMap<Integer(point id),Double(multiplier>
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.HashMap
 */
public synchronized java.util.HashMap getAllPointidMultiplierHashMap(){
	loadPointMaps();
	return allPointidMultiplierHashMap;
}

/**
 * Get a map of type Map<Integer(point id), Integer(data offset)
 * @return java.util.Map
 */
public synchronized java.util.Map getAllPointIDOffsetMap() {
	loadPointMaps();
	return allPointIDOffsetHashMap;
}

/**
 * Convenience method to load multiplier and offset maps
 * Populates allPointidMultiplierHashMap and allPointIDOffsetHashMap
 */
private synchronized void loadPointMaps() {
	if( allPointidMultiplierHashMap != null &&
		allPointIDOffsetHashMap != null)
		return;
	
	{

	allPointIDOffsetHashMap = new java.util.HashMap(getAllPoints().size()*2);
	allPointidMultiplierHashMap = new java.util.HashMap(getAllPoints().size()*2);	//guess of how many points there may be.
	
	String sql = new String("SELECT ACC.POINTID, ACC.MULTIPLIER, ACC.DATAOFFSET FROM POINTACCUMULATOR ACC");

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			CTILogger.error(":  Error getting database connection.");
			return;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();

			while( rset.next() )
			{
				Integer pointID = new Integer(rset.getInt(1));
				Double multiplier = new Double(rset.getDouble(2));
				Integer offset = new Integer(rset.getInt(3));
				allPointidMultiplierHashMap.put(pointID, multiplier);
				allPointIDOffsetHashMap.put(pointID, offset);				
			}
			
			sql = new String("SELECT ANA.POINTID, ANA.MULTIPLIER, ANA.DATAOFFSET FROM POINTANALOG ANA");
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();
			
			while( rset.next())
			{
				Integer pointID = new Integer( rset.getInt(1));
				Double multiplier = new Double( rset.getDouble(2));
				Integer offset = new Integer(rset.getInt(3));
				allPointidMultiplierHashMap.put(pointID, multiplier);
				allPointIDOffsetHashMap.put(pointID, offset);
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
			return;
		}	
	}
		return;
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
		allPorts = new ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() 
				 == PAOGroups.CAT_PORT )
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
		allRoutes = new ArrayList( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( ((LiteYukonPAObject)getAllYukonPAObjects().get(i)).getCategory() == PAOGroups.CAT_ROUTE )
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
public synchronized Map getAllStateGroupMap()
{
	if( allStateGroupMap == null )
	{
		allStateGroupMap = new HashMap();
		StateGroupLoader stateGroupLoader = new StateGroupLoader(allStateGroupMap);
		stateGroupLoader.run();
		return allStateGroupMap;
	}

	return allStateGroupMap;
}

public synchronized java.util.List getAllTags() {
	if(allTags == null)
	{
		allTags = new ArrayList();
		TagLoader tagLoader = new TagLoader(allTags, databaseAlias);
		tagLoader.run();
		return allTags;	
	}
	return allTags;
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
		allUnitMeasures = new ArrayList();
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
			allUnusedCCDevices = new ArrayList( (int)(allDevices.size() * 0.7)  );
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while (rset.next())
			{
				int paoID = rset.getInt(1);
				
				LiteYukonPAObject pao = PAOFuncs.getLiteYukonPAO( paoID );
				if( pao != null )
				{
					allUnusedCCDevices.add( pao );
				}
			}
				
			if( rset != null )
				rset.close();

	   //temp code
	   java.util.Date timerStop = new java.util.Date();
	   CTILogger.info( 
			   (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for getAllUnusedCCPaos()" );
	   //temp code

		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
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
				CTILogger.error( e.getMessage(), e );
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
	if( allYukonPAObjects != null && allPAOsMap != null)
		return allYukonPAObjects;
	else
	{
		allYukonPAObjects = new ArrayList();
		allPAOsMap = new HashMap();
		YukonPAOLoader yukLoader = new YukonPAOLoader(allYukonPAObjects, allPAOsMap, databaseAlias);
		yukLoader.run();
		
		return allYukonPAObjects;
	}
}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroups()
	 */
	public synchronized List getAllYukonGroups() {		
		if(allYukonGroups == null) {
			allYukonGroups = new ArrayList();
			YukonGroupLoader l = new YukonGroupLoader(allYukonGroups, databaseAlias);
			l.run();
		}
		return allYukonGroups;				
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonRoles()
	 */
	public synchronized List getAllYukonRoles() {		
		if( allYukonRoles == null) {
			allYukonRoles = new ArrayList();
			YukonRoleLoader l = new YukonRoleLoader(allYukonRoles, databaseAlias);
			l.run();
		}
		return allYukonRoles;				
	}
		
	public synchronized List getAllYukonRoleProperties() { 
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
	public synchronized List getAllYukonUsers()
	{		
		if( allYukonUsers != null && allUsersMap != null )
			return allYukonUsers;
		else
		{
			allYukonUsers = new ArrayList();
			allUsersMap = new HashMap();
			YukonUserLoader l = new YukonUserLoader(allYukonUsers, allUsersMap, databaseAlias);
			l.run();
			return allYukonUsers;
		}

	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserRolePropertyMap()
	 */
	public synchronized Map getYukonUserRolePropertyMap() 
	{
		if(allYukonUserRolePropertiesMap == null) {
			allYukonUserRolePropertiesMap = new java.util.HashMap();
			final YukonUserRoleLoader l = 
				new YukonUserRoleLoader(allYukonUserRolePropertiesMap, getAllYukonUsers(), getAllYukonRoles(), getAllYukonRoleProperties(), databaseAlias);
			l.run();
		}									
		return allYukonUserRolePropertiesMap;		
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupRolePropertyMap()
	 */
	public synchronized Map getYukonGroupRolePropertyMap() 
	{
		if(allYukonGroupRolePropertiesMap == null) 
		{
			allYukonGroupRolePropertiesMap = new java.util.HashMap();
			final YukonGroupRoleLoader l = 
				new YukonGroupRoleLoader(allYukonGroupRolePropertiesMap, getAllYukonGroups(), getAllYukonRoles(), getAllYukonRoleProperties(), databaseAlias);
			l.run();
		}		
		return allYukonGroupRolePropertiesMap;
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserGroupMap()
	 */
	public synchronized Map getYukonUserGroupMap() 
	{
		if(allYukonUserGroupsMap == null) {
			loadUsersAndGroups();
		}
		return allYukonUserGroupsMap;
	}
		
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonGroupUserMap()
	 */
	public synchronized Map getYukonGroupUserMap()
	{
		if(allYukonUserGroupsMap == null) {
			loadUsersAndGroups();
		}
		return allYukonGroupUsersMap;				
	}

	private void loadUsersAndGroups() 
	{
		allYukonUserGroupsMap = new HashMap();
		allYukonGroupUsersMap = new HashMap();
		YukonUserGroupLoader l = 
				new YukonUserGroupLoader(allYukonUserGroupsMap, allYukonGroupUsersMap, getAllYukonUsers(), getAllYukonGroups(), databaseAlias);
		l.run();
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserRoleIDLookupMap()
	 */
	public synchronized Map getYukonUserRoleIDLookupMap() 
	{
		if(allYukonUserLookupRoleIDsMap == null) {
			loadRoleLookupMaps();
		}
		return allYukonUserLookupRoleIDsMap;
	}
	
	public synchronized Map getYukonUserRolePropertyIDLookupMap() 
	{
		if(allYukonUserLookupRolePropertyIDsMap == null) {
			loadRoleLookupMaps();
		}
		return allYukonUserLookupRolePropertyIDsMap;
	}
	
	/**
	 * Fill in allYukonUserLookupRoleIDs and allYukonUserLookupRolePropertyIDs
	 */	
	private void loadRoleLookupMaps() 
	{		
		allYukonUserLookupRoleIDsMap = new HashMap();
		allYukonUserLookupRolePropertyIDsMap = new HashMap();
		
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
			
			allYukonUserLookupRoleIDsMap.put(user, userRoleIDsLookupMap);
			allYukonUserLookupRolePropertyIDsMap.put(user, userRolePropertyIDsLookupMap);
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
	public synchronized List getAllEnergyCompanies() {
		if(allEnergyCompanies == null) {
			allEnergyCompanies = new ArrayList();
			EnergyCompanyLoader l = new EnergyCompanyLoader(allEnergyCompanies, databaseAlias);
			l.run();
		}
		return allEnergyCompanies;
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllUserEnergyCompanies()
	 */
	public synchronized Map getAllUserEnergyCompanies() {
		if(allUserEnergyCompaniesMap == null) {
			allUserEnergyCompaniesMap = new java.util.HashMap();
			UserEnergyCompanyLoader l = new UserEnergyCompanyLoader(allUserEnergyCompaniesMap,getAllYukonUsers(), getAllEnergyCompanies(), databaseAlias);
			l.run();
		}
		return allUserEnergyCompaniesMap;
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getYukonUserPaoOwners()
	 */
	public synchronized Map getYukonUserPaoOwners() {
		if(userPaoOwnersMap == null) {
			userPaoOwnersMap = new java.util.HashMap();
			UserPoaOwnerLoader l = new UserPoaOwnerLoader( userPaoOwnersMap, getAllYukonUsers(), databaseAlias);
			l.run();
		}
		return userPaoOwnersMap;
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllCustomers()
	 */
	public synchronized List getAllCustomers() {
		if (allCustomers == null)
		{
			allCustomers = new ArrayList();
			allCustomersMap = new HashMap();
			CustomerLoader custLoader = new CustomerLoader(allCustomers, allCustomersMap, databaseAlias);
			custLoader.run();
		}
		return allCustomers;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/14/00 3:19:19 PM)
	 * @return java.util.Collection
	 */
	public synchronized java.util.List getAllDeviceTypeCommands(){

		if( allDeviceTypeCommands != null )
			return allDeviceTypeCommands;
		else
		{
			allDeviceTypeCommands= new ArrayList();
			DeviceTypeCommandLoader devTypeCmdLoader = new DeviceTypeCommandLoader(allDeviceTypeCommands, databaseAlias);
			devTypeCmdLoader.run();
			return allDeviceTypeCommands;
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
public static synchronized com.cannontech.yukon.IDatabaseCache getInstance()
{
	if( cache == null )
	{
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
		case DBChangeMsg.CHANGE_TYPE_ADD:
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

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
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
		case DBChangeMsg.CHANGE_TYPE_DELETE:
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
   if( allYukonImages == null )
	  return lBase;

   switch(changeType)
   {
	  case DBChangeMsg.CHANGE_TYPE_ADD:
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

	  case DBChangeMsg.CHANGE_TYPE_UPDATE:
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
	  case DBChangeMsg.CHANGE_TYPE_DELETE:
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
private synchronized LiteBase handleContactChange( int changeType, int id )
{
	LiteBase lBase = null;
	
	// if the storage is not already loaded, we must not care about it
	if( allContacts == null )
		return lBase;;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
		
				if( id == DBChangeMsg.CHANGE_INVALID_ID )
					break;
		
				lBase = (LiteBase)allContactsMap.get( new Integer(id) );                
				if( lBase == null )
				{
					LiteContact lc = new LiteContact(id);
					lc.retrieve(databaseAlias);
					allContacts.add(lc);
					allContactsMap.put( new Integer(lc.getContactID()), lc );
        
					lBase = lc;
				}
				break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:

				LiteContact lc = (LiteContact)allContactsMap.get( new Integer(id) );                
				lc.retrieve( databaseAlias );                
				lBase = lc;

				if( lBase == null ) //we did not find the contact, just create a new one
				{
					lc = new LiteContact(id);
					lc.retrieve(databaseAlias);
					allContacts.add(lc);
					allContactsMap.put( new Integer(lc.getContactID()), lc );
        
					lBase = lc;
				}                
				break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
				//special case for this handler!!!!
				if( id == DBChangeMsg.CHANGE_INVALID_ID )
				{
					releaseAllContacts();
					break;
				}		
				
				for(int i=0;i<allContacts.size();i++)
				{
					if( ((LiteContact)allContacts.get(i)).getLiteID() == id )
					{
						allContactsMap.remove( new Integer(id) );
						lBase = (LiteBase)allContacts.remove(i);
						break;
					}
				}
				break;

		default:
				releaseAllContacts();
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
public synchronized LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg)
{
	String objectType = dbChangeMsg.getObjectType();
	String dbCategory = dbChangeMsg.getCategory();
	int dbType = dbChangeMsg.getTypeOfChange();
	int database = dbChangeMsg.getDatabase();
	int id = dbChangeMsg.getId();
	LiteBase retLBase = null;

	if( database == DBChangeMsg.CHANGE_POINT_DB )
	{
		allGraphTaggedPoints = null;
		allPointsUnits = null;
		allPointidMultiplierHashMap = null;
		allPointIDOffsetHashMap = null;
		allPointLimits = null;
		retLBase = handlePointChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_PAO_DB )
	{
		retLBase = handleYukonPAOChange( dbType, id);
		

		//if any device changes, 
		// reload all the DeviceMeterGroup data (may be inefficient!!)
		if( dbCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_DEVICE) )
		{
			allDevices = null;
			allMCTs = null;
			allUnusedCCDevices = null;
			allLoadManagement = null; //PAOGroups are here, oops!
			
			//we should not have to return the DeviceMeterGroup since
			// the liteObject that was edited/added was actually a Device
			//retLBase = handleDeviceMeterGroupChange( dbType, id);
			
			//Verify that this a device that even cares about DeviceMeterGroups
			int type = PAOGroups.getDeviceType(objectType);
			if(com.cannontech.database.data.device.DeviceTypesFuncs.usesDeviceMeterGroup(type))
			{
				allDMG_CollectionGroups = null;
				allDMG_AlternateGroups = null;
				allDMG_BillingGroups = null;
									
				handleDeviceMeterGroupChange( dbType, id);
			}
		}
		else if( dbCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_LOADMANAGEMENT) )
		{
			allLoadManagement = null;
			allLMPrograms = null;
			allLMControlAreas = null;
			allLMGroups = null;
			allLMScenarios = null;
			allLMScenarioProgs = null;
			allLMPAOExclusions = null;
		}
		else if( dbCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_CAPCONTROL) )
		{
			allCapControlFeeders = null;
			allCapControlSubBuses = null;	
		}
		else if( dbCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_PORT) )
		{
			allPorts = null;
		}
		else if( dbCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_ROUTE) )
		{
			allRoutes = null;	
		}

	}
	else if( database == DBChangeMsg.CHANGE_STATE_GROUP_DB )
	{
		retLBase = handleStateGroupChange( dbType, id );		
	}
	else if( database == DBChangeMsg.CHANGE_ALARM_CATEGORY_DB )
	{
		retLBase = handleAlarmCategoryChange( dbType, id );
	}
   else if( database == DBChangeMsg.CHANGE_YUKON_IMAGE )
   { 
	  retLBase = handleYukonImageChange( dbType, id );
   }
	else if( database == DBChangeMsg.CHANGE_NOTIFICATION_GROUP_DB )
	{
		retLBase = handleNotificationGroupChange( dbType, id );
	}
//	else if( database == DBChangeMsg.CHANGE_NOTIFICATION_RECIPIENT_DB )
//	{
//		retLBase = handleContactNotificationChange( dbType, id );
//	}
	else if( database == DBChangeMsg.CHANGE_CONTACT_DB )
	{
		//clear out the CICustomers & NotificationGroups as they may have changed
		allCICustomers = null;
		allNotificationGroups = null;
		allContactNotifsMap = null;

		retLBase = handleContactChange( dbType, id );		
	}
	else if( database == DBChangeMsg.CHANGE_GRAPH_DB )
	{
		retLBase = handleGraphDefinitionChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_HOLIDAY_SCHEDULE_DB )
	{
		retLBase = handleHolidayScheduleChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_BASELINE_DB )
	{
		retLBase = handleBaselineChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_SEASON_SCHEDULE_DB )
	{
		retLBase = handleSeasonScheduleChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_TOU_SCHEDULE_DB )
	{
		retLBase = handleTOUScheduleChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_CONFIG_DB )
	{
		retLBase = handleConfigChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_TAG_DB )
	{
		retLBase = handleTagChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_LMCONSTRAINT_DB )
	{
		retLBase = handleLMProgramConstraintChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_DEVICETYPE_COMMAND_DB)
	{
		retLBase = handleDeviceTypeCommandChange(dbType, id);
	}
	else if( database == DBChangeMsg.CHANGE_COMMAND_DB )
	{
		retLBase = handleCommandChange( dbType, id );
	}
	else if( database == DBChangeMsg.CHANGE_CUSTOMER_DB
			|| database == DBChangeMsg.CHANGE_ENERGY_COMPANY_DB )
	{
		allEnergyCompanies = null;
		allUserEnergyCompaniesMap = null;

		//only let the Customer DBChange go into here
		if( database == DBChangeMsg.CHANGE_CUSTOMER_DB )
		{
			allNotificationGroups = null;
			retLBase = handleCustomerChange( dbType, id, dbCategory );
			
			//TODO Find a better way to update the cicustomers, this sweep is begining to hurt my knees.
			if( dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CI_CUSTOMER))
				allCICustomers = null;
		}			
	}	
	else if( database == DBChangeMsg.CHANGE_YUKON_USER_DB ) 
	{
		if( DBChangeMsg.CAT_YUKON_USER_GROUP.equalsIgnoreCase(dbCategory) )
			retLBase = handleYukonGroupChange( dbType, id );
		else
			retLBase = handleYukonUserChange( dbType, id );
		
		// This seems heavy handed!
		allYukonGroups = null;
		allYukonRoles = null;
		allYukonUserRolePropertiesMap = null;
		allYukonGroupRolePropertiesMap = null;
		allYukonUserGroupsMap = null;
		allYukonGroupUsersMap = null;
		allUserEnergyCompaniesMap = null;
		allYukonUserLookupRoleIDsMap = null;
		allYukonUserLookupRolePropertyIDsMap = null;

		userPaoOwnersMap = null;
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
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( ((LiteDeviceMeterNumber )allDeviceMeterGroups.get(i)).getDeviceID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allDeviceMeterGroups.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteDeviceMeterNumber liteDMG = new LiteDeviceMeterNumber(id);
					liteDMG.retrieve(databaseAlias);
					allDeviceMeterGroups.add(liteDMG);
					lBase = liteDMG;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( ((LiteDeviceMeterNumber )allDeviceMeterGroups.get(i)).getDeviceID() == id )
					{
						((LiteDeviceMeterNumber )allDeviceMeterGroups.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allDeviceMeterGroups.get(i);
						
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
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
		case DBChangeMsg.CHANGE_TYPE_ADD:
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
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
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
		case DBChangeMsg.CHANGE_TYPE_DELETE:
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
		case DBChangeMsg.CHANGE_TYPE_ADD:
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
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
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
		case DBChangeMsg.CHANGE_TYPE_DELETE:
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
private synchronized LiteBase handleDeviceTypeCommandChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allDeviceTypeCommands == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allDeviceTypeCommands.size();i++)
				{
					if( ((LiteDeviceTypeCommand)allDeviceTypeCommands.get(i)).getDeviceCommandID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allDeviceTypeCommands.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteDeviceTypeCommand ldtc = new LiteDeviceTypeCommand(id);
					ldtc.retrieve(databaseAlias);
					allDeviceTypeCommands.add(ldtc);
					lBase = ldtc;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allDeviceTypeCommands.size();i++)
				{
					if( ((LiteDeviceTypeCommand)allDeviceTypeCommands.get(i)).getDeviceCommandID() == id )
					{
						((LiteDeviceTypeCommand)allDeviceTypeCommands.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allDeviceTypeCommands.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allDeviceTypeCommands.size();i++)
				{
					if( ((LiteDeviceTypeCommand)allDeviceTypeCommands.get(i)).getDeviceCommandID() == id )
					{
						lBase = (LiteBase)allDeviceTypeCommands.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllDeviceTypeCommands();
				break;
	}

	return lBase;
}


private synchronized LiteBase handleBaselineChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allBaselines == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allBaselines.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteBaseline)allBaselines.get(i)).getBaselineID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allBaselines.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteBaseline lh = new com.cannontech.database.data.lite.LiteBaseline(id);
					lh.retrieve(databaseAlias);
					allBaselines.add(lh);
					lBase = lh;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allBaselines.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteBaseline)allBaselines.get(i)).getBaselineID() == id )
					{
						((com.cannontech.database.data.lite.LiteBaseline)allBaselines.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allBaselines.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allBaselines.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteBaseline)allBaselines.get(i)).getBaselineID() == id )
					{
						lBase = (LiteBase)allBaselines.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllBaselines();
				break;
	}

	return lBase;
}

private synchronized LiteBase handleSeasonScheduleChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allSeasonSchedules == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allSeasonSchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteSeasonSchedule)allSeasonSchedules.get(i)).getScheduleID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allSeasonSchedules.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteSeasonSchedule lh = new com.cannontech.database.data.lite.LiteSeasonSchedule(id);
					lh.retrieve(databaseAlias);
					allSeasonSchedules.add(lh);
					lBase = lh;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allSeasonSchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteSeasonSchedule)allSeasonSchedules.get(i)).getScheduleID() == id )
					{
						((com.cannontech.database.data.lite.LiteSeasonSchedule)allSeasonSchedules.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allSeasonSchedules.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allSeasonSchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteSeasonSchedule)allSeasonSchedules.get(i)).getScheduleID() == id )
					{
						lBase = (LiteBase)allSeasonSchedules.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllSeasonSchedules();
				break;
	}

	return lBase;
}

private synchronized LiteBase handleTOUScheduleChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allTOUSchedules == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allTOUSchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteTOUSchedule)allTOUSchedules.get(i)).getScheduleID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allTOUSchedules.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteTOUSchedule lh = new com.cannontech.database.data.lite.LiteTOUSchedule(id);
					lh.retrieve(databaseAlias);
					allTOUSchedules.add(lh);
					lBase = lh;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allTOUSchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteTOUSchedule)allTOUSchedules.get(i)).getScheduleID() == id )
					{
						((com.cannontech.database.data.lite.LiteTOUSchedule)allTOUSchedules.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allTOUSchedules.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allTOUSchedules.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteTOUSchedule)allTOUSchedules.get(i)).getScheduleID() == id )
					{
						lBase = (LiteBase)allTOUSchedules.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllTOUSchedules();
				break;
	}

	return lBase;
}

//private synchronized LiteBase handleTOUDayChange( int changeType, int id )
//{
//	boolean alreadyAdded = false;
//	LiteBase lBase = null;
//
//	// if the storage is not already loaded, we must not care about it
//	if( allTOUDays == null )
//		return lBase;
//
//	switch(changeType)
//	{
//		case DBChangeMsg.CHANGE_TYPE_ADD:
//				for(int i=0;i<allTOUDays.size();i++)
//				{
//					if( ((com.cannontech.database.data.lite.LiteTOUDay)allTOUDays.get(i)).getDayID() == id )
//					{
//						alreadyAdded = true;
//						lBase = (LiteBase)allTOUDays.get(i);
//						break;
//					}
//				}
//				if( !alreadyAdded )
//				{
//					com.cannontech.database.data.lite.LiteTOUDay lh = new com.cannontech.database.data.lite.LiteTOUDay(id);
//					lh.retrieve(databaseAlias);
//					allTOUDays.add(lh);
//					lBase = lh;
//				}
//				break;
//				
//		case DBChangeMsg.CHANGE_TYPE_UPDATE:
//				for(int i=0;i<allTOUDays.size();i++)
//				{
//					if( ((com.cannontech.database.data.lite.LiteTOUDay)allTOUDays.get(i)).getDayID() == id )
//					{
//						((com.cannontech.database.data.lite.LiteTOUDay)allTOUDays.get(i)).retrieve(databaseAlias);
//						lBase = (LiteBase)allTOUDays.get(i);
//						break;
//					}
//				}
//				break;
//				
//		case DBChangeMsg.CHANGE_TYPE_DELETE:
//				for(int i=0;i<allTOUDays.size();i++)
//				{
//					if( ((com.cannontech.database.data.lite.LiteTOUDay)allTOUDays.get(i)).getDayID() == id )
//					{
//						lBase = (LiteBase)allTOUDays.remove(i);
//						break;
//					}
//				}
//				break;
//		default:
//				releaseAllTOUDays();
//				break;
//	}
//
//	return lBase;
//}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleCommandChange( int changeType, int id )
{
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allCommands == null )
		return lBase;
	
	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
		
				lBase = (LiteBase)allCommandsMap.get( new Integer(id) );				
				if( lBase == null )
				{
					LiteCommand lc = new LiteCommand(id);
					lc.retrieve(databaseAlias);
					allCommands.add(lc);
					allCommandsMap.put( new Integer(lc.getCommandID()), lc );

					lBase = lc;
				}
				break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
		
				LiteCommand lc = (LiteCommand)allCommandsMap.get( new Integer(id) );				
				lc.retrieve( databaseAlias );
				
				lBase = lc;
				break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:

				for(int i=0;i<allCommands.size();i++)
				{
					if( ((LiteCommand)allCommands.get(i)).getCommandID() == id )
					{
						allCommandsMap.remove( new Integer(id) );
						lBase = (LiteBase)allCommands.remove(i);
						break;
					}
				}
				break;

		default:
				releaseAllCommands();
				break;
	}

	return lBase;
}


private synchronized LiteBase handleConfigChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allConfigs == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allConfigs.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteConfig)allConfigs.get(i)).getConfigID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allConfigs.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteConfig lh = new com.cannontech.database.data.lite.LiteConfig(id);
					lh.retrieve(databaseAlias);
					allConfigs.add(lh);
					lBase = lh;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allConfigs.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteConfig)allConfigs.get(i)).getConfigID() == id )
					{
						((com.cannontech.database.data.lite.LiteConfig)allConfigs.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allConfigs.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allConfigs.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteConfig)allConfigs.get(i)).getConfigID() == id )
					{
						lBase = (LiteBase)allConfigs.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllConfigs();
				break;
	}

	return lBase;
}

private synchronized LiteBase handleTagChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lTag = null;

	// if the storage is not already loaded, we must not care about it
	if( allTags == null )
		return lTag;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allTags.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteTag)allTags.get(i)).getTagID() == id )
					{
						alreadyAdded = true;
						lTag = (LiteBase)allTags.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteTag lh = new com.cannontech.database.data.lite.LiteTag(id);
					lh.retrieve(databaseAlias);
					allTags.add(lh);
					lTag = lh;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allTags.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteTag)allTags.get(i)).getTagID() == id )
					{
						((com.cannontech.database.data.lite.LiteTag)allTags.get(i)).retrieve(databaseAlias);
						lTag = (LiteBase)allTags.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allTags.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteTag)allTags.get(i)).getTagID() == id )
					{
						lTag = (LiteBase)allTags.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllTags();
				break;
	}

	return lTag;
}

private synchronized LiteBase handleLMProgramConstraintChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allLMProgramConstraints == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allLMProgramConstraints.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteLMConstraint)allLMProgramConstraints.get(i)).getConstraintID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allLMProgramConstraints.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					com.cannontech.database.data.lite.LiteLMConstraint lh = new com.cannontech.database.data.lite.LiteLMConstraint(id);
					lh.retrieve(databaseAlias);
					allLMProgramConstraints.add(lh);
					lBase = lh;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allLMProgramConstraints.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteLMConstraint)allLMProgramConstraints.get(i)).getConstraintID() == id )
					{
						((com.cannontech.database.data.lite.LiteLMConstraint)allLMProgramConstraints.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allLMProgramConstraints.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allLMProgramConstraints.size();i++)
				{
					if( ((com.cannontech.database.data.lite.LiteLMConstraint)allLMProgramConstraints.get(i)).getConstraintID() == id )
					{
						lBase = (LiteBase)allLMProgramConstraints.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllLMProgramConstraints();
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
		case DBChangeMsg.CHANGE_TYPE_ADD:
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
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
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
		case DBChangeMsg.CHANGE_TYPE_DELETE:
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
//private synchronized LiteBase handleContactNotificationChange( int changeType, int id )
//{
//	boolean alreadyAdded = false;
//	LiteBase lBase = null;
//
//	// if the storage is not already loaded, we must not care about it
//	if( allContactNotifications == null )
//		return lBase;
//
//	switch(changeType)
//	{
//		case DBChangeMsg.CHANGE_TYPE_ADD:
//				for(int i=0;i<allContactNotifications.size();i++)
//				{
//					if( ((LiteContactNotification)allContactNotifications.get(i)).getContactID() == id )
//					{
//						alreadyAdded = true;
//						lBase = (LiteBase)allContactNotifications.get(i);
//						break;
//					}
//				}
//				if( !alreadyAdded )
//				{
//					LiteContactNotification lg = new LiteContactNotification(id);
//					lg.retrieve(databaseAlias);
//					allContactNotifications.add(lg);
//					lBase = lg;
//				}
//				break;
//		case DBChangeMsg.CHANGE_TYPE_UPDATE:
//				for(int i=0;i<allContactNotifications.size();i++)
//				{
//					if( ((LiteContactNotification)allContactNotifications.get(i)).getContactID() == id )
//					{
//						((LiteContactNotification)allContactNotifications.get(i)).retrieve(databaseAlias);
//						lBase = (LiteBase)allContactNotifications.get(i);
//						break;
//					}
//				}
//				break;
//		case DBChangeMsg.CHANGE_TYPE_DELETE:
//				for(int i=0;i<allContactNotifications.size();i++)
//				{
//					if( ((LiteContactNotification)allContactNotifications.get(i)).getContactID() == id )
//					{
//						lBase = (LiteBase)allContactNotifications.remove(i);
//						break;
//					}
//				}
//				break;
//		default:
//				releaseAllContactNotifications();
//				break;
//	}
//
//	return lBase;
//}
/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handlePointChange( int changeType, int id )
{
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allPoints == null )
		return lBase;
	
	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
		
				lBase = (LiteBase)allPointsMap.get( new Integer(id) );				
				if( lBase == null )
				{
					LitePoint lp = new LitePoint(id);
					lp.retrieve(databaseAlias);
					allPoints.add(lp);
					allPointsMap.put( new Integer(lp.getPointID()), lp );

					lBase = lp;
				}
				break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
		
				LitePoint lp = (LitePoint)allPointsMap.get( new Integer(id) );				
				lp.retrieve( databaseAlias );
				
				lBase = lp;
				break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:

				for(int i=0;i<allPoints.size();i++)
				{
					if( ((LitePoint)allPoints.get(i)).getPointID() == id )
					{
						allPointsMap.remove( new Integer(id) );
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
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allStateGroupMap == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
			lBase = (LiteBase)allStateGroupMap.get( new Integer(id) );				
			if( lBase == null )
			{	
				LiteStateGroup lsg = new LiteStateGroup(id);
				lsg.retrieve(databaseAlias);
				allStateGroupMap.put( new Integer(lsg.getStateGroupID()), lsg );
				lBase = lsg;
			}
			break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
			LiteStateGroup ly = (LiteStateGroup)allStateGroupMap.get( new Integer(id) );				
			ly.retrieve( databaseAlias );
					
			lBase = ly;
			break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
			lBase = (LiteBase)allStateGroupMap.remove( new Integer(id) );
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
private synchronized LiteBase handleCustomerChange( int changeType, int id, String dbCategory)
{
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allCustomers == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:

			lBase = (LiteBase)allCustomersMap.get( new Integer(id));
			if( lBase == null)
			{
				LiteCustomer lc;
				if( dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CI_CUSTOMER ))
					lc = new LiteCICustomer(id);
				else 
					lc = new LiteCustomer(id);
				lc.retrieve(databaseAlias);
				allCustomers.add(lc);
				allCustomersMap.put( new Integer(lc.getCustomerID()), lc);
				
				lBase = lc;
			}
			break;
			
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
			
			LiteCustomer lc = (LiteCustomer)allCustomersMap.get(new Integer(id));
			lc.retrieve(databaseAlias);
			lBase = lc;
			break;
			
		case DBChangeMsg.CHANGE_TYPE_DELETE:
			for(int i=0;i<allCustomers.size();i++)
			{
				if( ((LiteCustomer)allCustomers.get(i)).getCustomerID() == id )
				{
					allCustomersMap.remove( new Integer(id));
					lBase = (LiteBase)allCustomers.remove(i);
					break;
				}
			}
			break;
		default:
				releaseAllCustomers();
				break;
	}

	return lBase;
}

/**
 * Insert the method's description here.
 * Creation date: (12/7/00 12:34:05 PM)
 */
private synchronized LiteBase handleYukonGroupChange( int changeType, int id )
{
	boolean alreadyAdded = false;
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allYukonGroups == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allYukonGroups.size();i++)
				{
					if( ((LiteYukonGroup)allYukonGroups.get(i)).getGroupID() == id )
					{
						alreadyAdded = true;
						lBase = (LiteBase)allYukonGroups.get(i);
						break;
					}
				}
				if( !alreadyAdded )
				{
					LiteYukonGroup lcst = new LiteYukonGroup(id);
					lcst.retrieve(databaseAlias);
					allYukonGroups.add(lcst);
					lBase = lcst;
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
				for(int i=0;i<allYukonGroups.size();i++)
				{
					if( ((LiteYukonGroup)allYukonGroups.get(i)).getGroupID() == id )
					{
						((LiteYukonGroup)allYukonGroups.get(i)).retrieve(databaseAlias);
						lBase = (LiteBase)allYukonGroups.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allYukonGroups.size();i++)
				{
					if( ((LiteYukonGroup)allYukonGroups.get(i)).getGroupID() == id )
					{
						lBase = (LiteBase)allYukonGroups.remove(i);
						break;
					}
				}
				break;
		default:
				releaseAllYukonGroups();
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
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allYukonUsers == null || allUsersMap == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
			lBase = (LiteBase)allUsersMap.get( new Integer(id) );				
			if( lBase == null )
			{
				LiteYukonUser lu = new LiteYukonUser(id);
				lu.retrieve(databaseAlias);
				allYukonUsers.add(lu);
				allUsersMap.put( new Integer(lu.getUserID()), lu );
		
				lBase = lu;
			}
			break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
			LiteYukonUser lu = (LiteYukonUser)allUsersMap.get( new Integer(id) );				
			lu.retrieve( databaseAlias );
						
			lBase = lu;
			break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
			for(int i=0;i<allYukonUsers.size();i++)
			{
				if( ((LiteYukonUser)allYukonUsers.get(i)).getUserID() == id )
				{
					allUsersMap.remove( new Integer(id) );
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
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allYukonPAObjects == null || allPAOsMap == null)
		return lBase;
		
		
	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:

			lBase = (LiteBase)allPAOsMap.get( new Integer(id) );				
			if( lBase == null )
			{
				LiteYukonPAObject ly = new LiteYukonPAObject(id);
				ly.retrieve(databaseAlias);
				allYukonPAObjects.add(ly);
				allPAOsMap.put( new Integer(ly.getYukonID()), ly );
	
				lBase = ly;
			}
			break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:

			LiteYukonPAObject ly = (LiteYukonPAObject)allPAOsMap.get( new Integer(id) );				
			ly.retrieve( databaseAlias );
					
			lBase = ly;
			break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allYukonPAObjects.size();i++)
				{
					if( ((LiteYukonPAObject)allYukonPAObjects.get(i)).getYukonID() == id )
					{
						allPAOsMap.remove( new Integer(id) );
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
 * 
 */
public synchronized void releaseAllCache()
{
	allYukonPAObjects = null;
	allPoints = null;
	allStateGroupMap = null;
	allUnitMeasures = null;
	allNotificationGroups = null;
	allContactNotifsMap = null;
    
	//allUsedContactNotifications = null;
	allContactNotifications = null;
    
	allAlarmCategories = null;
	allContacts = null;
	allGraphDefinitions = null;
	allMCTs = null;
	allHolidaySchedules = null;
	allBaselines = null;
	allConfigs = null;
	allDeviceMeterGroups = null;
	allDMG_CollectionGroups = null;
	allDMG_AlternateGroups = null;
	allDMG_BillingGroups = null;    
	allPointsUnits = null;
	allPointLimits = null;
	allYukonImages = null;
	allCICustomers = null;
	allCustomers = null;
	allLMProgramConstraints = null;
	allLMScenarios = null;
	allLMScenarioProgs = null;

	allTags = null;
	allSeasonSchedules = null;
	allGears = null;
	allDeviceTypeCommands = null;
	allTOUSchedules = null;
	allTOUDays = null;
    
	allYukonUsers = null;
	allYukonRoles = null;
	allYukonRoleProperties = null;
	allYukonGroups = null;
    
	allYukonUserRolePropertiesMap = null;
	allYukonGroupRolePropertiesMap = null;
	allYukonUserGroupsMap = null;
	allYukonGroupUsersMap = null;
        
	allEnergyCompanies = null;
    
	//lists that are created by the joining/parsing of existing lists
	allGraphTaggedPoints = null; //Points
	allUnusedCCDevices = null; //PAO
	allCapControlFeeders = null; //PAO
	allCapControlSubBuses = null; //PAO   
	allDevices = null; //PAO
	allLMPrograms = null; //PAO
	allLMControlAreas = null; //PAO
	allLMGroups = null;	//PAO
	allLoadManagement = null; //PAO
	allPorts = null; //PAO
	allRoutes = null; //PAO
    
    
	//Maps that are created by the joining/parsing of existing lists
	allPointidMultiplierHashMap = null;
	allPointIDOffsetHashMap = null;
	allPointsMap = null;
	allPAOsMap = null;
	allCustomersMap = null;
	allContactsMap = null;
	allUsersMap = null;


	//derived from allYukonUsers,allYukonRoles,allYukonGroups
	//see type info in IDatabaseCache
	allYukonUserLookupRoleIDsMap = null;
	allYukonUserLookupRolePropertyIDsMap = null;    
	allUserEnergyCompaniesMap = null;
	userPaoOwnersMap = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllContacts()
{
	allContacts = null;
	allContactsMap = null;
	allContactNotifsMap = null;
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

public synchronized void releaseAllBaselines()
{
	allBaselines = null;
}

public synchronized void releaseAllSeasonSchedules()
{
	allSeasonSchedules = null;
}
public synchronized void releaseAllCommands()
{
	allCommands = null;
	allCommandsMap = null;
}

public synchronized void releaseAllTOUSchedules()
{
	allTOUSchedules = null;
}

public synchronized void releaseAllTOUDays()
{
	allTOUDays = null;
}

public synchronized void releaseAllConfigs()
{
	allConfigs = null;
}

public synchronized void releaseAllTags()
{
	allTags = null;
}

public synchronized void releaseAllLMProgramConstraints()
{
	allLMProgramConstraints = null;
}

public synchronized void releaseAllLMScenarios()
{
	allLMScenarios = null;
}

public synchronized void releaseAllLMPAOExclusions()
{
	allLMPAOExclusions = null;
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

public synchronized void releaseAllCustomers()
{
	allCustomers = null;
	allCustomersMap = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllPoints()
{
	allPoints = null;
	allPointsMap = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllYukonUsers(){
	allYukonUsers = null;
	allUsersMap = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllYukonGroups(){
	allYukonGroups = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllStateGroups()
{
	allStateGroupMap = null;
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
	allPAOsMap = null;
}

/**
 * Insert the method's description here.
 */
public synchronized void releaseAllDeviceTypeCommands()
{
	allDeviceTypeCommands = null;
}

/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:09:04 PM)
 * @param DBChangeListener
 */
public synchronized void removeDBChangeListener(DBChangeListener listener) 
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
public synchronized void setDatabaseAlias(String newAlias){
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
