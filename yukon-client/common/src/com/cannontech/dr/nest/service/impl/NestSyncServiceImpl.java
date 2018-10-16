package com.cannontech.dr.nest.service.impl;

import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_GROUP_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_GROUP_ONLY_IN_NEST;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_GROUP_ONLY_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MANUALLY_DELETE_GROUP_FROM_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_AREA_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_PROGRAM_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncType.AUTO;
import static com.cannontech.dr.nest.model.NestSyncType.MANUAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceAlreadyExistsException;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.stars.ws.StarsControllableDeviceHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

public class NestSyncServiceImpl implements NestSyncService{

    public static final int MINUTES_TO_WAIT_BEFORE_NEXT_SYNC = 15;
    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private DemandResponseDao demandResponseDao;
    @Autowired private StarsControllableDeviceHelper starsControllableDeviceHelper;
    @Autowired DbChangeManager dbChangeManager;
    
    @Autowired private IDatabaseCache dbCache;
    private boolean syncInProgress = false;
    public static final String EMPTY_ROW = "***";
    private static final Logger log = YukonLogManager.getLogger(NestSyncServiceImpl.class);
    @Autowired private NestDao nestDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private YukonListDao yukonListDao;
    private EnergyCompany HARDCODED_EC;
    private LiteYukonUser HARDCODED_OPERATOR;
       
    @Override
    public void sync(boolean forceSync) {
        
        List<EnergyCompany> energyCompanies = Lists.newArrayList(energyCompanyDao.getAllEnergyCompanies());
        energyCompanies.removeIf(e -> e.getId() == -1);
        HARDCODED_EC = energyCompanies.get(0);
        List<Integer> ids = energyCompanyDao.getOperatorUserIds(HARDCODED_EC);
        HARDCODED_OPERATOR = yukonUserDao.getLiteYukonUser(ids.get(0));
        
        if(!runSync(forceSync)) {
            return;
        }
        
        syncInProgress = true;
        log.info("Nest sync started");
        persistedSystemValueDao.setValue(PersistedSystemValueKey.NEST_SYNC_TIME, new Instant());
        NestSync sync = new NestSync();
        nestDao.saveSyncInfo(sync);
        List<NestExisting> existing = nestCommunicationService.downloadExisting();
        existing.removeIf(row -> Strings.isEmpty(row.getAccountNumber()) || row.getAccountNumber().equals(EMPTY_ROW));
        
        if (!existing.isEmpty()) {
           List<String> groupsInNest = parseGroupsFromTheNestFile(existing); 
           log.debug("Poccessing {} rows", existing.size());
           syncGroups(groupsInNest, sync.getId());
           validateProgramAndAreaSetup(groupsInNest, sync.getId());
           syncAccounts(existing, groupsInNest, sync.getId());
        }
        sync.setStopTime(new Instant());
        nestDao.saveSyncInfo(sync);
        
        syncInProgress = false;
        log.info("Nest sync finished");
    }
    
    private void syncAccounts(List<NestExisting> existing, List<String> validGroups, int syncId) {
        List<String> invalidGroups = new ArrayList<>(validGroups);
        log.debug("Syncing {} accounts", existing.size());
        Set<String> thermostatsToCreate = getThermostatsToCreate(existing);
        log.debug("New thremostats to create {}", thermostatsToCreate);
        Set<String> usersToCreate = getUsersToCreate(customerAccountDao.getAll(), existing);
        log.debug("New users to create {}", usersToCreate);
        List<LiteYukonPAObject> groups = getGroupPaos(validGroups);
        log.debug("Groups in Yukon {}", groups);
        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms = getGroupsToPrograms(groups);
        Map<PaoIdentifier, String> programIdentToProgramName = dbCache.getAllLMPrograms().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_NEST_PROGRAM)
                .collect(Collectors.toMap(program -> program.getPaoIdentifier(), program -> program.getPaoName()));
        
        existing.removeIf(row -> !validGroups.contains(row.getGroup()));
       
        invalidGroups.removeAll(validGroups);
        if(!invalidGroups.isEmpty()) {
            log.debug("Program/Area is not setup for {} groups. Skipping any account creation/changes in Yukon");
        }
        existing.forEach(row -> syncAccount(syncId, thermostatsToCreate, usersToCreate, groups, groupsToPrograms,
            programIdentToProgramName, row));
    }

    /**
     * Syncs individual account
     */
    private void syncAccount(int syncId, Set<String> thermostatsToCreate, Set<String> usersToCreate,
            List<LiteYukonPAObject> groups, Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms,
            Map<PaoIdentifier, String> programIdentToPaoName, NestExisting row) {
        List<NestSyncDetail> details = new ArrayList<>();
        try {
            log.debug("Proccessing {}", row);
            if (usersToCreate.contains(row.getAccountNumber())) {
               //create account
            }
            Set<String> newThermostats = getThermsotatsToCreateForAccount(row, thermostatsToCreate);
            if (!newThermostats.isEmpty()) {
                log.debug("Adding new thermostats {} to account {}", newThermostats, row.getAccountNumber());
                nestDao.saveSyncDetails(createThermostats(row.getAccountNumber(), newThermostats, syncId));
            }
            LiteYukonPAObject groupPao = groups.stream()
                    .filter(group -> group.getPaoName().equalsIgnoreCase(row.getGroup()))
                    .findFirst()
                    .get();
            PaoIdentifier program = groupsToPrograms.get(groupPao.getPaoIdentifier()).iterator().next();
            String programName = programIdentToPaoName.get(program);
            details.addAll(enrollThermostats(row, programName, syncId));
        } catch (Exception e) {
            log.debug("Sync failed for row:" + row, e);
        } finally {
            nestDao.saveSyncDetails(details);
        }
    }
    
    /**
     * Enrolls thermostats in a program for account
     */
    private List<NestSyncDetail> enrollThermostats(NestExisting row, String programName, int syncId) {
        Set<String> serialNumbers = getThemostatsForAccountInNest(row);
        log.debug("Adding new thermostats {} to account {}", serialNumbers, row.getAccountNumber());
        serialNumbers.forEach(serialNumber -> {
            EnrollmentHelper enrollmentHelper =
                new EnrollmentHelper(row.getAccountNumber(), row.getGroup(), programName, serialNumber);
            enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, HARDCODED_OPERATOR);
        });
      
        // todo table changes
        return new ArrayList<>();
    }
    
    
    /**
     * Creates thermostats
     */
    private List<NestSyncDetail> createThermostats(String accountNumber, Set<String> thermostatsToCreate, int syncId) {   
        List<NestSyncDetail> details = new ArrayList<>();        
        thermostatsToCreate.forEach(thermostat ->{
            LmDeviceDto deviceInfo = new LmDeviceDto();
            deviceInfo.setAccountNumber(accountNumber);
            deviceInfo.setSerialNumber(thermostat);      
            //DEV_TYPE_NEST_THERMOSTAT
            deviceInfo.setDeviceType("Nest Thermostat");
            try {
                starsControllableDeviceHelper.addDeviceToAccount(deviceInfo, HARDCODED_OPERATOR);
                details.add(new NestSyncDetail(0, syncId, AUTO, NOT_FOUND_THERMOSTAT, thermostat,
                    AUTO_CREATED_THERMOSTAT, thermostat));
            } catch (StarsDeviceAlreadyExistsException e) {
                // thermostat already exists
            }
        }); 
        return details;
    }
    
    ///////////////////Unit test
    /**
     * Returns the set of thermostats to create for an account
     */
    private Set<String> getThermsotatsToCreateForAccount(NestExisting row, Set<String> thermostatsToCreate){
        if(thermostatsToCreate.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> newThermostats = getThemostatsForAccountInNest(row);
        newThermostats.retainAll(thermostatsToCreate);
        return newThermostats;
    }
    
    ///////////////////Unit test
    /**
     * Returns set of thermostats to for an account in Nest
     */
    private Set<String> getThemostatsForAccountInNest(NestExisting row){
        Set<String> newThermostats = new HashSet<>();
        if(!Strings.isEmpty(row.getSummerRhr())){
            newThermostats.addAll(Arrays.asList(row.getSummerRhr().split(",")));
        }
        if(!Strings.isEmpty(row.getWinterRhr())){
            newThermostats.addAll(Arrays.asList(row.getWinterRhr().split(",")));
        }
        return newThermostats;
    }

    ///////////////////Unit test
    /**
     * Returns the list of thermostats names is Nest but not in Yukon
     */
    private Set<String> getThermostatsToCreate(List<NestExisting> existing) {
        Set<String> thermostatsToCreate =  existing.stream().map(row -> getThemostatsForAccountInNest(row))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        
        List<LiteLmHardwareBase> hardware = inventoryBaseDao.getAllLMHardware(Lists.newArrayList(HARDCODED_EC));
 
        List<String> nestThermostatsExisting = hardware.stream()
                .filter(thermostat -> 
                HardwareType.valueOf(yukonListDao.getYukonListEntry(thermostat.getLmHardwareTypeID()).getYukonDefID()) == HardwareType.NEST_THERMOSTAT)
                .map(thermostat -> thermostat.getManufacturerSerialNumber()).collect(Collectors.toList());
        thermostatsToCreate.removeAll(nestThermostatsExisting);
        return thermostatsToCreate;
    }
    
    ///////////////////Unit test
    /**
     * Returns the list of users is Nest but not in Yukon
     */
    private Set<String> getUsersToCreate(List<CustomerAccount> list, List<NestExisting> existing) {
        Set<String> usersToCreate = existing.stream()
                .map(row -> row.getAccountNumber())
                .collect(Collectors.toSet());
        Set<String> usersInYukon = list.stream()
                .map(user -> user.getAccountNumber())
                .collect(Collectors.toSet());
        usersToCreate.removeAll(usersInYukon);
        return usersToCreate;
    }

    /**
     * Returns a multimap of groups to programs
     */
    private Multimap<PaoIdentifier, PaoIdentifier> getGroupsToPrograms(List<LiteYukonPAObject> nestGroups){
         SetMultimap<PaoIdentifier, PaoIdentifier> programToGroupMap = 
                 demandResponseDao.getProgramToGroupMappingForGroups(nestGroups.stream()
                     .map(LiteYukonPAObject::getPaoIdentifier)
                     .collect(Collectors.toList()));
         Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms =
             Multimaps.invertFrom(programToGroupMap, ArrayListMultimap.<PaoIdentifier, PaoIdentifier> create());
         return groupsToPrograms;
    }
     
    /**
     * Returns a multimap of programs to areas
     */
    private Multimap<PaoIdentifier, PaoIdentifier> getProgramsToAreas(Collection<PaoIdentifier> programs){
        SetMultimap<PaoIdentifier, PaoIdentifier> areasToProgram =
                demandResponseDao.getControlAreaToProgramMappingForPrograms(programs);
        Multimap<PaoIdentifier, PaoIdentifier> programsToAreas = Multimaps.invertFrom(areasToProgram,
                ArrayListMultimap.<PaoIdentifier, PaoIdentifier> create());   
        return programsToAreas;
   }
    
    ///////////////////Unit test
    /**
     * Returns true if the program has an area
     */
    private boolean hasArea(Collection<PaoIdentifier> programs,
            Multimap<PaoIdentifier, PaoIdentifier> programsToAreas) {
        for (PaoIdentifier programId : programs) {
            if (!programsToAreas.get(programId).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if Nest groups have areas and programs are setup correctly, if the setup is not correct creates a discrepancy.
     */
    private void validateProgramAndAreaSetup(List<String> groupsInNest, int syncId) {
        log.debug("Validating program and area setup");
        List<LiteYukonPAObject> nestGroups = getGroupPaos(groupsInNest);

        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms = getGroupsToPrograms(nestGroups);
        Multimap<PaoIdentifier, PaoIdentifier> programsToAreas = getProgramsToAreas(groupsToPrograms.values().stream()
            .distinct()
            .collect(Collectors.toList()));
        List<NestSyncDetail> details = validateProgramAndAreaSetup(syncId, nestGroups, groupsToPrograms, programsToAreas);
        nestDao.saveSyncDetails(details);
    }
    
    private List<LiteYukonPAObject> getGroupPaos(List<String> groupsInNest){
        return dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST && groupsInNest.contains(group.getPaoName()))
                .collect( Collectors.toList());
    }
    
    ///////////////////Unit test
    /**
     * Checks if Nest groups have areas and programs are setup correctly
     */
    private List<NestSyncDetail> validateProgramAndAreaSetup(int syncId, List<LiteYukonPAObject> nestGroups,
            Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms,
            Multimap<PaoIdentifier, PaoIdentifier> programsToAreas) {
        
        return nestGroups.stream()
                         .map(group -> buildSyncDetail(syncId, group, groupsToPrograms, programsToAreas))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .collect(Collectors.toList());
    }
       
    ///////////////////Unit test
    /**
     * Builds a NestSyncDetail, or returns an empty Optional if the setup is correct.
     */
    private Optional<NestSyncDetail> buildSyncDetail(int syncId, LiteYukonPAObject group,
                                           Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms,
                                           Multimap<PaoIdentifier, PaoIdentifier> programsToAreas) {
        
        Collection<PaoIdentifier> programs = groupsToPrograms.get(group.getPaoIdentifier());
        NestSyncDetail detail = null;
        if (programs.isEmpty()) {
            log.debug("Nest group {} doesn't have a program setup", group.getPaoName());
            detail = new NestSyncDetail(0, syncId, MANUAL, NOT_FOUND_PROGRAM_FOR_NEST_GROUP, group.getPaoName(),
                SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP, group.getPaoName());
        } else if (!hasArea(programs, programsToAreas)) {
            log.debug("Nest group {} doesn't have an area setup", group.getPaoName());
            detail = new NestSyncDetail(0, syncId, MANUAL, NOT_FOUND_AREA_FOR_NEST_GROUP, group.getPaoName(),
                SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP, group.getPaoName());
        }
        return Optional.ofNullable(detail);
    }
    
    /**
     * Returns true if sync can run.
     */
    private boolean runSync(boolean forceSync) {
        if(forceSync) {
            return true;
        }
        if(syncInProgress) {
            return false;
        }
        Instant lastSyncTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.NEST_SYNC_TIME);
        
        return TimeUtil.isXMinutesBeforeNow(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC, lastSyncTime);
    }

    ///////////////////Unit test
    /**
     * If group in Nest file has the same name as group in Yukon that is not a Nest group, mark as
     * discrepancy and remove it from the list of groups to be synced.
     * 
     * @return list of sync details
     */
    private List<NestSyncDetail> filterNestGroups(List<String> groupsInNest, int syncId) {
        List<String> nonNestGroupsWithNestGroupName = dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() != PaoType.LM_GROUP_NEST && groupsInNest.contains(group.getPaoName()))
                .map(group -> group.getPaoName())
                .collect(Collectors.toList());
        log.debug("Groups in Yukon that is not Nest that have the same name as Group in Nest {}", nonNestGroupsWithNestGroupName);
        groupsInNest.removeAll(nonNestGroupsWithNestGroupName);
        List<NestSyncDetail> details = nonNestGroupsWithNestGroupName.stream().map(group -> {
            return new NestSyncDetail(0, syncId, MANUAL, FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME, group,
                MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP, group);
        }).collect(Collectors.toList());
        return details;
    }
    
    private void syncGroups(List<String> groupsInNest, int syncId) {
        log.debug("Syncing Nest {} groups",  groupsInNest);
        List<String> nestGroupsInYukon = dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST)
                .map(group -> group.getPaoName())
                .collect(Collectors.toList());
        List<String> groupsOnlyInYukon = new ArrayList<>(nestGroupsInYukon);
        groupsOnlyInYukon.removeAll(groupsInNest);
        List<NestSyncDetail> discrepancies = groupsOnlyInYukon.stream().map(group -> {
            return new NestSyncDetail(0, syncId, MANUAL, FOUND_GROUP_ONLY_IN_YUKON, group, MANUALLY_DELETE_GROUP_FROM_YUKON, group);
            }).collect(Collectors.toList());
        if(!discrepancies.isEmpty()) {
            log.debug("Found Nest groups in Yukon that is not in Nest {}", groupsOnlyInYukon);
            nestDao.saveSyncDetails(discrepancies);  
        }
        
        nestDao.saveSyncDetails(filterNestGroups(groupsInNest, syncId));
        
        List<String> groupsToCreate = new ArrayList<>(groupsInNest);
        groupsToCreate.removeAll(nestGroupsInYukon);
        nestDao.saveSyncDetails(createGroups(groupsToCreate, syncId));
    }
    
    ///////////////////Unit test
    /**
     * Returns groups in the Nest file.
     */
    private List<String> parseGroupsFromTheNestFile(List<NestExisting> existing) {
        List<String> groupsInNest = existing.stream()
                .filter(row -> Strings.isNotEmpty(row.getGroup()) && !row.getGroup().equals(EMPTY_ROW))
                .map(row -> row.getGroup())
                .distinct()
                .collect(Collectors.toList());
        return groupsInNest;
    }
    
    /**
     * Creates Nest Group
     */
    private List<NestSyncDetail> createGroups(List<String> groups, int syncId) {
        if(!groups.isEmpty()) {
            log.debug("Groups found in Nest by not in Yukon: " + groups);
        }
        
        log.debug("Creating Nest Groups in Yukon {}", groups);
        List<NestSyncDetail> details = new ArrayList<>();
        groups.forEach(group -> {
            YukonPAObject pao = LMFactory.createLoadManagement(PaoType.LM_GROUP_NEST);
            pao.setPAOName(group);
            details.add(new NestSyncDetail(0, syncId, AUTO, FOUND_GROUP_ONLY_IN_NEST, group,
                AUTO_CREATED_GROUP_IN_YUKON, group));
        });
        return details;
    }

    @Override
    public NestSyncTimeInfo getSyncTimeInfo() {
        NestSyncTimeInfo info = new NestSyncTimeInfo();
        info.setSyncTime(persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.NEST_SYNC_TIME));
        info.setSyncInProgress(syncInProgress);
        if(info.getNextSyncTime() != null) {
            info.setNextSyncTime(info.getNextSyncTime().plus(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC));
        }
        return info;
    }
}
