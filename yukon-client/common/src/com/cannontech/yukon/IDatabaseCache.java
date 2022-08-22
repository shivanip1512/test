package com.cannontech.yukon;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.data.lite.LiteLMPAOExclusion;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public interface IDatabaseCache {
    
    DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem, DbChangeType dbChangeType);

    List<LiteYukonPAObject> getAllCapControlFeeders();

    List<LiteYukonPAObject> getAllCapControlSubBuses();

    List<LiteYukonPAObject> getAllCapControlSubStations();

    List<LiteCICustomer> getAllCICustomers();

    Map<Integer, SimpleMeter> getAllMeters();

    List<LiteYukonPAObject> getAllDevices();

    List<LiteYukonPAObject> getAllMCTs();

    List<LiteGraphDefinition> getAllGraphDefinitions();

    List<LiteHolidaySchedule> getAllHolidaySchedules();

    List<LiteBaseline> getAllBaselines();

    List<LiteSeasonSchedule> getAllSeasonSchedules();

    List<LiteTOUSchedule> getAllTOUSchedules();

    Map<Integer, LiteCommand> getAllCommands();

    List<LiteConfig> getAllConfigs();

    List<LiteLMConstraint> getAllLMProgramConstraints();

    List<LiteLMProgScenario> getAllLMScenarioProgs();

    List<LiteYukonPAObject> getAllLMScenarios();

    List<LiteLMPAOExclusion> getAllLMPAOExclusions();

    List<LiteYukonPAObject> getAllLMPrograms();

    List<LiteYukonPAObject> getAllLMControlAreas();

    List<LiteYukonPAObject> getAllLMGroups();
    
    /** Returns a map of lmGroup name to yukonPaobject (lmGroup). */
    Map<String, LiteYukonPAObject> getAllLMGroupsMap();

    List<LiteYukonPAObject> getAllLoadManagement();

    List<LiteNotificationGroup> getAllContactNotificationGroups();

    List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone();

    List<LitePoint> getAllSystemPoints();

    /**
     * @return Map of paoID to LiteYukonPAObject>
     */
    Map<Integer, LiteYukonPAObject> getAllPaosMap();
    
    Set<PaoType> getAllPaoTypes();

    /**
     * @return Map of contactNotifID to LiteContactNotification
     */
    Map<Integer, LiteContactNotification> getAllContactNotifsMap();

    /** Return map of point id to {@link LitePointLimit} */
    Map<Integer, LitePointLimit> getAllPointLimits();

    List<LiteYukonPAObject> getAllPorts();

    List<LiteYukonPAObject> getAllRoutes();
    
    /** Retuns a map of route id to route. */
    Map<Integer, LiteYukonPAObject> getAllRoutesMap();
    
    List<LiteYukonPAObject> getAllYukonPAObjects();

    List<LiteYukonGroup> getAllYukonGroups();

    List<LiteYukonRole> getAllYukonRoles();

    List<LiteYukonRoleProperty> getAllYukonRoleProperties();

    Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> getYukonGroupRolePropertyMap();

    List<LiteDeviceTypeCommand> getAllDeviceTypeCommands();
    
    Map<Integer, LiteDeviceTypeCommand> getAllDeviceTypeCommandsMap();
    
    LiteDeviceTypeCommand getDeviceTypeCommand(int deviceCommandId);
    
    /**
     * Returns the LiteBase object that was added, deleted or updated,
     * else null is returned.
     * @param noObjectNeeded
     */
    LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg, boolean noObjectNeeded);

    LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg);

    void releaseAllCache();

    void releaseAllContacts();

    void releaseAllMeters();

    void releaseAllYukonImages();

    void releaseAllGraphDefinitions();

    void releaseAllHolidaySchedules();

    void releaseAllBaselines();

    void releaseAllSeasonSchedules();

    void releaseAllCommands();

    void releaseAllTOUSchedules();

    void releaseAllConfigs();

    void releaseAllLMProgramConstraints();

    void releaseAllLMScenarios();

    void releaseAllLMPAOExclusions();

    void releaseAllNotificationGroups();

    void releaseAllCustomers();

    void releaseAllYukonPAObjects();

    void releaseAllDeviceTypeCommands();

    LiteContact getAContactByUserID(int userID);

    LiteContact getAContactByContactID(int contactID);

    LiteContactNotification getContactNotification(int contactNotificationId);

    LiteCustomer getACustomerByPrimaryContactID(int contactID);

    LiteCustomer getCustomer(int customerId);

    void releaseUserContactMap();

    Map<Integer, LiteYukonImage> getImages();
}