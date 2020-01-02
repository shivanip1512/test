package com.cannontech.loadcontrol.dao;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory.GearAction;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;

public interface LoadControlProgramDao {

    /**
     * Return id of program for given programName. Throws IllegalArgumentException if no program
     * exists for given programName.
     * @param programName
     * @return
     * @throws NotFoundException if no program exists for given programName.
     */
    public int getProgramIdByProgramName(String programName) throws NotFoundException;

    /**
     * Return id of scenario for given scenarioName. Throws IllegalArgumentException if no scenario
     * exists for given scenarioName.
     * @param scenarioName
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     */
    public int getScenarioIdForScenarioName(String scenarioName) throws NotFoundException;

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

    /**
     * Get a list of ProgramControlHistory.
     * Each ProgramControlHistory contains information about the program start/stop.
     * @param startDateTime required
     * @param stopDateTime optional (use null for "now")
     * @return
     */
    public List<ProgramControlHistory> getAllProgramControlHistory(Date startDateTime, Date stopDateTime);

    /**
     * Get a list of ProgramControlHistory for a given program id.
     * Each ProgramControlHistory contains information about the program start/stop.
     * @param programId optional (use null for all programs) Should use {@link#getAllProgramControlHistory(Date startDateTime, Date stopDateTime)} in this case though.
     * @param startDateTime required
     * @param stopDateTime optional (use null for "now")
     * @return
     */
    public List<ProgramControlHistory> getProgramControlHistoryByProgramId(int programId, Date startDateTime, Date stopDateTime);

    /**
     * If the given program was being controlled at the given time, return the
     * ProgramControlHistory event representing that control. If the program was
     * not being controlled, null will be returned.
     */
    public ProgramControlHistory findHistoryForProgramAtTime(int programId, ReadableInstant when);

    /**
     * Get the gear number for a given program and gear name.
     * @param gearName
     * @return
     * @throws NotFoundException
     */
    public int getGearNumberForGearName(int programId, String gearName) throws NotFoundException;
    
    /**
     * Get a list of LmProgramGearHistory based on Date Range.
     */

    public List<LmProgramGearHistory> getProgramHistoryDetails(DateTime from, DateTime to);

    /**
     * Get LmProgramGearHistory based on programHistoryId and gearAction.
     */

    public LmProgramGearHistory getProgramHistoryDetail(Integer programHistoryId, GearAction action);

}
