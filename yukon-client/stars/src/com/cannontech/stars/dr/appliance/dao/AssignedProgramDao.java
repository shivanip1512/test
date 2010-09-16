package com.cannontech.stars.dr.appliance.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public interface AssignedProgramDao {
    public AssignedProgram getById(int assignedProgramId);

    public List<AssignedProgram> getByIds(Collection<Integer> assignedProgramIds);

    public Map<Integer, Integer> getProgramIdsByAssignedProgramIds(
            Iterable<Integer> assignedProgramIds);

    public int getHighestProgramOrderForApplianceCategory(
            int applianceCategoryId);
}
