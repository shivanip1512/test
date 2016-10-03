package com.cannontech.loadcontrol.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ProgramStatusEnum;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class LoadControlServiceImpl implements LoadControlService {
    
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private LoadControlClientConnection loadControlClientConnection;
    
    // GET PROGRAM SATUS BY PROGRAM NAME
    @Override
    public ProgramStatus getProgramStatusByProgramName(String programName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        
        int programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        
        validateProgramIsVisibleToUser(programName, programId, user);
        
        LMProgramBase program = loadControlClientConnection.getProgramSafe(programId);
        
        return new ProgramStatus(program);
    }
    
    // GET ALL CURRENTLY ACTIVE PROGRAMS
    @Override
    public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user) {
        
    	// all LMProgramBase set
        Set<LMProgramBase> allProgramBaseSet = loadControlClientConnection.getAllProgramsSet();
        
        // active LMProgramBase set
        Set<LMProgramBase> activeProgramBaseSet = Sets.newHashSet();
        for (LMProgramBase programBase : allProgramBaseSet) {
            if (ProgramUtils.isActive(programBase)) {
            	activeProgramBaseSet.add(programBase);
            }
        }
        
        // filter out unauthorized YukonPao
        List<LMProgramBase> authorizedPrograms = paoAuthorizationService.filterAuthorized(user, activeProgramBaseSet, Permission.LM_VISIBLE);
        
        // build ProgramStatus list
        List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
        for (LMProgramBase lmProgramBase: authorizedPrograms) {
        	
        	ProgramStatus programStatus = new ProgramStatus(lmProgramBase);
            if (programStatus.isActive()) {
                programStatuses.add(programStatus);
            }
        }
        
       return programStatuses;
    }

    @Override
    public List<ProgramStatus> getAllProgramStatus(LiteYukonUser user, Set<ProgramStatusEnum> programStatusEnums) {
        List<ProgramStatus> programStatuses = new ArrayList<>();
        // all LMProgramBase
        Set<LMProgramBase> allProgramBase = loadControlClientConnection.getAllProgramsSet();

        if (programStatusEnums.isEmpty()) {
            // filter out unauthorized YukonPao
            List<LMProgramBase> authorizedPrograms = paoAuthorizationService.filterAuthorized(user, allProgramBase, Permission.LM_VISIBLE);
            authorizedPrograms.forEach(programBase -> {
                programStatuses.add(new ProgramStatus(programBase));
            });
            return programStatuses;
        }

        Set<LMProgramBase> filteredProgramBaseSet = Sets.newHashSet();
        programStatusEnums.forEach(statusEnum -> {
            allProgramBase.stream().filter(statusEnum::checkProgramStatus).forEach(filteredProgramBaseSet::add);
        });

        if (filteredProgramBaseSet.isEmpty()) {
            return Collections.emptyList();
        }
        // filter out unauthorized YukonPao
        List<LMProgramBase> authorizedPrograms = paoAuthorizationService.filterAuthorized(user, filteredProgramBaseSet, Permission.LM_VISIBLE);

        programStatusEnums.forEach(statusEnum -> {
            authorizedPrograms.stream().filter(statusEnum::checkProgramStatus).map(
                programBase -> new ProgramStatus(programBase)).forEach(programStatuses::add);
        });

        return programStatuses;
    }

	// GET ALL SCENARIO PROGRAMS
	@Override
    public List<ScenarioProgramStartingGears> getAllScenarioProgramStartingGears(LiteYukonUser user) {
        
		List<Scenario> allScenarios = scenarioDao.getAllScenarios();
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

}
