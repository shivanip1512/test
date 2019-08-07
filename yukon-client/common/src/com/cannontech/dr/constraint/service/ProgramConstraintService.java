package com.cannontech.dr.constraint.service;

import java.util.List;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.dr.setup.service.LMSetupService;

public interface ProgramConstraintService extends LMSetupService <ProgramConstraint, LMCopy> {
    /**
     * Retrieve available season schedules.
     */
    List<LMDto> getSeasonSchedules();

    /**
     * Retrieve available holiday schedules.
     */
    List<LMDto> getHolidaySchedules();
    
    /**
     * Retrieve all program constraint.
     */
    
    List<LMDto> getAllProgramConstraint();
}
