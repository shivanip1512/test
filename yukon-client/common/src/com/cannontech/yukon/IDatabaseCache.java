package com.cannontech.yukon;

import java.util.Map;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Interface to Yukon data.
 *
 * @author alauinger
 */
public interface IDatabaseCache
{
	public void addDBChangeListener(DBChangeListener listener);
	public DBChangeMsg[] createDBChangeMessages( com.cannontech.database.db.CTIDbChange newItem, int changeType);
	public java.util.List getAllAlarmCategories();
	public java.util.List getAllYukonImages();
	public java.util.List getAllCapControlFeeders();
	public java.util.List getAllCapControlSubBuses();

	public java.util.List getAllContactNotificationGroups();
	public java.util.List getAllContacts();

	public java.util.List getAllCICustomers();
	public java.util.List getAllCustomers();
	//	Map<Integer(custID), LiteCustomer>
	public java.util.Map getAllCustomersMap();

	public java.util.List getAllDeviceMeterGroups();
	public java.util.List getAllDevices();
	public java.util.List getAllMCTs();
	public java.util.List getAllGraphDefinitions();

	// This cache is derive from the point cache
	//public java.util.List getAllGraphTaggedPoints();

	public java.util.List getAllHolidaySchedules();
	public java.util.List getAllBaselines();
	public java.util.List getAllConfigs();
	public java.util.List getAllLMProgramConstraints();
	public java.util.List getAllLMScenarios();
	public java.util.List getAllLMPrograms();
	public java.util.List getAllGears();
	public java.util.List getAllLoadManagement();
	public java.util.List getAllLMScenarioProgs();
	public java.util.List getAllLMPAOExclusions();
	public java.util.List getAllSeasonSchedules();
	public java.util.List getAllTOUSchedules();
	public java.util.List getAllTOUDays();

	public java.util.List getAllPoints();
	//	Map<Integer(ptID), LitePoint>
	public java.util.Map getAllPointsMap();

	public java.util.List getAllPointsUnits();
	public java.util.List getAllPointLimits();
	public java.util.HashMap getAllPointidMultiplierHashMap();
	//Map<Integer,Integer>
	public java.util.Map getAllPointIDOffsetMap();
	public java.util.List getAllPorts();
	public java.util.List getAllRoutes();
	
	//Map<Integer(stateGroupID), LiteStateGroup>
	public java.util.Map getAllStateGroupMap();

	public java.util.List getAllTags();
	public java.util.List getAllUnitMeasures();

	public java.util.List getAllYukonUsers();
	public java.util.List getAllYukonGroups();
	public java.util.List getAllYukonRoles();
	public java.util.List getAllYukonRoleProperties();

	//Map< LiteYukonUser, int[]<paoIDs> > 
	public Map getYukonUserPaoOwners();

	//Map<LiteYukonUser,List<LiteYukonGroup>> 
	public java.util.Map getYukonUserGroupMap();

	//Map<LiteYukonGroup,List<LiteYukonUser>>
	public java.util.Map getYukonGroupUserMap();

	//Map<LiteYukonUser, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
	public java.util.Map getYukonUserRolePropertyMap();

	//Map<LiteYukonUser,List<Pair<LiteYukonRole,String(value)>>> 
	//public java.util.Map getAllYukonUserRoleMap();

	//Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
	public java.util.Map getYukonGroupRolePropertyMap();

	//Map<LiteYukonUser,List<Pair<LiteYukonRole,String(value)>>>
	//public java.util.Map getAllYukonGroupRoleMap();	

	//Map<LiteYukonUser, Map<Integer(rolepropertyid), Pair<LiteYukonRoleProperty, String(value)>>>
	//Provided as a means to efficiently obtain a roleproperty and its value
	public java.util.Map getYukonUserRolePropertyIDLookupMap();

	//Map<LiteYukonUser, Map<Integer(roleid), LiteYukonRole>>
	//Provided as a means to efficiently obtain a role
	public java.util.Map getYukonUserRoleIDLookupMap();

	//Map<LiteYukonUser,Map<Integer(roleid),Pair<LiteYukonRole,String(value)>>
	//Provided as a means to efficiently obtain a role and its value
	//   public java.util.Map getAllYukonUserRoleIDLookupMap(); 

	public java.util.List getAllEnergyCompanies();
	public java.util.Map getAllUserEnergyCompanies();

	// This cache is derive from the Device cache
	public java.util.List getAllUnusedCCDevices();

	public java.util.List getAllYukonPAObjects();

	//Map<Integer(paoID), LiteYukonPAObject>
	public java.util.Map getAllPAOsMap();

	public java.util.List getAllDeviceTypeCommands();
	public java.util.List getAllCommands();
	//Map<Integer(commandID), LiteCommand>
	public java.util.Map getAllCommandsMap();
	
	//Map<Integer(contactID), LiteContact>
	public java.util.Map getAllContactsMap();

	/**
	 *  Returns the LiteBase object that was added,deleted or updated, 
	 *    else null is returned.
	 */
	public LiteBase handleDBChangeMessage(
		com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg);

	public void releaseAllCache();
	public void releaseAllAlarmCategories();
	public void releaseAllContacts();
	public void releaseAllCustomers();
	public void releaseAllDeviceMeterGroups();
	public void releaseAllYukonImages();
	public void releaseAllGraphDefinitions();
	public void releaseAllHolidaySchedules();
	public void releaseAllBaselines();
	public void releaseAllConfigs();
	public void releaseAllLMProgramConstraints();
	public void releaseAllLMScenarios();
	public void releaseAllLMPAOExclusions();
	public void releaseAllTags();
	public void releaseAllSeasonSchedules();
	public void releaseAllTOUSchedules();
	public void releaseAllTOUDays();
	public void releaseAllNotificationGroups();
	public void releaseAllContactNotifications();
	public void releaseAllPoints();
	public void releaseAllStateGroups();
	public void releaseAllUnitMeasures();
	public void releaseAllYukonPAObjects();
	public void releaseAllYukonUsers();
	public void releaseAllDeviceTypeCommands();
	public void releaseAllCommands();

	public void removeDBChangeListener(DBChangeListener listener);
	public void setDatabaseAlias(String newAlias);

}
