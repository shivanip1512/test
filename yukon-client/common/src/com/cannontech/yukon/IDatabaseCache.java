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
import com.cannontech.message.dispatch.message.DbChangeType;

public interface IDatabaseCache {
    DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem, DbChangeType dbChangeType);

    List<LiteAlarmCategory> getAllAlarmCategories();

    List<LiteYukonImage> getAllYukonImages();

    List<LiteYukonPAObject> getAllCapControlFeeders();

    List<LiteYukonPAObject> getAllCapControlSubBuses();

    List<LiteYukonPAObject> getAllCapControlSubStations();

    List<LiteCICustomer> getAllCICustomers();

    List<LiteDeviceMeterNumber> getAllDeviceMeterGroups();

    List<LiteYukonPAObject> getAllDevices();

    List<LiteYukonPAObject> getAllMCTs();

    List<LiteGraphDefinition> getAllGraphDefinitions();

    List<LiteHolidaySchedule> getAllHolidaySchedules();

    List<LiteBaseline> getAllBaselines();

    List<LiteSeasonSchedule> getAllSeasonSchedules();

    List<LiteTOUSchedule> getAllTOUSchedules();

    List<LiteTOUDay> getAllTOUDays();

    List<LiteGear> getAllGears();

    List<LiteCommand> getAllCommands();

    /**
     * @return Map of commandID to LiteCommand
     */
    Map<Integer, LiteCommand> getAllCommandsMap();

    List<LiteConfig> getAllConfigs();

    List<LiteLMConstraint> getAllLMProgramConstraints();

    List<LiteLMProgScenario> getAllLMScenarioProgs();

    List<LiteYukonPAObject> getAllLMScenarios();

    List<LiteLMPAOExclusion> getAllLMPAOExclusions();

    List<LiteYukonPAObject> getAllLMPrograms();

    List<LiteYukonPAObject> getAllLMControlAreas();

    List<LiteYukonPAObject> getAllLMGroups();

    List<LiteYukonPAObject> getAllLoadManagement();

    List<LiteNotificationGroup> getAllContactNotificationGroups();

    List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone();

    List<LitePoint> getAllSystemPoints();

    /**
     * @return Map of paoID to LiteYukonPAObject>
     */
    Map<Integer, LiteYukonPAObject> getAllPAOsMap();

    /**
     * @return Map of contactNotifID to LiteContactNotification
     */
    Map<Integer, LiteContactNotification> getAllContactNotifsMap();

    List<LitePointLimit> getAllPointLimits();

    List<LiteYukonPAObject> getAllPorts();

    List<LiteYukonPAObject> getAllRoutes();

    /**
     * @return Map if stateGroupID to LiteStateGroup
     */
    Map<Integer, LiteStateGroup> getAllStateGroupMap();

    List<LiteTag> getAllTags();

    List<LiteYukonPAObject> getAllUnusedCCDevices();

    List<LiteYukonPAObject> getAllYukonPAObjects();

    List<LiteYukonGroup> getAllYukonGroups();

    List<LiteYukonRole> getAllYukonRoles();

    List<LiteYukonRoleProperty> getAllYukonRoleProperties();

    Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> getYukonGroupRolePropertyMap();

    List<LiteEnergyCompany> getAllEnergyCompanies();

    List<LiteDeviceTypeCommand> getAllDeviceTypeCommands();

    /**
     * Returns the LiteBase object that was added, deleted or updated,
     * else null is returned.
     * @param noObjectNeeded
     */
    LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg, boolean noObjectNeeded);

    LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg);

    void releaseAllAlarmCategories();

    void releaseAllCache();

    void releaseAllContacts();

    void releaseAllDeviceMeterGroups();

    void releaseAllYukonImages();

    void releaseAllGraphDefinitions();

    void releaseAllHolidaySchedules();

    void releaseAllBaselines();

    void releaseAllSeasonSchedules();

    void releaseAllCommands();

    void releaseAllTOUSchedules();

    void releaseAllTOUDays();

    void releaseAllConfigs();

    void releaseAllTags();

    void releaseAllLMProgramConstraints();

    void releaseAllLMScenarios();

    void releaseAllLMPAOExclusions();

    void releaseAllNotificationGroups();

    void releaseAllCustomers();

    void releaseAllStateGroups();

    void releaseAllYukonPAObjects();

    void releaseAllDeviceTypeCommands();

    LiteYukonRole getARole(LiteYukonUser user, int roleID);

    LiteContact getAContactByUserID(int userID);

    LiteContact getAContactByContactID(int contactID);

    LiteContactNotification getAContactNotifByNotifID(int contNotifyID);

    LiteCustomer getACustomerByPrimaryContactID(int contactID);

    LiteCustomer getACustomerByCustomerID(int customerID);

    LiteEnergyCompany getALiteEnergyCompanyByUserID(LiteYukonUser liteYukonUser);

    void releaseUserRoleMap();

    void releaseUserRolePropertyValueMap();

    void releaseUserContactMap();

    List<Integer> getDevicesByCommPort(int portId);

    List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress);
}
