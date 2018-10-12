package com.cannontech.dr.nest.service.impl;

import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_GROUP_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_GROUP_ONLY_IN_NEST;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_GROUP_ONLY_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_NONE_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MANUALLY_DELETE_GROUP_FROM_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_AREA_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_PROGRAM_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncType.AUTO;
import static com.cannontech.dr.nest.model.NestSyncType.MANUAL;
import static org.joda.time.Instant.now;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

public class NestSyncServiceImpl implements NestSyncService{

    public static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_SYNC = Duration.standardMinutes(15);
    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private DemandResponseDao demandResponseDao;
    @Autowired DbChangeManager dbChangeManager;
    
    @Autowired private IDatabaseCache dbCache;
    private boolean syncInProgress = false;
    public static final String EMPTY_ROW = "***";
    private static final Logger log = YukonLogManager.getLogger(NestSyncServiceImpl.class);
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private NestDao nestDao;
        
    @Override
    public void sync(boolean forceSync) {
        
        if(!runSync(forceSync)) {
            return;
        }
        
        syncInProgress = true;
        log.info("Nest sync started");
        persistedSystemValueDao.setValue(PersistedSystemValueKey.NEST_SYNC_TIME, new Instant());
        NestSync sync = new NestSync();
        nestDao.saveSyncInfo(sync);
        List<NestExisting> existing = nestCommunicationService.downloadExisting();
        if (!existing.isEmpty()) {
           List<String> groupsInNest = parseGroupsFromTheNestFile(existing); 
           syncGroups(groupsInNest, sync.getId());
           validateProgramAndAreaSetup(groupsInNest, sync.getId());
         

           // processAccounts
           // processGroupChanges
        }
        sync.setStopTime(new Instant());
        nestDao.saveSyncInfo(sync);
        
        syncInProgress = false;
        log.info("Nest sync finished");
    }
    
    /**
     * Returns a multimap of groups to programs
     */
    private Multimap<PaoIdentifier, PaoIdentifier> getGroupsToPrograms(List<LiteYukonPAObject> nestGroups){
         SetMultimap<PaoIdentifier, PaoIdentifier> programToGroupMap = 
                 demandResponseDao.getProgramToGroupMappingForGroups(nestGroups.stream()
                     .map(g -> g.getPaoIdentifier())
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
        List<LiteYukonPAObject> nestGroups =
            dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST && groupsInNest.contains(group.getPaoName()))
                .collect( Collectors.toList());

        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms = getGroupsToPrograms(nestGroups);

        Multimap<PaoIdentifier, PaoIdentifier> programsToAreas =
            getProgramsToAreas(groupsToPrograms.values().stream().distinct().collect(Collectors.toList()));
        List<NestSyncDetail> details =
            validateProgramAndAreaSetup(syncId, nestGroups, groupsToPrograms, programsToAreas);
        nestDao.saveSyncDetails(details);
    }
    
    ///////////////////Unit test
    /**
     * Checks if Nest groups have areas and programs are setup correctly
     */
    private List<NestSyncDetail> validateProgramAndAreaSetup(int syncId, List<LiteYukonPAObject> nestGroups,
            Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms,
            Multimap<PaoIdentifier, PaoIdentifier> programsToAreas) {

        List<NestSyncDetail> details = new ArrayList<>();
        nestGroups.forEach(group -> {
            Collection<PaoIdentifier> programs = groupsToPrograms.get(group.getPaoIdentifier());
            if (programs.isEmpty()) {
                log.debug("Nest group {} doesn't have a program setup", group.getPaoName());
                details.add(new NestSyncDetail(0, syncId, MANUAL, NOT_FOUND_PROGRAM_FOR_NEST_GROUP, group.getPaoName(),
                    SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP, group.getPaoName()));
            } else if (!hasArea(programs, programsToAreas)) {
                log.debug("Nest group {} doesn't have an area setup", group.getPaoName());
                details.add(new NestSyncDetail(0, syncId, MANUAL, NOT_FOUND_AREA_FOR_NEST_GROUP, group.getPaoName(),
                    SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP, group.getPaoName()));
            }
        });
        return details;
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
        if (lastSyncTime == null || now().isAfter(lastSyncTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC))) {
            return true;
        }
        return false;
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
            return new NestSyncDetail(0, syncId, MANUAL, FOUND_NONE_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME, group,
                MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP, group);
        }).collect(Collectors.toList());
        return details;
    }
    
    private void syncGroups(List<String> groupsInNest, int syncId) {
        nestDao.saveSyncDetails(filterNestGroups(groupsInNest, syncId));
        
        List<String> nestGroupsInYukon = dbCache.getAllLMGroups().stream()
                    .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST)
                    .map(group -> group.getPaoName())
                    .collect(Collectors.toList());
        
        List<String> groupsToCreate = new ArrayList<>(groupsInNest);
        groupsToCreate.removeAll(nestGroupsInYukon);
        nestDao.saveSyncDetails(createGroups(groupsToCreate, syncId));
        
        List<String> groupsOnlyInYukon = new ArrayList<>(nestGroupsInYukon);
        groupsOnlyInYukon.removeAll(groupsInNest);
        List<NestSyncDetail> discrepancies = groupsOnlyInYukon.stream().map(group -> {
            return new NestSyncDetail(0, syncId, MANUAL, FOUND_GROUP_ONLY_IN_YUKON, group, MANUALLY_DELETE_GROUP_FROM_YUKON, group);
            }).collect(Collectors.toList());
        if(!discrepancies.isEmpty()) {
            log.debug("Found Nest groups in Yukon that is not in Nest {}", groupsOnlyInYukon);
            nestDao.saveSyncDetails(discrepancies);  
        }
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
            try {
                dbPersistentDao.performDBChange(pao, TransactionType.INSERT);
                DBChangeMsg msg  = new DBChangeMsg();
                msg.setDatabase(DBChangeMsg.CHANGE_PAO_DB);
                msg.setCategory(PaoCategory.LOADMANAGEMENT.getDbString());
                msg.setObjectType(pao.getPaoType().toString());
                dbChangeManager.processDbChange(msg);
                details.add(new NestSyncDetail(0, syncId, AUTO, FOUND_GROUP_ONLY_IN_NEST, group,
                    AUTO_CREATED_GROUP_IN_YUKON, group));
            } catch (PersistenceException e) {
                log.error("Unable to create group" + group, e );
            }
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
