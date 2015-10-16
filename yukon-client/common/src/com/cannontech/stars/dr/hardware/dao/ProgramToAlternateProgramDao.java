package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.ProgramToAlternateProgram;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface ProgramToAlternateProgramDao {

    /**
     * Will attempt to insert the mapping, if that fails will attempt to update it.
     * 
     * @param programToAlternateProgram
     */
    void save(ProgramToAlternateProgram programToAlternateProgram);

    /**
     * Deletes a program to seasonal program mapping
     * 
     * @param assignedProgramId
     */
    void delete(int assignedProgramId);

    /**
     * This method gets the program to seasonal program mappings for the supplied assignedProgramIds.
     */
    List<ProgramToAlternateProgram> getByAssignedProgramId(Iterable<Integer> assignedProgramIds);

    /**
     * This method gets the program to seasonal program mappings for the supplied programIds. Keep in mind
     * programIds will be matched with assignedProgramIds or seasonalProgramIds.
     */
    List<ProgramToAlternateProgram> getByEitherAssignedProgramIdOrSeasonalProgramId(Iterable<Integer> programIds);

    /**
     * Returns a list of all program to alternate program mappings
     */
    List<ProgramToAlternateProgram> getAll();

    /**
     * Returns a list of program ids for all programs that are being used as alternate programs.
     */
    List<Integer> getAllAlternateProgramIds();

    /**
     * Returns the number of devices that are currently in a seasonal opt out.
     */
    int getTotalNumberOfDevicesInSeasonalOptOuts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds);

    /**
     * Gets the Program id for which SeasonalProgramId is load program id
     * 
     * @param SeasonalProgramId : Program id which is getting deleted but it might be seasonal/alternate
     *        program for some other load program.
     * @return
     */
    int getAssignedProgramIdBySeasonalProgramId(int SeasonalProgramId);

}