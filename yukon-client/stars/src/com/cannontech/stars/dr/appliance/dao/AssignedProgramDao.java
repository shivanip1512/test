package com.cannontech.stars.dr.appliance.dao;

import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public interface AssignedProgramDao {
    public AssignedProgram getById(int assignedProgramId);

    public int getHighestProgramOrderForApplianceCategory(
            int applianceCategoryId);
}
