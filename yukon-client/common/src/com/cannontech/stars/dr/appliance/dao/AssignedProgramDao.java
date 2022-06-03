package com.cannontech.stars.dr.appliance.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public interface AssignedProgramDao {
    
    AssignedProgram getById(int assignedProgramId);
    
    List<AssignedProgram> getByIds(Collection<Integer> assignedProgramIds);
    
    /**
     * Get a map of assigned (STARS) program ids to LM program ids for the given
     * list of assigned program ids.
     */
    Map<Integer, Integer> getProgramIdsByAssignedProgramIds(Iterable<Integer> assignedProgramIds);
    
    int getHighestProgramOrder(int applianceCategoryId);

    AssignedProgram getByDeviceId(int deviceId);
    
}