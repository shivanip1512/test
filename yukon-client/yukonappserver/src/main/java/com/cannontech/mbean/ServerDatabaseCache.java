package com.cannontech.mbean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
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
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.server.cache.AlarmCategoryLoader;
import com.cannontech.yukon.server.cache.BaselineLoader;
import com.cannontech.yukon.server.cache.CICustomerLoader;
import com.cannontech.yukon.server.cache.CommandLoader;
import com.cannontech.yukon.server.cache.ConfigLoader;
import com.cannontech.yukon.server.cache.ContactLoader;
import com.cannontech.yukon.server.cache.ContactNotificationGroupLoader;
import com.cannontech.yukon.server.cache.DeviceCommPortLoader;
import com.cannontech.yukon.server.cache.DeviceMeterGroupLoader;
import com.cannontech.yukon.server.cache.GearLoader;
import com.cannontech.yukon.server.cache.GraphDefinitionLoader;
import com.cannontech.yukon.server.cache.HolidayScheduleLoader;
import com.cannontech.yukon.server.cache.LMConstraintLoader;
import com.cannontech.yukon.server.cache.LMPAOExclusionLoader;
import com.cannontech.yukon.server.cache.LMScenarioProgramLoader;
import com.cannontech.yukon.server.cache.PointLimitLoader;
import com.cannontech.yukon.server.cache.SeasonScheduleLoader;
import com.cannontech.yukon.server.cache.StateGroupLoader;
import com.cannontech.yukon.server.cache.SystemPointLoader;
import com.cannontech.yukon.server.cache.TOUDayLoader;
import com.cannontech.yukon.server.cache.TOUScheduleLoader;
import com.cannontech.yukon.server.cache.TagLoader;
import com.cannontech.yukon.server.cache.YukonGroupLoader;
import com.cannontech.yukon.server.cache.YukonGroupRoleLoader;
import com.cannontech.yukon.server.cache.YukonImageLoader;
import com.cannontech.yukon.server.cache.YukonPAOLoader;
import com.cannontech.yukon.server.cache.YukonRoleLoader;
import com.cannontech.yukon.server.cache.YukonRolePropertyLoader;
import com.cannontech.yukon.server.cache.bypass.MapKeyInts;
import com.cannontech.yukon.server.cache.bypass.YukonCustomerLookup;
import com.cannontech.yukon.server.cache.bypass.YukonUserRolePropertyLookup;
import com.google.common.collect.Lists;

/**
 * All the action is here!
 * Creation date: (3/14/00 3:20:44 PM)
 * @author: everyone and their dog
 */
public class ServerDatabaseCache extends CTIMBeanBase implements IDatabaseCache {
    // stores a soft reference to the cache
    private static ServerDatabaseCache cache = null;

    @Autowired private PointDao pointDao = null;
    @Autowired private PaoDao paoDao = null;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private ContactDao contactDao;
    @Autowired private CommandDao commandDao;

    private String databaseAlias = CtiUtilities.getDatabaseAlias();

    private List<LiteYukonPAObject> allYukonPAObjects = null;
    private List<LitePoint> allSystemPoints = null;
    private List<LiteNotificationGroup> allNotificationGroups = null;

    private List<LiteAlarmCategory> allAlarmCategories = null;
    private List<LiteGraphDefinition> allGraphDefinitions = null;
    private List<LiteYukonPAObject> allMCTs = null;
    private List<LiteHolidaySchedule> allHolidaySchedules = null;
    private List<LiteBaseline> allBaselines = null;
    private List<LiteConfig> allConfigs = null;
    private List<LiteDeviceMeterNumber> allDeviceMeterGroups = null;
    private List<LitePointLimit> allPointLimits = null;
    private List<LiteYukonImage> allYukonImages = null;
    private volatile List<LiteCICustomer> allCICustomers = null;
    private List<LiteLMConstraint> allLMProgramConstraints = null;
    private List<LiteYukonPAObject> allLMScenarios = null;
    private List<LiteLMProgScenario> allLMScenarioProgs = null;
    private List<LiteLMPAOExclusion> allLMPAOExclusions = null;

    private List<LiteTag> allTags = null;

    private List<LiteSeasonSchedule> allSeasonSchedules = null;
    private List<LiteTOUSchedule> allTOUSchedules = null;
    private List<LiteTOUDay> allTOUDays = null;

    private List<LiteYukonRole> allYukonRoles = null;
    private List<LiteYukonRoleProperty> allYukonRoleProperties = null;
    private List<LiteYukonGroup> allYukonGroups = null;

    private Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> allYukonGroupRolePropertiesMap =
        null;

    // lists that are created by the joining/parsing of existing lists
    private List<LiteYukonPAObject> allUnusedCCDevices = null;
    private List<LiteYukonPAObject> allCapControlFeeders = null;
    private List<LiteYukonPAObject> allCapControlSubBuses = null;
    private List<LiteYukonPAObject> allCapControlSubStations = null;
    private List<LiteYukonPAObject> allDevices = null;
    private List<LiteYukonPAObject> allLMPrograms = null;
    private List<LiteYukonPAObject> allLMControlAreas = null;
    private List<LiteYukonPAObject> allLMGroups = null;
    private List<LiteYukonPAObject> allLoadManagement = null;
    private List<LiteYukonPAObject> allPorts = null;
    
    // Maps that are created by the joining/parsing of existing lists
    // private HashMap allPointidMultiplierHashMap = null;
    // private Map allPointIDOffsetHashMap = null;
    // private Map allPointsMap = null;
    private Map<Integer, LiteYukonPAObject> allPAOsMap = null;
    private final Map<Integer, LiteCustomer> customerCache = new ConcurrentHashMap<>(1000, .75f, 30);
    private final Map<Integer, LiteContact> allContactsMap = new ConcurrentHashMap<>(1000, .75f, 30);

    // derived from allYukonUsers,allYukonRoles,allYukonGroups
    // see type info in IDatabaseCache
    private Map<Integer, LiteDeviceTypeCommand> allDeviceTypeCommands = null;
    private Map<Integer, LiteYukonPAObject> allRoutes = null;
    private List<LiteCommand> allCommands = null;
    private Map<Integer, LiteCommand> allCommandsMap = null;
    private Map<Integer, LiteStateGroup> allStateGroupMap = null;
    private Map<Integer, LiteContactNotification> allContactNotifsMap = null;

    private final Map<Integer, LiteContact> userContactMap = new ConcurrentHashMap<>(1000, .75f, 30);
    private Map<MapKeyInts, String> userRolePropertyValueMap = null;
    private Map<MapKeyInts, LiteYukonRole> userRoleMap = null;

    @Override
    public synchronized DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem, DbChangeType dbChangeType) {
        return newItem.getDBChangeMsgs(dbChangeType);
    }

    @Override
    public synchronized List<LiteAlarmCategory> getAllAlarmCategories() {
        if (allAlarmCategories != null) {
            return allAlarmCategories;
        }
        allAlarmCategories = new ArrayList<>();
        AlarmCategoryLoader alarmStateLoader = new AlarmCategoryLoader(allAlarmCategories, databaseAlias);
        alarmStateLoader.run();
        return allAlarmCategories;
    }

    @Override
    public synchronized List<LiteYukonImage> getAllYukonImages() {
        if (allYukonImages != null) {
            return allYukonImages;
        }
        allYukonImages = new ArrayList<>();
        YukonImageLoader imageLoader = new YukonImageLoader(allYukonImages, databaseAlias);
        imageLoader.run();
        return allYukonImages;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllCapControlFeeders() {
        if (allCapControlFeeders == null) {
            allCapControlFeeders = new ArrayList<>();

            for (int i = 0; i < getAllYukonPAObjects().size(); i++) {
                if (getAllYukonPAObjects().get(i).getPaoType().getPaoCategory() == PaoCategory.CAPCONTROL
                    && getAllYukonPAObjects().get(i).getPaoType() == PaoType.CAP_CONTROL_FEEDER) {
                    allCapControlFeeders.add(getAllYukonPAObjects().get(i));
                }
            }
        }

        return allCapControlFeeders;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllCapControlSubBuses() {
        if (allCapControlSubBuses == null) {
            allCapControlSubBuses = new ArrayList<>();

            for (int i = 0; i < getAllYukonPAObjects().size(); i++) {
                if (getAllYukonPAObjects().get(i).getPaoType().getPaoCategory() == PaoCategory.CAPCONTROL
                    && getAllYukonPAObjects().get(i).getPaoType() == PaoType.CAP_CONTROL_SUBBUS) {
                    allCapControlSubBuses.add(getAllYukonPAObjects().get(i));
                }
            }
        }

        return allCapControlSubBuses;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllCapControlSubStations() {
        if (allCapControlSubStations == null) {
            allCapControlSubStations = new ArrayList<>();

            for (int i = 0; i < getAllYukonPAObjects().size(); i++) {
                if (getAllYukonPAObjects().get(i).getPaoType().getPaoCategory() == PaoCategory.CAPCONTROL
                    && getAllYukonPAObjects().get(i).getPaoType() == PaoType.CAP_CONTROL_SUBSTATION) {
                    allCapControlSubStations.add(getAllYukonPAObjects().get(i));
                }
            }
        }

        return allCapControlSubStations;
    }

    private synchronized void loadAllContacts() {
        if (allContactNotifsMap != null) {
            return;
        }

        allContactsMap.clear();
        allContactNotifsMap = new HashMap<Integer, LiteContactNotification>();

        ContactLoader contactLoader = new ContactLoader(allContactsMap, allContactNotifsMap, databaseAlias);
        contactLoader.run();
    }

    /**
     * Called by:
     * HECO_SettlementModelBase.loadSettlementCustomerMap()
     * LMProgramEnergyExchangeCustomerListPanel.initializeAddPanel()
     * NotificationPanel.getJTreeNotifs()
     */
    @Override
    public List<LiteCICustomer> getAllCICustomers() {
        List<LiteCICustomer> tempAllCICustomers = allCICustomers;
        if (tempAllCICustomers == null) {
            tempAllCICustomers = new ArrayList<>();
            CICustomerLoader custLoader = new CICustomerLoader(tempAllCICustomers, databaseAlias);
            custLoader.run();
            allCICustomers = tempAllCICustomers;
        }
        return Collections.unmodifiableList(tempAllCICustomers);
    }

    @Override
    public synchronized List<LiteDeviceMeterNumber> getAllDeviceMeterGroups() {
        if (allDeviceMeterGroups != null) {
            return allDeviceMeterGroups;
        }

        allDeviceMeterGroups = new ArrayList<>();
        DeviceMeterGroupLoader deviceMeterGroupLoader = new DeviceMeterGroupLoader(allDeviceMeterGroups, databaseAlias);
        deviceMeterGroupLoader.run();
        return allDeviceMeterGroups;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllDevices() {
        if (allDevices == null) {
            allDevices = new ArrayList<>();

            for (int i = 0; i < getAllYukonPAObjects().size(); i++) {
                if (getAllYukonPAObjects().get(i).getPaoType().getPaoCategory() == PaoCategory.DEVICE) {
                    allDevices.add(getAllYukonPAObjects().get(i));
                }
            }
        }

        return allDevices;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllMCTs() {
        if (allMCTs == null) {
            allMCTs = new ArrayList<>();

            for (LiteYukonPAObject device : getAllDevices()) {
                if (device.getPaoType().isMct()) {
                    allMCTs.add(device);
                }
            }
        }

        return allMCTs;
    }

    @Override
    public synchronized List<LiteGraphDefinition> getAllGraphDefinitions() {
        if (allGraphDefinitions != null) {
            return allGraphDefinitions;
        }

        allGraphDefinitions = new ArrayList<>();
        GraphDefinitionLoader graphDefinitionLoader = new GraphDefinitionLoader(allGraphDefinitions, databaseAlias);
        graphDefinitionLoader.run();
        return allGraphDefinitions;
    }

    @Override
    public synchronized List<LiteHolidaySchedule> getAllHolidaySchedules() {
        if (allHolidaySchedules != null) {
            return allHolidaySchedules;
        }

        allHolidaySchedules = new ArrayList<>();
        HolidayScheduleLoader holidayScheduleLoader = new HolidayScheduleLoader(allHolidaySchedules, databaseAlias);
        holidayScheduleLoader.run();
        return allHolidaySchedules;
    }

    @Override
    public synchronized List<LiteBaseline> getAllBaselines() {
        if (allBaselines != null) {
            return allBaselines;
        }

        allBaselines = new ArrayList<>();
        BaselineLoader baselineLoader = new BaselineLoader(allBaselines, databaseAlias);
        baselineLoader.run();
        return allBaselines;
    }

    @Override
    public synchronized List<LiteSeasonSchedule> getAllSeasonSchedules() {
        if (allSeasonSchedules != null) {
            return allSeasonSchedules;
        }

        allSeasonSchedules = new ArrayList<>();
        SeasonScheduleLoader seasonLoader = new SeasonScheduleLoader(allSeasonSchedules, databaseAlias);
        seasonLoader.run();
        return allSeasonSchedules;
    }

    @Override
    public synchronized List<LiteTOUSchedule> getAllTOUSchedules() {
        if (allTOUSchedules != null) {
            return allTOUSchedules;
        }

        allTOUSchedules = new ArrayList<>();
        TOUScheduleLoader touLoader = new TOUScheduleLoader(allTOUSchedules, databaseAlias);
        touLoader.run();
        return allTOUSchedules;
    }

    @Override
    public synchronized List<LiteTOUDay> getAllTOUDays() {
        if (allTOUDays != null) {
            return allTOUDays;
        }

        allTOUDays = new ArrayList<>();
        TOUDayLoader dayLoader = new TOUDayLoader(allTOUDays, databaseAlias);
        dayLoader.run();
        return allTOUDays;
    }

    @Override
    public List<LiteGear> getAllGears() {
        List<LiteGear> allGears = new ArrayList<>();
        GearLoader gearLoader = new GearLoader(allGears, databaseAlias);
        gearLoader.run();
        return allGears;
    }

    @Override
    public synchronized List<LiteCommand> getAllCommands() {
        if (allCommands == null) {
            allCommands = new ArrayList<>();
            allCommandsMap = new HashMap<>();
            CommandLoader commandLoader = new CommandLoader(allCommands, allCommandsMap, databaseAlias);
            commandLoader.run();
        }
        return allCommands;
    }

    @Override
    public synchronized Map<Integer, LiteCommand> getAllCommandsMap() {
        if (allCommandsMap != null) {
            return allCommandsMap;
        }

        releaseAllCommands();
        getAllCommands();

        return allCommandsMap;
    }

    @Override
    public synchronized List<LiteConfig> getAllConfigs() {
        if (allConfigs != null) {
            return allConfigs;
        }

        allConfigs = new ArrayList<>();
        ConfigLoader configLoader = new ConfigLoader(allConfigs, databaseAlias);
        configLoader.run();
        return allConfigs;
    }

    @Override
    public synchronized List<LiteLMConstraint> getAllLMProgramConstraints() {

        if (allLMProgramConstraints != null) {
            return allLMProgramConstraints;
        }

        allLMProgramConstraints = new ArrayList<>();
        LMConstraintLoader lmConstraintsLoader = new LMConstraintLoader(allLMProgramConstraints, databaseAlias);
        lmConstraintsLoader.run();
        return allLMProgramConstraints;
    }

    @Override
    public synchronized List<LiteLMProgScenario> getAllLMScenarioProgs() {
        if (allLMScenarioProgs == null) {
            allLMScenarioProgs = new ArrayList<>();
            LMScenarioProgramLoader ldr = new LMScenarioProgramLoader(allLMScenarioProgs, databaseAlias);
            ldr.run();
        }

        return allLMScenarioProgs;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllLMScenarios() {
        if (allLMScenarios == null) {
            allLMScenarios = new ArrayList<>();

            for (int i = 0; i < getAllLoadManagement().size(); i++) {
                if (getAllLoadManagement().get(i).getPaoType() == PaoType.LM_SCENARIO) {
                    allLMScenarios.add(getAllLoadManagement().get(i));
                }
            }
        }

        return allLMScenarios;
    }

    @Override
    public synchronized List<LiteLMPAOExclusion> getAllLMPAOExclusions() {
        if (allLMPAOExclusions == null) {
            allLMPAOExclusions = new ArrayList<>();
            LMPAOExclusionLoader ldr = new LMPAOExclusionLoader(allLMPAOExclusions, databaseAlias);
            ldr.run();
        }

        return allLMPAOExclusions;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllLMPrograms() {
        if (allLMPrograms == null) {
            // List allDevices = getAllLoadManagement();
            allLMPrograms = new ArrayList<>();

            for (int i = 0; i < getAllLoadManagement().size(); i++) {
                if (getAllLoadManagement().get(i).getPaoType() == PaoType.LM_CURTAIL_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_DIRECT_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_SEP_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_ECOBEE_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_ENERGY_EXCHANGE_PROGRAM) {
                    allLMPrograms.add(getAllLoadManagement().get(i));
                }
            }
        }
        return allLMPrograms;
    }

    @Override
    public List<LiteYukonPAObject> getAllLMControlAreas() {
        if (allLMControlAreas == null) {
            allLMControlAreas = new ArrayList<>();

            for (int i = 0; i < getAllLoadManagement().size(); i++) {
                if (getAllLoadManagement().get(i).getPaoType() == PaoType.LM_CONTROL_AREA) {
                    allLMControlAreas.add(getAllLoadManagement().get(i));
                }
            }
        }
        return allLMControlAreas;
    }

    @Override
    public List<LiteYukonPAObject> getAllLMGroups() {
        if (allLMGroups == null) {
            allLMGroups = new ArrayList<>();

            for (LiteYukonPAObject liteYukonPAObject : getAllLoadManagement()) {
                if (liteYukonPAObject.getPaoType().isLoadGroup()) {
                    allLMGroups.add(liteYukonPAObject);
                }
            }
        }
        return allLMGroups;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllLoadManagement() {
        if (allLoadManagement == null) {
            allLoadManagement = new ArrayList<>();

            for (LiteYukonPAObject liteYukonPAObject : getAllYukonPAObjects()) {
                if (liteYukonPAObject.getPaoType().isLoadManagement()) {
                    allLoadManagement.add(liteYukonPAObject);
                }
            }
        }

        return allLoadManagement;
    }

    @Override
    public synchronized List<LiteNotificationGroup> getAllContactNotificationGroups() {
        if (allNotificationGroups != null) {
            return allNotificationGroups;
        }

        allNotificationGroups = new ArrayList<>();
        ContactNotificationGroupLoader notifLoader =
            new ContactNotificationGroupLoader(allNotificationGroups, databaseAlias);
        notifLoader.run();

        // allUsedContactNotifications = notifLoader.getAllUsedContactNotifications();
        return allNotificationGroups;
    }

    @Override
    public synchronized List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone() {
        List<LiteNotificationGroup> notifGroup = new ArrayList<>();

        LiteNotificationGroup noneGroup = new LiteNotificationGroup(PointAlarming.NONE_NOTIFICATIONID);
        noneGroup.setNotificationGroupName("(none)");
        notifGroup.add(noneGroup);

        notifGroup.addAll(getAllContactNotificationGroups());

        return notifGroup;
    }

    @Override
    public synchronized List<LitePoint> getAllSystemPoints() {
        if (allSystemPoints != null) {
            return allSystemPoints;
        }

        allSystemPoints = new ArrayList<>();
        // allPointsMap = new HashMap();
        SystemPointLoader systemPointLoader = new SystemPointLoader(allSystemPoints, databaseAlias);
        systemPointLoader.run();
        return allSystemPoints;
    }

    @Override
    public synchronized Map<Integer, LiteYukonPAObject> getAllPAOsMap() {
        if (allPAOsMap != null) {
            return allPAOsMap;
        }

        releaseAllYukonPAObjects();
        getAllYukonPAObjects();

        return allPAOsMap;
    }

    @Override
    public synchronized Map<Integer, LiteContactNotification> getAllContactNotifsMap() {
        loadAllContacts();
        return allContactNotifsMap;
    }

    @Override
    public synchronized List<LitePointLimit> getAllPointLimits() {
        if (allPointLimits != null) {
            return allPointLimits;
        }

        allPointLimits = new ArrayList<>();
        PointLimitLoader pointLimitLoader = new PointLimitLoader(allPointLimits, databaseAlias);
        pointLimitLoader.run();
        return allPointLimits;
    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllPorts() {
        if (allPorts == null) {
            allPorts = new ArrayList<>();

            for (LiteYukonPAObject pao : getAllYukonPAObjects()) {
                if (pao.getPaoType().getPaoCategory() == PaoCategory.PORT || pao.getPaoType() == PaoType.RFN_1200) {
                    allPorts.add(pao);
                }
            }
        }
        return allPorts;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllRoutes() {
        return new ArrayList<>(getAllRoutesMap().values());
    }
    
    @Override
    public synchronized Map<Integer, LiteYukonPAObject> getAllRoutesMap() {
        if (allRoutes == null) {
            allRoutes = new ConcurrentHashMap<>();
            
            for (LiteYukonPAObject pao : getAllYukonPAObjects()) {
                PaoCategory category = pao.getPaoType().getPaoCategory();
                if (category == PaoCategory.ROUTE) {
                    allRoutes.put(pao.getPaoIdentifier().getPaoId(), pao);
                }
            }
        }
        
        return allRoutes;
    }
    
    @Override
    public synchronized Map<Integer, LiteStateGroup> getAllStateGroupMap() {
        if (allStateGroupMap == null) {
            allStateGroupMap = new HashMap<Integer, LiteStateGroup>();
            StateGroupLoader stateGroupLoader = new StateGroupLoader(allStateGroupMap);
            stateGroupLoader.run();
            return allStateGroupMap;
        }

        return allStateGroupMap;
    }

    @Override
    public synchronized List<LiteTag> getAllTags() {
        if (allTags == null) {
            allTags = new ArrayList<>();
            TagLoader tagLoader = new TagLoader(allTags, databaseAlias);
            tagLoader.run();
            return allTags;
        }
        return allTags;
    }

    // This cache is derive from the Device cache
    @Override
    public synchronized List<LiteYukonPAObject> getAllUnusedCCDevices() {
        if (allUnusedCCDevices != null) {
            return allUnusedCCDevices;
        }

        // temp code
        Date timerStart = new Date();
        // temp code

        // add all the CBC and and addressable Devices (RTU, DNP, etc) into these results
        String sqlString =
            "select deviceID from " + DeviceAddress.TABLE_NAME + " union select deviceID from "
                + DeviceCBC.TABLE_NAME + " where deviceID not in " + " (select controldeviceid from "
                + CapBank.TABLE_NAME + ")";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        allUnusedCCDevices = new ArrayList<>(32);

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            while (rset.next()) {
                int paoID = rset.getInt(1);
                LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoID);

                if (pao != null) {
                    allUnusedCCDevices.add(pao);
                }
            }

            if (rset != null) {
                rset.close();
            }

            // ensure is list is sorted by name
            Collections.sort(allUnusedCCDevices, LiteComparators.liteYukonPAObjectIDComparator);

            // temp code
            Date timerStop = new Date();
            CTILogger.info((timerStop.getTime() - timerStart.getTime()) * .001 + " Secs for getAllUnusedCCPaos()");
            // temp code
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }

        return allUnusedCCDevices;

    }

    @Override
    public synchronized List<LiteYukonPAObject> getAllYukonPAObjects() {
        if (allYukonPAObjects != null && allPAOsMap != null) {
            return allYukonPAObjects;
        }

        allYukonPAObjects = new ArrayList<>();
        allPAOsMap = new HashMap<Integer, LiteYukonPAObject>();
        YukonPAOLoader yukLoader = new YukonPAOLoader(allYukonPAObjects, allPAOsMap);
        yukLoader.run();

        return allYukonPAObjects;
    }

    @Override
    public synchronized List<LiteYukonGroup> getAllYukonGroups() {
        if (allYukonGroups == null) {
            allYukonGroups = new ArrayList<>();
            YukonGroupLoader l = new YukonGroupLoader(allYukonGroups, databaseAlias);
            l.run();
        }
        return allYukonGroups;
    }

    @Override
    public synchronized List<LiteYukonRole> getAllYukonRoles() {
        if (allYukonRoles == null) {
            allYukonRoles = new ArrayList<>();
            YukonRoleLoader l = new YukonRoleLoader(allYukonRoles, databaseAlias);
            l.run();
        }
        return allYukonRoles;
    }

    @Override
    public synchronized List<LiteYukonRoleProperty> getAllYukonRoleProperties() {
        if (allYukonRoleProperties == null) {
            allYukonRoleProperties = new ArrayList<>();
            final YukonRolePropertyLoader l = new YukonRolePropertyLoader(allYukonRoleProperties, databaseAlias);
            l.run();
        }
        return allYukonRoleProperties;
    }

    @Override
    public synchronized Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>>
        getYukonGroupRolePropertyMap() {
        if (allYukonGroupRolePropertiesMap == null) {
            allYukonGroupRolePropertiesMap =
                new HashMap<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>>();
            final YukonGroupRoleLoader l =
                new YukonGroupRoleLoader(allYukonGroupRolePropertiesMap, getAllYukonGroups(), getAllYukonRoles(),
                    getAllYukonRoleProperties(), databaseAlias);
            l.run();
        }
        return allYukonGroupRolePropertiesMap;
    }
    
    @Override
    public synchronized Map<Integer, LiteDeviceTypeCommand> getAllDeviceTypeCommandsMap() {
        
        if (allDeviceTypeCommands == null) {
            allDeviceTypeCommands = new ConcurrentHashMap<>(1000, .75f, 30);
            for (LiteDeviceTypeCommand command : commandDao.getAllDeviceTypeCommands()) {
                allDeviceTypeCommands.put(command.getDeviceCommandId(), command);
            }
        }
        
        return allDeviceTypeCommands;
    }
    
    @Override
    public synchronized List<LiteDeviceTypeCommand> getAllDeviceTypeCommands() {
        return Lists.newArrayList(getAllDeviceTypeCommandsMap().values());
    }
    
    @Override
    public synchronized LiteDeviceTypeCommand getDeviceTypeCommand(int deviceCommandId) {
        return getAllDeviceTypeCommandsMap().get(deviceCommandId);
    }
    
    public static synchronized IDatabaseCache getInstance() {
        
        if (cache == null) {
            cache = new ServerDatabaseCache();
        }
        
        return cache;
    }

    private synchronized LiteBase handleAlarmCategoryChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allAlarmCategories == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allAlarmCategories.size(); i++) {
                if (allAlarmCategories.get(i).getAlarmStateID() == id) {
                    alreadyAdded = true;
                    lBase = allAlarmCategories.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteAlarmCategory la = new LiteAlarmCategory(id);
                la.retrieve(databaseAlias);
                allAlarmCategories.add(la);
                lBase = la;
            }
            break;

        case UPDATE:
            for (int i = 0; i < allAlarmCategories.size(); i++) {
                if (allAlarmCategories.get(i).getAlarmStateID() == id) {
                    allAlarmCategories.get(i).retrieve(databaseAlias);
                    lBase = allAlarmCategories.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allAlarmCategories.size(); i++) {
                if (allAlarmCategories.get(i).getAlarmStateID() == id) {
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

    private synchronized LiteBase handleYukonImageChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allYukonImages == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allYukonImages.size(); i++) {
                if (allYukonImages.get(i).getImageID() == id) {
                    alreadyAdded = true;
                    lBase = allYukonImages.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteYukonImage ls = new LiteYukonImage(id);
                ls.retrieve(databaseAlias);
                allYukonImages.add(ls);
                lBase = ls;
            }
            break;

        case UPDATE:
            for (int i = 0; i < allYukonImages.size(); i++) {
                if (allYukonImages.get(i).getImageID() == id) {
                    allYukonImages.get(i).retrieve(databaseAlias);
                    lBase = allYukonImages.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allYukonImages.size(); i++) {
                if (allYukonImages.get(i).getImageID() == id) {
                    lBase = allYukonImages.remove(i);
                    break;
                }
            }
            break;
        default:
            releaseAllYukonImages();
            break;
        }

        return lBase;
    }

    private synchronized LiteBase handleContactChange(DbChangeType dbChangeType, int id) {
        LiteBase lBase = null;

        switch (dbChangeType) {
        case ADD: {
            if (id == DBChangeMsg.CHANGE_INVALID_ID) {
                break;
            }

            LiteContact lc = getAContactByContactID(id);
            lBase = lc;

            break;
        }
        case UPDATE: {
            allContactsMap.remove(id);
            LiteContact lc = getAContactByContactID(id);
            lBase = lc;

            // better wipe the user to contact mappings in case a contact changed that was mapped
            // releaseUserContactMap();

            break;
        }
        case DELETE:
            // special case for this handler!!!!
            if (id == DBChangeMsg.CHANGE_INVALID_ID) {
                releaseAllContacts();
                break;
            }

            allContactsMap.remove(new Integer(id));

            // better wipe the user to contact mapping, in case one of the contacts from the map was deleted
            releaseUserContactMap();

            break;

        default:
            releaseAllContacts();
            releaseUserContactMap();
            break;
        }

        return lBase;
    }

    @Override
    public LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg) {
        return handleDBChangeMessage(dbChangeMsg, false);
    }

    /**
     * Returns the LiteBase object that was added, deleted or updated.
     * However, the noObjectNeeded serves as a hint that the caller
     * does not need the LiteBase object to be returned and that any
     * available optimizations can be made to ignore it.
     */
    @Override
    public synchronized LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg, boolean noObjectNeeded) {
        String objectType = dbChangeMsg.getObjectType();
        String dbCategory = dbChangeMsg.getCategory();
        DbChangeType dbChangeType = dbChangeMsg.getDbChangeType();
        int database = dbChangeMsg.getDatabase();
        int id = dbChangeMsg.getId();
        LiteBase retLBase = null;

        if (database == DBChangeMsg.CHANGE_POINT_DB) {
            allPointLimits = null;
            retLBase = handlePointChange(dbChangeType, id, noObjectNeeded);
        } else if (database == DBChangeMsg.CHANGE_PAO_DB) {
            retLBase = handleYukonPAOChange(dbChangeType, id);

            // if any device changes,
            // reload all the DeviceMeterGroup data (may be inefficient!!)
            if (dbCategory.equalsIgnoreCase(PaoCategory.DEVICE.getDbString())) {
                allDevices = null;
                allMCTs = null;
                allUnusedCCDevices = null;
                allLoadManagement = null; // PAOGroups are here, oops!

                // we should not have to return the DeviceMeterGroup since
                // the liteObject that was edited/added was actually a Device
                // retLBase = handleDeviceMeterGroupChange( dbType, id);

                // Verify that this a device that even cares about DeviceMeterGroups
                if (DeviceTypesFuncs.usesDeviceMeterGroup(PaoType.getForDbString(objectType))) {
                    handleDeviceMeterGroupChange(dbChangeType, id);
                }
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.LOADMANAGEMENT.getDbString())) {
                allLoadManagement = null;
                allLMPrograms = null;
                allLMControlAreas = null;
                allLMGroups = null;
                allLMScenarios = null;
                allLMScenarioProgs = null;
                allLMPAOExclusions = null;
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.CAPCONTROL.getDbString())) {
                allCapControlFeeders = null;
                allCapControlSubBuses = null;
                allCapControlSubStations = null;
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.PORT.getDbString())) {
                allPorts = null;
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.ROUTE.getDbString())) {
                allRoutes = null;
            }
        } else if (database == DBChangeMsg.CHANGE_STATE_GROUP_DB) {
            retLBase = handleStateGroupChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_ALARM_CATEGORY_DB) {
            retLBase = handleAlarmCategoryChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_YUKON_IMAGE) {
            retLBase = handleYukonImageChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_NOTIFICATION_GROUP_DB) {
            // clear out the Contacts as they may have changed
            releaseAllContacts();

            retLBase = handleNotificationGroupChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_CONTACT_DB) {
            // clear out the CICustomers & NotificationGroups as they may have changed
            allCICustomers = null;
            allNotificationGroups = null;

            releaseAllCustomers();

            retLBase = handleContactChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_GRAPH_DB) {
            retLBase = handleGraphDefinitionChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_HOLIDAY_SCHEDULE_DB) {
            retLBase = handleHolidayScheduleChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_BASELINE_DB) {
            retLBase = handleBaselineChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_SEASON_SCHEDULE_DB) {
            retLBase = handleSeasonScheduleChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_TOU_SCHEDULE_DB) {
            retLBase = handleTOUScheduleChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_CONFIG_DB) {
            if (DBChangeMsg.CAT_DEVICE_CONFIG.equals(dbCategory)) {
                // Do nothing - no cache for device configs
            } else {
                retLBase = handleConfigChange(dbChangeType, id);
            }
        } else if (database == DBChangeMsg.CHANGE_TAG_DB) {
            retLBase = handleTagChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_LMCONSTRAINT_DB) {
            retLBase = handleLMProgramConstraintChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_DEVICETYPE_COMMAND_DB) {
            retLBase = handleDeviceTypeCommandChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_COMMAND_DB) {
            retLBase = handleCommandChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_CUSTOMER_DB) {
            allNotificationGroups = null;
            retLBase = handleCustomerChange(dbChangeType, id, dbCategory, noObjectNeeded);

            if (dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CI_CUSTOMER)) {
                allCICustomers = null;
            }
        } else if (database == DBChangeMsg.CHANGE_YUKON_USER_DB) {
            if (DBChangeMsg.CAT_YUKON_USER_GROUP.equalsIgnoreCase(dbCategory)) {
                retLBase = handleYukonGroupChange(dbChangeType, id);
            } else {
                retLBase = handleYukonUserChange(dbChangeType, id, noObjectNeeded);
            }

            // This seems heavy handed!
            allYukonGroups = null;
            allYukonRoles = null;
            allYukonGroupRolePropertiesMap = null;
        } else if (database == DBChangeMsg.CHANGE_USER_GROUP_DB) {
            retLBase = handleUserGroupChange(dbChangeType, id);
        } else if (database == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB) {
            if (dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CUSTOMER_ACCOUNT)) {
                // allContacts = null;
            }
            retLBase = null;
        } else {
            // There are several messages we don't care about.
            CTILogger.debug("Unhandled DBChangeMessage with category " + dbCategory);
        }

        return retLBase;
    }

    private synchronized LiteBase handleDeviceMeterGroupChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allDeviceMeterGroups == null) {
            return lBase;
        }

        if (id == 0) { // A force reload of all devicemetergroups was sent.
            releaseAllDeviceMeterGroups();
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allDeviceMeterGroups.size(); i++) {
                if (allDeviceMeterGroups.get(i).getDeviceID() == id) {
                    alreadyAdded = true;
                    lBase = allDeviceMeterGroups.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteDeviceMeterNumber liteDMG = new LiteDeviceMeterNumber(id);
                liteDMG.retrieve(databaseAlias);
                allDeviceMeterGroups.add(liteDMG);
                lBase = liteDMG;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allDeviceMeterGroups.size(); i++) {
                if (allDeviceMeterGroups.get(i).getDeviceID() == id) {
                    allDeviceMeterGroups.get(i).retrieve(databaseAlias);
                    lBase = allDeviceMeterGroups.get(i);

                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allDeviceMeterGroups.size(); i++) {
                if (allDeviceMeterGroups.get(i).getDeviceID() == id) {
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

    private synchronized LiteBase handleGraphDefinitionChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allGraphDefinitions == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allGraphDefinitions.size(); i++) {
                if (allGraphDefinitions.get(i).getGraphDefinitionID() == id) {
                    alreadyAdded = true;
                    lBase = allGraphDefinitions.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteGraphDefinition lsg = new LiteGraphDefinition(id);
                lsg.retrieve(databaseAlias);
                allGraphDefinitions.add(lsg);
                lBase = lsg;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allGraphDefinitions.size(); i++) {
                if (allGraphDefinitions.get(i).getGraphDefinitionID() == id) {
                    allGraphDefinitions.get(i).retrieve(databaseAlias);
                    lBase = allGraphDefinitions.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allGraphDefinitions.size(); i++) {
                if (allGraphDefinitions.get(i).getGraphDefinitionID() == id) {
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

    private synchronized LiteBase handleHolidayScheduleChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allHolidaySchedules == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allHolidaySchedules.size(); i++) {
                if (allHolidaySchedules.get(i).getHolidayScheduleID() == id) {
                    alreadyAdded = true;
                    lBase = allHolidaySchedules.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteHolidaySchedule lh = new LiteHolidaySchedule(id);
                lh.retrieve(databaseAlias);
                allHolidaySchedules.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allHolidaySchedules.size(); i++) {
                if (allHolidaySchedules.get(i).getHolidayScheduleID() == id) {
                    allHolidaySchedules.get(i).retrieve(databaseAlias);
                    lBase = allHolidaySchedules.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allHolidaySchedules.size(); i++) {
                if (allHolidaySchedules.get(i).getHolidayScheduleID() == id) {
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

    private synchronized LiteBase handleDeviceTypeCommandChange(DbChangeType change, int id) {
        
        LiteBase lBase = null;
        
        Map<Integer, LiteDeviceTypeCommand> commands = getAllDeviceTypeCommandsMap();
        
        if (change == DbChangeType.ADD) {
            LiteDeviceTypeCommand command = commands.get(id);
            if (command != null) {
                lBase = command;
            } else {
                command = commandDao.getDeviceTypeCommand(id);
                commands.put(command.getDeviceCommandId(), command);
                lBase = command;
            }
        } else if (change == DbChangeType.UPDATE) {
            
            LiteDeviceTypeCommand command = commandDao.getDeviceTypeCommand(id);
            commands.put(command.getDeviceCommandId(), command);
            lBase = command;
            
        } else if (change == DbChangeType.DELETE) {
            lBase = commands.remove(id);
        } else {
            releaseAllDeviceTypeCommands();
        }
        
        return lBase;
    }

    private synchronized LiteBase handleBaselineChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allBaselines == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allBaselines.size(); i++) {
                if (allBaselines.get(i).getBaselineID() == id) {
                    alreadyAdded = true;
                    lBase = allBaselines.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteBaseline lh = new LiteBaseline(id);
                lh.retrieve(databaseAlias);
                allBaselines.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allBaselines.size(); i++) {
                if (allBaselines.get(i).getBaselineID() == id) {
                    allBaselines.get(i).retrieve(databaseAlias);
                    lBase = allBaselines.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allBaselines.size(); i++) {
                if (allBaselines.get(i).getBaselineID() == id) {
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

    private synchronized LiteBase handleSeasonScheduleChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allSeasonSchedules == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allSeasonSchedules.size(); i++) {
                if (allSeasonSchedules.get(i).getScheduleID() == id) {
                    alreadyAdded = true;
                    lBase = allSeasonSchedules.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteSeasonSchedule lh = new LiteSeasonSchedule(id);
                lh.retrieve(databaseAlias);
                allSeasonSchedules.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allSeasonSchedules.size(); i++) {
                if (allSeasonSchedules.get(i).getScheduleID() == id) {
                    allSeasonSchedules.get(i).retrieve(databaseAlias);
                    lBase = allSeasonSchedules.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allSeasonSchedules.size(); i++) {
                if (allSeasonSchedules.get(i).getScheduleID() == id) {
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

    private synchronized LiteBase handleTOUScheduleChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allTOUSchedules == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allTOUSchedules.size(); i++) {
                if (allTOUSchedules.get(i).getScheduleID() == id) {
                    alreadyAdded = true;
                    lBase = allTOUSchedules.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteTOUSchedule lh = new LiteTOUSchedule(id);
                lh.retrieve(databaseAlias);
                allTOUSchedules.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allTOUSchedules.size(); i++) {
                if (allTOUSchedules.get(i).getScheduleID() == id) {
                    allTOUSchedules.get(i).retrieve(databaseAlias);
                    lBase = allTOUSchedules.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allTOUSchedules.size(); i++) {
                if (allTOUSchedules.get(i).getScheduleID() == id) {
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

    private synchronized LiteBase handleCommandChange(DbChangeType dbChangeType, int id) {
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allCommands == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            lBase = allCommandsMap.get(new Integer(id));
            if (lBase == null) {
                LiteCommand lc = commandDao.getCommand(id);
                allCommands.add(lc);
                allCommandsMap.put(lc.getCommandId(), lc);
                
                lBase = lc;
            }
            break;

        case UPDATE:
            LiteCommand lc = commandDao.getCommand(id);
            allCommandsMap.put(id, lc);
            
            lBase = lc;
            break;

        case DELETE:
            for (int i = 0; i < allCommands.size(); i++) {
                if (allCommands.get(i).getCommandId() == id) {
                    allCommandsMap.remove(new Integer(id));
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

    private synchronized LiteBase handleConfigChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allConfigs == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allConfigs.size(); i++) {
                if (allConfigs.get(i).getConfigID() == id) {
                    alreadyAdded = true;
                    lBase = allConfigs.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteConfig lh = new LiteConfig(id);
                lh.retrieve(databaseAlias);
                allConfigs.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allConfigs.size(); i++) {
                if (allConfigs.get(i).getConfigID() == id) {
                    allConfigs.get(i).retrieve(databaseAlias);
                    lBase = allConfigs.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allConfigs.size(); i++) {
                if (allConfigs.get(i).getConfigID() == id) {
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

    private synchronized LiteBase handleTagChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lTag = null;

        // if the storage is not already loaded, we must not care about it
        if (allTags == null) {
            return lTag;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allTags.size(); i++) {
                if (allTags.get(i).getTagID() == id) {
                    alreadyAdded = true;
                    lTag = allTags.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteTag lh = new LiteTag(id);
                lh.retrieve(databaseAlias);
                allTags.add(lh);
                lTag = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allTags.size(); i++) {
                if (allTags.get(i).getTagID() == id) {
                    allTags.get(i).retrieve(databaseAlias);
                    lTag = allTags.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allTags.size(); i++) {
                if (allTags.get(i).getTagID() == id) {
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

    private synchronized LiteBase handleLMProgramConstraintChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allLMProgramConstraints == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allLMProgramConstraints.size(); i++) {
                if (allLMProgramConstraints.get(i).getConstraintID() == id) {
                    alreadyAdded = true;
                    lBase = allLMProgramConstraints.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteLMConstraint lh = new LiteLMConstraint(id);
                lh.retrieve(databaseAlias);
                allLMProgramConstraints.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allLMProgramConstraints.size(); i++) {
                if (allLMProgramConstraints.get(i).getConstraintID() == id) {
                    allLMProgramConstraints.get(i).retrieve(databaseAlias);
                    lBase = allLMProgramConstraints.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allLMProgramConstraints.size(); i++) {
                if (allLMProgramConstraints.get(i).getConstraintID() == id) {
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

    private synchronized LiteBase handleNotificationGroupChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allNotificationGroups == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allNotificationGroups.size(); i++) {
                if (allNotificationGroups.get(i).getNotificationGroupID() == id) {
                    alreadyAdded = true;
                    lBase = allNotificationGroups.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteNotificationGroup lg = new LiteNotificationGroup(id);
                lg.retrieve(databaseAlias);
                allNotificationGroups.add(lg);
                lBase = lg;
            }
            break;
        case UPDATE:
            for (int i = 0; i < allNotificationGroups.size(); i++) {
                if (allNotificationGroups.get(i).getNotificationGroupID() == id) {
                    allNotificationGroups.get(i).retrieve(databaseAlias);
                    lBase = allNotificationGroups.get(i);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allNotificationGroups.size(); i++) {
                if (allNotificationGroups.get(i).getNotificationGroupID() == id) {
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

    private synchronized LiteBase handlePointChange(DbChangeType dbChangeType, int id, boolean noObjectNeeded) {
        // this method is really simply now that we don't cache points
        if (noObjectNeeded) {
            return null;
        }
        LiteBase lBase = null;

        switch (dbChangeType) {
        case ADD:
            // Return this so clients like the editor get a db change :(
            lBase = pointDao.getLitePoint(id);
            break;

        case UPDATE:
            lBase = pointDao.getLitePoint(id);
            break;

        case DELETE:
            break;

        default:
            break;
        }

        return lBase;
    }

    private synchronized LiteBase handleStateGroupChange(DbChangeType dbChangeType, int id) {
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allStateGroupMap == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            lBase = allStateGroupMap.get(new Integer(id));
            if (lBase == null) {
                LiteStateGroup lsg = new LiteStateGroup(id);
                lsg.retrieve(databaseAlias);
                allStateGroupMap.put(new Integer(lsg.getStateGroupID()), lsg);
                lBase = lsg;
            }
            break;

        case UPDATE:
            LiteStateGroup ly = allStateGroupMap.get(new Integer(id));
            ly.retrieve(databaseAlias);

            lBase = ly;
            break;

        case DELETE:
            lBase = allStateGroupMap.remove(new Integer(id));
            break;

        default:
            releaseAllStateGroups();
            break;
        }

        return lBase;
    }

    private LiteBase handleCustomerChange(DbChangeType dbChangeType, int id, String dbCategory, boolean noObjectNeeded) {
        LiteBase lBase = null;

        switch (dbChangeType) {
        case ADD:
        case UPDATE:
            customerCache.remove(new Integer(id));
            if (!noObjectNeeded) {
                LiteCustomer lc;
                if (dbCategory.equalsIgnoreCase(DBChangeMsg.CAT_CI_CUSTOMER)) {
                    lc = new LiteCICustomer(id);
                } else {
                    lc = new LiteCustomer(id);
                }
                lc.retrieve(databaseAlias);
                customerCache.put(new Integer(lc.getCustomerID()), lc);

                lBase = lc;
            }
            break;

        case DELETE:
            customerCache.remove(new Integer(id));
            break;
        default:
            releaseAllCustomers();
            break;
        }

        return lBase;
    }

    private synchronized LiteBase handleYukonGroupChange(DbChangeType dbChangeType, int id) {
        boolean alreadyAdded = false;
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allYukonGroups != null) {

            switch (dbChangeType) {
            case ADD:
                for (int i = 0; i < allYukonGroups.size(); i++) {
                    if (allYukonGroups.get(i).getGroupID() == id) {
                        alreadyAdded = true;
                        lBase = allYukonGroups.get(i);
                        break;
                    }
                }
                if (!alreadyAdded) {
                    LiteYukonGroup lcst = new LiteYukonGroup(id);
                    lcst.retrieve(databaseAlias);
                    allYukonGroups.add(lcst);
                    lBase = lcst;
                }
                break;

            case UPDATE:
                for (int i = 0; i < allYukonGroups.size(); i++) {
                    if (allYukonGroups.get(i).getGroupID() == id) {
                        allYukonGroups.get(i).retrieve(databaseAlias);
                        lBase = allYukonGroups.get(i);
                        break;
                    }
                }
                break;

            case DELETE:
                for (int i = 0; i < allYukonGroups.size(); i++) {
                    if (allYukonGroups.get(i).getGroupID() == id) {
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

    private LiteBase handleUserGroupChange(DbChangeType dbChangeType, int userGroupId) {
        switch (dbChangeType) {
        case ADD:
        case UPDATE:
            return userGroupDao.getLiteUserGroup(userGroupId);

        case DELETE:
        default:
            return null;
        }
    }

    private synchronized LiteBase handleYukonUserChange(DbChangeType dbChangeType, int id, boolean noObjectNeeded) {
        LiteBase lBase = null;

        LiteYukonUser lu = null;
        switch (dbChangeType) {
        case ADD:
            if (!noObjectNeeded) {
                lu = new LiteYukonUser(id);
                lu.retrieve();
                lBase = lu;
            }
            break;

        case UPDATE:
            if (!noObjectNeeded) {
                lu = new LiteYukonUser(id);
                lu.retrieve();
                lBase = lu;
            }
            adjustUserMappings(id);
            break;

        case DELETE:
            adjustUserMappings(id);
            break;

        case NONE:
            break;
        }

        return lBase;
    }

    private synchronized LiteBase handleYukonPAOChange(DbChangeType dbChangeType, int id) {
        LiteBase lBase = null;

        // if the storage is not already loaded, we must not care about it
        if (allYukonPAObjects == null || allPAOsMap == null) {
            return lBase;
        }

        if (id == 0) { // A force reload of all paobjects was sent.
            releaseAllYukonPAObjects();
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:

            lBase = allPAOsMap.get(new Integer(id));
            if (lBase == null) {
                LiteYukonPAObject ly = new LiteYukonPAObject(id);
                ly.retrieve(databaseAlias);
                allYukonPAObjects.add(ly);
                allPAOsMap.put(new Integer(ly.getYukonID()), ly);

                lBase = ly;
            }
            break;

        case UPDATE:

            LiteYukonPAObject ly = allPAOsMap.get(new Integer(id));
            ly.retrieve(databaseAlias);

            lBase = ly;
            break;

        case DELETE:
            for (int i = 0; i < allYukonPAObjects.size(); i++) {
                if (allYukonPAObjects.get(i).getYukonID() == id) {
                    allPAOsMap.remove(new Integer(id));
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

    @Override
    public synchronized void releaseAllAlarmCategories() {
        allAlarmCategories = null;
    }

    /**
     * Drop all the junk we have accumulated.
     * Please be keeping this method in sync
     */
    @Override
    public synchronized void releaseAllCache() {
        
        allYukonPAObjects = null;
        allSystemPoints = null;
        allStateGroupMap = null;
        allNotificationGroups = null;
        allContactNotifsMap = null;

        allAlarmCategories = null;
        allGraphDefinitions = null;
        allMCTs = null;
        allHolidaySchedules = null;
        allBaselines = null;
        allConfigs = null;
        allDeviceMeterGroups = null;
        allPointLimits = null;
        allYukonImages = null;
        allCICustomers = null;
        allLMProgramConstraints = null;
        allLMScenarios = null;
        allLMScenarioProgs = null;

        allTags = null;
        allSeasonSchedules = null;
        allDeviceTypeCommands = null;
        allTOUSchedules = null;
        allTOUDays = null;

        allYukonRoles = null;
        allYukonRoleProperties = null;
        allYukonGroups = null;

        allYukonGroupRolePropertiesMap = null;

        // lists that are created by the joining/parsing of existing lists
        allUnusedCCDevices = null;
        allCapControlFeeders = null;
        allCapControlSubBuses = null;
        allCapControlSubStations = null;
        allDevices = null;
        allLMPrograms = null;
        allLMControlAreas = null;
        allLMGroups = null;
        allLoadManagement = null;
        allPorts = null;
        allRoutes = null;

        // Maps that are created by the joining/parsing of existing lists
        allPAOsMap = null;
        customerCache.clear();
        allContactsMap.clear();
    }

    @Override
    public synchronized void releaseAllContacts() {
        allContactsMap.clear();
        allContactNotifsMap = null;
    }

    @Override
    public synchronized void releaseAllDeviceMeterGroups() {
        allDeviceMeterGroups = null;
    }

    @Override
    public synchronized void releaseAllYukonImages() {
        allYukonImages = null;
    }

    @Override
    public synchronized void releaseAllGraphDefinitions() {
        allGraphDefinitions = null;
    }

    @Override
    public synchronized void releaseAllHolidaySchedules() {
        allHolidaySchedules = null;
    }

    @Override
    public synchronized void releaseAllBaselines() {
        allBaselines = null;
    }

    @Override
    public synchronized void releaseAllSeasonSchedules() {
        allSeasonSchedules = null;
    }

    @Override
    public synchronized void releaseAllCommands() {
        allCommands = null;
        allCommandsMap = null;
    }

    @Override
    public synchronized void releaseAllTOUSchedules() {
        allTOUSchedules = null;
    }

    @Override
    public synchronized void releaseAllTOUDays() {
        allTOUDays = null;
    }

    @Override
    public synchronized void releaseAllConfigs() {
        allConfigs = null;
    }

    @Override
    public synchronized void releaseAllTags() {
        allTags = null;
    }

    @Override
    public synchronized void releaseAllLMProgramConstraints() {
        allLMProgramConstraints = null;
    }

    @Override
    public synchronized void releaseAllLMScenarios() {
        allLMScenarios = null;
    }

    @Override
    public synchronized void releaseAllLMPAOExclusions() {
        allLMPAOExclusions = null;
    }

    @Override
    public synchronized void releaseAllNotificationGroups() {
        allNotificationGroups = null;
    }

    @Override
    public void releaseAllCustomers() {
        customerCache.clear();
    }

    public synchronized void releaseAllSystemPoints() {
        allSystemPoints = null;
    }

    public synchronized void releaseAllYukonGroups() {
        allYukonGroups = null;
        releaseUserRoleMap();
        releaseUserRolePropertyValueMap();
    }

    @Override
    public synchronized void releaseAllStateGroups() {
        allStateGroupMap = null;
    }

    @Override
    public synchronized void releaseAllYukonPAObjects() {
        allYukonPAObjects = null;
        allPAOsMap = null;
    }

    @Override
    public synchronized void releaseAllDeviceTypeCommands() {
        allDeviceTypeCommands = null;
    }

    /*
     * This method takes a userid and a roleid. It checks userRoleMap to see
     * if this role has been recovered from the db before. If it has not, it will
     * be taken directly from the database.
     */
    @Override
    public synchronized LiteYukonRole getARole(LiteYukonUser user, int roleID) {
        MapKeyInts keyInts = new MapKeyInts(user.getLiteID(), roleID);
        LiteYukonRole specifiedRole = null;
        // check cache for previous grabs
        if (userRoleMap == null) {
            userRoleMap = new HashMap<MapKeyInts, LiteYukonRole>();
        } else {
            specifiedRole = userRoleMap.get(keyInts);
        }

        // not in cache, go to DB.
        if (specifiedRole == null && !userRoleMap.containsKey(keyInts)) {
            specifiedRole = YukonUserRolePropertyLookup.loadSpecificRole(user, roleID);
            /*
             * found it, put it in the cache for later searches
             * Go ahead and put in null values, too. This will make it faster to check if this
             * role exists for this user next time around.
             */
            userRoleMap.put(keyInts, specifiedRole);
            /*
             * This is useful for checking the map after a DBChangeMsg is received to see if
             * this user exists in the map. If it does, then the map should be reset.
             */
            userRoleMap.put(new MapKeyInts(user.getLiteID(), CtiUtilities.NONE_ZERO_ID), null);
        }

        return specifiedRole;
    }

    /*
     * This method takes a userid to look for the relevant contact. It checks userContactMap
     * to see if this contact has been recovered from the db before. If it has not, it will
     * be taken directly from the database.
     */
    @Override
    public LiteContact getAContactByUserID(int userID) {
        LiteContact specifiedContact = null;
        // check cache for previous grabs
        specifiedContact = userContactMap.get(userID);

        // not in cache, go to DB.
        if (specifiedContact == null) {
            List<LiteContact> userContacts = contactDao.getContactsByLoginId(userID);
            if (!CollectionUtils.isEmpty(userContacts)) {
                // we will assume there is only one in this list, dao is accomodating multiple contacts per
                // user...oh boy.
                specifiedContact = userContacts.get(0);
                ;
                // found it, put it in the cache for later searches
                if (specifiedContact != null) {
                    userContactMap.put(userID, specifiedContact);
                    allContactsMap.put(specifiedContact.getContactID(), specifiedContact);
                }
            }
        }

        return specifiedContact;
    }

    @Override
    public synchronized LiteContact getAContactByContactID(int contactID) {
        // check cache for previous grabs
        LiteContact specifiedContact = allContactsMap.get(contactID);

        // not in cache, go to DB.
        if (specifiedContact == null) {
            specifiedContact = contactDao.getContact(contactID);

            // found it, put it in the cache for later searches
            if (specifiedContact != null) {
                allContactsMap.put(contactID, specifiedContact);
                userContactMap.put(specifiedContact.getLoginID(), specifiedContact);
            }
        }

        return specifiedContact;
    }

    @Override
    public synchronized LiteContactNotification getAContactNotifByNotifID(int contNotifyID) {
        loadAllContacts();

        LiteContactNotification specifiedNotify = allContactNotifsMap.get(new Integer(contNotifyID));

        // not in cache, go to DB.
        if (specifiedNotify == null) {
            specifiedNotify = contactNotificationDao.getNotificationForContact(contNotifyID);
            // found it, put it in the cache for later searches
            allContactNotifsMap.put(new Integer(contNotifyID), specifiedNotify);
            // make sure the contact is also loaded
            getAContactByContactID(specifiedNotify.getContactID());
        }

        return specifiedNotify;
    }

    /*
     * This method takes a primaryContactId to look for the relevant customer. It will
     * be taken directly from the database.
     */
    @Override
    public LiteCustomer getACustomerByPrimaryContactID(int primaryContactId) {
        return YukonCustomerLookup.loadSpecificCustomerByPrimaryContactID(primaryContactId);
    }

    /*
     * This method takes a customerid to look for the relevant customer. It checks allCustomersMap
     * to see if this customer has been recovered from the db before. If it has not, it will
     * be taken directly from the database.
     */
    @Override
    public LiteCustomer getACustomerByCustomerID(int customerID) {
        LiteCustomer specifiedCustomer = null;
        // check cache for previous grabs
        specifiedCustomer = customerCache.get(customerID);

        // not in cache, go to DB.
        if (specifiedCustomer == null) {
            specifiedCustomer = YukonCustomerLookup.loadSpecificCustomer(customerID);
            // found it, put it in the cache for later searches
            customerCache.put(customerID, specifiedCustomer);
        }

        return specifiedCustomer;
    }

    /*
     * Scrub out the userRoleMap. Any LiteYukonRoles that were in here will have to be
     * recovered from the database.
     */
    @Override
    public synchronized void releaseUserRoleMap() {
        userRoleMap = null;
    }

    /*
     * Scrub out the userRolePropertyValueMap. Any String values that were in here will have to be
     * recovered from the database.
     */
    @Override
    public synchronized void releaseUserRolePropertyValueMap() {
        userRolePropertyValueMap = null;
    }

    @Override
    public synchronized void releaseUserContactMap() {
        userContactMap.clear();
    }

    /*
     * Upon receiving a DBChangeMsg for a user or a group, this method
     * checks to see if this user is in the map. There is no point in
     * resetting these mappings if the user that was changed is not a one
     * that has been accessed before (and therefore mapped here).
     */
    public synchronized void adjustUserMappings(int userID) {
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

    @Override
    public synchronized List<Integer> getDevicesByCommPort(int portId) {

        return DeviceCommPortLoader.getDevicesByCommPort(portId);

    }

    @Override
    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        return DeviceCommPortLoader.getDevicesByDeviceAddress(masterAddress, slaveAddress);

    }
}
