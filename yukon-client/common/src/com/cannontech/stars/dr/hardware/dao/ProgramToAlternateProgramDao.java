package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.ProgramToAlternateProgram;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;


public interface ProgramToAlternateProgramDao {

	/**
	 * Will attempt to insert the mapping, if that fails will attempt to update it.
	 * @param programToAlternateProgram
	 */
    public void save(ProgramToAlternateProgram programToAlternateProgram);
    
    /**
     * Deletes a program to seasonal program mapping
     * @param programToAlternateProgram
     */
    public void delete(ProgramToAlternateProgram programToAlternateProgram);
    
    /**
     * Deletes a program to seasonal program mapping
     * @param assignedProgramId
     */
    public void delete(int assignedProgramId);

    /**
     * This method gets the program to seasonal program mappings for the supplied assignedProgramId.
     */
    public List<ProgramToAlternateProgram> getByAssignedProgramId(int assignedProgramId);

    /**
     * This method gets the program to seasonal program mappings for the supplied assignedProgramIds.
     */
    public List<ProgramToAlternateProgram> getByAssignedProgramId(Iterable<Integer> assignedProgramIds);

    /**
     * This method gets the program to seasonal program mappings for the supplied programIds.  Keep in mind
     * programIds will be matched with assignedProgramIds or seasonalProgramIds.
     */
    public List<ProgramToAlternateProgram> getByEitherAssignedProgramIdOrSeasonalProgramId(Iterable<Integer> programIds);

    /**
     * Returns a list of all program to alternate program mappings
     */
    public List<ProgramToAlternateProgram> getAll();

    /**
     * Returns a list of program ids for all programs that are being used as alternate programs.
     */
    public List<Integer> getAllAlternateProgramIds();

	/**
	 * Returns the number of accounts that are currently in a seasonal opt out.
	 */
    public int getTotalNumberOfAccountsInSeasonalOptOuts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds);

}