package com.cannontech.dr.area.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProgramAssignment;
import com.cannontech.common.dr.setup.ControlAreaProjection;
import com.cannontech.common.dr.setup.ControlAreaProjectionType;
import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.dr.setup.ControlAreaTriggerType;
import com.cannontech.common.dr.setup.DailyDefaultState;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.TFBoolean;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.dr.area.service.ControlAreaSetupService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;


public class ControlAreaSetupServiceImpl implements ControlAreaSetupService {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PointDao pointdao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DbChangeManager dbChangeManager;

    /**
     * Retrieve control area based on control area Id.
     */
    @Override
    public ControlArea retrieve(int areaId) {
        LiteYukonPAObject liteControlArea = dbCache.getAllLMControlAreas().stream()
                                                                          .filter(controlArea -> controlArea.getLiteID() == areaId)
                                                                          .findFirst().orElseThrow(() -> new NotFoundException("Control area Id not found " + areaId));

        LMControlArea lmControlArea = (LMControlArea) dbPersistentDao.retrieveDBPersistent(liteControlArea);
        return buildControlAreaModel(lmControlArea);
    }

    @Override
    public int create(ControlArea controlArea) {
        LMControlArea lmControlArea = getDBPersistent(controlArea.getControlAreaId());
        buildLMControlAreaDBPersistent(lmControlArea, controlArea);

        dbPersistentDao.performDBChange(lmControlArea, TransactionType.INSERT);

        SimpleDevice device = SimpleDevice.of(lmControlArea.getPAObjectID(), lmControlArea.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);

        return lmControlArea.getPAObjectID();
    }

    @Override
    public int update(int controlAreaId, ControlArea controlArea) {
        dbCache.getAllLMControlAreas().stream()
                                      .filter(controlarea -> controlarea.getLiteID() == controlAreaId)
                                      .findFirst().orElseThrow(() -> new NotFoundException(" Control Area Id not found  " + controlAreaId ));

        LMControlArea lmControlArea = getDBPersistent(controlAreaId);
        buildLMControlAreaDBPersistent(lmControlArea, controlArea);
        lmControlArea.setPAObjectID(controlAreaId);
        dbPersistentDao.performDBChange(lmControlArea, TransactionType.UPDATE);

        return lmControlArea.getPAObjectID();
    }

    @Override
    public int delete(int areaId, String areaName) {
        LiteYukonPAObject controlArea = dbCache.getAllLMControlAreas()
                                                         .stream()
                                                         .filter(area -> area.getLiteID() == areaId && area.getPaoName()
                                                         .equalsIgnoreCase(areaName))
                                                         .findFirst()
                                                         .orElseThrow(() -> new NotFoundException("Control Area Id and Name combination not found"));
        YukonPAObject lmControlArea = (YukonPAObject) LiteFactory.createDBPersistent(controlArea);
        dbPersistentDao.performDBChange(lmControlArea, TransactionType.DELETE);

        return lmControlArea.getPAObjectID();
    }

    /**
     * Build trigger model for Control Area.
     */
    private void buildTriggerModel(LMControlArea lmControlArea, ControlArea controlArea) {
        Integer triggerOrder = 1;
        List<ControlAreaTrigger> areaTriggers = new ArrayList<>();
        for (LMControlAreaTrigger areaTrigger : lmControlArea.getLmControlAreaTriggerVector()) {
            ControlAreaTrigger controlAreaTrigger = new ControlAreaTrigger();
            controlAreaTrigger.setTriggerId(areaTrigger.getTriggerID());
            controlAreaTrigger.setTriggerNumber(triggerOrder);
            controlAreaTrigger.setTriggerType(ControlAreaTriggerType.getTriggerValue(areaTrigger.getTriggerType()));
            controlAreaTrigger.setTriggerPointId(areaTrigger.getPointID());

            if (areaTrigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
                controlAreaTrigger.setNormalState(areaTrigger.getNormalState());
            } else {
                controlAreaTrigger.setMinRestoreOffset(areaTrigger.getMinRestoreOffset());
                controlAreaTrigger.setPeakPointId(areaTrigger.getPeakPointID());
                if ((areaTrigger.getTriggerType()).equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD_POINT)) {
                    controlAreaTrigger.setThresholdPointId(areaTrigger.getThresholdPointID());
                } else {
                    controlAreaTrigger.setThreshold(areaTrigger.getThreshold());
                    ControlAreaProjection projection = new ControlAreaProjection();
                    projection.setProjectionType(
                        ControlAreaProjectionType.getProjectionValue(areaTrigger.getProjectionType()));
                    projection.setProjectAheadDuration(
                        TimeIntervals.fromSeconds(areaTrigger.getProjectAheadDuration()));
                    projection.setProjectionPoint(areaTrigger.getProjectionPoints());
                    controlAreaTrigger.setControlAreaProjection(projection);
                    controlAreaTrigger.setAtku(areaTrigger.getThresholdKickPercent());
                }
            }
            areaTriggers.add(controlAreaTrigger);
            triggerOrder++;
        }

        if (CollectionUtils.isNotEmpty(areaTriggers)) {
            controlArea.setTriggers(areaTriggers);
        }
    }

    /**
     * Build program assignment model for control area.
     */
    private void buildProgramAssignmentModel(LMControlArea lmControlArea, ControlArea controlArea) {
        List<ControlAreaProgramAssignment> programAssignment = new ArrayList<>();

        lmControlArea.getLmControlAreaProgramVector().forEach(program -> {
            ControlAreaProgramAssignment controlAreaProgramAssignment = new ControlAreaProgramAssignment();
            controlAreaProgramAssignment.setProgramId(program.getLmProgramDeviceID());
            controlAreaProgramAssignment.setStartPriority(program.getStartPriority());
            controlAreaProgramAssignment.setStopPriority(program.getStopPriority());
            programAssignment.add(controlAreaProgramAssignment);
        });

        if (CollectionUtils.isNotEmpty(programAssignment)) {
            controlArea.setProgramAssignment(programAssignment);
        }

    }

    /**
     * Build Control Area Model object
     */
    private ControlArea buildControlAreaModel(LMControlArea lmControlArea) {
        ControlArea controlArea = new ControlArea();
        com.cannontech.database.db.device.lm.LMControlArea lmcontrolarea = lmControlArea.getControlArea();
        controlArea.setControlAreaId(lmControlArea.getPAObjectID());
        controlArea.setName(lmControlArea.getPAOName());

        if (lmcontrolarea.getControlInterval().intValue() != 0) {
            controlArea.setControlInterval(TimeIntervals.fromSeconds(lmcontrolarea.getControlInterval()));
        }

        if (lmcontrolarea.getMinResponseTime().intValue() != 0) {
            controlArea.setMinResponseTime(TimeIntervals.fromSeconds(lmcontrolarea.getMinResponseTime()));
        }

        controlArea.setDailyDefaultState(DailyDefaultState.valueOf(lmcontrolarea.getDefOperationalState()));

        controlArea.setDailyStartTimeInMinutes(
            lmcontrolarea.getDefDailyStartTime() == com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED
                ? null : lmcontrolarea.getDefDailyStartTime() / 60);

        controlArea.setDailyStopTimeInMinutes(
            lmcontrolarea.getDefDailyStopTime() == com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED
                ? null : lmcontrolarea.getDefDailyStopTime() / 60);

        if (lmcontrolarea.getRequireAllTriggersActiveFlag().equals(TFBoolean.TRUE.getDatabaseRepresentation())) {
            controlArea.setAllTriggersActiveFlag(TFBoolean.TRUE);
        } else {
            controlArea.setAllTriggersActiveFlag(TFBoolean.FALSE);
        }
        buildTriggerModel(lmControlArea, controlArea);
        buildProgramAssignmentModel(lmControlArea, controlArea);

        return controlArea;
    }

    /**
     * Return DB Persistent object
     */
    private LMControlArea getDBPersistent(Integer paoId) {
        LMControlArea lmControlArea = new LMControlArea();
        if (paoId != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
            lmControlArea = (LMControlArea) dbPersistentDao.retrieveDBPersistent(pao);
        }
        return lmControlArea;
    }

    /**
     * Build DB Persistent object for LMControlArea.
     */
    private LMControlArea buildLMControlAreaDBPersistent(LMControlArea lmControlArea, ControlArea controlArea) {
        lmControlArea.setPAOName(controlArea.getName());
        com.cannontech.database.db.device.lm.LMControlArea lmDbControlArea = lmControlArea.getControlArea();

        if (controlArea.getControlInterval() != null) {
            lmDbControlArea.setControlInterval(controlArea.getControlInterval().getSeconds());
        }

        if (controlArea.getMinResponseTime() != null) {
            lmDbControlArea.setMinResponseTime(controlArea.getMinResponseTime().getSeconds());
        }

        lmDbControlArea.setDefOperationalState(controlArea.getDailyDefaultState().name());

        if (controlArea.getAllTriggersActiveFlag() != null) {
            lmDbControlArea.setRequireAllTriggersActiveFlag((Character) controlArea.getAllTriggersActiveFlag().getDatabaseRepresentation());
        }
        
        if (controlArea.getDailyStartTimeInMinutes() != null) {
            lmDbControlArea.setDefDailyStartTime(controlArea.getDailyStartTimeInMinutes() * 60);
        } else {
            lmDbControlArea.setDefDailyStartTime(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED);
        }

        if (controlArea.getDailyStopTimeInMinutes() != null) {
            lmDbControlArea.setDefDailyStopTime(controlArea.getDailyStopTimeInMinutes() * 60);
        } else {
            lmDbControlArea.setDefDailyStopTime(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED);
        }

        buildAreaTriggersDBPersistent(lmControlArea, controlArea);
        buildAreaProgramAssignmentDBPersistent(lmControlArea, controlArea);

        return lmControlArea;

    }

    private void buildAreaTriggersDBPersistent(LMControlArea lmControlArea, ControlArea controlArea) {
        Integer triggerOrder = 1;
        if (CollectionUtils.isNotEmpty(lmControlArea.getLmControlAreaTriggerVector())) {
            lmControlArea.getLmControlAreaTriggerVector().clear();
        }
        if (CollectionUtils.isNotEmpty(controlArea.getTriggers())) {
            for (ControlAreaTrigger areaTrigger : controlArea.getTriggers()) {

                LMControlAreaTrigger lmControlAreaTrigger = new LMControlAreaTrigger();
                lmControlAreaTrigger.setPointID(areaTrigger.getTriggerPointId());
                lmControlAreaTrigger.setTriggerType(areaTrigger.getTriggerType().getTriggerTypeValue());
                lmControlAreaTrigger.setTriggerNumber(triggerOrder);
                buildTriggerByType(lmControlAreaTrigger,areaTrigger);
                lmControlArea.getLmControlAreaTriggerVector().add(lmControlAreaTrigger);
                triggerOrder++;
            }
        }
    }

    private void buildTriggerByType(LMControlAreaTrigger lmControlAreaTrigger, ControlAreaTrigger areaTrigger) {

        if (areaTrigger.getTriggerType().getTriggerTypeValue().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
            lmControlAreaTrigger.setNormalState(areaTrigger.getNormalState());
            lmControlAreaTrigger.setThreshold(0.0);
        } else {
            lmControlAreaTrigger.setNormalState(IlmDefines.INVALID_INT_VALUE);
            if ((areaTrigger.getTriggerType().getTriggerTypeValue()).equalsIgnoreCase( IlmDefines.TYPE_THRESHOLD_POINT)) {
                lmControlAreaTrigger.setThreshold(0.0);
                lmControlAreaTrigger.setThresholdPointID(areaTrigger.getThresholdPointId());
            } else {
                lmControlAreaTrigger.setThreshold(areaTrigger.getThreshold());
                if (areaTrigger.getAtku() != null) {
                    lmControlAreaTrigger.setThresholdKickPercent(areaTrigger.getAtku());
                }
                String projectionType=areaTrigger.getControlAreaProjection().getProjectionType().getProjectionTypeValue();
                lmControlAreaTrigger.setProjectionType(projectionType);
                if(projectionType.equals(ControlAreaProjectionType.NONE.getDatabaseRepresentation())) {
                     lmControlAreaTrigger.setProjectionPoints(5);
                     lmControlAreaTrigger.setProjectAheadDuration(TimeIntervals.MINUTES_5.getSeconds());
                }else {
                     lmControlAreaTrigger.setProjectionPoints(areaTrigger.getControlAreaProjection().getProjectionPoint());
                     lmControlAreaTrigger.setProjectAheadDuration(
                         areaTrigger.getControlAreaProjection().getProjectAheadDuration().getSeconds());
                }
            }

            if (areaTrigger.getMinRestoreOffset() != null) {
                lmControlAreaTrigger.setMinRestoreOffset(areaTrigger.getMinRestoreOffset());
            }

            if (areaTrigger.getPeakPointId() != null) {
                lmControlAreaTrigger.setPeakPointID(areaTrigger.getPeakPointId());
            } else {
                lmControlAreaTrigger.setPeakPointID(IlmDefines.INVALID_INT_VALUE);
            }
        }
    }

    private void buildAreaProgramAssignmentDBPersistent(LMControlArea lmControlArea, ControlArea controlArea) {

        if (CollectionUtils.isNotEmpty(lmControlArea.getLmControlAreaProgramVector())) {
            lmControlArea.getLmControlAreaProgramVector().clear();
        }
        if (CollectionUtils.isNotEmpty(controlArea.getProgramAssignment())) {
            controlArea.getProgramAssignment().forEach(assignedProgram -> {
                LMControlAreaProgram lmControlAreaProgram = new LMControlAreaProgram();
                lmControlAreaProgram.setLmProgramDeviceID(assignedProgram.getProgramId());
                if (assignedProgram.getStartPriority() != null) {
                    lmControlAreaProgram.setStartPriority(assignedProgram.getStartPriority());
                }
                if (assignedProgram.getStopPriority() != null) {
                    lmControlAreaProgram.setStopPriority(assignedProgram.getStopPriority());
                }
                lmControlArea.getLmControlAreaProgramVector().add(lmControlAreaProgram);
            });
        }
    }

    @Override
    public List<LMDto> retrieveUnassignedPrograms() {
        List<Integer> unassignedPrgIds = LMProgram.getUnassignedPrograms();

        return unassignedPrgIds.stream()
                               .map(programId -> dbCache.getAllPaosMap().get(programId))
                               .map(program -> buildProgramAssignment(program))
                               .collect(Collectors.toList());
    }

    private LMDto buildProgramAssignment(LiteYukonPAObject program) {
        return new LMDto(program.getYukonID(), program.getPaoName());
    }

    @Override
    public List<LMDto> retrieveNormalState(int pointId) {
        // look for the litePoint here
        LitePoint litePoint = pointdao.getLitePoint(pointId);
        if (litePoint == null) {
            throw new NotFoundException("Invalid point Id" + pointId);
        }
        List<LMDto> lmDtoList = new ArrayList<>();
        LiteStateGroup stateGroup = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
        for (LiteState state : stateGroup.getStatesList()) {
            lmDtoList.add(new LMDto(state.getLiteID(), state.getStateText()));
        }

        return lmDtoList;
    }

    @Override
    public int copy(int id, LMCopy lmCopy) {
        throw new UnsupportedOperationException("Not supported copy operation");
    }

}
