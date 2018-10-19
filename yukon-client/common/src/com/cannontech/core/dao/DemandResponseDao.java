package com.cannontech.core.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public interface DemandResponseDao {
    /**
     * Method to get all 'parent' control areas and scenarios for a program
     * @param program - Program to get parents for
     * @return - List of parents
     */
    List<PaoIdentifier> getControlAreasAndScenariosForProgram(YukonPao program);

    /**
     * Method to get all 'parent' programs for a load group
     * @param group - Load group to get parents for
     * @return - List of parents
     */
    List<PaoIdentifier> getProgramsForGroup(YukonPao group);

    SetMultimap<PaoIdentifier, PaoIdentifier> getProgramToGroupMappingForGroups(Collection<PaoIdentifier> groups);

    SetMultimap<PaoIdentifier, PaoIdentifier> getControlAreaToProgramMappingForPrograms(
        Collection<PaoIdentifier> progarms);

    SetMultimap<PaoIdentifier, PaoIdentifier>
        getScenarioToProgramMappingForPrograms(Collection<PaoIdentifier> progarms);

    /**
     * Returns a multimap of groups to programs
     */
    Multimap<PaoIdentifier, PaoIdentifier> getGroupsToPrograms(List<LiteYukonPAObject> groups);

    /**
    * Returns a multimap of programs to areas
    */
    Multimap<PaoIdentifier, PaoIdentifier> getProgramsToAreas(Collection<PaoIdentifier> programs);
}
