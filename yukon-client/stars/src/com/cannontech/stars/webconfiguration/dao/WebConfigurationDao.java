package com.cannontech.stars.webconfiguration.dao;

import java.util.Map;

import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public interface WebConfigurationDao {
    public WebConfiguration getById(int webConfigurationId);

    public WebConfiguration getForApplianceCateogry(int applianceCategoryId);

    public WebConfiguration getForAssignedProgram(int assignedProgramId);

    public Map<Integer, WebConfiguration> getForAssignedPrograms(Iterable<Integer> assignedProgramIds);

    public Map<Integer, WebConfiguration> getForProgramsForApplianceCateogry(
            int applianceCategoryId);
}
