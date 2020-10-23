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

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFieldsBuilder;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.Notification;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramConstraint;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindow;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindowFields;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.LMObjectDeletionFailureException;
import com.cannontech.common.exception.LoadProgramProcessingException;
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
import com.cannontech.database.data.lite.LiteLMPAOExclusion;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;
import com.cannontech.database.db.device.lm.LMProgramDirect;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
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
        LMProgramBase lmProgram = getDBPersistent(loadProgram.getProgramId(), loadProgram.getType());
        buildLMProgramBaseDBPersistent(lmProgram, loadProgram);

        dbPersistentDao.performDBChange(lmProgram, TransactionType.INSERT);

        SimpleDevice device = SimpleDevice.of(lmProgram.getPAObjectID(), lmProgram.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);

        // Logging events during load program creation
        processEventLogsForProgramCreate(loadProgram, liteYukonUser);

        return buildLoadProgramModel(lmProgram);
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
        buildLMProgramBaseDBPersistent(lmProgramBase, loadProgram);

        buildLMMemberControlDBPersistent(lmProgramBase, loadProgram);

        dbPersistentDao.performDBChange(lmProgramBase, TransactionType.UPDATE);

        // Logging events during load program update
        processEventLogsForProgramUpdate(loadProgram, oldGears, liteYukonUser);

        return buildLoadProgramModel(lmProgramBase);
    }

    @Override
    public LoadProgram retrieve(int programId) {
        LiteYukonPAObject lmProgram = getProgramFromCache(programId);
        LMProgramBase lmProgramBase = (LMProgramBase) dbPersistentDao.retrieveDBPersistent(lmProgram);
        return buildLoadProgramModel(lmProgramBase);
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
            throw new LMObjectDeletionFailureException(message);
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
    public int copy(int programId, LoadProgramCopy loadProgramCopy, LiteYukonUser liteYukonUser) {

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

        return program.getPAObjectID();
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
     * Build DB Persistent object for LMProgramBase
     */

    private LMProgramBase buildLMProgramBaseDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram) {

        lmProgram.setName(loadProgram.getName());
        lmProgram.getProgram().setControlType(loadProgram.getOperationalState().name());

        ProgramConstraint constraint = loadProgram.getConstraint();
        if (constraint.getConstraintId() != null) {
            lmProgram.getProgram().setConstraintID(constraint.getConstraintId());
        }

        buildProgramControlWindowDBPersistent(lmProgram, loadProgram);
        buildGroupsDBPersistent(lmProgram, loadProgram);

        if (lmProgram instanceof LMProgramDirectBase) {
            LMProgramDirectBase prog = (LMProgramDirectBase) lmProgram;
            if (loadProgram.getTriggerOffset() != null) {
                prog.getDirectProgram().setTriggerOffset(loadProgram.getTriggerOffset());
            }
            if (loadProgram.getRestoreOffset() != null) {
                prog.getDirectProgram().setRestoreOffset(loadProgram.getRestoreOffset());
            }

            buildNotificationDBPersistent(lmProgram, loadProgram);
            buildGearsDBPersistent(lmProgram, loadProgram);
        }
        return lmProgram;

    }

    /**
     *  Build  Gears DBPersistent object
     */

    private void buildGearsDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram) {
        LMProgramDirectBase prog = (LMProgramDirectBase) lmProgram;

        if (CollectionUtils.isNotEmpty(prog.getLmProgramDirectGearVector())) {
            prog.getLmProgramDirectGearVector().clear();
            LMProgramDirectGear.deleteAllDirectGearsForProgram(loadProgram.getProgramId());
        }

        if (loadProgram.getGears() != null) {

            List<ProgramGear> oldGears = loadProgram.getGears().stream().filter(gear -> gear.getGearId() != null).collect(Collectors.toList());
            List<ProgramGear> newGears = loadProgram.getGears().stream().filter(gear -> gear.getGearId() == null).collect(Collectors.toList());

            List<ProgramGear> gears = ListUtils.union(oldGears, newGears);
            gears.forEach(gear -> {
                LMProgramDirectGear directGear = gear.buildDBPersistent();
                directGear.setDeviceID(loadProgram.getProgramId());
                prog.getLmProgramDirectGearVector().add(directGear);
            });

        }
    }

    /**
     *  Build Notification DB Persistent object
     */

    private void buildNotificationDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram) {
        LMProgramDirectBase prog = (LMProgramDirectBase) lmProgram;

        if (CollectionUtils.isNotEmpty(prog.getLmProgramDirectNotifyGroupVector())) {
            prog.getLmProgramDirectNotifyGroupVector().clear();
        }

        if (loadProgram.getNotification() != null) {

            if (loadProgram.getNotification().getAssignedNotificationGroups() != null) {
                loadProgram.getNotification().getAssignedNotificationGroups().forEach(notificationGroup -> {

                    LMDirectNotificationGroupList group = new LMDirectNotificationGroupList();
                    group.setDeviceID(loadProgram.getProgramId());
                    group.setNotificationGrpID(notificationGroup.getNotificationGrpID());
                    prog.getLmProgramDirectNotifyGroupVector().addElement(group);

                });
            }

            if (loadProgram.getNotification().getProgramStartInMinutes() != null) {
                Integer programStart = loadProgram.getNotification().getProgramStartInMinutes();
                prog.getDirectProgram().setNotifyActiveOffset(programStart * 60);
            } else {
                prog.getDirectProgram().setNotifyActiveOffset(-1);
            }

            if (loadProgram.getNotification().getProgramStopInMinutes() != null) {
                Integer programStop = loadProgram.getNotification().getProgramStopInMinutes();
                prog.getDirectProgram().setNotifyInactiveOffset(programStop * 60);
            } else {
                prog.getDirectProgram().setNotifyInactiveOffset(-1);
            }

            Boolean notifyOnAdjust = loadProgram.getNotification().getNotifyOnAdjust();
            if (notifyOnAdjust != null && notifyOnAdjust) {
                prog.getDirectProgram().setNotifyAdjust(LMProgramDirect.NOTIFY_ADJUST_ENABLED);
            } else {
                prog.getDirectProgram().setNotifyAdjust(LMProgramDirect.NOTIFY_ADJUST_DISABLED);
            }

            Boolean enableOnSchedule = loadProgram.getNotification().getEnableOnSchedule();
            if (enableOnSchedule != null && enableOnSchedule) {
                prog.getDirectProgram().setEnableSchedule(LMProgramDirect.NOTIFY_SCHEDULE_ENABLED);
            } else {
                prog.getDirectProgram().setEnableSchedule(LMProgramDirect.NOTIFY_SCHEDULE_DISABLED);
            }
        }

    }

    /**
     *  Build Groups DB Persistent object
     */

    private void buildGroupsDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram) {
        
        if (CollectionUtils.isNotEmpty(lmProgram.getLmProgramStorageVector())) {
            lmProgram.getLmProgramStorageVector().clear();
        }

        if (loadProgram.getAssignedGroups() != null) {

            int groupsListSize = loadProgram.getAssignedGroups().size();

            loadProgram.getAssignedGroups().forEach(grp -> {
                LMProgramDirectGroup group = new LMProgramDirectGroup();

                group.setDeviceID(loadProgram.getProgramId());
                if (groupsListSize == 1) {
                    group.setGroupOrder(1);
                } else {
                    group.setGroupOrder(grp.getGroupOrder());
                }
                group.setLmGroupDeviceID(grp.getGroupId());
                lmProgram.getLmProgramStorageVector().addElement(group);
            });
        }
    }

    /**
     *  Build Control Window DB Persistent object
     */

    private void buildProgramControlWindowDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram) {

        if (CollectionUtils.isNotEmpty(lmProgram.getLmProgramControlWindowVector())) {
            lmProgram.getLmProgramControlWindowVector().clear();
        }

        if (loadProgram.getControlWindow() != null) {

            ProgramControlWindowFields controlWindowOne = loadProgram.getControlWindow().getControlWindowOne();
            ProgramControlWindowFields controlWindowTwo = loadProgram.getControlWindow().getControlWindowTwo();
            lmProgram.setPAObjectID(loadProgram.getProgramId());
            if (controlWindowOne != null) {
                buildLmProgramControlWindow(lmProgram, loadProgram.getProgramId(), controlWindowOne, 1);
            }
            if (controlWindowTwo != null) {
                buildLmProgramControlWindow(lmProgram, loadProgram.getProgramId(), controlWindowTwo, 2);
            }
        }
        
    }

    private void buildLmProgramControlWindow(LMProgramBase lmProgram, Integer programId, ProgramControlWindowFields controlWindowFields,
            Integer windowNumber) {

        if (controlWindowFields != null && controlWindowFields.getAvailableStartTimeInMinutes() != null && controlWindowFields.getAvailableStopTimeInMinutes() != null) {
            LMProgramControlWindow window = new LMProgramControlWindow();
            int startTimeInSeconds = controlWindowFields.getAvailableStartTimeInMinutes() * 60;
            int stopTimeInSeconds = controlWindowFields.getAvailableStopTimeInMinutes() * 60;
            if (stopTimeInSeconds < startTimeInSeconds) {
                // make sure server knows that this is the next day
                stopTimeInSeconds = stopTimeInSeconds + 86400;
            }
            window.setAvailableStartTime(startTimeInSeconds);
            window.setAvailableStopTime(stopTimeInSeconds);
            window.setDeviceID(programId);
            window.setWindowNumber(windowNumber);
            lmProgram.getLmProgramControlWindowVector().add(window);
        }

    }

    /**
     * Returns MemberControl DB Persistent object
     */
    
    private void buildLMMemberControlDBPersistent(LMProgramBase lmProgram, LoadProgram loadProgram) {

        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        if (rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS, user)
            || user.getUserID() == UserUtils.USER_ADMIN_ID) {
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
     *  Build Load Program Model object
     */
 
    public LoadProgram buildLoadProgramModel(LMProgramBase program) {

        LoadProgram loadProgram = new LoadProgram();
        loadProgram.setProgramId(program.getPAObjectID());
        loadProgram.setType(program.getPaoType());
        loadProgram.setName(program.getPAOName());
        loadProgram.setOperationalState(OperationalState.valueOf(program.getProgram().getControlType()));

        LiteLMConstraint liteLMConstraint = getProgramConstraint(program.getProgram().getConstraintID());
        ProgramConstraint programConstraint = new ProgramConstraint();
        programConstraint.setConstraintId(liteLMConstraint.getConstraintID());
        programConstraint.setConstraintName(liteLMConstraint.getConstraintName());
        loadProgram.setConstraint(programConstraint);

        buildGroupsModel(program, loadProgram);
        buildProgramControlWindowModel(program, loadProgram);

        if (program instanceof LMProgramDirectBase) {
            LMProgramDirectBase lmProgramDirectBase = (LMProgramDirectBase) program;

            loadProgram.setTriggerOffset(lmProgramDirectBase.getDirectProgram().getTriggerOffset());
            loadProgram.setRestoreOffset(lmProgramDirectBase.getDirectProgram().getRestoreOffset());

            buildNotificationModel(lmProgramDirectBase, loadProgram);
            buildGearsModel(lmProgramDirectBase, loadProgram);
            LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
            if (rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS, user)
                    || user.getUserID() == UserUtils.USER_ADMIN_ID) {
                buildLMMemberControlModel(lmProgramDirectBase, loadProgram);
            }
        }
        return loadProgram;

    }

    /**
     *  Build Load Groups Model object
     */
 
    private void buildGroupsModel(LMProgramBase program, LoadProgram loadProgram) {

        List<ProgramGroup> loadGroups = new ArrayList<>();

        program.getLmProgramStorageVector().forEach(group -> {

            LMProgramDirectGroup directGroup = (LMProgramDirectGroup) group;

            List<LiteYukonPAObject> paObjects = dbCache.getAllLMGroups();
            Optional <LiteYukonPAObject> paObject = paObjects.stream()
                                                  .filter(p -> p.getLiteID() == directGroup.getLmGroupDeviceID())
                                                  .findFirst();
            if (paObject.isPresent()) {
                ProgramGroup loadGroup = new ProgramGroup();
                loadGroup.setGroupId(directGroup.getLmGroupDeviceID());
                loadGroup.setGroupOrder(directGroup.getGroupOrder());
                loadGroup.setGroupName(paObject.get().getPaoName());
                loadGroup.setType(paObject.get().getPaoType());
                loadGroups.add(loadGroup);
            }

        });

        if (CollectionUtils.isNotEmpty(loadGroups)) {
            loadProgram.setAssignedGroups(loadGroups);
        }
    }

    /**
     *  Build Control Window Model object
     */
 
    private void buildProgramControlWindowModel(LMProgramBase program, LoadProgram loadProgram) {
        ProgramControlWindow programControlWindow = new ProgramControlWindow();
        program.getLmProgramControlWindowVector().forEach(window -> {
            ProgramControlWindowFields fields = buildProgramControlWindowFields(window);
            if (window.getWindowNumber().intValue() == 1) {
                programControlWindow.setControlWindowOne(fields);
            }

            if (window.getWindowNumber().intValue() == 2) {
                programControlWindow.setControlWindowTwo(fields);
            }
            loadProgram.setControlWindow(programControlWindow);
        });
    }

    /**
     *  Build Gear Model object
     */
 
    private void buildGearsModel(LMProgramDirectBase lmProgramDirectBase, LoadProgram loadProgram) {

        List<ProgramGear> gears = new ArrayList<>();
        lmProgramDirectBase.getLmProgramDirectGearVector().forEach(gear -> {

            ProgramGear programGear = new ProgramGear();
            programGear.buildModel(gear);
            gears.add(programGear);
        });

        if (CollectionUtils.isNotEmpty(gears)) {
            loadProgram.setGears(gears);
        }
    }

    /**
     *  Build Notification Model object
     */
 
    private void buildNotificationModel(LMProgramDirectBase lmProgramDirectBase, LoadProgram loadProgram) {
        Notification notification = new Notification();
        List<NotificationGroup> notificationGroupList = new ArrayList<>();
        lmProgramDirectBase.getLmProgramDirectNotifyGroupVector().forEach(notificationGroup -> {
            NotificationGroup group = new NotificationGroup();

            List<LiteNotificationGroup> notificationGroups = dbCache.getAllContactNotificationGroups();
            LiteNotificationGroup liteNotificationGroup = notificationGroups.stream()
                                                                            .filter(notifGroup -> notifGroup.getNotificationGroupID() == notificationGroup.getNotificationGroupID())
                                                                            .findFirst()
                                                                            .get();
            group.setNotificationGrpID(notificationGroup.getNotificationGroupID());
            group.setNotificationGrpName(liteNotificationGroup.getNotificationGroupName());
            notificationGroupList.add(group);

        });

        if (CollectionUtils.isNotEmpty(notificationGroupList)) {
            notification.setAssignedNotificationGroups(notificationGroupList);
        }

        Integer numStart = lmProgramDirectBase.getDirectProgram().getNotifyActiveOffset();
        Integer numStop = lmProgramDirectBase.getDirectProgram().getNotifyInactiveOffset();

        if (numStart.intValue() != -1) {
            notification.setProgramStartInMinutes(lmProgramDirectBase.getDirectProgram().getNotifyActiveOffset() / 60);
        }
        if (numStop.intValue() != -1) {
            notification.setProgramStopInMinutes(lmProgramDirectBase.getDirectProgram().getNotifyInactiveOffset() / 60);
        }
        boolean isNotifyAdjust = lmProgramDirectBase.getDirectProgram().getNotifyAdjust() == LMProgramDirect.NOTIFY_ADJUST_ENABLED.intValue();
        if (isNotifyAdjust) {
            notification.setNotifyOnAdjust(true);
        } else {
            notification.setNotifyOnAdjust(false);
        }
        boolean isNotifyWhenScheduled = lmProgramDirectBase.getDirectProgram().shouldNotifyWhenScheduled() == LMProgramDirect.NOTIFY_SCHEDULE_ENABLED.intValue();
        if (isNotifyWhenScheduled) {
            notification.setEnableOnSchedule(true);
        } else {
            notification.setEnableOnSchedule(false);
        }

        if (CollectionUtils.isNotEmpty(notificationGroupList) || numStart.intValue() != -1 || numStop.intValue() != -1
            || isNotifyAdjust || isNotifyWhenScheduled) {
            loadProgram.setNotification(notification);
        }
    }

    private ProgramControlWindowFields buildProgramControlWindowFields(LMProgramControlWindow window) {
        int localStartTime = window.getAvailableStartTime() / 60;
        int stopTime = window.getAvailableStopTime();
        if (stopTime > 86400) {
            stopTime = stopTime - 86400;
        }
        int localStopTime = stopTime / 60;
        ProgramControlWindowFields controlWindow = new ProgramControlWindowFields(localStartTime, localStopTime);
        return controlWindow;
    }
    

    private void buildLMMemberControlModel(LMProgramDirectBase programDirectBase, LoadProgram loadProgram) {
        List <ProgramDirectMemberControl> memberControls = new ArrayList<>();
        programDirectBase.getPAOExclusionVector().forEach(paoExclusion -> {
            ProgramDirectMemberControl memberControl = new ProgramDirectMemberControl();
            memberControl.setSubordinateProgId(paoExclusion.getExcludedPaoID());

            LiteYukonPAObject excludedPao =
                    dbCache.getAllLMPrograms().stream()
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

    @Override
    public List<ProgramGroup> getAllAvailableProgramLoadGroups(PaoType programType) {
        
        if(!programType.isLmProgram()) {
            throw new LoadProgramProcessingException("ProgramType not supported");
        }

        return getAllProgramLoadGroups(programType);
    }

    @Override
    public List<ProgramGroup> getAvailableProgramLoadGroups(int programId) {
        LiteYukonPAObject lmProgram = getProgramFromCache(programId);

        LMProgramDirectBase dirProg = (LMProgramDirectBase) getDBPersistent(lmProgram.getLiteID(), lmProgram.getPaoType());

        List<ProgramGroup> programGroups = getAllProgramLoadGroups(lmProgram.getPaoType());

        List<ProgramGroup> availableProgramLoadGroups = programGroups.stream()
                                                .filter(group -> dirProg.getLmProgramStorageVector().stream()
                                                                                                    .allMatch(dirGroup -> dirGroup.getDeviceID().intValue() != group.getGroupId().intValue()))
                                                .collect(Collectors.toList());
        
       return availableProgramLoadGroups;
           
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

                if (isSepProgram && isGroupSepCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if ((!isSepProgram && !isGroupSepCompatible(loadGroupType))
                    && (!isEcobeeProgram && !isGroupEcobeeCompatible(loadGroupType))
                    && (!isHoneywellProgram && !isGroupHoneywellCompatible(loadGroupType))
                    && (!isItronProgram && !isGroupItronCompatible(loadGroupType))
                    && (!isNestProgram && !isGroupNestCompatible(loadGroupType))) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isEcobeeProgram && isGroupEcobeeCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isHoneywellProgram && isGroupHoneywellCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isNestProgram && isGroupNestCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isItronProgram && isGroupItronCompatible(loadGroupType)) {
                    programGroups.add(buildProgramLoadGroup(group));
                } else if (isMeterDisconnectProgram && isGroupMeterDisconnectCompatible(loadGroupType)) {
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

    @Override
    public List<NotificationGroup> getAllAvailableProgramNotificationGroups() {
        List<NotificationGroup> notificationGroups = getAllProgramNotificationGroups();
        return notificationGroups;
    }

    @Override
    public List<NotificationGroup> getAvailableProgramNotificationGroups(int programId) {

        LiteYukonPAObject lmProgram = getProgramFromCache(programId);

        LMProgramDirectBase dirProg = (LMProgramDirectBase) getDBPersistent(lmProgram.getLiteID(), lmProgram.getPaoType());
        List<NotificationGroup> notificationGroups = getAllProgramNotificationGroups();

        List<NotificationGroup> availableNotificationGroups =
            notificationGroups.stream()
                              .filter(group -> dirProg.getLmProgramDirectNotifyGroupVector().stream()
                                                                                            .allMatch(dirGroup -> dirGroup.getNotificationGroupID().intValue() != group.getNotificationGrpID().intValue()))
                              .collect(Collectors.toList());
        return availableNotificationGroups;
    }

    private List<NotificationGroup> getAllProgramNotificationGroups() {
        List<LiteNotificationGroup> allNotificationGroups = dbCache.getAllContactNotificationGroups();
        List<NotificationGroup> notificationGroups = allNotificationGroups.stream()
                                                                          .map(group ->  new NotificationGroup(group.getNotificationGroupID(), group.getNotificationGroupName()))
                                                                          .collect(Collectors.toList());
        return notificationGroups;
    }

    @Override
    public List<ProgramDirectMemberControl> getAllAvailableDirectMemberControls() {

        List<LiteYukonPAObject> programs = dbCache.getAllLMPrograms();
        List<LiteLMPAOExclusion> currentlyExcluded = dbCache.getAllLMPAOExclusions();

        List<LiteYukonPAObject> lmSubordinates =
                programs.stream()
                         .filter(program -> (program.getPaoType().isDirectProgram()
                                 && !(isMasterProgram(program.getLiteID(), currentlyExcluded))))
                         .collect(Collectors.toList());

        return buildProgramDirectMemberControl(lmSubordinates);

    }

    @Override
    public List<ProgramDirectMemberControl> getAvailableDirectMemberControls(int programId) {

        List<LiteYukonPAObject> programs = dbCache.getAllLMPrograms();

        LiteYukonPAObject lmProgram = getProgramFromCache(programId);

        LMProgramDirectBase dirProg = (LMProgramDirectBase) getDBPersistent(lmProgram.getLiteID(), lmProgram.getPaoType());

        List<LiteLMPAOExclusion> currentlyExcluded = dbCache.getAllLMPAOExclusions();

        // init storage that will contain exclusion (member control) information
        // make sure this program itself isn't showing up as an available subordinate
        List<LiteYukonPAObject> lmSubordinates = programs.stream()
                                                         .filter(program -> (program.getPaoType().isDirectProgram()
                                                                 && !(isMasterProgram(program.getLiteID(), currentlyExcluded)) && (program.getLiteID() != programId)))
                                                         .collect(Collectors.toList());

        List<LiteYukonPAObject> availableLmSubordinates =
                lmSubordinates.stream()
                              .filter(group -> dirProg.getPAOExclusionVector().stream()
                                                                              .allMatch(dirGroup -> dirGroup.getExcludedPaoID().intValue() != group.getLiteID()))
                              .collect(Collectors.toList());

        return buildProgramDirectMemberControl(availableLmSubordinates);

    }

    private List<ProgramDirectMemberControl> buildProgramDirectMemberControl(List<LiteYukonPAObject> availableLmSubordinates) {
        List<ProgramDirectMemberControl> directMemberControls = new ArrayList<>();
        availableLmSubordinates.forEach(lmSubordinate -> {
            ProgramDirectMemberControl directMemberControl = new ProgramDirectMemberControl();
            directMemberControl.setSubordinateProgId(lmSubordinate.getLiteID());
            directMemberControl.setSubordinateProgName(lmSubordinate.getPaoName());
            directMemberControls.add(directMemberControl);
        });
        return directMemberControls;
    }
 
    /**
     * makes sure it is a direct program and it is not already a master
     */
    private boolean isMasterProgram(int programId, List<LiteLMPAOExclusion> liteLMPAOExclusions) {
        Boolean isMasterProgram = false;
        if (liteLMPAOExclusions != null) {

            isMasterProgram =
                    liteLMPAOExclusions.stream()
                                       .anyMatch(paoExclusion -> programId == paoExclusion.getMasterPaoID());

        }
        return isMasterProgram;
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
