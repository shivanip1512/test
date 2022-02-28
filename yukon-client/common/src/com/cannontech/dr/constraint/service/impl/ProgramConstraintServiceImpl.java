package com.cannontech.dr.constraint.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.DeletionFailureException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintServiceImpl implements ProgramConstraintService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private LMServiceHelper lmServiceHelper;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
    @Autowired DBDeletionDao dbDeletionDao;

    @Override
    public ProgramConstraint retrieve(int constraintId, LiteYukonUser liteYukonUser) {
        Optional<LiteLMConstraint> lmConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(liteLMConstraint -> liteLMConstraint.getConstraintID() == constraintId)
                .findFirst();
        if (lmConstraint.isEmpty()) {
            throw new NotFoundException("Constraint Id not found");
        }

        LMProgramConstraint constraint = (LMProgramConstraint) dbPersistentDao.retrieveDBPersistent(lmConstraint.get());
        ProgramConstraint programConstraint = new ProgramConstraint();
        programConstraint.buildModel(constraint);
        Optional<LMDto> holidaySchedule = lmServiceHelper.getHolidaySchedule(programConstraint.getHolidaySchedule().getId());
        if (holidaySchedule.isPresent()) {
            programConstraint.getHolidaySchedule().setName(holidaySchedule.get().getName());
        }
        Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
        if (seasonSchedule.isPresent()) {
            programConstraint.getSeasonSchedule().setName(seasonSchedule.get().getName());
        }
        return programConstraint;
    }

    @Override
    public ProgramConstraint create(ProgramConstraint programConstraint, LiteYukonUser liteYukonUser) {
        Optional<LMDto> holidaySchedule = lmServiceHelper.getHolidaySchedule(programConstraint.getHolidaySchedule().getId());
        if (holidaySchedule.isEmpty()) {
            throw new NotFoundException("Holiday Schedule Id not found");
        }
        if (programConstraint.getSeasonSchedule().getId() != 0) {
            Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
            if (seasonSchedule.isEmpty()) {
                throw new NotFoundException("Season Schedule Id not found");
            }
        }

        LMProgramConstraint constraint = new LMProgramConstraint();
        programConstraint.buildDBPersistent(constraint);
        if (constraint.getConstraintID() == null) {
            dbPersistentDao.performDBChange(constraint, TransactionType.INSERT);
        }
        programConstraint.buildModel(constraint);
        if (holidaySchedule.isPresent()) {
            programConstraint.getHolidaySchedule().setName(holidaySchedule.get().getName());
        }
        Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
        if (seasonSchedule.isPresent()) {
            programConstraint.getSeasonSchedule().setName(seasonSchedule.get().getName());
        }
        
        demandResponseEventLogService.programConstraintCreated(constraint.getConstraintName(), liteYukonUser);

        return programConstraint;
    }

    @Override
    public int delete(int constraintId, LiteYukonUser liteYukonUser) {
        Optional<LiteLMConstraint> liteLMConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(constraint -> constraint.getConstraintID() == constraintId)
                .findFirst();
        if (liteLMConstraint.isEmpty()) {
            throw new NotFoundException("Constraint Id not found");
        }
        Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
        checkIfConstriantIsUsed(liteLMConstraint.get(), paoId);
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint.get());
        dbPersistentDao.performDBChange(constraint, TransactionType.DELETE);

        demandResponseEventLogService.programConstraintDeleted(constraint.getConstraintName(), liteYukonUser);

        return constraint.getConstraintID();
    }

    @Override
    public ProgramConstraint update(int constraintId, ProgramConstraint programConstraint, LiteYukonUser liteYukonUser) {
        Optional<LiteLMConstraint> lmConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(liteLMConstraint -> liteLMConstraint.getConstraintID() == constraintId)
                .findFirst();
        if (lmConstraint.isEmpty()) {
            throw new NotFoundException("Constraint Id not found");
        }
        Optional<LMDto> holidaySchedule = lmServiceHelper.getHolidaySchedule(programConstraint.getHolidaySchedule().getId());
        if (holidaySchedule.isEmpty()) {
            throw new NotFoundException("Holiday Schedule Id not found");
        }
        if (programConstraint.getSeasonSchedule().getId() != 0) {
            Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
            if (seasonSchedule.isEmpty()) {
                throw new NotFoundException("Season Schedule Id not found");
            }
        }
        
        programConstraint.setId(constraintId);
        LMProgramConstraint lmprogramConstraint = new LMProgramConstraint();
        programConstraint.buildDBPersistent(lmprogramConstraint);
        dbPersistentDao.performDBChange(lmprogramConstraint, TransactionType.UPDATE);
        programConstraint.buildModel(lmprogramConstraint);
        if (holidaySchedule.isPresent()) {
            programConstraint.getHolidaySchedule().setName(holidaySchedule.get().getName());
        }
        Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
        if (seasonSchedule.isPresent()) {
            programConstraint.getSeasonSchedule().setName(seasonSchedule.get().getName());
        }

        demandResponseEventLogService.programConstraintUpdated(lmprogramConstraint.getConstraintName(), liteYukonUser);

        return programConstraint;
    }

    @Override
    public List<LMDto> getSeasonSchedules() {
        return dbCache.getAllSeasonSchedules().stream()
                .map(liteBase -> new LMDto(liteBase.getLiteID(), liteBase.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LMDto> getHolidaySchedules() {
        return dbCache.getAllHolidaySchedules().stream()
                .map(liteBase -> new LMDto(liteBase.getLiteID(), liteBase.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public ProgramConstraint copy(int id, LMCopy lmCopy, LiteYukonUser liteYukonUser) {
        throw new UnsupportedOperationException("Not supported copy operation");
    }

    /**
     * Checks that if program constraint provided is associated with any load program or not
     */
    private void checkIfConstriantIsUsed(LiteLMConstraint liteLMConstraint, Integer paoId) {
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint);
        DBDeleteResult dbDeleteResult = dbDeletionDao.getDeleteInfo(constraint, constraint.getConstraintName());
        if (LMProgramConstraint.inUseByProgram(dbDeleteResult.getItemID(), CtiUtilities.getDatabaseAlias())) {
            if (dbDeleteResult.isDeletable()) {
                String message = "You cannot delete the Program Constraint '" + constraint.getConstraintName()
                    + "' because it is used by a program.";
                throw new DeletionFailureException(message);
            }
        }
    }

    @Override
    public List<LMDto> getAllProgramConstraint() {

        return dbCache.getAllLMProgramConstraints().stream()
                                                   .map(liteBase -> new LMDto(liteBase.getLiteID(), liteBase.toString()))
                                                   .collect(Collectors.toList());
    }
}
