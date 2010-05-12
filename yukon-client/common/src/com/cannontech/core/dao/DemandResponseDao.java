package com.cannontech.core.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.google.common.collect.SetMultimap;

public interface DemandResponseDao {


    /**
     * Method to get all 'parent' control areas and scenarios for a program
     * @param program - Program to get parents for
     * @return - List of parents
     */
    public List<YukonPao> getControlAreasAndScenariosForProgram(YukonPao program);

    /**
     * Method to get all 'parent' programs for a load group
     * @param group - Load group to get parents for
     * @return - List of parents
     */
    public List<YukonPao> getProgramsForGroup(YukonPao group);
    
    public SetMultimap<PaoIdentifier, PaoIdentifier> getProgramToGroupMappingForGroups(
                                                                       Collection<PaoIdentifier> groups);

    public SetMultimap<PaoIdentifier, PaoIdentifier> getControlAreaToProgramMappingForPrograms(
                                                                       Collection<PaoIdentifier> progarms);

    public SetMultimap<PaoIdentifier, PaoIdentifier> getScenarioToProgramMappingForPrograms(
                                                                       Collection<PaoIdentifier> progarms);
}
