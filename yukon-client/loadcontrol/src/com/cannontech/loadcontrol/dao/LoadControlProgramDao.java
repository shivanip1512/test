package com.cannontech.loadcontrol.dao;

import java.util.List;

import com.cannontech.loadcontrol.service.data.ProgramStartingGear;

public interface LoadControlProgramDao {

    /**
     * Return id of program for given programName. Throws IllegalArgumentException if no program
     * exists for given programName.
     * @param programName
     * @return
     * @throws IllegalArgumentException
     */
    public int getProgramIdByProgramName(String programName) throws IllegalArgumentException;
    
    /**
     * Return id of scenario for given scenarioName. Throws IllegalArgumentException if no scenario
     * exists for given scenarioName.
     * @param scenarioName
     * @return
     * @throws IllegalArgumentException
     */
    public int getScenarioIdForScenarioName(String scenarioName) throws IllegalArgumentException;
    
    /**
     * Returns list of a program ids.
     * @return
     */
    public List<Integer> getAllProgramIds();
    
    /**
     * Find all program id linked to given scenario. Returns empty list if no programs are linked to scenario.
     * @param scenarioName
     * @return
     */
    public List<Integer> getProgramIdsByScenarioId(int scenarioId);

    /**
     * Find all program id linked to given scenario. Returns empty list if no programs are linked to scenario.
     * @param scenarioName
     * @return
     * @throws IllegalArgumentException if no scenario exists for given scenarioName.
     */
    public List<Integer> getProgramIdsByScenarioName(String scenarioName) throws IllegalArgumentException;
    
    /**
     * Get starting gear number for given program and scenario id.
     * @param programId
     * @param scenarioId
     * @return
     */
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId);
    
    /**
     * Get a list of ProgramStartingGear for given scenario id.
     * Each ProgramStartingGear contains the program name, and the name and number of it's starting gear.
     * @param scenarioId
     * @return
     */
    public List<ProgramStartingGear> getProgramStartingGearsForScenarioId(int scenarioId);
}
