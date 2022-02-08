package com.cannontech.dr.loadprogram.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFieldsBuilder;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.ProgramConstraint;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.DeletionFailureException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;

public class LoadProgramSetupServiceImpl implements LoadProgramSetupService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private LMServiceHelper lmServiceHelper;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private PointDao pointDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ProgramDao programDao;
    @Autowired private LMGearDao lmGearDao;
    @Autowired private ProgramGearFieldsBuilder programGearFieldsBuilder;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;

    @Override
    @Transactional
    public LoadProgram create(LoadProgram loadProgram, LiteYukonUser liteYukonUser) {
        LMProgramBase lmProgramBase = getDBPersistent(loadProgram.getProgramId(), loadProgram.getType());
        
        loadProgram.buildDBPersistent(lmProgramBase);
        buildGearDBPersistent(loadProgram, lmProgramBase);

        dbPersistentDao.performDBChange(lmProgramBase, TransactionType.INSERT);

        SimpleDevice device = SimpleDevice.of(lmProgramBase.getPAObjectID(), lmProgramBase.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);

        // Logging events during load program creation
        processEventLogsForProgramCreate(loadProgram, liteYukonUser);
        updateLoadProgramModel(loadProgram, lmProgramBase, liteYukonUser);
        return loadProgram;
    }

    @Override
    @Transactional
    public LoadProgram update(int programId, LoadProgram loadProgram, LiteYukonUser liteYukonUser) {
        // Validate programId
        getProgramFromCache(programId);
        List<LMProgramDirectGear> oldGears = null;
        LMProgramBase lmProgramBase = getDBPersistent(programId, loadProgram.getType());
        if (lmProgramBase instanceof LMProgramDirectBase) {
            LMProgramDirectBase lmProgramDirectBase = (LMProgramDirectBase) lmProgramBase;
            oldGears = lmProgramDirectBase.getLmProgramDirectGearVector().stream()
                    .collect(Collectors.toList());
        }

        loadProgram.setProgramId(programId);
        loadProgram.buildDBPersistent(lmProgramBase);
        buildGearDBPersistent(loadProgram, lmProgramBase);
        buildLMMemberControlDBPersistent(lmProgramBase, loadProgram, liteYukonUser);

        dbPersistentDao.performDBChange(lmProgramBase, TransactionType.UPDATE);

        // Logging events during load program update
        processEventLogsForProgramUpdate(loadProgram, oldGears, liteYukonUser);

        updateLoadProgramModel(loadProgram, lmProgramBase, liteYukonUser);
        return loadProgram;
    }

    @Override
    public LoadProgram retrieve(int programId, LiteYukonUser liteYukonUser) {
        LiteYukonPAObject lmProgram = getProgramFromCache(programId);
        LMProgramBase lmProgramBase = (LMProgramBase) dbPersistentDao.retrieveDBPersistent(lmProgram);
        LoadProgram loadProgram = new LoadProgram();
        updateLoadProgramModel(loadProgram, lmProgramBase, liteYukonUser);
        return loadProgram;
    }

    @Override
    @Transactional
    public int delete(int programId, LiteYukonUser liteYukonUser) {
        LiteYukonPAObject loadProgram = dbCache.getAllLMPrograms().stream()
                                                                  .filter( program -> program.getLiteID() == programId)
                                                                  .findFirst().orElseThrow(() -> new NotFoundException("Id not found"));;
        Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
        if (programDao.getByProgramIds(Collections.singletonList(paoId)).size() > 0) {
            String message = "You cannot delete the load management program '" + loadProgram.getPaoName()
                + "' because it is currently in use as a STARS assigned program, Unassign it from all appliance categories and try again.";
            throw new DeletionFailureException(message);
        }
        YukonPAObject lmProgram = (YukonPAObject) LiteFactory.createDBPersistent(loadProgram);
        List<LiteGear> gears = getGearsForProgram(lmProgram.getPAObjectID());
        dbPersistentDao.performDBChange(lmProgram, TransactionType.DELETE);

        // Logging events during load program deletion
        processEventLogsForProgramDelete(lmProgram, gears, liteYukonUser);

        return lmProgram.getPAObjectID();
    }

    @Override
    @Transactional
    public LoadProgram copy(int programId, LoadProgramCopy loadProgramCopy, LiteYukonUser liteYukonUser) {

        LiteYukonPAObject lmProgram = getProgramFromCache(programId);
 
        LMProgramBase program = (LMProgramBase) dbPersistentDao.retrieveDBPersistent(lmProgram);
        // old programId is used to get points 
        int oldProgramId = program.getPAObjectID();

        program.setPAObjectID(null);
        program.setName(loadProgramCopy.getName());
        program.getProgram().setControlType(loadProgramCopy.getOperationalState().name());
 
        ProgramConstraint constraint = loadProgramCopy.getConstraint();
        if (constraint.getConstraintId() != null) {
            program.getProgram().setConstraintID(constraint.getConstraintId());
        }

        LMProgramDirectBase directBase = (LMProgramDirectBase) program;

        directBase.getLmProgramDirectGearVector().forEach(gear -> {
            gear.setGearID(null);
        });

        if (loadProgramCopy.getCopyMemberControl() != null && !loadProgramCopy.getCopyMemberControl()) {
            program.getPAOExclusionVector().removeAllElements();
        }

        if (CollectionUtils.isNotEmpty(program.getLmProgramStorageVector())) {
            program.getLmProgramStorageVector().clear();
        }

        dbPersistentDao.performDBChange(program, TransactionType.INSERT);

        List<PointBase> points = pointDao.getPointsForPao(oldProgramId);
        SimpleDevice device = SimpleDevice.of(program.getPAObjectID(), program.getPaoType());
        paoCreationHelper.applyPoints(device, points);
        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);

        // Logging events during load program copy
        processEventLogsForProgramCopy(directBase, liteYukonUser);
        
        LoadProgram loadProgram = new LoadProgram();
        updateLoadProgramModel(loadProgram, program, liteYukonUser);
        
        return loadProgram;
        
    }

    /**
     * Returns DB Persistent object
     */
    private LMProgramBase getDBPersistent(Integer paoId, PaoType type) {
        LMProgramBase lmProgram = (LMProgramBase) LMFactory.createLoadManagement(type);
        if (paoId != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
            lmProgram = (LMProgramBase) dbPersistentDao.retrieveDBPersistent(pao);
        }
        return lmProgram;
    }

    /**
     * Build DB Persistent for gear
     */
    private void buildGearDBPersistent(LoadProgram loadProgram, LMProgramBase lmProgramBase) {
        if (lmProgramBase instanceof LMProgramDirectBase) {
            LMProgramDirectBase lmProgramDirectBase = (LMProgramDirectBase) lmProgramBase;
            if (CollectionUtils.isNotEmpty(lmProgramDirectBase.getLmProgramDirectGearVector())) {
                lmProgramDirectBase.getLmProgramDirectGearVector().clear();
                LMProgramDirectGear.deleteAllDirectGearsForProgram(loadProgram.getProgramId());
            }
            List<ProgramGear> newGears = getNewGears(loadProgram.getGears());
            newGears.forEach(gear -> {
                LMProgramDirectGear directGear = gear.getControlMethod().createNewGear();
                gear.buildDBPersistent(directGear);
                lmProgramDirectBase.getLmProgramDirectGearVector().add(directGear);
            });
        }
    }

    /**
     * Build DB Persistent for member Control 
     */
    private void buildLMMemberControlDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram, LiteYukonUser liteYukonUser) {

        if (rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS, liteYukonUser)
            || liteYukonUser.getUserID() == UserUtils.USER_ADMIN_ID) {
            LMProgramDirectBase program = (LMProgramDirectBase) lmProgram;

            if (CollectionUtils.isNotEmpty(program.getPAOExclusionVector())) {
                program.getPAOExclusionVector().clear();
            }

            if (loadProgram.getMemberControl() != null) {

                loadProgram.getMemberControl().forEach(subordinateProg -> {
                    PAOExclusion subordinate = new PAOExclusion();

                    subordinate.setPaoID(program.getPAObjectID());
                    subordinate.setExcludedPaoID(subordinateProg.getSubordinateProgId());

                    // this is very important: server needs funcID set to this or it won't recognize the subordinate
                    subordinate.setFuncName(CtiUtilities.LM_SUBORDINATION_INFO);
                    subordinate.setFunctionID(CtiUtilities.LM_SUBORDINATION_FUNC_ID);
                    program.getPAOExclusionVector().addElement(subordinate);
                });
            }
        }
    }

    /**
     * Return latest list of gears which needs to be added.
     */
    private List<ProgramGear> getNewGears(List<ProgramGear> gears) {

        if (gears != null) {
            List<ProgramGear> oldGears = gears.stream().filter(gear -> gear.getGearId() != null).collect(Collectors.toList());
            List<ProgramGear> newGears = gears.stream().filter(gear -> gear.getGearId() == null).collect(Collectors.toList());

            return ListUtils.union(oldGears, newGears);
        }
        return null;
    }

    /**
     * Update required fields of Load Program Model. 
     */
    private void updateLoadProgramModel(LoadProgram loadProgram, LMProgramBase lmProgramBase, LiteYukonUser liteYukonUser) {
        loadProgram.buildModel(lmProgramBase);
        setConstraintName(loadProgram);
        if (lmProgramBase instanceof LMProgramDirectBase) {
            if (loadProgram.getNotification() != null
                    && (CollectionUtils.isNotEmpty(loadProgram.getNotification().getAssignedNotificationGroups())
                            || loadProgram.getNotification().getProgramStartInMinutes() != null
                            || loadProgram.getNotification().getProgramStopInMinutes() != null
                            || loadProgram.getNotification().getNotifyOnAdjust()
                            || loadProgram.getNotification().getEnableOnSchedule())) {
                setNotificationGroupNames(loadProgram);
            }
            LMProgramDirectBase lmProgramDirectBase = (LMProgramDirectBase) lmProgramBase;
            if (rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS, liteYukonUser)
                    || liteYukonUser.getUserID() == UserUtils.USER_ADMIN_ID) {
                updateMemberControlModel(lmProgramDirectBase, loadProgram);
            }
        }
        updateLoadGroupModel(loadProgram);
    }

    /**
     * Set Constraint name for load program.
     */
    private void setConstraintName(LoadProgram loadProgram) {
        LiteLMConstraint liteLMConstraint = getProgramConstraint(loadProgram.getConstraint().getConstraintId());
        loadProgram.getConstraint().setConstraintName(liteLMConstraint.getConstraintName());
    }

    /**
     * Set Notification Group Names for Load Program.
     */
    private void setNotificationGroupNames(LoadProgram loadProgram) {
        loadProgram.getNotification().getAssignedNotificationGroups().forEach(notificationGroup -> {
            List<LiteNotificationGroup> notificationGroups = dbCache.getAllContactNotificationGroups();
            LiteNotificationGroup liteNotificationGroup = notificationGroups.stream()
                                                                            .filter(notifGroup -> notifGroup.getNotificationGroupID() == notificationGroup.getNotificationGrpID())
                                                                            .findFirst()
                                                                            .get();

            notificationGroup.setNotificationGrpName(liteNotificationGroup.getNotificationGroupName());
        });
    }

    /**
     * Update Member Control fields of Load Program.
     */
    private void updateMemberControlModel(LMProgramDirectBase programDirectBase, LoadProgram loadProgram) {
        List<ProgramDirectMemberControl> memberControls = new ArrayList<>();
        programDirectBase.getPAOExclusionVector().forEach(paoExclusion -> {
            ProgramDirectMemberControl memberControl = new ProgramDirectMemberControl();
            memberControl.setSubordinateProgId(paoExclusion.getExcludedPaoID());

            LiteYukonPAObject excludedPao = dbCache.getAllLMPrograms().stream()
                                                                      .filter(program -> program.getLiteID() == paoExclusion.getExcludedPaoID())
                                                                      .findFirst()
                                                                      .get();

            memberControl.setSubordinateProgName(excludedPao.getPaoName());
            memberControls.add(memberControl);
        });

        if (CollectionUtils.isNotEmpty(memberControls)) {
            loadProgram.setMemberControl(memberControls);
        }
    }

    /**
     * Update Load group fields of Load Program.
     */
    private void updateLoadGroupModel(LoadProgram loadProgram) {
        loadProgram.getAssignedGroups().forEach(group -> {

           List<LiteYukonPAObject> paObjects = dbCache.getAllLMGroups();
            Optional <LiteYukonPAObject> paObject = paObjects.stream()
                                                  .filter(p -> p.getLiteID() == group.getGroupId())
                                                  .findFirst();
            if (paObject.isPresent()) {
                group.setGroupName(paObject.get().getPaoName());
                group.setType(paObject.get().getPaoType());
            }
        });
    }

    @Override
    public List<ProgramGroup> getAllProgramLoadGroups(PaoType programType) {
        List<LiteYukonPAObject> groups = dbCache.getAllLoadManagement();
        return getAllProgramLoadGroups(programType, groups);
    }

    @Override
    public List<ProgramGroup> getAllProgramLoadGroups(PaoType programType, List<LiteYukonPAObject> groups) {

        List<ProgramGroup> programGroups = new ArrayList<>();
        groups.forEach(group -> {
            PaoType loadGroupType = group.getPaoType();
            if (loadGroupType.isLoadGroup()) {
               
                boolean isSepProgram = programType == PaoType.LM_SEP_PROGRAM;
                boolean isEcobeeProgram = programType == PaoType.LM_ECOBEE_PROGRAM;
                boolean isHoneywellProgram = programType == PaoType.LM_HONEYWELL_PROGRAM;
                boolean isNestProgram = programType == PaoType.LM_NEST_PROGRAM;
                boolean isItronProgram = programType == PaoType.LM_ITRON_PROGRAM;
                boolean isMeterDisconnectProgram = programType == PaoType.LM_METER_DISCONNECT_PROGRAM;
                boolean isEatonCloudProgram = programType == PaoType.LM_EATON_CLOUD_PROGRAM;

                if (isSepProgram && isGroupSepCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                }  else if (isEcobeeProgram && isGroupEcobeeCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isHoneywellProgram && isGroupHoneywellCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isNestProgram && isGroupNestCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isItronProgram && isGroupItronCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isMeterDisconnectProgram && isGroupMeterDisconnectCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isEatonCloudProgram && isGroupEatonCloudCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if ((!isSepProgram && !isGroupSepCompatible(loadGroupType))
                        && (!isEcobeeProgram && !isGroupEcobeeCompatible(loadGroupType))
                        && (!isHoneywellProgram && !isGroupHoneywellCompatible(loadGroupType))
                        && (!isNestProgram && !isGroupNestCompatible(loadGroupType))
                        && (!isItronProgram && !isGroupItronCompatible(loadGroupType))
                        && (!isMeterDisconnectProgram && !isGroupMeterDisconnectCompatible(loadGroupType))
                        && (!isEatonCloudProgram && !isGroupEatonCloudCompatible(loadGroupType))) {
                    programGroups.add(buildProgramLoadGroup(group));
                }
            }
        });
        return programGroups;
    }

    private ProgramGroup buildProgramLoadGroup(LiteYukonPAObject group) {
        ProgramGroup programGroup = new ProgramGroup();
        programGroup.setGroupName(group.getPaoName());
        programGroup.setGroupId(group.getLiteID());
        programGroup.setType(group.getPaoType());
        return programGroup;
    }
    
    private boolean isGroupSepCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_DIGI_SEP;
    }

    private boolean isGroupEcobeeCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_ECOBEE;
    }

    private boolean isGroupHoneywellCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_HONEYWELL;
    }

    private boolean isGroupNestCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_NEST;
    }

    private boolean isGroupItronCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_ITRON;
    }
    
    private boolean isGroupMeterDisconnectCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_METER_DISCONNECT;
    }

    private boolean isGroupEatonCloudCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_EATON_CLOUD;
    }

    @Override
    public List<ProgramDetails> getAvailablePrograms() {
        List<Integer> programIdsInControlArea = LMControlAreaProgram.getAllProgramsInControlAreas();
        return programIdsInControlArea.stream()
                                      .map(programId -> dbCache.getAllPaosMap().get(programId))
                                      .filter(program -> program.getPaoType().isDirectProgram())
                                      .map(program -> buildProgramDetails(program))
                                      .collect(Collectors.toList());
    }

    private ProgramDetails buildProgramDetails(LiteYukonPAObject program) {

        ProgramDetails programDetails = new ProgramDetails();
        programDetails.setProgramId(program.getLiteID());
        programDetails.setGears(lmServiceHelper.getGearsforModel(program.getLiteID(), programDetails.getGears()));
        return programDetails;
    }

    LiteYukonPAObject getProgramFromCache(int programId) {
        LiteYukonPAObject lmProgram = dbCache.getAllLMPrograms().stream()
                                                                .filter(program -> program.getLiteID() == programId)
                                                                .findFirst()
                                                                .orElseThrow(() -> new NotFoundException("Id not found"));
        return lmProgram;
    }

    @Override
    public List<LiteGear> getGearsForProgram(int programId) {
        return lmGearDao.getAllLiteGears(programId);
    }

    @Override
    public ProgramGear getProgramGear(Integer gearId) {
        com.cannontech.loadcontrol.data.LMProgramDirectGear directGear = lmGearDao.getByGearId(gearId);
        ProgramGearFields fields = programGearFieldsBuilder.getProgramGearFields(directGear);

        ProgramGear gear = new ProgramGear();
        gear.setControlMethod(directGear.getControlMethod());
        gear.setGearId(directGear.getGearId());
        gear.setGearName(directGear.getGearName());
        gear.setGearNumber(directGear.getGearNumber());
        gear.setFields(fields);
        return gear;
    }

    /*
     * Process event log for program creation and gear creation
     */
    private void processEventLogsForProgramCreate(LoadProgram loadProgram, LiteYukonUser liteYukonUser) {

        LiteLMConstraint litelmConstraint = getProgramConstraint(loadProgram.getConstraint().getConstraintId());
        String gearNames = getGearNamesString(loadProgram);
        String loadGroupNames = getLoadGroupNamesString(loadProgram);

        // event log for gear creation
        logEventsForGearCreation(loadProgram, liteYukonUser);

        // event log for program creation
        demandResponseEventLogService.loadProgramCreated(loadProgram.getName(),
                                                         loadProgram.getType(),
                                                         litelmConstraint.getConstraintName(),
                                                         gearNames,
                                                         loadGroupNames,
                                                         liteYukonUser);
    }

    /*
     * Process event log for updating load program
     */
    private void processEventLogsForProgramUpdate(LoadProgram loadProgram, List<LMProgramDirectGear> oldGears,
            LiteYukonUser liteYukonUser) {

        LiteLMConstraint litelmConstraint = getProgramConstraint(loadProgram.getConstraint().getConstraintId());
        String gearNames = getGearNamesString(loadProgram);
        String loadGroupNames = getLoadGroupNamesString(loadProgram);
        
        // event log for gear deletion
        for (LMProgramDirectGear lmProgramDirectGear : oldGears) {
            demandResponseEventLogService.gearDeleted(lmProgramDirectGear.getGearName(),
                                                      lmProgramDirectGear.getControlMethod().name(),
                                                      loadProgram.getName(),
                                                      lmProgramDirectGear.getGearNumber(),
                                                      liteYukonUser);
        }

        // event log for gear creation
        logEventsForGearCreation(loadProgram, liteYukonUser);

        // event log for program update
        demandResponseEventLogService.loadProgramUpdated(loadProgram.getName(),
                                                         loadProgram.getType(),
                                                         litelmConstraint.getConstraintName(),
                                                         gearNames,
                                                         loadGroupNames,
                                                         liteYukonUser);
    }

    /*
     * Process event log for program deletion and gear deletion
     */
    private void processEventLogsForProgramDelete(YukonPAObject lmProgram, List<LiteGear> gears, LiteYukonUser liteYukonUser) {

        // event log for gear deletion
        for (LiteGear liteGear : gears) {
            demandResponseEventLogService.gearDeleted(liteGear.getGearName(),
                                                      liteGear.getGearType(),
                                                      lmProgram.getPAOName(),
                                                      liteGear.getGearNumber(),
                                                      liteYukonUser);
        }

        // event log for program deletion
        demandResponseEventLogService.loadProgramDeleted(lmProgram.getPAOName(),
                                                         lmProgram.getPaoType(),
                                                         liteYukonUser);

       
    }

    /**
     * Process event log for load program copy
     */
    private void processEventLogsForProgramCopy(LMProgramDirectBase directBase, LiteYukonUser liteYukonUser) {

        List<LMProgramDirectGear> gears = directBase.getLmProgramDirectGearVector().stream()
                                                                                   .collect(Collectors.toList());

        String gearNames = gears.stream()
                                .map(gear -> gear.getGearName())
                                .collect(Collectors.joining(", "));

        String reducedGearNames = StringUtils.abbreviate(gearNames, 2000);
        LiteLMConstraint litelmConstraint = getProgramConstraint(directBase.getProgram().getConstraintID());

        // event log for gear creation
        for (LMProgramDirectGear lmProgramDirectGear : gears) {
            demandResponseEventLogService.gearCreated(lmProgramDirectGear.getGearName(),
                                                      lmProgramDirectGear.getControlMethod().name(),
                                                      directBase.getPAOName(),
                                                      lmProgramDirectGear.getGearNumber(),
                                                      liteYukonUser);
        }

        // event log for copying load program
        demandResponseEventLogService.loadProgramCreated(directBase.getPAOName(),
                                                         directBase.getPaoType(),
                                                         litelmConstraint.getConstraintName(),
                                                         reducedGearNames,
                                                         null, // In case of load program copy, load groups didn't copied
                                                         liteYukonUser);
    }

    /**
     * Returns Program Constraint from constraintId
     */
    private LiteLMConstraint getProgramConstraint(Integer constraintId) {
        LiteLMConstraint liteLMConstraint = dbCache.getAllLMProgramConstraints().stream()
                                                                                .filter(constraint -> constraint.getConstraintID() == constraintId)
                                                                                .findFirst()
                                                                                .get();
        return liteLMConstraint;
    }

    /**
     * Returns Comma separated values of gear names
     */
    private String getGearNamesString(LoadProgram loadProgram) {
        String gearNames = loadProgram.getGears().stream()
                                                 .map(gear -> gear.getGearName())
                                                 .collect(Collectors.joining(", "));
        return StringUtils.abbreviate(gearNames, 2000);
    }

    /**
     * Returns Comma separated values of load group names
     */
    private String getLoadGroupNamesString(LoadProgram loadProgram) {
        String groupNames = loadProgram.getAssignedGroups().stream()
                                                           .peek(lg -> lg.setGroupName(dbCache.getAllPaosMap().get(lg.getGroupId()).getPaoName()))
                                                           .map(lg -> lg.getGroupName())
                                                           .collect(Collectors.joining(", "));
        return StringUtils.abbreviate(groupNames, 2000);
    }

    /**
     *  Log events for gear creation from Program Gear Object
     */
    private void logEventsForGearCreation(LoadProgram loadProgram, LiteYukonUser liteYukonUser) {
        for (ProgramGear programGear : loadProgram.getGears()) {
            demandResponseEventLogService.gearCreated(programGear.getGearName(),
                                                      programGear.getControlMethod().name(),
                                                      loadProgram.getName(),
                                                      programGear.getGearNumber(),
                                                      liteYukonUser);
        }
    }


}
