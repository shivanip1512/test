package com.cannontech.yukon;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteEnergyCompany;
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
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public interface IDatabaseCache {

    public DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem,
                                                int changeType);

    public List<LiteAlarmCategory> getAllAlarmCategories();

    public List<LiteYukonImage> getAllYukonImages();

    public List<LiteYukonPAObject> getAllCapControlFeeders();

    public List<LiteYukonPAObject> getAllCapControlSubBuses();
    
    public List<LiteYukonPAObject> getAllCapControlSubStations();

    public List<LiteContact> getAllContacts();

    public List<LiteCICustomer> getAllCICustomers();

    public List<LiteDeviceMeterNumber> getAllDeviceMeterGroups();

    public List<LiteYukonPAObject> getAllDevices();

    public List<LiteYukonPAObject> getAllMCTs();

    public List<LiteGraphDefinition> getAllGraphDefinitions();

    public List<LiteHolidaySchedule> getAllHolidaySchedules();

    public List<LiteBaseline> getAllBaselines();

    public List<LiteSeasonSchedule> getAllSeasonSchedules();

    public List<LiteTOUSchedule> getAllTOUSchedules();

    public List<LiteTOUDay> getAllTOUDays();

    public List<LiteGear> getAllGears();

    public List<LiteCommand> getAllCommands();

    /**
     * @return Map of commandID to LiteCommand
     */
    public Map<Integer,LiteCommand> getAllCommandsMap();

    public List<LiteConfig> getAllConfigs();

    public List<LiteLMConstraint> getAllLMProgramConstraints();

    public List<LiteLMProgScenario> getAllLMScenarioProgs();

    public List<LiteYukonPAObject> getAllLMScenarios();

    public List<LiteLMPAOExclusion> getAllLMPAOExclusions();

    public List<LiteYukonPAObject> getAllLMPrograms();

    public List<LiteYukonPAObject> getAllLMControlAreas();

    public List<LiteYukonPAObject> getAllLMGroups();

    public List<LiteYukonPAObject> getAllLoadManagement();

    public List<LiteNotificationGroup> getAllContactNotificationGroups();
    public List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone();

    public List<LitePoint> getAllSystemPoints();

    /**
     * @return Map of paoID to LiteYukonPAObject>
     */
    public Map<Integer,LiteYukonPAObject> getAllPAOsMap();

    /**
     * @return Map of userID to LiteYukonUser
     */
    public Map<Integer,LiteYukonUser> getAllUsersMap();

    /**
     * @return Map of contactNotifID to LiteContactNotification
     */
    public Map<Integer,LiteContactNotification> getAllContactNotifsMap();

    /**
     * @return Map of customerID to LiteCustomer
     */
    public Map<Integer,LiteCustomer> getAllCustomersMap();

    public List<LitePointLimit> getAllPointLimits();

    public List<LiteYukonPAObject> getAllPorts();

    public List<LiteYukonPAObject> getAllRoutes();

    /**
     * @return Map if stateGroupID to LiteStateGroup
     */
    public Map<Integer,LiteStateGroup> getAllStateGroupMap();

    public List<LiteTag> getAllTags();

    public List<LiteSettlementConfig> getAllSettlementConfigs();

    /**
     * @return  Map configID LiteSettlementConfig
     */
    public Map<Integer, LiteSettlementConfig> getAllSettlementConfigsMap();

    public List<LiteYukonPAObject> getAllUnusedCCDevices();

    public List<LiteYukonPAObject> getAllYukonPAObjects();

    public List<LiteYukonGroup> getAllYukonGroups();

    public List<LiteYukonRole> getAllYukonRoles();

    public List<LiteYukonRoleProperty> getAllYukonRoleProperties();

    public List<LiteYukonUser> getAllYukonUsers();

    public Map<LiteYukonUser, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> getYukonUserRolePropertyMap();

    public Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> getYukonGroupRolePropertyMap();

    public Map<LiteYukonUser,List<LiteYukonGroup>> getYukonUserGroupMap();

    public Map<LiteYukonGroup,List<LiteYukonUser>> getYukonGroupUserMap();

    public List<LiteEnergyCompany> getAllEnergyCompanies();

    public Map<LiteYukonUser, LiteEnergyCompany> getAllUserEnergyCompanies();

    public List<LiteCustomer> getAllCustomers();

    public List<LiteDeviceTypeCommand> getAllDeviceTypeCommands();

    /**
     *  Returns the LiteBase object that was added, deleted or updated, 
     *    else null is returned.
     */
    public LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg);

    public void releaseAllAlarmCategories();

    public void releaseAllCache();

    public void releaseAllContacts();

    public void releaseAllDeviceMeterGroups();

    public void releaseAllYukonImages();

    public void releaseAllGraphDefinitions();

    public void releaseAllHolidaySchedules();

    public void releaseAllBaselines();

    public void releaseAllSeasonSchedules();

    public void releaseAllCommands();

    public void releaseAllTOUSchedules();

    public void releaseAllTOUDays();

    public void releaseAllConfigs();

    public void releaseAllTags();

    public void releaseAllSettlementConfigs();

    public void releaseAllLMProgramConstraints();

    public void releaseAllLMScenarios();

    public void releaseAllLMPAOExclusions();

    public void releaseAllNotificationGroups();

    public void releaseAllCustomers();

    public void releaseAllYukonUsers();

    public void releaseAllStateGroups();

    public void releaseAllYukonPAObjects();

    public void releaseAllDeviceTypeCommands();

    public void setDatabaseAlias(String newAlias);

    public LiteYukonRole getARole(LiteYukonUser user, int roleID);

    public String getARolePropertyValue(LiteYukonUser user, int rolePropertyID);

    public LiteContact getAContactByUserID(int userID);

    public LiteContact getAContactByContactID(int contactID);

    public LiteContact[] getContactsByLastName(String lastName, boolean partialMatch);

    public LiteContact[] getContactsByFirstName(String firstName, boolean partialMatch);

    public LiteContact[] getContactsByPhoneNumber(String phone, boolean partialMatch);

    public LiteContact getContactsByEmail(String email);

    public LiteContactNotification getAContactNotifByNotifID(int contNotifyID);

    public LiteCustomer getACustomerByContactID(int contactID);

    public LiteCustomer getACustomerByCustomerID(int customerID);

    public void releaseUserRoleMap();

    public void releaseUserRolePropertyValueMap();

    public void releaseUserContactMap();

    public List getDevicesByCommPort(int portId);

    public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress);

}