package com.cannontech.stars.dr.appliance.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public interface AssignedProgramDao {
    public AssignedProgram getById(int assignedProgramId);
    public List<AssignedProgram> getByIds(Collection<Integer> assignedProgramIds);

    public int getHighestProgramOrderForApplianceCategory(
            int applianceCategoryId);
}
