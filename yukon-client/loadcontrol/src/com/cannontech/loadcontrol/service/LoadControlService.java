package com.cannontech.loadcontrol.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public interface LoadControlService {

    /**
     * Find program id by name in database them retrieve program from
     * connection cache. If the cache contains no program for id then return
     * null.
     * @param programName
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * @return A ProgramStatus object based on the Program
     * @throws NotFoundException if no program exists in database for given programName
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     */
    public ProgramStatus getProgramStatusByProgramName(String programName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException;

    /**
     * Returns a list of ProgramStatus where the program is active, and the program is visible to the user
     * either because it is directly visible, or belongs to a control area that is visible.
     * @return
     */
    public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user);

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
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false. 
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control start
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     * to them
     */
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, int gearNumber, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user)
            throws NotFoundException, TimeoutException, NotAuthorizedException;
    
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
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false. 
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control start
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     */
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user)
            throws NotFoundException, TimeoutException, NotAuthorizedException;

    /**
     * Stops control of program of given programName. Returns a ProgramStatus
     * object containing the updated program status info if successful, if
     * program has constraint violations will contain current program status
     * info and the list of violations in the ProgramStatus.
     * @param programName
     * @param stopTime
     * @param gearNumber
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * Note: This value only matters when using forceStop=false.
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws TimeoutException if server fails to send an update response for the control stop
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     */
    public ProgramStatus stopControlByProgramName(String programName,
            Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user)
            throws NotFoundException, TimeoutException, NotAuthorizedException;
    
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
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control start attempted.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public ScenarioStatus startControlByScenarioName(String scenarioName,
            Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user)
            throws NotFoundException, TimeoutException, NotAuthorizedException;

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
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws TimeoutException if server fails to send an program update response for any of the program control stop attempted.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public ScenarioStatus stopControlByScenarioName(String scenarioName,
            Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException;

    
    /**
     * Returns ScenarioProgramStartingGears for the given scenarioName.
     * The ScenarioProgramStartingGears object contains the name of the scenario, and a list of 
     * ProgramStartingGear. Each ProgramStartingGear contains the name of the program, and the name
     * and id of it's starting gear.
     * @param scenarioName
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException;
    
    /**
     * Returns a list of ProgramControlHistory objects for the given programName, within the given
     * time period.
     * @param programName
     * @param fromTime inclusive
     * @param throughTime inclusive
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws NotAuthorizedException for any of of the programs in the scenario - if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     */
    public List<ProgramControlHistory> getControlHistoryByProgramName(
			String programName, Date fromTime, Date throughTime,
			LiteYukonUser user) throws NotFoundException, NotAuthorizedException;
}
