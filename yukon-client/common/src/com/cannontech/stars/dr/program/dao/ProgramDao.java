package com.cannontech.stars.dr.program.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.model.Program;
import com.google.common.collect.Multimap;

public interface ProgramDao {

    public Program getByProgramId(int programId);

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
    
    /*
     *Finds the associated gear name of the gear id
     * @param programId The program Id of the object a gearName is being retrieved for
     * @param gearNumber The gear id of the object associated to the program, and carries the gear name
     * @return string label associate to the gear id record.
     * */
    public String findGearName(int programId, int gearNumber);
    /*
     *Finds the associated gearId of the gear gear number
     * @param programId The program Id of the object a gearName is being retrieved for
     * @param gearNumber The gearNumber of the object associated to the program, and carries the gear id
     * @return string label associate to the gear id record.
     * */
    public int getGearId( int programId, int gearNumber);

    /**
     * Returns the of controlAreaId to groupIds
     */
    Multimap<Integer, Integer> getGroupIdsByProgramIds(Set<Integer> programIds);
}
