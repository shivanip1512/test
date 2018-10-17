package com.cannontech.mbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.core.users.dao.UserGroupDao;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.server.cache.BaselineLoader;
import com.cannontech.yukon.server.cache.CICustomerLoader;
import com.cannontech.yukon.server.cache.ConfigLoader;
import com.cannontech.yukon.server.cache.ContactLoader;
import com.cannontech.yukon.server.cache.ContactNotificationGroupLoader;
import com.cannontech.yukon.server.cache.DeviceCommPortLoader;
import com.cannontech.yukon.server.cache.GraphDefinitionLoader;
import com.cannontech.yukon.server.cache.HolidayScheduleLoader;
import com.cannontech.yukon.server.cache.LMConstraintLoader;
import com.cannontech.yukon.server.cache.LMPAOExclusionLoader;
import com.cannontech.yukon.server.cache.LMScenarioProgramLoader;
import com.cannontech.yukon.server.cache.SeasonScheduleLoader;
import com.cannontech.yukon.server.cache.SystemPointLoader;
import com.cannontech.yukon.server.cache.TOUScheduleLoader;
import com.cannontech.yukon.server.cache.YukonGroupLoader;
import com.cannontech.yukon.server.cache.YukonGroupRoleLoader;
import com.cannontech.yukon.server.cache.YukonRoleLoader;
import com.cannontech.yukon.server.cache.YukonRolePropertyLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ServerDatabaseCache extends CTIMBeanBase implements IDatabaseCache {
    
    // stores a soft reference to the cache
    private static ServerDatabaseCache cache;
    
    @Autowired private CommandDao commandDao;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private GraphDao graphDao;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonImageDao imageDao;
    @Autowired private YukonUserDao yukonUserDao;
    
    private final String databaseAlias = CtiUtilities.getDatabaseAlias();
    
    private Map<Integer, LiteYukonPAObject> allLitePaos;
    private Set<PaoType> allPaoTypes = Collections.synchronizedSet(new HashSet<PaoType>());
    private Map<Integer, SimpleMeter> allMeters;
    
    private List<LitePoint> allSystemPoints;
    private List<LiteNotificationGroup> allNotificationGroups;
    
    private List<LiteGraphDefinition> allGraphDefinitions;
    private List<LiteYukonPAObject> allMcts;
    private List<LiteHolidaySchedule> allHolidaySchedules;
    private List<LiteBaseline> allBaselines;
    private List<LiteConfig> allConfigs;
    private Map<Integer, LitePointLimit> allPointLimits;
    private Map<Integer, LiteYukonImage> images;
    private volatile List<LiteCICustomer> allCICustomers;
    private List<LiteLMConstraint> allLMProgramConstraints;
    private List<LiteYukonPAObject> allLMScenarios;
    private List<LiteLMProgScenario> allLMScenarioProgs;
    private List<LiteLMPAOExclusion> allLMPAOExclusions;
    
    private List<LiteSeasonSchedule> allSeasonSchedules;
    private List<LiteTOUSchedule> allTouSchedules;
    
    private List<LiteYukonRole> allYukonRoles;
    private List<LiteYukonRoleProperty> allYukonRoleProperties;
    private List<LiteYukonGroup> allYukonGroups;
    
    private Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> allYukonGroupRolePropertiesMap;
    
    // lists that are created by the joining/parsing of existing lists
    private List<LiteYukonPAObject> allDevices;
    private List<LiteYukonPAObject> allLMPrograms;
    private List<LiteYukonPAObject> allLMControlAreas;
    private Map<String, LiteYukonPAObject> allLMGroups;
    private List<LiteYukonPAObject> allLoadManagement;
    private List<LiteYukonPAObject> allPorts;
    
    private final Map<Integer, LiteCustomer> customerCache = new ConcurrentHashMap<>(1000, .75f, 30);
    private final Map<Integer, LiteContact> allContactsMap = new ConcurrentHashMap<>(1000, .75f, 30);
    
    // derived from allYukonUsers,allYukonRoles,allYukonGroups
    // see type info in IDatabaseCache
    private Map<Integer, LiteDeviceTypeCommand> allDeviceTypeCommands;
    private Map<Integer, LiteCommand> allCommands;
    private Map<Integer, LiteYukonPAObject> allRoutes;
    private Map<Integer, LiteContactNotification> allContactNotifsMap;
    
    private final Map<Integer, LiteContact> userContactMap = new ConcurrentHashMap<>(1000, .75f, 30);
    
    @Override
    public synchronized DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem, DbChangeType dbChangeType) {
        return newItem.getDBChangeMsgs(dbChangeType);
    }
    
    @Override
    public synchronized Map<Integer, LiteYukonImage> getImages() {
        
        if (images == null) {
            images = new ConcurrentHashMap<>();
            for (LiteYukonImage image : imageDao.load()) {
                images.put(image.getImageID(), image);
            }
        }
        
        return images;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllCapControlFeeders() {
        
        List<LiteYukonPAObject> feeders = new ArrayList<>();
        
        for (LiteYukonPAObject pao : getAllYukonPAObjects()) {
            if (pao.getPaoType() == PaoType.CAP_CONTROL_FEEDER) {
                feeders.add(pao);
            }
        }
        
        return feeders;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllCapControlSubBuses() {
        
        List<LiteYukonPAObject> buses = new ArrayList<>();
        
        for (LiteYukonPAObject pao : getAllYukonPAObjects()) {
            PaoType type = pao.getPaoType();
            if (type == PaoType.CAP_CONTROL_SUBBUS) {
                buses.add(pao);
            }
        }
        
        return buses;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllCapControlSubStations() {
        
        List<LiteYukonPAObject> subs = new ArrayList<>();
        
        for (LiteYukonPAObject pao : getAllYukonPAObjects()) {
            if (pao.getPaoType() == PaoType.CAP_CONTROL_SUBSTATION) {
                subs.add(pao);
            }
        }
        
        return subs;
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
    public synchronized Map<Integer, SimpleMeter> getAllMeters() {
        
        if (allMeters != null) {
            return allMeters;
        }
        
        allMeters = new ConcurrentHashMap<Integer, SimpleMeter>();
        List<SimpleMeter> allSimpleMeters = meterDao.getAllSimpleMeters();
        for (SimpleMeter meter : allSimpleMeters) {
            allMeters.put(meter.getPaoIdentifier().getPaoId(), meter);
        }
        
        return allMeters;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllDevices() {
        
        if (allDevices == null) {
            allDevices = new ArrayList<>();
            
            for (LiteYukonPAObject pao : getAllYukonPAObjects()) {
                if (pao.getPaoType().getPaoCategory() == PaoCategory.DEVICE) {
                    allDevices.add(pao);
                }
            }
        }
        
        return allDevices;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllMCTs() {
        
        if (allMcts == null) {
            allMcts = new ArrayList<>();
            
            for (LiteYukonPAObject device : getAllDevices()) {
                if (device.getPaoType().isMct()) {
                    allMcts.add(device);
                }
            }
        }
        
        return allMcts;
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
        HolidayScheduleLoader holidayScheduleLoader = new HolidayScheduleLoader(allHolidaySchedules);
        holidayScheduleLoader.run();
        
        return allHolidaySchedules;
    }
    
    @Override
    public synchronized List<LiteBaseline> getAllBaselines() {
        
        if (allBaselines != null) {
            return allBaselines;
        }
        
        allBaselines = new ArrayList<>();
        BaselineLoader baselineLoader = new BaselineLoader(allBaselines);
        baselineLoader.run();
        
        return allBaselines;
    }
    
    @Override
    public synchronized List<LiteSeasonSchedule> getAllSeasonSchedules() {
        
        if (allSeasonSchedules != null) {
            return allSeasonSchedules;
        }
        
        allSeasonSchedules = new ArrayList<>();
        SeasonScheduleLoader seasonLoader = new SeasonScheduleLoader(allSeasonSchedules);
        seasonLoader.run();
        
        return allSeasonSchedules;
    }
    
    @Override
    public synchronized List<LiteTOUSchedule> getAllTOUSchedules() {
        
        if (allTouSchedules != null) {
            return allTouSchedules;
        }
        
        allTouSchedules = new ArrayList<>();
        TOUScheduleLoader touLoader = new TOUScheduleLoader(allTouSchedules);
        touLoader.run();
        
        return allTouSchedules;
    }
    
    @Override
    public synchronized Map<Integer, LiteCommand> getAllCommands() {
        
        if (allCommands == null) {
            allCommands = new ConcurrentHashMap<Integer, LiteCommand>();
            for (LiteCommand command : commandDao.getAllCommands()) {
                allCommands.put(command.getLiteID(), command);
            }
        }
        
        return allCommands;
    }

    @Override
    public synchronized List<LiteConfig> getAllConfigs() {
        if (allConfigs != null) {
            return allConfigs;
        }

        allConfigs = new ArrayList<>();
        ConfigLoader configLoader = new ConfigLoader(allConfigs);
        configLoader.run();
        return allConfigs;
    }

    @Override
    public synchronized List<LiteLMConstraint> getAllLMProgramConstraints() {

        if (allLMProgramConstraints != null) {
            return allLMProgramConstraints;
        }

        allLMProgramConstraints = new ArrayList<>();
        LMConstraintLoader lmConstraintsLoader = new LMConstraintLoader(allLMProgramConstraints);
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
            allLMPrograms = new ArrayList<>();

            for (int i = 0; i < getAllLoadManagement().size(); i++) {
                if (getAllLoadManagement().get(i).getPaoType() == PaoType.LM_CURTAIL_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_DIRECT_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_SEP_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_ECOBEE_PROGRAM
                    || getAllLoadManagement().get(i).getPaoType() == PaoType.LM_ENERGY_EXCHANGE_PROGRAM
					|| getAllLoadManagement().get(i).getPaoType() == PaoType.LM_HONEYWELL_PROGRAM) {
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
    public synchronized List<LiteYukonPAObject> getAllLMGroups() {
        List<LiteYukonPAObject> allLMGroups = new ArrayList<>(getAllLMGroupsMap().values());
        Collections.sort(allLMGroups,  PaoUtils.NAME_COMPARE);
        return allLMGroups;
    }

    @Override
    public Map<String, LiteYukonPAObject> getAllLMGroupsMap() {
        if (allLMGroups == null) {
            allLMGroups = new ConcurrentHashMap<>();

            for (LiteYukonPAObject liteYukonPAObject : getAllLoadManagement()) {
                if (liteYukonPAObject.getPaoType().isLoadGroup()) {
                    allLMGroups.put(liteYukonPAObject.getPaoName(), liteYukonPAObject);
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
            new ContactNotificationGroupLoader(allNotificationGroups);
        notifLoader.run();

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
        SystemPointLoader systemPointLoader = new SystemPointLoader(allSystemPoints);
        systemPointLoader.run();
        return allSystemPoints;
    }

    @Override
    public synchronized Map<Integer, LiteYukonPAObject> getAllPaosMap() {
        
        if (allLitePaos != null) {
            return allLitePaos;
        }
        
        allLitePaos = new ConcurrentHashMap<Integer, LiteYukonPAObject>();
        
        List<LiteYukonPAObject> paos = paoDao.getAllPaos();
        for (LiteYukonPAObject pao : paos) {
            allLitePaos.put(pao.getLiteID(), pao);
        }
        
        return allLitePaos;
    }
    
    @Override
    public synchronized List<LiteYukonPAObject> getAllYukonPAObjects() {
        List<LiteYukonPAObject> allPaos = new ArrayList<>(getAllPaosMap().values());
        Collections.sort(allPaos, PaoUtils.NAME_COMPARE); 
        return allPaos;
    }
    
    @Override
    public synchronized Map<Integer, LiteContactNotification> getAllContactNotifsMap() {
        loadAllContacts();
        return allContactNotifsMap;
    }
    
    @Override
    public synchronized Map<Integer, LitePointLimit> getAllPointLimits() {
        
        if (allPointLimits != null) {
            return allPointLimits;
        }
        
        allPointLimits = new ConcurrentHashMap<Integer, LitePointLimit>();
        
        for (LitePointLimit limit : pointDao.getAllPointLimits()) {
            allPointLimits.put(limit.getPointID(), limit);
        }
        
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
        List<LiteYukonPAObject> allRoutePaos = new ArrayList<>(getAllRoutesMap().values());
        Collections.sort(allRoutePaos, PaoUtils.NAME_COMPARE); 
        return allRoutePaos;
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


    private synchronized LiteBase handleYukonImageChange(DbChangeType type, int id) {
        
        if (type == DbChangeType.ADD || type == DbChangeType.UPDATE) {
            LiteYukonImage image = imageDao.load(id);
            getImages().put(image.getImageID(), image);
            return image;
        } else if (type == DbChangeType.DELETE) {
            return getImages().remove(id);
        } else {
            releaseAllYukonImages();
            return null;
        }
        
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
    @SuppressWarnings("deprecation")
    @Override
    public synchronized LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg, boolean noObjectNeeded) {
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
            
            if (dbCategory.equalsIgnoreCase(PaoCategory.DEVICE.getDbString())) {
                allDevices = null;
                allMcts = null;
                allLoadManagement = null; // PAOGroups are here, oops!
                
                PaoType paoType = PaoType.getForDbString(dbChangeMsg.getObjectType());
                
				/*
				 * This is special case for the RF-DA/RFN-1200 device.
				 * 
				 * This oddball device-is-a-port scheme will probably be
				 * replaced with something more akin to the way the RfnAddress
				 * table is attached to RF meters, so at this point this port check
				 * can be removed.
				 */
                if(paoType.isPort()){
                	allPorts = null;
                }
                if (paoType.hasMeterNumber()) {
                    handleMeterNumberChange(dbChangeType, id);
                }
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.LOADMANAGEMENT.getDbString())) {
                allLoadManagement = null;
                allLMPrograms = null;
                allLMControlAreas = null;
                allLMGroups = null;
                allLMScenarios = null;
                allLMScenarioProgs = null;
                allLMPAOExclusions = null;
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.PORT.getDbString())) {
                allPorts = null;
            } else if (dbCategory.equalsIgnoreCase(PaoCategory.ROUTE.getDbString())) {
                allRoutes = null;
            }
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
    
    @Override
    public synchronized Set<PaoType> getAllPaoTypes() {
        return ImmutableSet.copyOf(getPaoTypesSet());
    }
    
    private synchronized Set<PaoType> getPaoTypesSet() {
        if (allPaoTypes.isEmpty()) {
            allPaoTypes = Sets.newHashSet(paoDao.getExistingPaoTypes());
        }
        return allPaoTypes;
    }
    
    private synchronized void handleMeterNumberChange(DbChangeType type, int id) {
        
        if (id == 0) { // A force reload of all devicemetergroups was sent.
            releaseAllMeters();
        }
        
        if (type == DbChangeType.ADD || type == DbChangeType.UPDATE) {
            SimpleMeter meter = meterDao.getSimpleMeterForId(id);
            getAllMeters().put(meter.getPaoIdentifier().getPaoId(), meter);
        } else if (type == DbChangeType.DELETE) {
            getAllMeters().remove(id);
        } else {
            releaseAllMeters();
        }
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
                LiteGraphDefinition lsg = graphDao.retrieveLiteGraphDefinition(id);
                allGraphDefinitions.add(lsg);
                lBase = lsg;
            }
            break;
        case UPDATE:
            for (LiteGraphDefinition liteGraphDefinition : allGraphDefinitions) {
                if (liteGraphDefinition.getGraphDefinitionID() == id) {
                    allGraphDefinitions.remove(liteGraphDefinition);
                    lBase = graphDao.retrieveLiteGraphDefinition(id);
                    allGraphDefinitions.add((LiteGraphDefinition)lBase);
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
                LiteHolidaySchedule lh = HolidayScheduleLoader.getForId(id);
                allHolidaySchedules.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (LiteHolidaySchedule liteHolidaySchedule : allHolidaySchedules) {
                if (liteHolidaySchedule.getHolidayScheduleID() == id) {
                    allHolidaySchedules.remove(liteHolidaySchedule);
                    lBase = HolidayScheduleLoader.getForId(id);
                    allHolidaySchedules.add((LiteHolidaySchedule)lBase);
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
                LiteBaseline lh = BaselineLoader.getForId(id);
                allBaselines.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (LiteBaseline liteBaseline : allBaselines) {
                if (liteBaseline.getBaselineID() == id) {
                    allBaselines.remove(liteBaseline);
                    lBase = BaselineLoader.getForId(id);
                    allBaselines.add((LiteBaseline)lBase);
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
                LiteSeasonSchedule lh = SeasonScheduleLoader.getForId(id);
                allSeasonSchedules.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (LiteSeasonSchedule liteSeasonSchedule : allSeasonSchedules) {
                if (liteSeasonSchedule.getScheduleID() == id) {
                    allSeasonSchedules.remove(liteSeasonSchedule);
                    lBase = SeasonScheduleLoader.getForId(id);
                    allSeasonSchedules.add((LiteSeasonSchedule)lBase);
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
        if (allTouSchedules == null) {
            return lBase;
        }

        switch (dbChangeType) {
        case ADD:
            for (int i = 0; i < allTouSchedules.size(); i++) {
                if (allTouSchedules.get(i).getScheduleID() == id) {
                    alreadyAdded = true;
                    lBase = allTouSchedules.get(i);
                    break;
                }
            }
            if (!alreadyAdded) {
                LiteTOUSchedule lh = TOUScheduleLoader.getForId(id);
                allTouSchedules.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (LiteTOUSchedule liteTOUSchedule : allTouSchedules) {
                if (liteTOUSchedule.getScheduleID() == id) {
                    allTouSchedules.remove(liteTOUSchedule);
                    lBase = TOUScheduleLoader.getForId(id);
                    allTouSchedules.add((LiteTOUSchedule)lBase);
                    break;
                }
            }
            break;
        case DELETE:
            for (int i = 0; i < allTouSchedules.size(); i++) {
                if (allTouSchedules.get(i).getScheduleID() == id) {
                    lBase = allTouSchedules.remove(i);
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

    private synchronized LiteBase handleCommandChange(DbChangeType type, int commandId) {
        
        if (type == DbChangeType.ADD || type == DbChangeType.UPDATE) {
            
            LiteCommand lc = commandDao.getCommand(commandId);
            getAllCommands().put(lc.getCommandId(), lc);
            return lc;
            
        } else if (type == DbChangeType.DELETE) {
            return getAllCommands().remove(commandId);
        } else {
            releaseAllCommands();
            return null;
        }
        
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
                LiteConfig lh = ConfigLoader.getForId(id);
                allConfigs.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (LiteConfig liteConfig : allConfigs) {
                if (liteConfig.getConfigID() == id) {
                    allConfigs.remove(liteConfig);
                    lBase = ConfigLoader.getForId(id);
                    allConfigs.add((LiteConfig)lBase);
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
                LiteLMConstraint lh = LMConstraintLoader.getForId(id);
                allLMProgramConstraints.add(lh);
                lBase = lh;
            }
            break;
        case UPDATE:
            for (LiteLMConstraint liteLMConstraint : allLMProgramConstraints) {
                if (liteLMConstraint.getConstraintID() == id) {
                    allLMProgramConstraints.remove(liteLMConstraint);
                    lBase = LMConstraintLoader.getForId(id);
                    allLMProgramConstraints.add((LiteLMConstraint)lBase);
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
                LiteNotificationGroup lg = ContactNotificationGroupLoader.getForId(id);
                allNotificationGroups.add(lg);
                lBase = lg;
            }
            break;
        case UPDATE:
            for (LiteNotificationGroup lg : allNotificationGroups) {
                if (lg.getNotificationGroupID() == id) {
                    allNotificationGroups.remove(lg);
                    lBase = ContactNotificationGroupLoader.getForId(id);
                    allNotificationGroups.add((LiteNotificationGroup)lBase);
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
            // Create dummy object so dbChangeMsgs can be processed to update dbEditor (YUK-17882)
            lBase = new LitePoint(id);
            break;

        default:
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
                    lc = customerDao.getLiteCICustomer(id);
                } else {
                    lc = customerDao.getLiteCustomer(id);
                }
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
                    LiteYukonGroup liteYukonGroup = yukonGroupDao.getLiteYukonGroup(id);
                    allYukonGroups.add(liteYukonGroup);
                    lBase = liteYukonGroup;
                }
                break;

            case UPDATE:
                for (LiteYukonGroup liteYukonGroup : allYukonGroups) {
                    if (liteYukonGroup.getGroupID() == id) {
                        allYukonGroups.remove(liteYukonGroup);
                        lBase = yukonGroupDao.getLiteYukonGroup(id);
                        allYukonGroups.add((LiteYukonGroup)lBase);
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
                lu = yukonUserDao.getLiteYukonUser(id);
                lBase = lu;
            }
            break;

        case UPDATE:
            if (!noObjectNeeded) {
                lu = yukonUserDao.getLiteYukonUser(id);
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
    
    private synchronized LiteBase handleYukonPAOChange(DbChangeType type, int id) {
        
        LiteBase lBase = null;
        
        if (id == 0) { 
            // A force reload of all paobjects was sent.
            releaseAllYukonPAObjects();
            return lBase;
        } else if (type == DbChangeType.ADD) {
            lBase = getAllPaosMap().get(id);
            if (lBase == null) {
                LiteYukonPAObject pao = paoDao.getLiteYukonPAO(id);
                getAllPaosMap().put(pao.getLiteID(), pao);
                lBase = pao;
                getPaoTypesSet().add(pao.getPaoType());
            }
        } else if (type == DbChangeType.UPDATE) {
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(id);
            getAllPaosMap().put(pao.getLiteID(), pao);
            lBase = pao;
            getPaoTypesSet().add(pao.getPaoType());
        } else if (type == DbChangeType.DELETE) {
            lBase = getAllPaosMap().remove(id);
            allPaoTypes.clear();
        } else {
            releaseAllYukonPAObjects();
        }
        
        return lBase;
    }
    
    /**
     * Drop all the junk we have accumulated.
     * Please be keeping this method in sync
     */
    @Override
    public synchronized void releaseAllCache() {
        
        releaseAllYukonPAObjects();
        allSystemPoints = null;
        allNotificationGroups = null;
        allContactNotifsMap = null;
        
        allGraphDefinitions = null;
        allMcts = null;
        allHolidaySchedules = null;
        allBaselines = null;
        allConfigs = null;
        allMeters = null;
        allPointLimits = null;
        images = null;
        allCICustomers = null;
        allLMProgramConstraints = null;
        allLMScenarios = null;
        allLMScenarioProgs = null;
        
        allSeasonSchedules = null;
        allDeviceTypeCommands = null;
        allTouSchedules = null;
        
        allYukonRoles = null;
        allYukonRoleProperties = null;
        allYukonGroups = null;
        
        allYukonGroupRolePropertiesMap = null;
        
        // lists that are created by the joining/parsing of existing lists
        allDevices = null;
        allLMPrograms = null;
        allLMControlAreas = null;
        allLMGroups = null;
        allLoadManagement = null;
        allPorts = null;
        allRoutes = null;
        
        // Maps that are created by the joining/parsing of existing lists
        customerCache.clear();
        allContactsMap.clear();
    }

    @Override
    public synchronized void releaseAllContacts() {
        allContactsMap.clear();
        allContactNotifsMap = null;
    }

    @Override
    public synchronized void releaseAllMeters() {
        allMeters = null;
    }

    @Override
    public synchronized void releaseAllYukonImages() {
        images = null;
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
    }

    @Override
    public synchronized void releaseAllTOUSchedules() {
        allTouSchedules = null;
    }
    
    @Override
    public synchronized void releaseAllConfigs() {
        allConfigs = null;
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
    }

    @Override
    public synchronized void releaseAllYukonPAObjects() {
        allLitePaos = null;
        allPaoTypes.clear();
    }

    @Override
    public synchronized void releaseAllDeviceTypeCommands() {
        allDeviceTypeCommands = null;
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
    public synchronized LiteContactNotification getContactNotification(int contactNotificationId) {
        
        loadAllContacts();
        LiteContactNotification notification = allContactNotifsMap.get(contactNotificationId);
        
        // not in cache, go to DB.
        if (notification == null) {
            notification = contactNotificationDao.getNotificationForContact(contactNotificationId);
            // found it, put it in the cache for later searches
            allContactNotifsMap.put(new Integer(contactNotificationId), notification);
            // make sure the contact is also loaded
            getAContactByContactID(notification.getContactID());
        }
        
        return notification;
    }
    
    /*
     * This method takes a primaryContactId to look for the relevant customer. It will
     * be taken directly from the database.
     */
    @Override
    public LiteCustomer getACustomerByPrimaryContactID(int primaryContactId) {
        return customerDao.getLiteCustomerByPrimaryContact(primaryContactId);
    }

    /*
     * This method takes a customerid to look for the relevant customer. It checks allCustomersMap
     * to see if this customer has been recovered from the db before. If it has not, it will
     * be taken directly from the database.
     */
    @Override
    public LiteCustomer getCustomer(int customerID) {
        
        LiteCustomer specifiedCustomer = null;
        // check cache for previous grabs
        specifiedCustomer = customerCache.get(customerID);

        // not in cache, go to DB.
        if (specifiedCustomer == null) {
            specifiedCustomer = customerDao.getLiteCustomer(customerID);
            // found it, put it in the cache for later searches
            customerCache.put(customerID, specifiedCustomer);
        }

        return specifiedCustomer;
    }
    
    @Override
    public synchronized void releaseUserContactMap() {
        userContactMap.clear();
    }
    
    /**
     * Upon receiving a DBChangeMsg for a user or a group, this method
     * checks to see if this user is in the map. There is no point in
     * resetting these mappings if the user that was changed is not a one
     * that has been accessed before (and therefore mapped here).
     */
    public synchronized void adjustUserMappings(int userID) {
        
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