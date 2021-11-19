package com.cannontech.dr.area.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProgramAssignment;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.DeletionFailureException;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.dr.area.service.ControlAreaSetupService;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;


public class ControlAreaSetupServiceImpl implements ControlAreaSetupService {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DemandResponseEventLogService logService;
    @Autowired private LMServiceHelper lmServiceHelper;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private ControlAreaDao controlAreaDao;

    /**
     * Retrieve control area based on control area Id.
     */
    @Override
    public ControlArea retrieve(int areaId, LiteYukonUser liteYukonUser) {
        LiteYukonPAObject liteControlArea = dbCache.getAllLMControlAreas()
                                                                 .stream()
                                                                 .filter(controlArea -> controlArea.getLiteID() == areaId)
                                                                 .findFirst()
                                                                 .orElseThrow(() -> new NotFoundException("Control area Id not found " + areaId));

        LMControlArea lmControlArea = (LMControlArea) dbPersistentDao.retrieveDBPersistent(liteControlArea);
        ControlArea controlArea = new ControlArea();
        controlArea.buildModel(lmControlArea);
        setProgramAssignmentName(lmControlArea, controlArea);
        List<ControlAreaProgramAssignment> assignedProgramList = controlArea.getProgramAssignment();
        if (assignedProgramList != null) {
            Comparator<ControlAreaProgramAssignment> comparator = controlArea.getStartPriorityComparator();
            Collections.sort(assignedProgramList, comparator);
            controlArea.setProgramAssignment(assignedProgramList);
        }
        return controlArea;
    }

    @Override
    @Transactional
    public ControlArea create(ControlArea controlArea, LiteYukonUser liteYukonUser) {
        LMControlArea lmControlArea = getDBPersistent(controlArea.getControlAreaId());
        controlArea.buildDBPersistent(lmControlArea);

        dbPersistentDao.performDBChange(lmControlArea, TransactionType.INSERT);

        SimpleDevice device = SimpleDevice.of(lmControlArea.getPAObjectID(), lmControlArea.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);

        String startTime = controlArea.getDailyStartTimeInMinutes() != null ? TimeUtil
                .fromMinutesToHHmm(controlArea.getDailyStartTimeInMinutes()) : null;
        String stopTime = controlArea.getDailyStopTimeInMinutes() != null ? TimeUtil
                .fromMinutesToHHmm(controlArea.getDailyStopTimeInMinutes()) : null;

        logService.controlAreaCreated(controlArea.getName(), getTriggerNamesString(lmControlArea.getLmControlAreaTriggerVector()),
                getProgramNamesString(lmControlArea.getLmControlAreaProgramVector()), startTime, stopTime, liteYukonUser);

        controlArea.buildModel(lmControlArea);
        setProgramAssignmentName(lmControlArea, controlArea);
 
       return controlArea;
    }

    @Override
    @Transactional
    public ControlArea update(int controlAreaId, ControlArea controlArea, LiteYukonUser liteYukonUser) {
        dbCache.getAllLMControlAreas().stream()
                                      .filter(controlarea -> controlarea.getLiteID() == controlAreaId)
                                      .findFirst().orElseThrow(() -> new NotFoundException(" Control Area Id not found  " + controlAreaId ));

        LMControlArea lmControlArea = getDBPersistent(controlAreaId);

        // Checks if any assigned load program(s) which is removed while updating control area is associated with any control
        // scenario(s) or not.
        validateUpdate(controlArea.getProgramAssignment(), lmControlArea.getLmControlAreaProgramVector());

        controlArea.buildDBPersistent(lmControlArea);
        lmControlArea.setPAObjectID(controlAreaId);
        dbPersistentDao.performDBChange(lmControlArea, TransactionType.UPDATE);

        String startTime = controlArea.getDailyStartTimeInMinutes() != null ? TimeUtil
                .fromMinutesToHHmm(controlArea.getDailyStartTimeInMinutes()) : null;
        String stopTime = controlArea.getDailyStopTimeInMinutes() != null ? TimeUtil
                .fromMinutesToHHmm(controlArea.getDailyStopTimeInMinutes()) : null;

          logService.controlAreaUpdated(lmControlArea.getPAOName(), getTriggerNamesString(lmControlArea.getLmControlAreaTriggerVector()),
          getProgramNamesString(lmControlArea.getLmControlAreaProgramVector()), startTime, stopTime, liteYukonUser);

        controlArea.buildModel(lmControlArea);
        setProgramAssignmentName(lmControlArea, controlArea);
        return controlArea;
    }

    /**
     * Return a string with comma separated trigger names for the provided LMControlAreaTrigger List.
     */
    private String getTriggerNamesString(Vector<LMControlAreaTrigger> triggerList) {
        if (CollectionUtils.isNotEmpty(triggerList)) {
            List<String> triggerNames = new ArrayList<String>();
            triggerList.forEach(trigger -> {
                triggerNames.add(trigger.toString());
            });
            return String.join(", ", triggerNames);
        }
        return null;
    }

    /**
     * Return a string with comma separated program names for the provided LMControlAreaProgram List
     */
    private String getProgramNamesString(Vector<LMControlAreaProgram> programList) {
        if (CollectionUtils.isNotEmpty(programList)) {
            List<Integer> programIds = programList.stream()
                                                  .map(program -> program.getLmProgramDeviceID())
                                                  .collect(Collectors.toList());
            return lmServiceHelper.getAbbreviatedPaoNames(programIds);
        }
        return null;
    }

    @Override
    @Transactional
    public int delete(int areaId, LiteYukonUser liteYukonUser) {
        LiteYukonPAObject controlArea = dbCache.getAllLMControlAreas()
                                                         .stream()
                                                         .filter(area -> area.getLiteID() == areaId)
                                                         .findFirst()
                                                         .orElseThrow(() -> new NotFoundException("Control Area Id not found"));

        // Checks if any assigned load program(s) is associated with any control scenario(s) 
        validateDelete(areaId);

        YukonPAObject lmControlArea = (YukonPAObject) LiteFactory.createDBPersistent(controlArea);
        dbPersistentDao.performDBChange(lmControlArea, TransactionType.DELETE);

        logService.controlAreaDeleted(lmControlArea.getPAOName(), liteYukonUser);
        return lmControlArea.getPAObjectID();
    }

    /**
     * Set names of assigned Programs for Control Area..
     */
    private void setProgramAssignmentName(LMControlArea lmControlArea, ControlArea controlArea) {
        lmControlArea.getLmControlAreaProgramVector().forEach(lmprogram -> {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(lmprogram.getLmProgramDeviceID());
            controlArea.getProgramAssignment()
                    .forEach(program -> {
                        if (program.getProgramId().equals(lmprogram.getLmProgramDeviceID())) {
                            program.setProgramName(pao.getPaoName());
                        }
                    });
        });
    }

    private LMControlArea getDBPersistent(Integer paoId) {
        LMControlArea lmControlArea = new LMControlArea();
        if (paoId != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
            lmControlArea = (LMControlArea) dbPersistentDao.retrieveDBPersistent(pao);
        }
        return lmControlArea;
    }

    @Override
    public ControlArea copy(int id, LMCopy lmCopy, LiteYukonUser liteYukonUser) {
        throw new UnsupportedOperationException("Not supported copy operation");
    }

    /**
     * Validate deletion for Control Area.
     */
    private void validateDelete(int controlAreaId) {
        Set<Integer> programIds = controlAreaDao.getProgramIdsForControlArea(controlAreaId);
        checkProgramAssignment(programIds);
    }

    /**
     * Validate Control Area update.
     */
    private void validateUpdate(List<ControlAreaProgramAssignment> newPrograms, Vector<LMControlAreaProgram> oldPrograms) {
        if (CollectionUtils.isEmpty(oldPrograms)) {
            return;
        }
        List<Integer> oldProgramIds = oldPrograms.stream()
                                                 .map(lp -> lp.getLmProgramDeviceID())
                                                 .sorted()
                                                 .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(newPrograms)) {
            List<Integer> newProgramIds = newPrograms.stream()
                                                     .map(lp -> lp.getProgramId())
                                                     .sorted()
                                                     .collect(Collectors.toList());

            oldProgramIds.removeAll(newProgramIds);
        }
        checkProgramAssignment(oldProgramIds.stream().collect(Collectors.toSet()));
    }

    /**
     * Checks that in control area is there any assigned load program(s) is associated with any control scenario(s).
     * @throws DeletionFailureException if any assigned program is associated to any scenario.
     */
    private void checkProgramAssignment(Set<Integer> programIds) {
        for (Integer programId : programIds) {
            boolean isAssignedProgramsAssociatedWithScenario = dbCache.getAllLMScenarioProgs().stream()
                                                                                              .anyMatch(scenarioProg -> scenarioProg.getProgramID() == programId);

            if (isAssignedProgramsAssociatedWithScenario) {
                throw new DeletionFailureException(
                        "A program on this control area is assigned to a scenario. Program must be removed from scenario before it can be removed from a control area.");
            }
        }
    }
}
