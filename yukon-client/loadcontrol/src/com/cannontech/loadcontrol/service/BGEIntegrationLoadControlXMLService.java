package com.cannontech.loadcontrol.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.jdom.JDOMException;

import com.cannontech.loadcontrol.dao.LMProgramGearHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public interface BGEIntegrationLoadControlXMLService {

    /**
     * Find program id by name in database them retreieve program from
     * connection cache. If the cache contains no program for id then return
     * null.
     * @param programName
     * @return A ProgramStatus object based on the Program
     * @throws IllegalArgumentException if no program exists in database for given programName
     */
    public ProgramStatus getProgramStatusByProgramName(String xml) throws IOException, JDOMException, IllegalArgumentException;

    /**
     * Returns a list of ProgramStatus where the program is active.
     * @return
     */
    public List<ProgramStatus> getAllCurrentlyActivePrograms(String xml) throws IOException, IllegalArgumentException, JDOMException;

    /**
     * Starts control of program of given programName. Returns a ProgramStatus
     * object containing the updated program status info if successful, if
     * program has constraint violations will contain current program status
     * info and the list of violations in the ProgramStatus.
     * @param programName
     * @param startTime
     * @param stopTime
     * @param gearNumber
     * @param forceStart should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @return
     * @throws IllegalArgumentException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control start
     */
    public ProgramStatus startControlByProgramName(String xml)
            throws IOException, JDOMException, ParseException, IllegalArgumentException, TimeoutException;

    /**
     * Stops control of program of given programName. Returns a ProgramStatus
     * object containing the updated program status info if successful, if
     * program has constraint violations will contain current program status
     * info and the list of violations in the ProgramStatus.
     * @param programName
     * @param stopTime
     * @param gearNumber
     * @param forceStop should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @return
     * @throws IllegalArgumentException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control stop
     */
    public ProgramStatus stopControlByProgramName(String xml)
            throws IOException, JDOMException, ParseException, IllegalArgumentException, TimeoutException;
    
    /**
     * Starts control for all programs belonging to a given scenario. Returns a
     * ScenarioStatus object which contains the scenarioName, and a list of
     * ProgramStatus for each program that control start was attempted. Each
     * ProgramStatus will contain the updated program status info if successful,
     * those programs that have constraint violations will contain current
     * program status info and the list of violations in their ProgramStatus.
     * @param scenarioName
     * @param startTime
     * @param stopTime
     * @param forceStart should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @return
     * @throws IllegalArgumentException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control start attempted.
     */
    public ScenarioStatus startControlByScenarioName(String xml)
            throws IOException, JDOMException, ParseException, IllegalArgumentException, TimeoutException;

    /**
     * Stop control for all programs belonging to a given scenario. Returns a
     * ScenarioStatus object which contains the scenarioName, and a list of
     * ProgramStatus for each program that control stop was attempted. Each
     * program status will contain the updated program status info if
     * successful, those programs that have constraint violations will contain
     * current program status info and the list of violations in their
     * ProgramStatus.
     * @param scenarioName
     * @param stopTime
     * @param forceStop should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @return
     * @throws IllegalArgumentException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control stop attempted.
     */
    public ScenarioStatus stopControlByScenarioName(String xml) throws IOException, JDOMException, ParseException, IllegalArgumentException,
            TimeoutException;

    
    /**
     * Returns ScenarioProgramStartingGears for the given scenarioName.
     * The ScenarioProgramStartingGears object contains the name of the scenario, and a list of 
     * ProgramStartingGear. Each ProgramStartingGear contains the name of the program, and the name
     * and id of it's starting gear.
     * @param scenarioName
     * @return
     * @throws IllegalArgumentException if no scenario exists for given scenarioName.
     */
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String xml) throws IOException, JDOMException, ParseException, IllegalArgumentException;
    
    /**
     * Returns a list of LMProgramGearHistory objects for the given programName, within the given
     * time period.
     * @param programName
     * @param fromTime inclusive
     * @param throughTime inclusive
     * @return
     * @throws IllegalArgumentException if no program exists for given programName
     */
    public List<LMProgramGearHistory> getControlHistoryByProgramName(String xml) throws IOException, JDOMException, ParseException, IllegalArgumentException;
}
