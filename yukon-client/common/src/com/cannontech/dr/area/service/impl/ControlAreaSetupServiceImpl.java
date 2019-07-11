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
import com.cannontech.common.dr.setup.TriggerActiveFlag;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
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
        LiteYukonPAObject liteControlArea = dbCache.getAllLMControlAreas()
                                                             .stream()
                                                             .filter(controlArea -> controlArea.getLiteID() == areaId)
                                                             .findFirst()
                                                             .orElseThrow(() -> new NotFoundException("Control area Id not found"));

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
       dbCache.getAllLMControlAreas()
              .stream()
              .filter(controlarea -> controlarea.getLiteID() == controlAreaId)
              .findFirst()
              .orElseThrow(() -> new NotFoundException(" Control Area Id not found " + controlAreaId));
       
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
                                                         .equals(areaName))
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

        for (LMControlAreaTrigger trigger : lmControlArea.getLmControlAreaTriggerVector()) {
            LMControlAreaTrigger areaTrigger = (LMControlAreaTrigger) trigger;
            ControlAreaTrigger controlAreaTrigger = new ControlAreaTrigger();
            controlAreaTrigger.setTriggerId(areaTrigger.getTriggerID());
            controlAreaTrigger.setTriggerNumber(triggerOrder);
            triggerOrder++;
            controlAreaTrigger.setTriggerType(ControlAreaTriggerType.getDisplayValue(areaTrigger.getTriggerType()));
            controlAreaTrigger.setTriggerPointId(areaTrigger.getPointID());
            controlAreaTrigger.setNormalState(areaTrigger.getNormalState());
            controlAreaTrigger.setThreshold(areaTrigger.getThreshold());

            ControlAreaProjection projection = new ControlAreaProjection();
            projection.setProjectionType(ControlAreaProjectionType.getDisplayValue(areaTrigger.getProjectionType()));
            projection.setProjectAheadDuration(TimeIntervals.fromSeconds(areaTrigger.getProjectAheadDuration()));
            projection.setProjectionPoints(areaTrigger.getProjectionPoints());

            controlAreaTrigger.setControlAreaProjection(projection);
            controlAreaTrigger.setAtku(areaTrigger.getThresholdKickPercent());
            controlAreaTrigger.setMinRestoreOffset(areaTrigger.getMinRestoreOffset());
            controlAreaTrigger.setPeakPointId(areaTrigger.getPeakPointID());
            controlAreaTrigger.setThresholdPointId(areaTrigger.getThresholdPointID());

            areaTriggers.add(controlAreaTrigger);
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

            LMControlAreaProgram areaTrigger = (LMControlAreaProgram) program;

            ControlAreaProgramAssignment controlAreaProgramAssignment = new ControlAreaProgramAssignment();
            controlAreaProgramAssignment.setProgramDeviceId(areaTrigger.getLmProgramDeviceID());
            controlAreaProgramAssignment.setStartPriority(areaTrigger.getStartPriority());
            controlAreaProgramAssignment.setStopPriority(areaTrigger.getStopPriority());

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
        controlArea.setControlInterval(TimeIntervals.fromSeconds(lmcontrolarea.getControlInterval()));
        controlArea.setMinResponseTime(TimeIntervals.fromSeconds(lmcontrolarea.getMinResponseTime()));
        controlArea.setDailyDefaultState(DailyDefaultState.getDisplayValue(lmcontrolarea.getDefOperationalState()));
        controlArea.setDailyStartTimeInMinutes(lmcontrolarea.getDefDailyStartTime() / 60);
        controlArea.setDailyStopTimeInMinutes(lmcontrolarea.getDefDailyStopTime() / 60);
        controlArea.setAllTriggersActiveFlag(
            TriggerActiveFlag.getDisplayValue(lmcontrolarea.getRequireAllTriggersActiveFlag()));

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
        com.cannontech.database.db.device.lm.LMControlArea lmDbControlArea =
            new com.cannontech.database.db.device.lm.LMControlArea();
        lmDbControlArea.setControlInterval(controlArea.getControlInterval().getSeconds());
        lmDbControlArea.setMinResponseTime(controlArea.getMinResponseTime().getSeconds());
        lmDbControlArea.setDefOperationalState((String) controlArea.getDailyDefaultState().getDatabaseRepresentation());
        lmDbControlArea.setRequireAllTriggersActiveFlag(
            (Character) controlArea.getAllTriggersActiveFlag().getDatabaseRepresentation());

        lmDbControlArea.setDefDailyStartTime(controlArea.getDailyStartTimeInMinutes() * 60);
        lmDbControlArea.setDefDailyStopTime(controlArea.getDailyStopTimeInMinutes() * 60);

        lmControlArea.setControlArea(lmDbControlArea);

        buildAreaTriggersDBPersistent(lmControlArea, controlArea);
        buildAreaProgramAssignmentDBPersistent(lmControlArea, controlArea);

        return lmControlArea;

    }

    private void buildAreaTriggersDBPersistent(LMControlArea lmControlArea, ControlArea controlArea) {
        Integer triggerOrder = 1;
        if (!lmControlArea.getLmControlAreaTriggerVector().isEmpty()) {
            lmControlArea.getLmControlAreaTriggerVector().clear();
        }
        for (ControlAreaTrigger trigger : controlArea.getTriggers()) {

            ControlAreaTrigger areaTrigger = (ControlAreaTrigger) trigger;

            LMControlAreaTrigger lmControlAreaTrigger = new LMControlAreaTrigger();
            lmControlAreaTrigger.setTriggerID(areaTrigger.getTriggerId());
            lmControlAreaTrigger.setTriggerNumber(triggerOrder);
            triggerOrder++;
            lmControlAreaTrigger.setTriggerType((String) areaTrigger.getTriggerType().getDatabaseRepresentation());
            lmControlAreaTrigger.setPointID(areaTrigger.getTriggerPointId());
            lmControlAreaTrigger.setNormalState(areaTrigger.getNormalState());

            lmControlAreaTrigger.setProjectionType(
                (String) areaTrigger.getControlAreaProjection().getProjectionType().getDatabaseRepresentation());
            lmControlAreaTrigger.setProjectionPoints(areaTrigger.getControlAreaProjection().getProjectionPoints());
            lmControlAreaTrigger.setProjectAheadDuration(
                areaTrigger.getControlAreaProjection().getProjectAheadDuration().getSeconds());

            lmControlAreaTrigger.setThresholdKickPercent(areaTrigger.getAtku());
            lmControlAreaTrigger.setMinRestoreOffset(areaTrigger.getMinRestoreOffset());
            lmControlAreaTrigger.setPeakPointID(areaTrigger.getPeakPointId());
            lmControlAreaTrigger.setThresholdPointID(areaTrigger.getThresholdPointId());
            lmControlAreaTrigger.setThreshold(areaTrigger.getThreshold());
            lmControlArea.getLmControlAreaTriggerVector().add(lmControlAreaTrigger);
        }

    }

    private void buildAreaProgramAssignmentDBPersistent(LMControlArea lmControlArea, ControlArea controlArea) {
        controlArea.getProgramAssignment().forEach(assignedProgram -> {
            ControlAreaProgramAssignment programAssignment = (ControlAreaProgramAssignment) assignedProgram;
            LMControlAreaProgram lmControlAreaProgram = new LMControlAreaProgram();
            lmControlAreaProgram.setLmProgramDeviceID(programAssignment.getProgramDeviceId());
            lmControlAreaProgram.setStartPriority(programAssignment.getStartPriority());
            lmControlAreaProgram.setStopPriority(programAssignment.getStopPriority());

            lmControlArea.getLmControlAreaProgramVector().add(lmControlAreaProgram);
        });

    }

    @Override
    public List<LMDto> retrieveUnassignedPrograms() {
        List<Integer> unassignedPrgIDs = LMProgram.getUnassignedPrograms();

        return unassignedPrgIDs.stream().map(programId -> dbCache.getAllPaosMap().get(programId)).map(
            program -> buildProgramAssignment(program)).collect(Collectors.toList());
    }

    private LMDto buildProgramAssignment(LiteYukonPAObject area) {
        return new LMDto(area.getYukonID(), area.getPaoName());
    }

    @Override
    public List<LMDto> retrieveNormalState(int pointId) {
        LitePoint litePoint = null;
        // look for the litePoint here
        litePoint = pointdao.getLitePoint(pointId);
        if (litePoint == null) {
            throw new NotFoundException("Invalid point Id");
        }
        List<LMDto> lmDtoList = new ArrayList<LMDto>();
        LiteStateGroup stateGroup = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
        if (stateGroup == null) {
            throw new NotFoundException("State Group not available for point Id " + pointId);
        }
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
