package com.cannontech.dr.constraint.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.exception.LMObjectDeletionFailureException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintServiceImpl implements ProgramConstraintService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private LMServiceHelper lmServiceHelper;
    @Autowired private IDatabaseCache dbCache;
    @Autowired DBDeletionDao dbDeletionDao;
    private static final Logger log = YukonLogManager.getLogger(ProgramConstraintServiceImpl.class);

    @Override
    public ProgramConstraint retrieve(int constraintId) {
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
    public int create(ProgramConstraint programConstraint) {
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
        return constraint.getConstraintID();
    }

    @Override
    public int delete(int constraintId, String constraintName) {
        Optional<LiteLMConstraint> liteLMConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(constraint -> constraint.getConstraintID() == constraintId
                    && constraint.getConstraintName().equalsIgnoreCase(constraintName))
                .findFirst();
        if (liteLMConstraint.isEmpty()) {
            throw new NotFoundException("Constraint Id and Name combination not found");
        }
        Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
        checkIfConstriantIsUsed(liteLMConstraint.get(), paoId);
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint.get());
        dbPersistentDao.performDBChange(constraint, TransactionType.DELETE);
        return constraint.getConstraintID();
    }

    @Override
    public int update(int constraintId, ProgramConstraint programConstraint) {
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
        return lmprogramConstraint.getConstraintID();
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
    public int copy(int id, LMCopy lmCopy) {
        throw new UnsupportedOperationException("Not supported copy operation");
    }

    private void checkIfConstriantIsUsed(LiteLMConstraint liteLMConstraint, Integer paoId) {
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint);
        DBDeleteResult dbDeleteResult = dbDeletionDao.getDeleteInfo(constraint, constraint.getConstraintName());
        try {
            if (LMProgramConstraint.inUseByProgram(dbDeleteResult.getItemID(), CtiUtilities.getDatabaseAlias())) {
                if (dbDeleteResult.isDeletable()) {
                    String message = "You cannot delete the Program Constraint '" + constraint.getConstraintName()
                        + "' because it is used by a program.";
                    throw new LMObjectDeletionFailureException(message);
                }
            }
        } catch (SQLException e) {
            log.error("Unable to delete Constraint with name : " + constraint.getConstraintName() + e);
        }
    }
}
