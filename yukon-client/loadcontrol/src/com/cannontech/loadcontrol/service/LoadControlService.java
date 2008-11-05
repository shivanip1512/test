package com.cannontech.loadcontrol.service;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public interface LoadControlService {

    /**
     * Find program id by name in database them retreieve program from
     * connection cache. If the cache contains no program for id then return
     * null.
     * @param programName
     * @return A ProgramStatus object based on the Program
     * @throws NotFoundException if no program exists in database for given programName
     */
    public ProgramStatus getProgramStatusByProgramName(String programName) throws NotFoundException;

    /**
     * Returns a list of ProgramStatus where the program is active.
     * @return
     */
    public List<ProgramStatus> getAllCurrentlyActivePrograms();

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
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false. 
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control start
     */
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, int gearNumber, boolean forceStart, boolean observeConstraintsAndExecute)
            throws NotFoundException, TimeoutException;
    
    /**
     * Starts control of program of given programName. Returns a ProgramStatus
     * object containing the updated program status info if successful, if
     * program has constraint violations will contain current program status
     * info and the list of violations in the ProgramStatus.
     * Does not take a gearNumber parameter, uses program's current gear always.
     * @param programName
     * @param startTime
     * @param stopTime
     * @param gearNumber
     * @param forceStart should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false. 
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control start
     */
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute)
            throws NotFoundException, TimeoutException;

    /**
     * Stops control of program of given programName. Returns a ProgramStatus
     * object containing the updated program status info if successful, if
     * program has constraint violations will contain current program status
     * info and the list of violations in the ProgramStatus.
     * @param programName
     * @param stopTime
     * @param gearNumber
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control stop
     */
    public ProgramStatus stopControlByProgramName(String programName,
            Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute)
            throws NotFoundException, TimeoutException;
    
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
     * @param forceStart Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control start attempted.
     */
    public ScenarioStatus startControlByScenarioName(String scenarioName,
            Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute)
            throws NotFoundException, TimeoutException;

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
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore contraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control stop attempted.
     */
    public ScenarioStatus stopControlByScenarioName(String scenarioName,
            Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException;

    
    /**
     * Returns ScenarioProgramStartingGears for the given scenarioName.
     * The ScenarioProgramStartingGears object contains the name of the scenario, and a list of 
     * ProgramStartingGear. Each ProgramStartingGear contains the name of the program, and the name
     * and id of it's starting gear.
     * @param scenarioName
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     */
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName) throws NotFoundException;
    
    /**
     * Returns a list of ProgramControlHistory objects for the given programName, within the given
     * time period.
     * @param programName
     * @param fromTime inclusive
     * @param throughTime inclusive
     * @return
     * @throws NotFoundException if no program exists for given programName
     */
    public List<ProgramControlHistory> getControlHistoryByProgramName(String programName, Date fromTime, Date throughTime) throws NotFoundException;
}
