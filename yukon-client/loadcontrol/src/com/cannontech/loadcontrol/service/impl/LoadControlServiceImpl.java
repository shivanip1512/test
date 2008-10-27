package com.cannontech.loadcontrol.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.LoadManagementProxy;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.ProgramChangeBlocker;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public class LoadControlServiceImpl implements LoadControlService, InitializingBean {
    
    private Logger log = YukonLogManager.getLogger(LoadControlServiceImpl.class);
    private long programChangeTimeout = 5000;
    
    private LoadManagementProxy loadManagementService;
    private LoadControlProgramDao loadControlProgramDao;
    private LoadControlCommandService loadControlCommandService;
    
    private LoadControlClientConnection loadControlClientConnection;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        loadControlClientConnection = loadManagementService.getLoadControlClientConnection();
    }
    
    // GET PROGRAM SATUS BY PROGRAM NAME
    public ProgramStatus getProgramStatusByProgramName(String programName) throws IllegalArgumentException {
        
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
    public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime, int gearNumber, boolean forceStart) throws IllegalArgumentException, TimeoutException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return doExecuteStartRequest(program, startTime, stopTime, gearNumber, forceStart);
    }

    // STOP CONTROL BY PROGRAM NAME
    public ProgramStatus stopControlByProgramName(String programName, Date stopTime, int gearNumber, boolean forceStop) throws IllegalArgumentException, TimeoutException {

        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        
        if (program == null) {
            return null;
        }
        
        return doExecuteStopRequest(program, stopTime, gearNumber, forceStop);
    }
    
    // START CONTROL BY SCENAIO NAME
    public ScenarioStatus startControlByScenarioName(String scenarioName, Date startTime, Date stopTime, boolean forceStart) throws IllegalArgumentException, TimeoutException {

        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        
        List<ProgramStatus> programStatii = new ArrayList<ProgramStatus>();
        List<LMProgramBase> programs = getProgramsForProgramIds(programIds);
        for (LMProgramBase program : programs) {
                
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            
            ProgramStatus programStatus = doExecuteStartRequest(program, startTime, stopTime, startingGearNumber, forceStart);
            programStatii.add(programStatus);
        }
        
        return new ScenarioStatus(scenarioName, programStatii);
    }
    
    // STOP CONTROL BY SCENAIO NAME
    public ScenarioStatus stopControlByScenarioName(String scenarioName, Date stopTime, boolean forceStop) throws IllegalArgumentException, TimeoutException {

        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        
        List<ProgramStatus> programStatii = new ArrayList<ProgramStatus>();
        List<LMProgramBase> programs = getProgramsForProgramIds(programIds);
        for (LMProgramBase program : programs) {
            
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            
            ProgramStatus programStatus = doExecuteStopRequest(program, stopTime, startingGearNumber, forceStop);
            programStatii.add(programStatus);
        }
        
        return new ScenarioStatus(scenarioName, programStatii);
    }
    
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName) throws IllegalArgumentException {
        
        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<ProgramStartingGear> programStartingGears = loadControlProgramDao.getProgramStartingGearsForScenarioId(scenarioId);
        
        return new ScenarioProgramStartingGears(scenarioName, programStartingGears);
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
    private ProgramStatus doExecuteStartRequest(LMProgramBase program, Date startTime, Date stopTime, int gearNumber, boolean forceStart) throws TimeoutException {
        
        ProgramStatus programStatus = new ProgramStatus(program);
        
        // build control msg
        LMManualControlRequest controlRequest = ProgramUtils.createStartRequest(program, startTime, stopTime, gearNumber, forceStart);
        
        // execute and update program status
        executeProgramChangeAndUpdateProgramStatus(program, controlRequest, programStatus, forceStart);
        
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
    private ProgramStatus doExecuteStopRequest(LMProgramBase program, Date stopTime, int gearNumber, boolean forceStop) throws TimeoutException {
        
        ProgramStatus programStatus = new ProgramStatus(program);
        
        // build control msg
        LMManualControlRequest controlRequest = ProgramUtils.createStopRequest(program, stopTime, gearNumber, forceStop);
        
        // execute and update program status
        executeProgramChangeAndUpdateProgramStatus(program, controlRequest, programStatus, forceStop);
        
        return programStatus;
    }
    
    /**
     * Helper method that first executes a request and looks for contraint violations (not not force). 
     * If violations are found they are applied to the ProgramStatus and the ProgramStatus is returned.
     * If no violations are found the request is executed with USE constraint flag if not force, otherwise
     * with OVERRRIDE if force. The thread then waits for a ProgramChange event from the server, if 
     * one is returned within timeout period, the program is re-retreived from connection cache and 
     * the Program Status is updated and returned, otherwise a TimeoutExeception will be thrown.
     * @param program
     * @param controlRequest
     * @param programStatus
     * @throws TimeoutException
     */
    private void executeProgramChangeAndUpdateProgramStatus(
            LMProgramBase program, LMManualControlRequest controlRequest,
            ProgramStatus programStatus,
            boolean force) throws TimeoutException {

        
        // execute check. if has violations return without executing for real
        if (!force) {
            
            controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_CHECK);
            List<String> violations = loadControlCommandService.executeManualCommand(controlRequest);
            
            // log violations, add to status, return
            if (violations.size() > 0) {
                for (String violation : violations) {
                    log.info("Constraint Violation: " + violation + " for request: " + controlRequest);
                }
                programStatus.addConstraintViolations(violations);
                return;
            } else {
                log.info("No constraint violations for request: " + controlRequest);
            }
        }

        // execute for real
        controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_USE);
        if (force) {
            controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE);
        }
        
        long executeTime = (new Date()).getTime();
        List<String> violations = loadControlCommandService.executeManualCommand(controlRequest);
        
        // wait for this program to change
        ProgramChangeBlocker programChangeBlocker = new ProgramChangeBlocker(this.loadControlClientConnection,
                                                                             program.getYukonID(),
                                                                             executeTime,
                                                                             this.programChangeTimeout);
        program = programChangeBlocker.getUpdatedProgram();

        // update program statusstatus, slim chance we now have violation even though we didnt during check
        programStatus.setProgram(program);
        programStatus.addConstraintViolations(violations);
    }
    
    //==============================================================================================
    // INJECTED DEPENDANCIES
    ///=============================================================================================
    
    @Autowired
    public void setLoadManagementService(
            LoadManagementProxy loadManagementService) {
        this.loadManagementService = loadManagementService;
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
