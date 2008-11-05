package com.cannontech.loadcontrol.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
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

public class LoadControlServiceImpl implements LoadControlService {
    
    private Logger log = YukonLogManager.getLogger(LoadControlServiceImpl.class);
    private long programChangeTimeout = 5000; //ms
    
    private LoadControlProgramDao loadControlProgramDao;
    private LoadControlCommandService loadControlCommandService;
    
    private LoadControlClientConnection loadControlClientConnection;
    
    // GET PROGRAM SATUS BY PROGRAM NAME
    public ProgramStatus getProgramStatusByProgramName(String programName) throws NotFoundException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return new ProgramStatus(program);
    }
    
    // GET ALL CURRENTLY ACTIVE PROGRAMS
    public List<ProgramStatus> getAllCurrentlyActivePrograms() {
        
        List<ProgramStatus> programStatii = new ArrayList<ProgramStatus>();
        
        List<Integer> programIds = loadControlProgramDao.getAllProgramIds();
        for (int programId : programIds) {
            
            LMProgramBase program = loadControlClientConnection.getProgram(programId);
            
            if (program != null) {
                ProgramStatus programStatus = new ProgramStatus(program);
                
                if (programStatus.isActive()) {
                    programStatii.add(programStatus);
                }
            }
        }
        
        return programStatii;
    }
    
    // START CONTROL BY PROGRAM NAME
    public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime, int gearNumber, boolean forceStart, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return doExecuteStartRequest(program, startTime, stopTime, gearNumber, forceStart, observeConstraintsAndExecute);
    }
    
    public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        int gearNumber = ((LMProgramDirect)program).getCurrentGearNumber();
        
        return doExecuteStartRequest(program, startTime, stopTime, gearNumber, forceStart, observeConstraintsAndExecute);
    }

    // STOP CONTROL BY PROGRAM NAME
    public ProgramStatus stopControlByProgramName(String programName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {

        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return doExecuteStopRequest(program, stopTime, ((LMProgramDirect)program).getCurrentGearNumber(), forceStop, observeConstraintsAndExecute);
    }
    
    // START CONTROL BY SCENAIO NAME
    public ScenarioStatus startControlByScenarioName(String scenarioName, Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {

        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        
        List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
        List<LMProgramBase> programs = getProgramsForProgramIds(programIds);
        for (LMProgramBase program : programs) {
                
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            
            ProgramStatus programStatus = doExecuteStartRequest(program, startTime, stopTime, startingGearNumber, forceStart, observeConstraintsAndExecute);
            programStatuses.add(programStatus);
        }
        
        return new ScenarioStatus(scenarioName, programStatuses);
    }
    
    // STOP CONTROL BY SCENAIO NAME
    public ScenarioStatus stopControlByScenarioName(String scenarioName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {

        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        
        List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
        List<LMProgramBase> programs = getProgramsForProgramIds(programIds);
        for (LMProgramBase program : programs) {
            
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            
            ProgramStatus programStatus = doExecuteStopRequest(program, stopTime, startingGearNumber, forceStop, observeConstraintsAndExecute);
            programStatuses.add(programStatus);
        }
        
        return new ScenarioStatus(scenarioName, programStatuses);
    }
    
    // PROGRAM STARTING GEARS BY SCENARIO NAME
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName) throws NotFoundException {
        
        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<ProgramStartingGear> programStartingGears = loadControlProgramDao.getProgramStartingGearsForScenarioId(scenarioId);
        
        return new ScenarioProgramStartingGears(scenarioName, programStartingGears);
    }
    
    // CONTROL HISTORY BY PROGRAM NAME
    public List<ProgramControlHistory> getControlHistoryByProgramName(String programName, Date fromTime, Date throughTime) throws NotFoundException {
        
        //TODO everything
        List<ProgramControlHistory> programControlHistory = new ArrayList<ProgramControlHistory>();
     
        return programControlHistory;
    }
    
    //==============================================================================================
    // PRIVATE HELPER METHODS
    ///=============================================================================================
    
    private List<LMProgramBase> getProgramsForProgramIds(List<Integer> programIds) {
        
        List<LMProgramBase> programs = new ArrayList<LMProgramBase>(programIds.size());
        for (int programId : programIds) {
            LMProgramBase program = loadControlClientConnection.getProgram(programId);
            if (program != null) {
                programs.add(program);
            }
        }
        
        return programs;
    }
    
    /**
     * Helper method to create a new ProgramStatus object for given Program, creates a start request
     * with a CHECK constraint, then calls into executeProgramChangeAndUpdateProgramStatus().
     * @param program
     * @param startTime
     * @param stopTime
     * @return
     * @throws TimeoutException
     */
    private ProgramStatus doExecuteStartRequest(LMProgramBase program, Date startTime, Date stopTime, int gearNumber, boolean forceStart, boolean observeConstraintsAndExecute) throws TimeoutException {
        
        ProgramStatus programStatus = new ProgramStatus(program);
        programStatus.setProgram(program);
        
        // build control msg
        LMManualControlRequest controlRequest = ProgramUtils.createStartRequest(program, startTime, stopTime, gearNumber, forceStart);
        
        // execute and update program status
        executeProgramChangeAndUpdateProgramStatus(controlRequest, programStatus, forceStart, observeConstraintsAndExecute);
        
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
    private ProgramStatus doExecuteStopRequest(LMProgramBase program, Date stopTime, int gearNumber, boolean forceStop,  boolean observeConstraintsAndExecute) throws TimeoutException {
        
        ProgramStatus programStatus = new ProgramStatus(program);
        programStatus.setProgram(program);
        
        // build control msg
        LMManualControlRequest controlRequest = ProgramUtils.createStopRequest(program, stopTime, gearNumber, forceStop);
        
        // execute and update program status
        executeProgramChangeAndUpdateProgramStatus(controlRequest, programStatus, forceStop, observeConstraintsAndExecute);
        
        return programStatus;
    }
    
    /**
     * Helper method that first executes a request and looks for contraint violations (not not force). 
     * If violations are found they are applied to the ProgramStatus.
     * If violations occur and observeConstraintsAndExecute=true, the request will be executed anyway
     * with in "Observe" mode (CONSTRAINTS_FLAG_USE), and the server will adjust the request to
     * meet contraints requirements.
     * If no constraints are violated the request will be executed in either OVERRIDE mode (if force=true) or
     * "Observe" mode (if force=false).
     * The thread then waits for a ProgramChange event from the server, if 
     * one is returned within timeout period, the program is re-retreived from connection cache and 
     * the Program Status is updated, otherwise a TimeoutExeception will be thrown.
     * @param program
     * @param controlRequest
     * @param programStatus
     * @throws TimeoutException
     */
    private void executeProgramChangeAndUpdateProgramStatus(
            final LMManualControlRequest controlRequest,
            ProgramStatus programStatus,
            boolean force, boolean observeConstraintsAndExecute) throws TimeoutException {

        
        
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

}
