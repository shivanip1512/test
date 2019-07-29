package com.cannontech.dr.program.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.user.YukonUserContext;

public interface ProgramService {

    public DisplayablePao getProgram(int programId);
    
    public LMProgramBase getProgramForPao(YukonPao from);
    
    /**
     * Finds the LMProgramBase object from a Pao. Will throw if the client
     * connection is not valid or if there is no program found for the given pao.
     */
    public LMProgramBase getProgramForPaoSafe(YukonPao from);
    
    public DatedObject<LMProgramBase> findDatedProgram(int programId);

    public List<DisplayablePao> findProgramsForLoadGroup(int loadGroupId, 
                                                         YukonUserContext userContext);

    public SearchResults<DisplayablePao> filterPrograms(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count, 
                                                       YukonUserContext userContext);

    public List<GearAdjustment> getDefaultAdjustments(Date startDate,
            Date stopDate, Collection<ScenarioProgram> scenarioPrograms,
            YukonUserContext userContext);

    /**
     * 
     * Check to see if any constraints would be violated by starting the program
     * specified by programId using the specified gear and start/end time.
     * 
     * Null safe on gearAdjustments
     * 
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @return Returns null if no constraints will be violated; otherwise
     *         returns the violated constraints.
     */
    public ConstraintViolations getConstraintViolationForStartProgram(
            int programId, int gearNumber, Date startDate,
            Duration startOffset, Date stopDate, Duration stopOffset,
            List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource);
    
    /**
     * Check to see if any constraints would be violated by starting the scenario
     * specified by scenarioId using the specified start/end time.
     * @param scenarioId the paoId of the scenario.
     * @param startDate - the time chosen to start the scenario
     * @param stopDate - the time chosen to stop the scenario
     */
    public List<ConstraintViolations> getConstraintViolationForStartScenario(
            int scenarioId, Date startDate, Date stopDate, ProgramOriginSource programOriginSource);
    
    /**
     * Check to see if any constraints would be violated by stopping the scenario
     * specified by scenarioId using the specified end time.
     * @param scenarioId the paoId of the scenario.
     * @param stopDate - the time chosen to stop the scenario
     */
    public List<ConstraintViolations> getConstraintViolationForStopScenario(
            int scenarioId, Date stopDate, ProgramOriginSource programOriginSource);

    /**
     * Start the given program with the given gear number, start and end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The gear number.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @param overrideConstraints If this is set to true, constraints will be
     *            overridden. If not, they will be observed.
     * @throws TimeoutException 
     */
    public ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate,
            Duration startOffset, boolean stopScheduled, Date stopDate,
            Duration stopOffset, boolean overrideConstraints,
            List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource) throws TimeoutException;

    /**
     * Starts control for all programs belonging to a given scenario. Returns a
     * ScenarioStatus object which contains the scenarioName, and a list of
     * ProgramStatus for each program that control start was attempted. Each
     * ProgramStatus will contain the updated program status info if successful,
     * those programs that have constraint violations will contain current
     * program status info and the list of violations in their ProgramStatus.
     * @param scenarioId
     * @param startTime
     * @param stopTime
     * @param overrideConstraints Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws TimeoutException if server fails to send an program update response for any of the program control start attempted.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     * @throws NotFoundException
     * @throws ConnectionException
     */
    public List<ProgramStatus> startScenarioBlocking(int scenarioId, Date startTime,
                                 Date stopTime, boolean overrideConstraints,
                                 boolean observeConstraintsAndExecute,
                                 LiteYukonUser user, ProgramOriginSource programOriginSource)
             throws NotFoundException, TimeoutException, NotAuthorizedException, ConnectionException;

    /**
     * Start the given program with the given gear number, start and end time.
     * @param programId The PAO id of the program to check.
     * @param gearNumber The one-based index of the program gear.
     * @param startDate The time chosen to start the program. Must not be null.
     * @param stopDate The time chosen to end the program. Cannot be null.
     * @param overrideConstraints If this is set to true, constraints will be
     *            overridden. If not, they will be observed.
     */
    public void startProgram(int programId, int gearNumber, Date startDate,
                             Duration startOffset, boolean stopScheduled, Date stopDate,
                             Duration stopOffset, boolean overrideConstraints,
                             List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource);

    /**
     * Starts a program. Blocks on Program Start Scheduled. Looks up program by programName.
     * 
     * Handles constraints based on observeConstraints boolean. If overrideConstraints is set true, constraints
     * will not be checked before starting.
     * 
     * @param overrideConstraints : If true causes constraints to not be checked before starting program
     * @param observeConstraints : If true will start a program if it is ok, modifying time as constraints specify

     * @throws NotFoundException
     * @throws TimeoutException
     * @throws UserNotInRoleException
     * @throws ConnectionException
     */
    public ProgramStatus startProgram(int programId, Date startTime, Date stopTime, String gearName, 
                                      boolean overrideConstraints, boolean observeConstraints, LiteYukonUser liteYukonUser,
                                      ProgramOriginSource programOriginSource)
                       throws NotFoundException, TimeoutException, UserNotInRoleException, ConnectionException;

    /**
     * Starts control for all programs belonging to a given scenario. 
     * Only checks for valid scenario name and user permission, then starts each program in the scenario as a
     * background process and returns immediately.
     * @param scenarioId
     * @param startTime
     * @param stopTime
     * @param overrideConstraints Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStart=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public void startScenario(int scenarioId, Date startTime, Date stopTime, boolean overrideConstraints, 
                              boolean observeConstraints, LiteYukonUser user,
                              ProgramOriginSource programOriginSource) 
                      throws NotFoundException, TimeoutException, NotAuthorizedException, 
                             BadServerResponseException, ConnectionException;

    /**
     * Schedules the program stop. Blocks on Program Stop Scheduled
     * @throws BadServerResponseException 
     * @throws TimeoutException 
     */
    public ProgramStatus stopProgramBlocking(int programId, Date stopDate, Duration stopOffset, ProgramOriginSource programOriginSource) throws TimeoutException;

    /**
     * Schedules the program stop. Non-blocking
     * @throws BadServerResponseException 

     * @throws TimeoutException 
     */
    public void stopProgram(int programId, Date stopDate, Duration stopOffset, ProgramOriginSource programOriginSource);

    /**
     * Schedules a stop program. Blocks on Program Stop Scheduled. Looks up program by programName.
     * 
     * Handles constraints based on observeConstraints boolean. If overrideConstraints is set true, constraints
     * will not be checked before scheduling program stop.
     * 
     * @param scenarioId
     * @param stopTime
     * @param overrideConstraints
     * @param observeConstraints
     * @return
     * @throws TimeoutException
     */
    public ProgramStatus stopProgram(int programId, Date stopTime, boolean overrideConstraints, boolean observeConstraints,
                                            ProgramOriginSource programOriginSource)
                                                          throws TimeoutException, BadServerResponseException;

    public void stopProgram(int programId, ProgramOriginSource programOriginSource);

    /**
     * Stop control for all programs belonging to a given scenario.
     * Only checks for valid scenario name and user permission, then stops each program in the scenario as a
     * background process and returns immediately.
     * @param scenarioId
     * @param stopTime
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public void stopScenario(int scenarioId, Date stopTime, boolean forceStop,
                             boolean observeConstraintsAndExecute, LiteYukonUser user,
                             ProgramOriginSource programOriginSource)
                             throws NotFoundException, NotAuthorizedException;

    /**
     * Stop control for all programs belonging to a given scenario. Returns a
     * ScenarioStatus object which contains the scenarioName, and a list of
     * ProgramStatus for each program that control stop was attempted. Each
     * program status will contain the updated program status info if
     * successful, those programs that have constraint violations will contain
     * current program status info and the list of violations in their
     * ProgramStatus.
     * @param scenarioId
     * @param stopTime
     * @param forceStop Should normally always be set to false, set to true only if
     * you're sure you really need want to ignore constraint violations.
     * @param observeConstraintsAndExecute If false, do not execute if there are constraint violations.
     * If true, allow the server to alter our request to abide by the constraints and execute (i.e. "Observe") 
     * Note: This value only matters when using forceStop=false.
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws TimeoutException if server fails to send an program update response for any of the program control stop attempted.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public List<ProgramStatus> stopScenarioBlocking(int scenarioid, Date stopTime, boolean forceStop,
                                                    boolean observeConstraintsAndExecute, LiteYukonUser user,
                                                    ProgramOriginSource programOriginSource) 
                    throws NotFoundException, TimeoutException, NotAuthorizedException, BadServerResponseException;
    
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber,
                                                                      Date stopDate, ProgramOriginSource programOriginSource);

    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate, 
                                    boolean overrideConstraints, ProgramOriginSource programOriginSource);
    
    public void changeGear(int programId, int gearNumber, ProgramOriginSource programOriginSource);
    
    /**
     * Sends an enable or disable program command to the load management service
     * for the program with the specified id.
     */
    public void setEnabled(int programId, boolean isEnabled);
    
    /**
     * Sends an emergency disable program command to the load management service
     * for the program with the specified id.
     */
    public void disableAndSupressRestoration(int programId);
}
