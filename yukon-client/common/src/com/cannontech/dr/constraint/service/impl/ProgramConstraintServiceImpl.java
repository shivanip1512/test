package com.cannontech.dr.constraint.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintServiceImpl implements ProgramConstraintService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private LMServiceHelper lmServiceHelper;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public ProgramConstraint retrieve(Integer constraintId) {
        Optional<LiteLMConstraint> lmConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(liteLMConstraint -> liteLMConstraint.getConstraintID() == constraintId.intValue())
                .findFirst();
        if (lmConstraint.isEmpty()) {
            throw new NotFoundException("Constarint Id not found");
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
    public Integer create(ProgramConstraint programConstraint) {
        Optional<LMDto> holidaySchedule = lmServiceHelper.getHolidaySchedule(programConstraint.getHolidaySchedule().getId());
        if (holidaySchedule.isEmpty()) {
            throw new NotFoundException("Invalid Holiday Schedule ID");
        }
        Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
        if (seasonSchedule.isEmpty()) {
            throw new NotFoundException("Invalid Season Schedule ID");
        }

        LMProgramConstraint constraint = new LMProgramConstraint();
        programConstraint.buildDBPersistent(constraint);
        if (constraint.getConstraintID() == null) {
            dbPersistentDao.performDBChange(constraint, TransactionType.INSERT);
        }
        return constraint.getConstraintID();
    }

    @Override
    public Integer delete(Integer constraintId, String constraintName) {
        Optional<LiteLMConstraint> liteLMConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(constraint -> constraint.getConstraintID() == constraintId.intValue()
                    && constraint.getConstraintName().equals(constraintName))
                .findFirst();
        if (liteLMConstraint.isEmpty()) {
            throw new NotFoundException("Constarint Id and Name combination not found");
        }
        
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint.get());
        dbPersistentDao.performDBChange(constraint, TransactionType.DELETE);
        return constraint.getConstraintID();
    }

    @Override
    public Integer update(Integer constraintId, ProgramConstraint programConstraint) {
        Optional<LiteLMConstraint> lmConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(liteLMConstraint -> liteLMConstraint.getConstraintID() == constraintId.intValue())
                .findFirst();
        if (lmConstraint.isEmpty()) {
            throw new NotFoundException("Constarint Id not found");
        }
        Optional<LMDto> holidaySchedule = lmServiceHelper.getHolidaySchedule(programConstraint.getHolidaySchedule().getId());
        if (holidaySchedule.isEmpty()) {
            throw new NotFoundException("Invalid Holiday Schedule ID");
        }
        Optional<LMDto> seasonSchedule = lmServiceHelper.getSeasonSchedule(programConstraint.getSeasonSchedule().getId());
        if (seasonSchedule.isEmpty()) {
            throw new NotFoundException("Invalid Season Schedule ID");
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
    public List<LMDto> getHoloidaySchedules() {
        return dbCache.getAllHolidaySchedules().stream()
                .map(liteBase -> new LMDto(liteBase.getLiteID(), liteBase.toString()))
                .collect(Collectors.toList());
    }
}
