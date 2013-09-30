package com.cannontech.stars.dr.program.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.model.Program;

public interface ProgramDao {

    public Program getByProgramId(int programId);

    public List<Program> getByApplianceCategory(int applianceCategoryId);
    public Map<ApplianceCategory, List<Program>> getByApplianceCategories(List<ApplianceCategory> applianceCategories);

    public List<Program> getByAppliances(List<Appliance> applianceList); 
    
    public List<Program> getByAssignedProgramIds(Iterable<Integer> assignedProgramIdList);
    
    public List<Program> getByProgramIds(Iterable<Integer> programIdList);
    
    /**
     * Gets a program by its by its program name.
     *
     * Throws an exception if the program isn't found
     * Throws an exception if more than one program is found
     */
    public Program getByProgramName(String programName, int energyCompanyId);
    
    /**
     * Gets a program by its by its alternate program name.
     *
     * Throws an exception if the program isn't found
     * Throws an exception if more than one program is found
     */
    public Program getByAlternateProgramName(String alternateProgramName, int energyCompanyId);
    
    public List<Integer> getDistinctGroupIdsByYukonProgramIds(final Set<Integer> programIds);

    public List<Integer> getDistinctGroupIdsByProgramIds(final Set<Integer> programIds);
    
    /**
     * Gets all the programIds for the given groupId without getting any duplicates.
     * 
     * @param groupIds
     * @return
     */
    public List<Integer> getDistinctProgramIdsByGroupIds(final Set<Integer> groupIds);
    
    public List<Integer> getGroupIdsByProgramId(int programId);
    
    /**
     * This method returns a string representation of all the programs that are attached to a 
     * given load group.  This string represented list is delimited by commas. 
     * 
     */
    public String getProgramNames(int loadGroupId);

    /**
     * Returns the first load group found assigned to this program.
     */
    public int getLoadGroupIdForProgramId(int programId);

    public DisplayablePao getProgram(int programId);
    public List<PaoIdentifier> getAllProgramPaoIdentifiers();
}
