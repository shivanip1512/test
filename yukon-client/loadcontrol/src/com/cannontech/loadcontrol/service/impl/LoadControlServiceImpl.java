package com.cannontech.loadcontrol.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.GearNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.ProgramChangeBlocker;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;
import com.google.common.collect.Lists;

public class LoadControlServiceImpl implements LoadControlService {
    
    private Logger log = YukonLogManager.getLogger(LoadControlServiceImpl.class);
    private long programChangeTimeout = 5000; //ms
    
    private LoadControlProgramDao loadControlProgramDao;
    private LoadControlCommandService loadControlCommandService;
    private PaoDao paoDao;
    private PaoAuthorizationService paoAuthorizationService;
    private Executor executor;
    private ScenarioDao scenarioDao;

    private LoadControlClientConnection loadControlClientConnection;
    
    // GET PROGRAM SATUS BY PROGRAM NAME
    @Override
    public ProgramStatus getProgramStatusByProgramName(String programName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        
        validateProgramIsVisibleToUser(programName, programId, user);
        
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return new ProgramStatus(program);
    }
    
    // GET ALL CURRENTLY ACTIVE PROGRAMS
    @Override
    public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user) {
        
        List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
        
        List<Integer> programIds = loadControlProgramDao.getAllProgramIds();
        for (int programId : programIds) {
            
        	if(programIsVisibleToUser(user, programId)) {
        	
	            LMProgramBase program = loadControlClientConnection.getProgram(programId);
	            
	            if (program != null) {
	                ProgramStatus programStatus = new ProgramStatus(program);
	                
	                if (programStatus.isActive()) {
	                    programStatuses.add(programStatus);
	                }
	            }
        	}
        }
        
        return programStatuses;
    }
    
    // START CONTROL BY PROGRAM NAME
    @Override
	public ProgramStatus startControlByProgramName(String programName,
			Date startTime, Date stopTime, String gearName, boolean forceStart,
			boolean observeConstraintsAndExecute, LiteYukonUser user)
			throws ProgramNotFoundException, GearNotFoundException, TimeoutException, NotAuthorizedException {
        
		int programId;
		int gearNumber;
		try {
			programId = loadControlProgramDao.getProgramIdByProgramName(programName);
		} catch (NotFoundException e) {
			throw new ProgramNotFoundException(e.getMessage(), e);
		}
			
		try {
			gearNumber = loadControlProgramDao.getGearNumberForGearName(programId, gearName);
		} catch (NotFoundException e) {
			throw new GearNotFoundException(e.getMessage(), e);
		}
		
        validateProgramIsVisibleToUser(programName, programId, user);
        
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return doExecuteStartRequest(program, startTime, null, stopTime, null, gearNumber, forceStart, observeConstraintsAndExecute, user);
    }
    
    // START CONTROL BY PROGRAM NAME
    @Override
    public ProgramStatus startControlByProgramName(String programName,
			Date startTime, Date stopTime, boolean forceStart,
			boolean observeConstraintsAndExecute, LiteYukonUser user)
			throws NotFoundException, TimeoutException, NotAuthorizedException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        validateProgramIsVisibleToUser(programName, programId, user);
        
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        int gearNumber = ((LMProgramDirect)program).getCurrentGearNumber();
        
        return doExecuteStartRequest(program, startTime, null, stopTime, null, gearNumber, forceStart, observeConstraintsAndExecute, user);
    }

    // STOP CONTROL BY PROGRAM NAME
    @Override
	public ProgramStatus stopControlByProgramName(String programName,
			Date stopTime, boolean forceStop,
			boolean observeConstraintsAndExecute, LiteYukonUser user)
			throws NotFoundException, TimeoutException, NotAuthorizedException {

        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        validateProgramIsVisibleToUser(programName, programId, user);
        
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return doExecuteStopRequest(program, stopTime, null, ((LMProgramDirect)program).getCurrentGearNumber(), forceStop, observeConstraintsAndExecute, user);
    }
    
    // START CONTROL BY SCENAIO NAME
    @Override
	public ScenarioStatus startControlByScenarioName(String scenarioName,
													Date startTime, 
													Date stopTime, 
													boolean forceStart,
													boolean observeConstraintsAndExecute, 
													LiteYukonUser user)
													throws NotFoundException, TimeoutException, NotAuthorizedException {

		int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
		validateScenarioIsVisibleToUser(scenarioName, scenarioId, user);
		List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);

		return doStartProgramsInScenario(programIds, scenarioId, scenarioName, startTime, stopTime, forceStart, observeConstraintsAndExecute, user);
	}
	
    // ASYNC START CONTROL BY SCENARIO NAME@Override
    @Override
	public void asynchStartControlByScenarioName(final String scenarioName,
												final Date startTime, 
												final Date stopTime,
												final boolean forceStart,
												final boolean observeConstraintsAndExecute, 
												final LiteYukonUser user)
												throws NotFoundException, NotAuthorizedException {

		final int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
		validateScenarioIsVisibleToUser(scenarioName, scenarioId, user);
		final List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);

		executor.execute(new Runnable() {
			public void run() {
				try {
					doStartProgramsInScenario(programIds, scenarioId,
							scenarioName, startTime, stopTime, forceStart,
							observeConstraintsAndExecute, user);
				} catch (Exception e) {
					log.debug("Error while running scenario start asynchronously. scenarioId = " + scenarioId + ", programIds = " + programIds, e);
				}
			}
		});
	}
	
	private ScenarioStatus doStartProgramsInScenario(List<Integer> programIds, int scenarioId, String scenarioName, Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user) throws TimeoutException {
		List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
        List<LMProgramBase> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =
            scenarioDao.findScenarioProgramsForScenario(scenarioId);
        for (LMProgramBase program : programs) {
                
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            ScenarioProgram scenarioProgram = scenarioPrograms.get(program.getYukonID());

            ProgramStatus programStatus =
                doExecuteStartRequest(program, startTime, scenarioProgram.getStartOffset(),
                                      stopTime, scenarioProgram.getStopOffset(), startingGearNumber,
                                      forceStart, observeConstraintsAndExecute, user);
            programStatuses.add(programStatus);
        }
        
        return new ScenarioStatus(scenarioName, programStatuses);
	}
    
    // STOP CONTROL BY SCENAIO NAME
	@Override
	public ScenarioStatus stopControlByScenarioName(String scenarioName,
													Date stopTime, 
													boolean forceStop,
													boolean observeConstraintsAndExecute, 
													LiteYukonUser user)
													throws NotFoundException, TimeoutException, NotAuthorizedException {

        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        validateScenarioIsVisibleToUser(scenarioName, scenarioId, user);
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        
        return doStopProgramsInScenario(programIds, scenarioId, scenarioName, stopTime, forceStop, observeConstraintsAndExecute, user);
    }
	
	// ASYNC STOP CONTROL BY SCENARIO NAME
	@Override
	public void asynchStopControlByScenarioName(final String scenarioName,
												final Date stopTime, 
												final boolean forceStop,
												final boolean observeConstraintsAndExecute, 
												final LiteYukonUser user)
												throws NotFoundException, NotAuthorizedException {

        final int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        validateScenarioIsVisibleToUser(scenarioName, scenarioId, user);
        final List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        
        executor.execute(new Runnable() {
    		public void run() {
    			try {
    				doStopProgramsInScenario(programIds, scenarioId, scenarioName, stopTime, forceStop, observeConstraintsAndExecute, user);
    			} catch (Exception e) {
    				log.debug("Error while running scenario stop asynchronously. scenarioId = " + scenarioId + ", programIds = " + programIds, e);
    			}
    		}
    	});
    }
	
	private ScenarioStatus doStopProgramsInScenario(List<Integer> programIds, int scenarioId, String scenarioName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user) throws TimeoutException {

		List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
        List<LMProgramBase> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =
            scenarioDao.findScenarioProgramsForScenario(scenarioId);
        for (LMProgramBase program : programs) {
            
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            ScenarioProgram scenarioProgram = scenarioPrograms.get(program.getYukonID());
            ProgramStatus programStatus =
                doExecuteStopRequest(program, stopTime, scenarioProgram.getStopOffset(),
                                     startingGearNumber, forceStop, observeConstraintsAndExecute, user);
            programStatuses.add(programStatus);
        }
        
        return new ScenarioStatus(scenarioName, programStatuses);
	}
    
	// GET ALL SCENARIO PROGRAMS
	@Override
    public List<ScenarioProgramStartingGears> getAllScenarioProgramStartingGears(LiteYukonUser user) {
        
		List<DisplayablePao> allScenarios = scenarioDao.getAllScenarios();
		List<ScenarioProgramStartingGears> allScenarioProgramStartingGears = Lists.newArrayListWithExpectedSize(allScenarios.size());
		
		for (DisplayablePao scenario : allScenarios) {
			
			int scenarioId = scenario.getPaoIdentifier().getPaoId();
			if (scenarioIsVisibleToUser(user, scenario)) {
				
				List<ProgramStartingGear> programStartingGears = loadControlProgramDao.getProgramStartingGearsForScenarioId(scenarioId);
	            ScenarioProgramStartingGears scenarioProgramStartingGears = new ScenarioProgramStartingGears(scenario.getName(), programStartingGears);
	            allScenarioProgramStartingGears.add(scenarioProgramStartingGears);
			}
		}
		
		return allScenarioProgramStartingGears;
    }
    
    // PROGRAM STARTING GEARS BY SCENARIO NAME
	@Override
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        
		int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        validateScenarioIsVisibleToUser(scenarioName, scenarioId, user);
        
        List<ProgramStartingGear> programStartingGears = loadControlProgramDao.getProgramStartingGearsForScenarioId(scenarioId);
        return new ScenarioProgramStartingGears(scenarioName, programStartingGears);
    }
    
    // CONTROL HISTORY BY PROGRAM NAME
	@Override
    public List<ProgramControlHistory> getControlHistoryByProgramName(String programName, Date fromTime, Date throughTime, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        
    	int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
    	validateProgramIsVisibleToUser(programName, programId, user);
        
        List<ProgramControlHistory> programControlHistory = loadControlProgramDao.getProgramControlHistoryByProgramId(programId, fromTime, throughTime);
     
        return programControlHistory;
    }
    
	// ALL CONTROL HISTORY
	@Override
    public List<ProgramControlHistory> getAllControlHistory(Date fromTime, Date throughTime, LiteYukonUser user) {
        
    	List<ProgramControlHistory> allProgramControlHistory = loadControlProgramDao.getAllProgramControlHistory(fromTime, throughTime);
    	
    	List<ProgramControlHistory> visibleProgramControlHistory = Lists.newArrayListWithExpectedSize(allProgramControlHistory.size());
    	for (ProgramControlHistory programControlHistory : allProgramControlHistory) {
    		
    		if (programIsVisibleToUser(user, programControlHistory.getProgramId())) {
    			visibleProgramControlHistory.add(programControlHistory);
    		}
    	}
     
        return visibleProgramControlHistory;
    }
    
    //==============================================================================================
    // PRIVATE HELPER METHODS
    ///=============================================================================================
    
    /**
     * Helper method to create a new ProgramStatus object for given Program, creates a start request
     * with a CHECK constraint, then calls into executeProgramChangeAndUpdateProgramStatus().
     * @param program
     * @param startTime
     * @param stopTime
     * @return
     * @throws TimeoutException
     */
    private ProgramStatus doExecuteStartRequest(LMProgramBase program, Date startTime, Duration startOffset, Date stopTime, Duration stopOffset, int gearNumber, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user) throws TimeoutException {
        
        ProgramStatus programStatus = new ProgramStatus(program);
        programStatus.setProgram(program);
        
        // build control msg
        LMManualControlRequest controlRequest = ProgramUtils.createStartRequest(program, startTime, startOffset, stopTime, stopOffset, gearNumber, forceStart);
        
        // execute and update program status
        executeProgramChangeAndUpdateProgramStatus(controlRequest, programStatus, forceStart, observeConstraintsAndExecute, user);
        
        return programStatus;
    }
    
    /**
     * Helper method to create a new ProgramStatus object for given Program, creates a stop request
     * with a CHECK constraint, then calls into executeProgramChangeAndUpdateProgramStatus().
     * @param program
     * @param startTime
     * @param stopTime
     * @return
     * @throws TimeoutException
     */
    private ProgramStatus doExecuteStopRequest(LMProgramBase program, Date stopTime, Duration stopOffset, int gearNumber, boolean forceStop,  boolean observeConstraintsAndExecute, LiteYukonUser user) throws TimeoutException {
        
        ProgramStatus programStatus = new ProgramStatus(program);
        programStatus.setProgram(program);
        
        // build control msg
        LMManualControlRequest controlRequest = ProgramUtils.createStopRequest(program, stopTime, stopOffset, gearNumber, forceStop);
        
        // execute and update program status
        executeProgramChangeAndUpdateProgramStatus(controlRequest, programStatus, forceStop, observeConstraintsAndExecute, user);
        
        return programStatus;
    }
    
    /**
     * Helper method that first executes a request and looks for constraint violations (not not force). 
     * If violations are found they are applied to the ProgramStatus.
     * If violations occur and observeConstraintsAndExecute=true, the request will be executed anyway
     * with in "Observe" mode (CONSTRAINTS_FLAG_USE), and the server will adjust the request to
     * meet constraints requirements.
     * If no constraints are violated the request will be executed in either OVERRIDE mode (if force=true) or
     * "Observe" mode (if force=false).
     * The thread then waits for a ProgramChange event from the server, if 
     * one is returned within timeout period, the program is re-retrieved from connection cache and 
     * the Program Status is updated, otherwise a TimeoutExeception will be thrown.
     * @param program
     * @param controlRequest
     * @param programStatus
     * @throws TimeoutException
     */
    private void executeProgramChangeAndUpdateProgramStatus(
            final LMManualControlRequest controlRequest,
            ProgramStatus programStatus,
            boolean force, boolean observeConstraintsAndExecute, LiteYukonUser user) throws TimeoutException, NotAuthorizedException {

        // execute check. if has violations return without executing for real
        if (!force) {
            
            controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_CHECK);
            List<String> checkViolations = loadControlCommandService.executeManualCommand(controlRequest);
            
            // log violations, add to status, return
            if (checkViolations.size() > 0) {
                for (String violation : checkViolations) {
                    log.info("Constraint Violation: " + violation + " for request: " + controlRequest);
                }
                programStatus.setConstraintViolations(checkViolations);
                
                // observeConstraintsAndExecute = false, return
                if (!observeConstraintsAndExecute) {
                    return;
                }
            } else {
                log.info("No constraint violations for request: " + controlRequest);
            }
        }

        // execute for real in wither "observe" (CONSTRAINTS_FLAG_USE) or OVERRIDE mode
        // either because this is a force, or because its not a force but we are going to let server
        // "observe" any constraints violations (i.e. alter our request to meet constraint requirements)
        // wait for server to respond with the updated program
        if (force || observeConstraintsAndExecute) {
            
            controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_USE);
            if (force) {
                controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE);
            }
            
            // perform execution of request in either
            // wait for this program to change
            // programStatus will be updated with new program and any violation that occur during execution
            ProgramChangeBlocker programChangeBlocker = new ProgramChangeBlocker(controlRequest, 
                                                                                 programStatus,
                                                                                 this.loadControlCommandService,
                                                                                 this.loadControlClientConnection,
                                                                                 this.programChangeTimeout);
            programChangeBlocker.updateProgramStatus();
        }
    }
    
    // program/scenario visibility checks
    private void validateScenarioIsVisibleToUser(String scenarioName, int scenarioId, LiteYukonUser user) {
    	if (!scenarioIsVisibleToUser(user, scenarioId)) {
        	throw new NotAuthorizedException("Scenario is not visible to user: " + scenarioName + " (id=" + scenarioId + ")");
        }
    }
    
    private void validateProgramIsVisibleToUser(String programName, int programId, LiteYukonUser user) {
    	if (!programIsVisibleToUser(user, programId)) {
        	throw new NotAuthorizedException("Program is not visible to user: " + programName + " (id=" + programId + ")");
        }
    }
    
    private boolean programIsVisibleToUser(LiteYukonUser user, int programId) {
    	
    	// first check if program is directly visible to user
    	if (isLmPaoVisibleToUser(user, programId)) {
    		return true;
    	}
    	
    	// otherwise, check if the program's control area (if any) is visible to user
    	LMControlArea controlAreaForProgram = loadControlClientConnection.findControlAreaForProgram(programId);
    	if (controlAreaForProgram != null) {
    		return isLmPaoVisibleToUser(user, controlAreaForProgram.getYukonID());
    	}
    	
    	return false;
    }
    
    private boolean scenarioIsVisibleToUser(LiteYukonUser user, int scenarioId) {
    	return isLmPaoVisibleToUser(user, scenarioId);
    }
    
    private boolean scenarioIsVisibleToUser(LiteYukonUser user, YukonPao scenarioPao) {
    	return isLmPaoVisibleToUser(user, scenarioPao);
    }
    
    private boolean isLmPaoVisibleToUser(LiteYukonUser user, int paoId) {
    	return paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(paoId));
    }
    
    private boolean isLmPaoVisibleToUser(LiteYukonUser user, YukonPao pao) {
    	return paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, pao);
    }

    //==============================================================================================
    // INJECTED DEPENDANCIES
    ///=============================================================================================
    
    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }
    @Autowired
    public void setLoadControlProgramDao(
            LoadControlProgramDao loadControlProgramDao) {
        this.loadControlProgramDao = loadControlProgramDao;
    }
    
    @Autowired
    public void setLoadControlCommandService(
            LoadControlCommandService loadControlCommandService) {
        this.loadControlCommandService = loadControlCommandService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
    
    @Autowired
    public void setPaoAuthorizationService(
			PaoAuthorizationService paoAuthorizationService) {
		this.paoAuthorizationService = paoAuthorizationService;
	}
    
    @Autowired
    public void setExecutor(ScheduledExecutor executor) {
		this.executor = executor;
	}
    
    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
		this.scenarioDao = scenarioDao;
	}
}
