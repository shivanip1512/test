package com.cannontech.yukon;

import java.util.List;
import java.util.Map;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Interface to Yukon data.
 *
 * @author alauinger
 */
public interface IDatabaseCache
{
	public void addDBChangeLiteListener(DBChangeLiteListener listener);
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

	public java.util.List getAllDeviceMeterGroups();
	public java.util.List getAllDMG_CollectionGroups();	//distinct DeviceMeterGroup.CollectionGroups
	public java.util.List getAllDMG_AlternateGroups();	//distinct DeviceMeterGroup.AlternateGroups
	public java.util.List getAllDMG_BillingGroups();	//distinct DeviceMeterGroup.BillingGroups
	public java.util.List getAllDevices();
	public java.util.List getAllMCTs();
	public java.util.List getAllGraphDefinitions();

	public java.util.List getAllHolidaySchedules();
	public java.util.List getAllBaselines();
	public java.util.List getAllConfigs();
	public java.util.List getAllLMProgramConstraints();
	public java.util.List getAllLMScenarios();
	public java.util.List getAllLMPrograms();
	public java.util.List getAllLMControlAreas();
	public java.util.List getAllLMGroups();
	public java.util.List getAllGears();
	public java.util.List getAllLoadManagement();
	public java.util.List getAllLMScenarioProgs();
	public java.util.List getAllLMPAOExclusions();
	public java.util.List getAllSeasonSchedules();
	public java.util.List getAllTOUSchedules();
	public java.util.List getAllTOUDays();

	public java.util.List getAllSystemPoints();
		
	//Map<Integer(contactNotifID), LiteContactNotification>
	public java.util.Map getAllContactNotifsMap();

	public java.util.List getAllPointsUnits();
	public java.util.List getAllPointLimits();

	public java.util.List getAllPorts();
	public java.util.List getAllRoutes();
	
	//Map<Integer(stateGroupID), LiteStateGroup>
	public java.util.Map getAllStateGroupMap();
	
	public java.util.List getAllSettlementConfigs();
	//Map<Integer(configID), LiteSettlementConfig>
	public java.util.Map getAllSettlementConfigsMap();

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
	//public java.util.Map getYukonUserRolePropertyIDLookupMap(int userid);

	//Map<LiteYukonUser, Map<Integer(roleid), LiteYukonRole>>
	//Provided as a means to efficiently obtain a role
	//public java.util.Map getYukonUserRoleIDLookupMap(int userid);

	//Map<Integer(custID), LiteCustomer>
	public java.util.Map getAllCustomersMap();
	
	//Map<Integer(userID), LiteYukonUser>
	public java.util.Map getAllUsersMap();

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
	
	public String getARolePropertyValue(LiteYukonUser user, int rolePropertyID);
	public LiteYukonRole getARole(LiteYukonUser user, int roleID);
    
    public LiteContact getAContactByUserID(int userID);
    public LiteContact getAContactByContactID(int contactID);
    public LiteContact[] getContactsByLastName(String lName, boolean partialMatch);
    public LiteContact[] getContactsByFirstName(String fName, boolean partialMatch);
    public LiteContact[] getContactsByPhoneNumber(String phoneNumber, boolean partialMatch);
    public LiteContact getContactsByEmail(String email);
    public LiteContactNotification getAContactNotifByNotifID(int contNotifyID); 
    public LiteCustomer getACustomerByContactID(int contactID);
    public LiteCustomer getACustomerByCustomerID(int customerID);

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
	public void releaseAllSettlementConfigs();
	public void releaseAllSeasonSchedules();
	public void releaseAllTOUSchedules();
	public void releaseAllTOUDays();
	public void releaseAllNotificationGroups();
	//public void releaseAllPoints();
	public void releaseAllStateGroups();
	public void releaseAllUnitMeasures();
	public void releaseAllYukonPAObjects();
	public void releaseAllYukonUsers();
	public void releaseAllDeviceTypeCommands();
	public void releaseAllCommands();
	
	public void releaseUserRolePropertyValueMap();
	public void releaseUserRoleMap();
    public void releaseUserContactMap();

	public void removeDBChangeLiteListener(DBChangeLiteListener listener);
    public void removeDBChangeListener(DBChangeListener listener);
	public void setDatabaseAlias(String newAlias);
    public List getDevicesByCommPort(int portId);
    public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress);

}
