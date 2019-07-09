package com.cannontech.dr.constraint.service;

import java.util.List;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;

public interface ProgramConstraintService {
    /**
     * Retrieve Program Constraint for the specified constraintId.
     */
    ProgramConstraint retrieve(Integer constraintId);

    /**
     * Creates the Program Constraint.
     */
    Integer create(ProgramConstraint programConstraint);

    /**
     * Delete the Program Constraint.
     */
    Integer delete(Integer constraintId, String constraintName);

    /**
     * Update the Program Constraint.
     */
    Integer update(Integer constraintId, ProgramConstraint programConstraint);

    /**
     * Retrieve available season schedules.
     */
    List<LMDto> getSeasonSchedules();

    /**
     * Retrieve available holiday schedules.
     */
    List<LMDto> getHolidaySchedules();
}
