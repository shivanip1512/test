package com.cannontech.mbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceConfiguration;
import com.cannontech.database.data.lite.LiteDeviceConfigurationCategory;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.data.lite.LiteLMPAOExclusion;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteTOUDay;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.server.cache.AlarmCategoryLoader;
import com.cannontech.yukon.server.cache.BaselineLoader;
import com.cannontech.yukon.server.cache.CommandLoader;
import com.cannontech.yukon.server.cache.ConfigLoader;
import com.cannontech.yukon.server.cache.ContactLoader;
import com.cannontech.yukon.server.cache.ContactNotificationGroupLoader;
import com.cannontech.yukon.server.cache.CustomerLoader;
import com.cannontech.yukon.server.cache.DeviceCommPortLoader;
import com.cannontech.yukon.server.cache.DeviceMeterGroupLoader;
import com.cannontech.yukon.server.cache.DeviceTypeCommandLoader;
import com.cannontech.yukon.server.cache.EnergyCompanyLoader;
import com.cannontech.yukon.server.cache.GearLoader;
import com.cannontech.yukon.server.cache.GraphDefinitionLoader;
import com.cannontech.yukon.server.cache.HolidayScheduleLoader;
import com.cannontech.yukon.server.cache.LMConstraintLoader;
import com.cannontech.yukon.server.cache.LMPAOExclusionLoader;
import com.cannontech.yukon.server.cache.LMScenarioProgramLoader;
import com.cannontech.yukon.server.cache.PointLimitLoader;
import com.cannontech.yukon.server.cache.SeasonScheduleLoader;
import com.cannontech.yukon.server.cache.SettlementConfigLoader;
import com.cannontech.yukon.server.cache.StateGroupLoader;
import com.cannontech.yukon.server.cache.SystemPointLoader;
import com.cannontech.yukon.server.cache.TOUDayLoader;
import com.cannontech.yukon.server.cache.TOUScheduleLoader;
import com.cannontech.yukon.server.cache.TagLoader;
import com.cannontech.yukon.server.cache.UserEnergyCompanyLoader;
import com.cannontech.yukon.server.cache.YukonGroupLoader;
import com.cannontech.yukon.server.cache.YukonGroupRoleLoader;
import com.cannontech.yukon.server.cache.YukonImageLoader;
import com.cannontech.yukon.server.cache.YukonPAOLoader;
import com.cannontech.yukon.server.cache.YukonRoleLoader;
import com.cannontech.yukon.server.cache.YukonRolePropertyLoader;
import com.cannontech.yukon.server.cache.YukonUserGroupLoader;
import com.cannontech.yukon.server.cache.YukonUserLoader;
import com.cannontech.yukon.server.cache.YukonUserRoleLoader;
import com.cannontech.yukon.server.cache.bypass.MapKeyInts;
import com.cannontech.yukon.server.cache.bypass.YukonCustomerLookup;
import com.cannontech.yukon.server.cache.bypass.YukonUserContactLookup;
import com.cannontech.yukon.server.cache.bypass.YukonUserContactNotificationLookup;
import com.cannontech.yukon.server.cache.bypass.YukonUserRolePropertyLookup;

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

	private ArrayList<LiteYukonPAObject> allYukonPAObjects = null;
	//private ArrayList allPoints = null;
    private ArrayList<LitePoint> allSystemPoints = null;
	private List<LiteNotificationGroup> allNotificationGroups = null;
		
	private ArrayList<LiteAlarmCategory> allAlarmCategories = null;
	private ArrayList<LiteContact> allContacts = null;
	private ArrayList<LiteGraphDefinition> allGraphDefinitions = null;
	private ArrayList<LiteYukonPAObject> allMCTs = null;
	private ArrayList<LiteHolidaySchedule> allHolidaySchedules = null;
	private ArrayList<LiteBaseline> allBaselines = null;
	private ArrayList<LiteConfig> allConfigs = null;
	private ArrayList<LiteDeviceMeterNumber> allDeviceMeterGroups = null;
	private ArrayList<String> allDMG_CollectionGroups = null;	//distinct DeviceMeterGroup.collectionGroup
	private ArrayList<String> allDMG_AlternateGroups = null;	//distinct DeviceMeterGroup.alternateGroup
	private ArrayList<String> allDMG_BillingGroups = null;	//distinct DeviceMeterGroup.billingGroup
	private ArrayList<LitePointLimit> allPointLimits = null;
	private ArrayList<LiteYukonImage> allYukonImages = null;
	private List<LiteCICustomer> allCICustomers = null;
	private List<LiteCustomer> allCustomers = null;
	private ArrayList<LiteLMConstraint> allLMProgramConstraints = null;
	private ArrayList<LiteYukonPAObject> allLMScenarios = null;
	private ArrayList<LiteLMProgScenario> allLMScenarioProgs = null;
	private ArrayList<LiteLMPAOExclusion> allLMPAOExclusions = null;

	private ArrayList<LiteTag> allTags = null;
	private ArrayList<LiteSettlementConfig> allSettlementConfigs = null;
	private Map<Integer, LiteSettlementConfig> allSettlementConfigsMap = null;
	
	private ArrayList<LiteSeasonSchedule> allSeasonSchedules = null;
	private ArrayList<LiteGear> allGears = null;
	private ArrayList <LiteTOUSchedule> allTOUSchedules = null;
	private ArrayList<LiteTOUDay> allTOUDays = null;
	
	private ArrayList<LiteYukonUser> allYukonUsers = null;
	private ArrayList<LiteYukonRole> allYukonRoles = null;
	private ArrayList<LiteYukonRoleProperty> allYukonRoleProperties = null;
	private ArrayList<LiteYukonGroup> allYukonGroups = null;
	
	private Map allYukonUserRolePropertiesMap = null;
	private Map allYukonGroupRolePropertiesMap = null;
	private Map allYukonUserGroupsMap = null;
	private Map allYukonGroupUsersMap = null;
		
	private ArrayList<LiteEnergyCompany> allEnergyCompanies = null;
	
	//lists that are created by the joining/parsing of existing lists
	private ArrayList<LiteYukonPAObject> allUnusedCCDevices = null; //PAO
	private ArrayList<LiteYukonPAObject> allCapControlFeeders = null; //PAO
	private ArrayList<LiteYukonPAObject> allCapControlSubBuses = null; //PAO
    private ArrayList<LiteYukonPAObject> allCapControlSubStations = null; //PAO    
	private ArrayList<LiteYukonPAObject> allDevices = null; //PAO
	private ArrayList<LiteYukonPAObject> allLMPrograms = null; //PAO
	private ArrayList<LiteYukonPAObject> allLMControlAreas = null;	//PAO
	private ArrayList<LiteYukonPAObject> allLMGroups = null;//PAO
	private ArrayList<LiteYukonPAObject> allLoadManagement = null; //PAO
	private ArrayList<LiteYukonPAObject> allPorts = null; //PAO
	private ArrayList<LiteYukonPAObject> allRoutes = null; //PAO
	
	
	//Maps that are created by the joining/parsing of existing lists
	//private HashMap allPointidMultiplierHashMap = null;
	//private Map allPointIDOffsetHashMap = null;
	//private Map allPointsMap = null;
	private Map<Integer, LiteYukonPAObject> allPAOsMap = null;
	private Map<Integer, LiteCustomer> allCustomersMap = null;    
	private Map<Integer, LiteContact> allContactsMap = new HashMap<Integer, LiteContact>();
    
	//derived from allYukonUsers,allYukonRoles,allYukonGroups
	//see type info in IDatabaseCache
	private Map<LiteYukonUser, LiteEnergyCompany> allUserEnergyCompaniesMap = null;
    
	private ArrayList<LiteDeviceTypeCommand> allDeviceTypeCommands = null;
	private ArrayList<LiteCommand> allCommands = null;
	private Map<Integer, LiteCommand> allCommandsMap = null;
	private Map<Integer, LiteStateGroup> allStateGroupMap = null;
	private Map<Integer, LiteYukonUser> allUsersMap = null;
	private Map<Integer, LiteContactNotification> allContactNotifsMap = null;
	
	private Map<Integer, LiteContact> userContactMap = new HashMap<Integer, LiteContact>();
    private Map<MapKeyInts, String> userRolePropertyValueMap = null;
	private Map<MapKeyInts, LiteYukonRole> userRoleMap = null;

    // injected loaders
    private YukonUserLoader yukonUserLoader;

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
 * @param DBChangeLiteListener
 */
public synchronized void addDBChangeLiteListener(DBChangeLiteListener listener) 
{
	getDbChangeListener().addDBChangeLiteListener( listener );
}

public synchronized void addDBChangeListener(DBChangeListener listener) {
    getDbChangeListener().addDBChangeListener(listener);
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
public synchronized List<LiteAlarmCategory> getAllAlarmCategories(){

	if( allAlarmCategories != null )
		return allAlarmCategories;
	else
	{
		allAlarmCategories = new ArrayList<LiteAlarmCategory>();
		AlarmCategoryLoader alarmStateLoader = new AlarmCategoryLoader(allAlarmCategories, databaseAlias);
		alarmStateLoader.run();
		return allAlarmCategories;
	}
}


/**
 * Returns a list of all
 * com.cannontech.database.data.lite.LiteYukonImage
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteYukonImage> getAllYukonImages()
{
   if( allYukonImages != null )
	  return allYukonImages;
   else
   {
	  allYukonImages = new ArrayList<LiteYukonImage>();
	  YukonImageLoader imageLoader = new YukonImageLoader(allYukonImages, databaseAlias);
	  imageLoader.run();
	  return allYukonImages;
   }

}

/**
 *
 */
public synchronized List<LiteYukonPAObject> getAllCapControlFeeders() 
{
	if( allCapControlFeeders == null )
	{
		allCapControlFeeders = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( getAllYukonPAObjects().get(i).getCategory() == PAOGroups.CAT_CAPCONTROL
				 && getAllYukonPAObjects().get(i).getType() == PAOGroups.CAP_CONTROL_FEEDER )
				allCapControlFeeders.add( getAllYukonPAObjects().get(i) );
		}

		allCapControlFeeders.trimToSize();
	}

	return allCapControlFeeders;
}
/**
 *
 */
public synchronized List<LiteYukonPAObject> getAllCapControlSubBuses() 
{
	if( allCapControlSubBuses == null )
	{
		allCapControlSubBuses = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( getAllYukonPAObjects().get(i).getCategory() == PAOGroups.CAT_CAPCONTROL
				 && getAllYukonPAObjects().get(i).getType() == PAOGroups.CAP_CONTROL_SUBBUS )
				allCapControlSubBuses.add( getAllYukonPAObjects().get(i) );
		}

		allCapControlSubBuses.trimToSize();
	}	

	return allCapControlSubBuses;
}

public synchronized List<LiteYukonPAObject> getAllCapControlSubStations() 
{
    if( allCapControlSubStations == null )
    {
        allCapControlSubStations = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

        for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
        {
            if( getAllYukonPAObjects().get(i).getCategory() == PAOGroups.CAT_CAPCONTROL
                 && getAllYukonPAObjects().get(i).getType() == PAOGroups.CAP_CONTROL_SUBSTATION )
                allCapControlSubStations.add( getAllYukonPAObjects().get(i) );
        }

        allCapControlSubStations.trimToSize();
    }   

    return allCapControlSubStations;
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteContact> getAllContacts() {
    if( allContacts != null && allContactsMap != null && allContactNotifsMap != null) {
        return allContacts;
    } else {
        allContacts = new ArrayList<LiteContact>();        
        allContactsMap.clear();
        allContactNotifsMap = new HashMap<Integer, LiteContactNotification>();
        
        ContactLoader contactLoader =
            new ContactLoader(allContacts, allContactsMap, allContactNotifsMap, databaseAlias);
        
        contactLoader.run();		
        return allContacts;
    }
}

/**
 *
 */
public synchronized List<LiteCICustomer> getAllCICustomers() {
    if( allCICustomers == null ) {
        List<LiteCustomer> customerList = getAllCustomers();
        ArrayList<LiteCICustomer> tempAllCICustomers = new ArrayList<LiteCICustomer>( customerList.size());
        
        for( int i = 0; i < customerList.size(); i++ ) {
            LiteCustomer aCustomer = customerList.get(i);
            if( aCustomer instanceof LiteCICustomer && aCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
                tempAllCICustomers.add((LiteCICustomer)aCustomer);
            }
        }
        tempAllCICustomers.trimToSize();
        allCICustomers = tempAllCICustomers;
    }
    
    return allCICustomers;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteDeviceMeterNumber> getAllDeviceMeterGroups()
{
	if( allDeviceMeterGroups != null )
		return allDeviceMeterGroups;
	else
	{
		allDeviceMeterGroups = new ArrayList<LiteDeviceMeterNumber>();
		DeviceMeterGroupLoader deviceMeterGroupLoader = new DeviceMeterGroupLoader (allDeviceMeterGroups, databaseAlias);
		deviceMeterGroupLoader.run();
		return allDeviceMeterGroups;
	}

}

/**
 * getAllDevices method comment.
 *
 */
public synchronized List<LiteYukonPAObject> getAllDevices() 
{
	if( allDevices == null )
	{
		allDevices = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( getAllYukonPAObjects().get(i).getCategory() 
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
public synchronized List<LiteYukonPAObject> getAllMCTs() {
	if (allMCTs == null) {
		allMCTs = new ArrayList<LiteYukonPAObject>( getAllDevices().size() / 2 );
		
		for (int i = 0; i < getAllDevices().size(); i++) {
			if (com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(
					getAllDevices().get(i).getType() ))
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
public synchronized List<LiteGraphDefinition> getAllGraphDefinitions()
{
	if( allGraphDefinitions != null )
		return allGraphDefinitions;
	else
	{
		allGraphDefinitions = new ArrayList<LiteGraphDefinition>();
		GraphDefinitionLoader graphDefinitionLoader = new GraphDefinitionLoader(allGraphDefinitions, databaseAlias);
		graphDefinitionLoader.run();
		return allGraphDefinitions;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteHolidaySchedule> getAllHolidaySchedules()
{

	if (allHolidaySchedules != null)
		return allHolidaySchedules;
	else
	{
		allHolidaySchedules = new ArrayList<LiteHolidaySchedule>();
		HolidayScheduleLoader holidayScheduleLoader = new HolidayScheduleLoader(allHolidaySchedules, databaseAlias);
		holidayScheduleLoader.run();
		return allHolidaySchedules;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteBaseline> getAllBaselines()
{

	if (allBaselines != null)
		return allBaselines;
	else
	{
		allBaselines = new ArrayList<LiteBaseline>();
		BaselineLoader baselineLoader = new BaselineLoader(allBaselines, databaseAlias);
		baselineLoader.run();
		return allBaselines;
	}
}

public synchronized List<LiteSeasonSchedule> getAllSeasonSchedules()
{

	if (allSeasonSchedules != null)
		return allSeasonSchedules;
	else
	{
		allSeasonSchedules = new ArrayList<LiteSeasonSchedule>();
		SeasonScheduleLoader seasonLoader = new SeasonScheduleLoader(allSeasonSchedules, databaseAlias);
		seasonLoader.run();
		return allSeasonSchedules;
	}
}

public synchronized List<LiteTOUSchedule> getAllTOUSchedules()
{

	if (allTOUSchedules != null)
		return allTOUSchedules;
	else
	{
		allTOUSchedules = new ArrayList<LiteTOUSchedule>();
		TOUScheduleLoader touLoader = new TOUScheduleLoader(allTOUSchedules, databaseAlias);
		touLoader.run();
		return allTOUSchedules;
	}
}

public synchronized List<LiteTOUDay> getAllTOUDays()
{

	if (allTOUDays != null)
		return allTOUDays;
	else
	{
		allTOUDays = new ArrayList<LiteTOUDay>();
		TOUDayLoader dayLoader = new TOUDayLoader(allTOUDays, databaseAlias);
		dayLoader.run();
		return allTOUDays;
	}
}

public synchronized List<LiteGear> getAllGears()
{

	if (allGears != null)
		return allGears;
	else
	{
		allGears = new ArrayList<LiteGear>();
		GearLoader gearLoader = new GearLoader(allGears, databaseAlias);
		gearLoader.run();
		return allGears;
	}
}

public synchronized List<LiteCommand> getAllCommands() {
	if (allCommands== null)
	{
		allCommands = new ArrayList<LiteCommand>();
		allCommandsMap = new HashMap<Integer, LiteCommand>();
		CommandLoader commandLoader = new CommandLoader(allCommands, allCommandsMap, databaseAlias);
		commandLoader.run();
	}
	return allCommands;
}

public synchronized java.util.Map<Integer, LiteCommand> getAllCommandsMap()
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

public synchronized List<LiteConfig> getAllConfigs()
{

	if (allConfigs != null)
		return allConfigs;
	else
	{
		allConfigs = new ArrayList<LiteConfig>();
		ConfigLoader configLoader = new ConfigLoader(allConfigs, databaseAlias);
		configLoader.run();
		return allConfigs;
	}
}

public synchronized List<LiteLMConstraint> getAllLMProgramConstraints()
{

	if (allLMProgramConstraints != null)
		return allLMProgramConstraints;
	else
	{
		allLMProgramConstraints = new ArrayList<LiteLMConstraint>();
		LMConstraintLoader lmConstraintsLoader = new LMConstraintLoader(allLMProgramConstraints, databaseAlias);
		lmConstraintsLoader.run();
		return allLMProgramConstraints;
	}
}

public synchronized List<LiteLMProgScenario> getAllLMScenarioProgs()
{

	if( allLMScenarioProgs == null )
	{
		allLMScenarioProgs = new ArrayList<LiteLMProgScenario>();
		LMScenarioProgramLoader ldr = new LMScenarioProgramLoader(allLMScenarioProgs, databaseAlias);
		ldr.run();
	}
	
	return allLMScenarioProgs;
}

public synchronized List<LiteYukonPAObject> getAllLMScenarios()
{

	if( allLMScenarios == null )
	{
		allLMScenarios = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( getAllLoadManagement().get(i).getType() 
				  == PAOGroups.LM_SCENARIO )
			allLMScenarios.add( getAllLoadManagement().get(i) );
		}

		allLMScenarios.trimToSize();		
	}

	return allLMScenarios;
}

public synchronized List<LiteLMPAOExclusion> getAllLMPAOExclusions()
{

	if( allLMPAOExclusions == null )
	{
		allLMPAOExclusions = new ArrayList<LiteLMPAOExclusion>();
		LMPAOExclusionLoader ldr = new LMPAOExclusionLoader(allLMPAOExclusions, databaseAlias);
		ldr.run();
	}
	
	return allLMPAOExclusions;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteYukonPAObject> getAllLMPrograms()
{
	if( allLMPrograms == null )
	{
		//List allDevices = getAllLoadManagement();
		allLMPrograms = new ArrayList<LiteYukonPAObject>( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( getAllLoadManagement().get(i).getType() == PAOGroups.LM_CURTAIL_PROGRAM
				 || getAllLoadManagement().get(i).getType() == PAOGroups.LM_DIRECT_PROGRAM
				 || getAllLoadManagement().get(i).getType() == PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM )
				allLMPrograms.add( getAllLoadManagement().get(i) );				
		}

		allLMPrograms.trimToSize();
	}
	return allLMPrograms;
}

/* (non-Javadoc)
 * @see com.cannontech.yukon.IDatabaseCache#getAllLMControlAreas()
 */
public List<LiteYukonPAObject> getAllLMControlAreas()
{
	if( allLMControlAreas == null )
	{
		allLMControlAreas = new ArrayList<LiteYukonPAObject>( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( getAllLoadManagement().get(i).getType() == PAOGroups.LM_CONTROL_AREA )
				allLMControlAreas.add( getAllLoadManagement().get(i) );				
		}

		allLMControlAreas.trimToSize();
	}
	return allLMControlAreas;
}


/* (non-Javadoc)
 * @see com.cannontech.yukon.IDatabaseCache#getAllLMGroups()
 */
public List<LiteYukonPAObject> getAllLMGroups()
{
    if( allLMGroups == null )
	{
		allLMGroups = new ArrayList<LiteYukonPAObject>( getAllLoadManagement().size() / 2 );

		for( int i = 0; i < getAllLoadManagement().size(); i++ )
		{
			if( DeviceTypesFuncs.isLmGroup( getAllLoadManagement().get(i).getType()) )
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
public synchronized List<LiteYukonPAObject> getAllLoadManagement() 
{
	if( allLoadManagement == null )
	{
		allLoadManagement = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( getAllYukonPAObjects().get(i).getPaoClass() == DeviceClasses.LOADMANAGEMENT ||
					getAllYukonPAObjects().get(i).getPaoClass() == DeviceClasses.GROUP )
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
public synchronized List<LiteNotificationGroup> getAllContactNotificationGroups()
{
	if( allNotificationGroups != null )
		return allNotificationGroups;
	else
	{
		allNotificationGroups = new ArrayList<LiteNotificationGroup>();
		ContactNotificationGroupLoader notifLoader = new ContactNotificationGroupLoader(allNotificationGroups, databaseAlias);
		notifLoader.run();
		
		//allUsedContactNotifications = notifLoader.getAllUsedContactNotifications();
		return allNotificationGroups;
	}
}

public synchronized List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone()
{
    List<LiteNotificationGroup> notifGroup = new ArrayList<LiteNotificationGroup>();
    
    LiteNotificationGroup noneGroup = new LiteNotificationGroup(PointAlarming.NONE_NOTIFICATIONID);
    noneGroup.setNotificationGroupName("(none)");
    notifGroup.add(noneGroup);
    
    notifGroup.addAll(this.getAllContactNotificationGroups());
    
    return notifGroup;
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized List<LitePoint> getAllSystemPoints(){

    if( allSystemPoints != null )
        return allSystemPoints;
    else
    {
        allSystemPoints = new ArrayList<LitePoint>();
        //allPointsMap = new HashMap();
        SystemPointLoader systemPointLoader = new SystemPointLoader(allSystemPoints, databaseAlias);
        systemPointLoader.run();
        return allSystemPoints;
    }
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.Map<Integer, LiteYukonPAObject> getAllPAOsMap()
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
public synchronized java.util.Map<Integer, LiteYukonUser> getAllUsersMap()
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

public synchronized java.util.Map<Integer, LiteContactNotification> getAllContactNotifsMap()
{
	if( allContactNotifsMap != null )
		return allContactNotifsMap;
	else
	{
        releaseAllContacts();
        getAllContacts();

        return allContactNotifsMap;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.Map<Integer, LiteCustomer> getAllCustomersMap()
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


public synchronized List<LitePointLimit> getAllPointLimits() {
	if( allPointLimits != null ) 
		return allPointLimits;
	else
	{
		allPointLimits = new ArrayList<LitePointLimit>();
		PointLimitLoader pointLimitLoader = new PointLimitLoader(allPointLimits, databaseAlias);
		pointLimitLoader.run();
		return allPointLimits;
	}
}
/**
 * getAllPorts method comment.
 *
 */
public synchronized List<LiteYukonPAObject> getAllPorts() 
{
	if( allPorts == null )
	{
		allPorts = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( getAllYukonPAObjects().get(i).getCategory() 
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
public synchronized List<LiteYukonPAObject> getAllRoutes() 
{
	if( allRoutes == null )
	{
		allRoutes = new ArrayList<LiteYukonPAObject>( getAllYukonPAObjects().size() / 2 );

		for( int i = 0; i < getAllYukonPAObjects().size(); i++ )
		{
			if( getAllYukonPAObjects().get(i).getCategory() == PAOGroups.CAT_ROUTE )
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
public synchronized Map<Integer, LiteStateGroup> getAllStateGroupMap()
{
	if( allStateGroupMap == null )
	{
		allStateGroupMap = new HashMap<Integer, LiteStateGroup>();
		StateGroupLoader stateGroupLoader = new StateGroupLoader(allStateGroupMap);
		stateGroupLoader.run();
		return allStateGroupMap;
	}

	return allStateGroupMap;
}

public synchronized List<LiteTag> getAllTags() {
	if(allTags == null)
	{
		allTags = new ArrayList<LiteTag>();
		TagLoader tagLoader = new TagLoader(allTags, databaseAlias);
		tagLoader.run();
		return allTags;	
	}
	return allTags;
}

public synchronized List<LiteSettlementConfig> getAllSettlementConfigs() {
	if(allSettlementConfigs == null)
	{
		allSettlementConfigs = new ArrayList<LiteSettlementConfig>();
		allSettlementConfigsMap = new HashMap<Integer, LiteSettlementConfig>();
		SettlementConfigLoader stlmtCfgLoader = new SettlementConfigLoader(allSettlementConfigs, allSettlementConfigsMap, databaseAlias);
		stlmtCfgLoader.run();
	}
	return allSettlementConfigs;
}

public synchronized java.util.Map<Integer, LiteSettlementConfig> getAllSettlementConfigsMap()
{
	if( allSettlementConfigsMap != null )
		return allSettlementConfigsMap;
	else
	{
		releaseAllSettlementConfigs();
		getAllSettlementConfigs();

		return allSettlementConfigsMap;
	}
}

// This cache is derive from the Device cache
public synchronized List<LiteYukonPAObject> getAllUnusedCCDevices()
{

	 if( allUnusedCCDevices != null )
		  return allUnusedCCDevices;
	 else
	 {
		//temp code
		java.util.Date timerStart = new java.util.Date();
		//temp code

		//add all the CBC and and addressable Devices (RTU, DNP, etc) into these results
		String sqlString =
			"select deviceID from " + DeviceAddress.TABLE_NAME + 
			" union select deviceID from " + DeviceCBC.TABLE_NAME +
			" where deviceID not in " + 
			" (select controldeviceid from " + CapBank.TABLE_NAME + ")";

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		allUnusedCCDevices = new ArrayList<LiteYukonPAObject>(32);
		
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while (rset.next()) {
				
				int paoID = rset.getInt(1);				
				LiteYukonPAObject pao = DaoFactory.getPaoDao().getLiteYukonPAO( paoID );
				
				if( pao != null ) {
					allUnusedCCDevices.add( pao );
				}
			}
				
			if( rset != null ) rset.close();
			
			//ensure is list is sorted by name
			java.util.Collections.sort(allUnusedCCDevices, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator);


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
			SqlUtils.close(rset, stmt, conn );
		}

		return allUnusedCCDevices;
	 }

}


/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized List<LiteYukonPAObject> getAllYukonPAObjects()
{
	if( allYukonPAObjects != null && allPAOsMap != null)
		return allYukonPAObjects;
	else
	{
		allYukonPAObjects = new ArrayList<LiteYukonPAObject>();
		allPAOsMap = new HashMap<Integer, LiteYukonPAObject>();
		YukonPAOLoader yukLoader = new YukonPAOLoader(allYukonPAObjects, allPAOsMap);
		yukLoader.run();
		
		return allYukonPAObjects;
	}
}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonGroups()
	 */
	public synchronized List<LiteYukonGroup> getAllYukonGroups() {		
		if(allYukonGroups == null) {
			allYukonGroups = new ArrayList<LiteYukonGroup>();
			YukonGroupLoader l = new YukonGroupLoader(allYukonGroups, databaseAlias);
			l.run();
		}
		return allYukonGroups;				
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonRoles()
	 */
	public synchronized List<LiteYukonRole> getAllYukonRoles() {		
		if( allYukonRoles == null) {
			allYukonRoles = new ArrayList<LiteYukonRole>();
			YukonRoleLoader l = new YukonRoleLoader(allYukonRoles, databaseAlias);
			l.run();
		}
		return allYukonRoles;				
	}
		
	public synchronized List<LiteYukonRoleProperty> getAllYukonRoleProperties() { 
		if( allYukonRoleProperties == null) {
			allYukonRoleProperties = new ArrayList<LiteYukonRoleProperty>();
			final YukonRolePropertyLoader l = new YukonRolePropertyLoader(allYukonRoleProperties, databaseAlias);
			l.run();
		}
		return allYukonRoleProperties;
	}
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllYukonUsers()
	 */
	public synchronized List<LiteYukonUser> getAllYukonUsers()
	{		
		if( allYukonUsers != null && allUsersMap != null )
			return allYukonUsers;
		else
		{
			allYukonUsers = new ArrayList<LiteYukonUser>();
			allUsersMap = new HashMap<Integer, LiteYukonUser>();
			yukonUserLoader.load(allYukonUsers, allUsersMap);
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
	 * roleMap<LiteYukonRole, Map<LiteYukonRoleProperty,String>>
	 * roleIDMap<Integer,LiteYukonRole>
	 * rolePropertyIDMap<Integer,Pair<LiteYukonRoleProperty,String>>
	 */
	private void addRolesAndPropertiesToLookupMap(final Map roleMap, final Map<Integer, LiteYukonRole> roleIDMap, final Map<Integer, Pair> rolePropertyIDMap) {
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
	private void addRolePropertiesToMap(final Map rolePropertyMap, final Map<Integer, Pair> rolePropertyIDMap) {
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
	public synchronized List<LiteEnergyCompany> getAllEnergyCompanies() {
		if(allEnergyCompanies == null) {
			allEnergyCompanies = new ArrayList<LiteEnergyCompany>();
			EnergyCompanyLoader l = new EnergyCompanyLoader(allEnergyCompanies, databaseAlias);
			l.run();
		}
		return allEnergyCompanies;
	}
	
	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllUserEnergyCompanies()
	 */
	public synchronized Map<LiteYukonUser, LiteEnergyCompany> getAllUserEnergyCompanies() {
		if(allUserEnergyCompaniesMap == null) {
			allUserEnergyCompaniesMap = new HashMap<LiteYukonUser, LiteEnergyCompany>();
			UserEnergyCompanyLoader l = new UserEnergyCompanyLoader(allUserEnergyCompaniesMap, getAllYukonUsers(), getAllEnergyCompanies(), databaseAlias);
			l.run();
		}
		return allUserEnergyCompaniesMap;
	}

	/**
	 * @see com.cannontech.yukon.IDatabaseCache#getAllCustomers()
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<LiteCustomer> getAllCustomers() {
	    if (allCustomers == null)
	    {
	        allCustomers = new ArrayList<LiteCustomer>();
	        allCustomersMap = new HashMap<Integer, LiteCustomer>();
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
	public synchronized List<LiteDeviceTypeCommand> getAllDeviceTypeCommands(){

		if( allDeviceTypeCommands != null )
			return allDeviceTypeCommands;
		else
		{
			allDeviceTypeCommands= new ArrayList<LiteDeviceTypeCommand>();
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
		//cache.setDbChangeListener( new CacheDBChangeListener() );
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
					if( allAlarmCategories.get(i).getAlarmStateID() == id )
					{
						alreadyAdded = true;
						lBase = allAlarmCategories.get(i);
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
					if( allAlarmCategories.get(i).getAlarmStateID() == id )
					{
						allAlarmCategories.get(i).retrieve(databaseAlias);
						lBase = allAlarmCategories.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allAlarmCategories.size();i++)
				{
					if( allAlarmCategories.get(i).getAlarmStateID() == id )
					{
						lBase = allAlarmCategories.remove(i);
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
			   if( allYukonImages.get(i).getImageID() == id )
			   {
				  alreadyAdded = true;
				  lBase = allYukonImages.get(i);
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
			   if( allYukonImages.get(i).getImageID() == id )
			   {
				  allYukonImages.get(i).retrieve(databaseAlias);
				  lBase = allYukonImages.get(i);
				  break;
			   }
			}
			break;
	  case DBChangeMsg.CHANGE_TYPE_DELETE:
			for(int i=0;i<allYukonImages.size();i++)
			{
			   if( allYukonImages.get(i).getImageID() == id )
			   {
				  lBase = allYukonImages.remove(i);
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
    
    switch(changeType)
    {
    case DBChangeMsg.CHANGE_TYPE_ADD:
    {
        if ( id == DBChangeMsg.CHANGE_INVALID_ID )
            break;
        
        LiteContact lc = getAContactByContactID(id);
        lBase = lc;
        
        break;
    }
    case DBChangeMsg.CHANGE_TYPE_UPDATE:
    {
        allContactsMap.remove(id);
        LiteContact lc = getAContactByContactID(id);
        lBase = lc;
        
        //better wipe the user to contact mappings in case a contact changed that was mapped
        //releaseUserContactMap();
        
        break;
    }
    case DBChangeMsg.CHANGE_TYPE_DELETE:
        //special case for this handler!!!!
        if ( id == DBChangeMsg.CHANGE_INVALID_ID )
        {
            releaseAllContacts();
            break;
        }		
        
        allContactsMap.remove( new Integer(id) );
        if (allContacts != null) {
            for (int i=0;i<allContacts.size();i++)
            {
                if ( allContacts.get(i).getLiteID() == id )
                {
                    lBase = allContacts.remove(i);
                    break;
                }
            }
        }
        
        //better wipe the user to contact mapping, in case one of the contacts from the map was deleted
        releaseUserContactMap();
        
        break;
        
    default:
        releaseAllContacts();
        releaseUserContactMap();
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
		//allGraphTaggedPoints = null;
		//allPointsUnits = null;
		//allPointidMultiplierHashMap = null;
		//allPointIDOffsetHashMap = null;
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
            allCapControlSubStations = null;
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
	    
	    releaseAllCustomers();
	    
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
	    // Do nothing - no cache for device configs
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
	}
	else if ( database == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB )
	{
		if ( dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CUSTOMER_ACCOUNT) ) {
			//allContacts = null;
		}
		retLBase = null;
	}
	else if ( database == DBChangeMsg.CHANGE_SETTLEMENT_DB)
	{
		retLBase = handleSettlementConfigChange(dbType, id);
	}
	else if( database == DBChangeMsg.CHANGE_SERVICE_COMPANY_DB ||
			database == DBChangeMsg.CHANGE_SERVICE_COMPANY_DESIGNATION_CODE_DB )
	{
		//Do nothing, there is no cache for service Companies, but please do not release all cache!
	}
    else if ( database == DBChangeMsg.CHANGE_CBC_STRATEGY_DB)
    {
        
        //Do nothing for now...
    }
    else if ( database == DBChangeMsg.CHANGE_CBC_ADDINFO_DB)
    {
        //Do nothing for now...
    }else if ( database == DBChangeMsg.CHANGE_PAO_SCHEDULE_DB) {
        //Do nothing we don't care.
    }else if( database == DBChangeMsg.CHANGE_WORK_ORDER_DB ) {
		//Do nothing, there is no default cache for workOrders/serviceRequests, but please do not release all cache!
	}else {
	    //BAD IDEA to let it all go, lets just tell everyone it wasn't handled instead!
	    CTILogger.error(" ***** Unhandled DBChangeMessage!  Category: " + dbCategory);
	}

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

    if( id == 0) {    //A force reload of all devicemetergroups was sent.
        releaseAllDeviceMeterGroups();
        return lBase;
    }

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( allDeviceMeterGroups.get(i).getDeviceID() == id )
					{
						alreadyAdded = true;
						lBase = allDeviceMeterGroups.get(i);
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
					if( allDeviceMeterGroups.get(i).getDeviceID() == id )
					{
						allDeviceMeterGroups.get(i).retrieve(databaseAlias);
						lBase = allDeviceMeterGroups.get(i);
						
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allDeviceMeterGroups.size();i++)
				{
					if( allDeviceMeterGroups.get(i).getDeviceID() == id )
					{
						lBase = allDeviceMeterGroups.remove(i);
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
					if( allGraphDefinitions.get(i).getGraphDefinitionID() == id )
					{
						alreadyAdded = true;
						lBase = allGraphDefinitions.get(i);
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
					if( allGraphDefinitions.get(i).getGraphDefinitionID() == id )
					{
						allGraphDefinitions.get(i).retrieve(databaseAlias);
						lBase = allGraphDefinitions.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allGraphDefinitions.size();i++)
				{
					if( allGraphDefinitions.get(i).getGraphDefinitionID() == id )
					{
						lBase = allGraphDefinitions.remove(i);
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
					if( allHolidaySchedules.get(i).getHolidayScheduleID() == id )
					{
						alreadyAdded = true;
						lBase = allHolidaySchedules.get(i);
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
					if( allHolidaySchedules.get(i).getHolidayScheduleID() == id )
					{
						allHolidaySchedules.get(i).retrieve(databaseAlias);
						lBase = allHolidaySchedules.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allHolidaySchedules.size();i++)
				{
					if( allHolidaySchedules.get(i).getHolidayScheduleID() == id )
					{
						lBase = allHolidaySchedules.remove(i);
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
					if( allDeviceTypeCommands.get(i).getDeviceCommandID() == id )
					{
						alreadyAdded = true;
						lBase = allDeviceTypeCommands.get(i);
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
					if( allDeviceTypeCommands.get(i).getDeviceCommandID() == id )
					{
						allDeviceTypeCommands.get(i).retrieve(databaseAlias);
						lBase = allDeviceTypeCommands.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allDeviceTypeCommands.size();i++)
				{
					if( allDeviceTypeCommands.get(i).getDeviceCommandID() == id )
					{
						lBase = allDeviceTypeCommands.remove(i);
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
					if( allBaselines.get(i).getBaselineID() == id )
					{
						alreadyAdded = true;
						lBase = allBaselines.get(i);
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
					if( allBaselines.get(i).getBaselineID() == id )
					{
						allBaselines.get(i).retrieve(databaseAlias);
						lBase = allBaselines.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allBaselines.size();i++)
				{
					if( allBaselines.get(i).getBaselineID() == id )
					{
						lBase = allBaselines.remove(i);
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
					if( allSeasonSchedules.get(i).getScheduleID() == id )
					{
						alreadyAdded = true;
						lBase = allSeasonSchedules.get(i);
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
					if( allSeasonSchedules.get(i).getScheduleID() == id )
					{
						allSeasonSchedules.get(i).retrieve(databaseAlias);
						lBase = allSeasonSchedules.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allSeasonSchedules.size();i++)
				{
					if( allSeasonSchedules.get(i).getScheduleID() == id )
					{
						lBase = allSeasonSchedules.remove(i);
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
					if( allTOUSchedules.get(i).getScheduleID() == id )
					{
						alreadyAdded = true;
						lBase = allTOUSchedules.get(i);
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
					if( allTOUSchedules.get(i).getScheduleID() == id )
					{
						allTOUSchedules.get(i).retrieve(databaseAlias);
						lBase = allTOUSchedules.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allTOUSchedules.size();i++)
				{
					if( allTOUSchedules.get(i).getScheduleID() == id )
					{
						lBase = allTOUSchedules.remove(i);
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
		
				lBase = allCommandsMap.get( new Integer(id) );				
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
		
				LiteCommand lc = allCommandsMap.get( new Integer(id) );				
				lc.retrieve( databaseAlias );
				
				lBase = lc;
				break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:

				for(int i=0;i<allCommands.size();i++)
				{
					if( allCommands.get(i).getCommandID() == id )
					{
						allCommandsMap.remove( new Integer(id) );
						lBase = allCommands.remove(i);
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


private synchronized LiteBase handleDeviceConfigCategoryChange( int changeType, int id )
{
    return new LiteDeviceConfigurationCategory(id, "Device Configuation Category");
}

private synchronized LiteBase handleDeviceConfigChange( int changeType, int id )
{
    
    LiteDeviceConfiguration configuration = new LiteDeviceConfiguration(id, "Device Configuation");
    
    switch (changeType) {
        case DBChangeMsg.CHANGE_TYPE_ADD:
            return LiteFactory.createLite(LiteFactory.convertLiteToDBPersAndRetrieve(configuration));
        default:
            return new LiteDeviceConfiguration(id, "Device Configuation");
        }
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
					if( allConfigs.get(i).getConfigID() == id )
					{
						alreadyAdded = true;
						lBase = allConfigs.get(i);
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
					if( allConfigs.get(i).getConfigID() == id )
					{
						allConfigs.get(i).retrieve(databaseAlias);
						lBase = allConfigs.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allConfigs.size();i++)
				{
					if( allConfigs.get(i).getConfigID() == id )
					{
						lBase = allConfigs.remove(i);
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
					if( allTags.get(i).getTagID() == id )
					{
						alreadyAdded = true;
						lTag = allTags.get(i);
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
					if( allTags.get(i).getTagID() == id )
					{
						allTags.get(i).retrieve(databaseAlias);
						lTag = allTags.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allTags.size();i++)
				{
					if( allTags.get(i).getTagID() == id )
					{
						lTag = allTags.remove(i);
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
private synchronized LiteBase handleSettlementConfigChange( int changeType, int id)
{
	LiteBase lBase = null;

	// if the storage is not already loaded, we must not care about it
	if( allSettlementConfigs == null )
		return lBase;

	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:

			lBase = allSettlementConfigsMap.get( new Integer(id));
			if( lBase == null)
			{
				LiteSettlementConfig lsc = new LiteSettlementConfig(id);
				lsc.retrieve(databaseAlias);
				allSettlementConfigs.add(lsc);
				allSettlementConfigsMap.put( new Integer(lsc.getConfigID()), lsc);
				
				lBase = lsc;
			}
			break;
			
		case DBChangeMsg.CHANGE_TYPE_UPDATE:
			
			LiteSettlementConfig lsc = allSettlementConfigsMap.get(new Integer(id));
			lsc.retrieve(databaseAlias);
			lBase = lsc;
			break;
			
		case DBChangeMsg.CHANGE_TYPE_DELETE:
			for(int i=0;i<allSettlementConfigs.size();i++)
			{
				if( allSettlementConfigs.get(i).getConfigID() == id )
				{
					allSettlementConfigsMap.remove( new Integer(id));
					lBase = allSettlementConfigs.remove(i);
					break;
				}
			}
			break;
		default:
				releaseAllSettlementConfigs();
				break;
	}

	return lBase;
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
					if( allLMProgramConstraints.get(i).getConstraintID() == id )
					{
						alreadyAdded = true;
						lBase = allLMProgramConstraints.get(i);
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
					if( allLMProgramConstraints.get(i).getConstraintID() == id )
					{
						allLMProgramConstraints.get(i).retrieve(databaseAlias);
						lBase = allLMProgramConstraints.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allLMProgramConstraints.size();i++)
				{
					if( allLMProgramConstraints.get(i).getConstraintID() == id )
					{
						lBase = allLMProgramConstraints.remove(i);
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
					if( allNotificationGroups.get(i).getNotificationGroupID() == id )
					{
						alreadyAdded = true;
						lBase = allNotificationGroups.get(i);
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
					if( allNotificationGroups.get(i).getNotificationGroupID() == id )
					{
						allNotificationGroups.get(i).retrieve(databaseAlias);
						lBase = allNotificationGroups.get(i);
						break;
					}
				}
				break;
		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allNotificationGroups.size();i++)
				{
					if( allNotificationGroups.get(i).getNotificationGroupID() == id )
					{
						lBase = allNotificationGroups.remove(i);
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
private synchronized LiteBase handlePointChange( int changeType, int id )
{
	LiteBase lBase = null;
	
	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:
		    // Return this so clients like the editor get a db change :(
            lBase = DaoFactory.getPointDao().getLitePoint(id);
            break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
            lBase = DaoFactory.getPointDao().getLitePoint(id);
            break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
            break;

		default:
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
			lBase = allStateGroupMap.get( new Integer(id) );				
			if( lBase == null )
			{	
				LiteStateGroup lsg = new LiteStateGroup(id);
				lsg.retrieve(databaseAlias);
				allStateGroupMap.put( new Integer(lsg.getStateGroupID()), lsg );
				lBase = lsg;
			}
			break;

		case DBChangeMsg.CHANGE_TYPE_UPDATE:
			LiteStateGroup ly = allStateGroupMap.get( new Integer(id) );				
			ly.retrieve( databaseAlias );
					
			lBase = ly;
			break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
			lBase = allStateGroupMap.remove( new Integer(id) );
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

			lBase = allCustomersMap.get( new Integer(id));
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
			
			LiteCustomer lc = allCustomersMap.get(new Integer(id));
			lc.retrieve(databaseAlias);
			lBase = lc;
			break;
			
		case DBChangeMsg.CHANGE_TYPE_DELETE:
			for(int i=0;i<allCustomers.size();i++)
			{
				if( allCustomers.get(i).getCustomerID() == id )
				{
					allCustomersMap.remove( new Integer(id));
					lBase = allCustomers.remove(i);
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
    if ( allYukonGroups != null ) {

        switch(changeType)
        {
        case DBChangeMsg.CHANGE_TYPE_ADD:
            for(int i=0;i<allYukonGroups.size();i++)
            {
                if( allYukonGroups.get(i).getGroupID() == id )
                {
                    alreadyAdded = true;
                    lBase = allYukonGroups.get(i);
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
                if( allYukonGroups.get(i).getGroupID() == id )
                {
                    allYukonGroups.get(i).retrieve(databaseAlias);
                    lBase = allYukonGroups.get(i);
                    break;
                }
            }
            break;

        case DBChangeMsg.CHANGE_TYPE_DELETE:
            for(int i=0;i<allYukonGroups.size();i++)
            {
                if( allYukonGroups.get(i).getGroupID() == id )
                {
                    lBase = allYukonGroups.remove(i);
                    break;
                }
            }
            break;
        default:
            allYukonGroups = null;
            break;
        }
    }

    releaseUserRoleMap();
    releaseUserRolePropertyValueMap();

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
			lBase = allUsersMap.get( new Integer(id) );				
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
			LiteYukonUser lu = allUsersMap.get( new Integer(id) );				
			lu.retrieve( databaseAlias );
						
			lBase = lu;
            adjustUserMappings(id);           
			break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
			for(int i=0;i<allYukonUsers.size();i++)
			{
				if( allYukonUsers.get(i).getUserID() == id )
				{
					allUsersMap.remove( new Integer(id) );
					lBase = allYukonUsers.remove(i);
					break;
				}
			}
            adjustUserMappings(id); 
			break;

		default:
			releaseAllYukonUsers();
            releaseUserRoleMap();
            releaseUserRolePropertyValueMap();
            releaseUserContactMap();
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
		
    if( id == 0) {    //A force reload of all paobjects was sent.
        releaseAllYukonPAObjects();
        return lBase;
    }   
    
	switch(changeType)
	{
		case DBChangeMsg.CHANGE_TYPE_ADD:

			lBase = allPAOsMap.get( new Integer(id) );				
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

			LiteYukonPAObject ly = allPAOsMap.get( new Integer(id) );				
			ly.retrieve( databaseAlias );
					
			lBase = ly;
			break;

		case DBChangeMsg.CHANGE_TYPE_DELETE:
				for(int i=0;i<allYukonPAObjects.size();i++)
				{
					if( allYukonPAObjects.get(i).getYukonID() == id )
					{
						allPAOsMap.remove( new Integer(id) );
						lBase = allYukonPAObjects.remove(i);
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
    allSystemPoints = null;
	allStateGroupMap = null;
	allNotificationGroups = null;
	allContactNotifsMap = null;
    
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
	allPointLimits = null;
	allYukonImages = null;
	allCICustomers = null;
	allCustomers = null;
	allLMProgramConstraints = null;
	allLMScenarios = null;
	allLMScenarioProgs = null;

	allTags = null;
	allSettlementConfigs = null;
	allSettlementConfigsMap = null;
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
	//allGraphTaggedPoints = null; //Points
	allUnusedCCDevices = null; //PAO
	allCapControlFeeders = null; //PAO
	allCapControlSubBuses = null; //PAO   
    allCapControlSubStations = null; //PAO
	allDevices = null; //PAO
	allLMPrograms = null; //PAO
	allLMControlAreas = null; //PAO
	allLMGroups = null;	//PAO
	allLoadManagement = null; //PAO
	allPorts = null; //PAO
	allRoutes = null; //PAO
    
    
	//Maps that are created by the joining/parsing of existing lists
	allPAOsMap = null;
	allCustomersMap = null;
	allContactsMap.clear();
	allUsersMap = null;


	//derived from allYukonUsers,allYukonRoles,allYukonGroups
	//see type info in IDatabaseCache
	allUserEnergyCompaniesMap = null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllContacts()
{
	allContacts = null;
	allContactsMap.clear();
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

public synchronized void releaseAllSettlementConfigs()
{
	allSettlementConfigs = null;
	allSettlementConfigsMap = null;
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

public synchronized void releaseAllCustomers()
{
	allCustomers = null;
	allCustomersMap = null;
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllSystemPoints()
{
    allSystemPoints = null;
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllYukonUsers(){
	allYukonUsers = null;
	allUsersMap = null;
	releaseUserRoleMap();
	releaseUserRolePropertyValueMap();
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllYukonGroups(){
	allYukonGroups = null;
	releaseUserRoleMap();
	releaseUserRolePropertyValueMap();
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
 * @param DBChangeLiteListener
 */
public synchronized void removeDBChangeLiteListener(DBChangeLiteListener listener) 
{
	if( getDbChangeListener() != null )
		getDbChangeListener().removeDBChangeLiteListener( listener );

}

public synchronized void removeDBChangeListener(DBChangeListener listener) {
    if( getDbChangeListener() != null )
        getDbChangeListener().removeDBChangeListener( listener );        
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
public void setDbChangeListener(CacheDBChangeListener newDbChangeListener) {
	dbChangeListener = newDbChangeListener;
}
	
/* (non-Javadoc)
 * This method takes a userid and a roleid.  It checks userRoleMap to see
 * if this role has been recovered from the db before.  If it has not, it will
 * be taken directly from the database.  
 */
public synchronized LiteYukonRole getARole(LiteYukonUser user, int roleID) 
{
	MapKeyInts keyInts = new MapKeyInts(user.getLiteID(), roleID);
	LiteYukonRole specifiedRole = null;
	//check cache for previous grabs
	if(userRoleMap == null)
		userRoleMap = new HashMap<MapKeyInts, LiteYukonRole>();
	else
		specifiedRole = userRoleMap.get(keyInts);
	
	//not in cache, go to DB.
	if(specifiedRole == null && !userRoleMap.containsKey(keyInts))
	{
		specifiedRole = YukonUserRolePropertyLookup.loadSpecificRole(user, roleID);
		/*found it, put it in the cache for later searches
		 * Go ahead and put in null values, too.  This will make it faster to check if this
		 * role exists for this user next time around.
		 */
		userRoleMap.put(keyInts, specifiedRole);
        /*
         * This is useful for checking the map after a DBChangeMsg is received to see if
         * this user exists in the map.  If it does, then the map should be reset.
         */
        userRoleMap.put(new MapKeyInts(user.getLiteID(), CtiUtilities.NONE_ZERO_ID), null);
	}	
		
	return specifiedRole;
}

/*This method takes a userid and a rolepropertyid.  It checks userRolePropertyValueMap
 *  to see if this role has been recovered from the db before.  If it has not, it will
 * be taken directly from the database.
 */
public synchronized String getARolePropertyValue(LiteYukonUser user, int rolePropertyID) 
{
	MapKeyInts keyInts = new MapKeyInts(user.getLiteID(), rolePropertyID);
	String specifiedPropVal = null;
	//check cache for previous grabs
	if(userRolePropertyValueMap == null)
		userRolePropertyValueMap = new HashMap<MapKeyInts, String>();
	else
		specifiedPropVal = userRolePropertyValueMap.get(keyInts);
	
	//not in cache, go to DB.
	if(specifiedPropVal == null)
	{
		specifiedPropVal = YukonUserRolePropertyLookup.loadSpecificRoleProperty(user, rolePropertyID);
		//found it, put it in the cache for later searches
		userRolePropertyValueMap.put(keyInts, specifiedPropVal);
        /*
         * This is useful for checking the map after a DBChangeMsg is received to see if
         * this user exists in the map.  If it does, then the map should be reset.
         */
        userRolePropertyValueMap.put(new MapKeyInts(user.getLiteID(), CtiUtilities.NONE_ZERO_ID), null);
	}
		
	return specifiedPropVal;
}

/*This method takes a userid to look for the relevant contact. It checks userContactMap
 *  to see if this contact has been recovered from the db before.  If it has not, it will
 * be taken directly from the database.
 */
public synchronized LiteContact getAContactByUserID(int userID) 
{
    LiteContact specifiedContact = null;
    //check cache for previous grabs
    specifiedContact = userContactMap.get(userID);
    
    //not in cache, go to DB.
    if(specifiedContact == null)
    {
        specifiedContact = YukonUserContactLookup.loadSpecificUserContact(userID);
        //found it, put it in the cache for later searches
        if (specifiedContact != null) {
            userContactMap.put(userID, specifiedContact);
            allContactsMap.put(specifiedContact.getContactID(), specifiedContact);
        }
    }
    
    return specifiedContact;
}

public synchronized LiteContact getAContactByContactID(int contactID) 
{
    //check cache for previous grabs
    LiteContact specifiedContact = allContactsMap.get(contactID);
    
    //not in cache, go to DB.
    if(specifiedContact == null)
    {
        specifiedContact = YukonUserContactLookup.loadSpecificContact(contactID);

        //found it, put it in the cache for later searches
        if (specifiedContact != null) {
            getAllContactsList().add(specifiedContact);
            allContactsMap.put(contactID, specifiedContact);  
            userContactMap.put(specifiedContact.getLoginID(), specifiedContact);
        }
    }
    
    return specifiedContact;
}

public synchronized List<LiteContact> getAllContactsList() {

    if (allContacts == null) {
        allContacts = new ArrayList<LiteContact>();
    }

    return allContacts;
}

public synchronized LiteContact[] getContactsByLastName(String lastName, boolean partialMatch) 
{
    return YukonUserContactLookup.loadContactsByLastName(lastName, partialMatch);
}

public synchronized LiteContact[] getContactsByFirstName(String firstName, boolean partialMatch) 
{
    return YukonUserContactLookup.loadContactsByFirstName(firstName, partialMatch);
}

public synchronized LiteContact[] getContactsByPhoneNumber(String phone, boolean partialMatch) 
{
    return YukonUserContactLookup.loadContactsByPhoneNumber(phone, partialMatch);
}
        
public synchronized LiteContact getContactsByEmail(String email) 
{
    return YukonUserContactLookup.loadContactsByEmail(email);
}

public synchronized LiteContactNotification getAContactNotifByNotifID(int contNotifyID) 
{
    //check cache for previous grabs
    if(allContactsMap == null || allContactNotifsMap == null) 
    {
		getAllContacts();
	} 
    
    LiteContactNotification specifiedNotify = allContactNotifsMap.get(new Integer(contNotifyID));
	
    //not in cache, go to DB.
    if(specifiedNotify == null)
    {
        specifiedNotify = YukonUserContactNotificationLookup.loadSpecificContactNotificationByID(contNotifyID);
        //found it, put it in the cache for later searches
        allContactNotifsMap.put(new Integer(contNotifyID), specifiedNotify);
        //make sure the contact is also loaded
        getAContactByContactID(specifiedNotify.getContactID());
    }
    
    return specifiedNotify;
}

/*This method takes a contactid to look for the relevant customer. It will
 * be taken directly from the database.
 */
public synchronized LiteCustomer getACustomerByContactID(int contactID) 
{
    return YukonCustomerLookup.loadSpecificCustomerByContactID(contactID);
}

/*This method takes a customerid to look for the relevant customer. It checks allCustomersMap
 *  to see if this customer has been recovered from the db before.  If it has not, it will
 * be taken directly from the database.
 */
public synchronized LiteCustomer getACustomerByCustomerID(int customerID) 
{
    LiteCustomer specifiedCustomer = null;
    //check cache for previous grabs
    if(allCustomersMap == null)
        allCustomersMap = new HashMap<Integer, LiteCustomer>();
    else
        specifiedCustomer = allCustomersMap.get(new Integer(customerID));
    
    //not in cache, go to DB.
    if(specifiedCustomer == null)
    {
        specifiedCustomer = YukonCustomerLookup.loadSpecificCustomer(customerID);
        //found it, put it in the cache for later searches
        allCustomersMap.put(new Integer(customerID), specifiedCustomer);
    }
    
    return specifiedCustomer;
}

/* (non-Javadoc)
 * Scrub out the userRoleMap.  Any LiteYukonRoles that were in here will have to be
 *recovered from the database.
 */
public synchronized void releaseUserRoleMap() 
{
	userRoleMap = null;
}

/* (non-Javadoc)
 * Scrub out the userRolePropertyValueMap.  Any String values that were in here will have to be
 *recovered from the database.
 */
public synchronized void releaseUserRolePropertyValueMap() 
{
	userRolePropertyValueMap = null;
}

public synchronized void releaseUserContactMap()
{
    userContactMap.clear();
}

/*
 * Upon receiving a DBChangeMsg for a user or a group, this method
 * checks to see if this user is in the map.  There is no point in
 * resetting these mappings if the user that was changed is not a one
 * that has been accessed before (and therefore mapped here).
 */
public synchronized void adjustUserMappings(int userID) 
{
    MapKeyInts keyInts = new MapKeyInts(userID, CtiUtilities.NONE_ZERO_ID);
   
    if (userRoleMap != null) {    
        if (userRoleMap.containsKey(keyInts)) {
            releaseUserRoleMap();
        }
    }
    
    if (userRolePropertyValueMap != null) {
        if (userRolePropertyValueMap.containsKey(keyInts)) {
            releaseUserRolePropertyValueMap();
        }
    }
    
    if (userContactMap.containsKey(userID)) {
        releaseUserContactMap();
    }
    
    return;
}

public synchronized List getDevicesByCommPort(int portId) 
{
    
    return DeviceCommPortLoader.getDevicesByCommPort(portId);
    
}

public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
    return DeviceCommPortLoader.getDevicesByDeviceAddress(masterAddress, slaveAddress);
    
}

@Required
public void setYukonUserLoader(YukonUserLoader yukonUserLoader) {
    this.yukonUserLoader = yukonUserLoader;
}
}