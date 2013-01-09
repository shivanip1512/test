package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Date;
import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public class LoadControlServiceAdapter implements LoadControlService {

    @Override
    public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public java.util.List<ProgramControlHistory> getControlHistoryByProgramName(
			String programName, Date fromTime, Date throughTime,
			LiteYukonUser user) throws NotFoundException,
			NotAuthorizedException {
    	throw new UnsupportedOperationException("Not Implemented");
	};

    @Override
    public ProgramStatus getProgramStatusByProgramName(String programName, LiteYukonUser user)
            throws NotFoundException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    @Override
    public List<ScenarioProgramStartingGears> getAllScenarioProgramStartingGears(LiteYukonUser user) {
    	throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(
            String scenarioName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ScenarioStatus startControlByScenarioName(String scenarioName,
            Date startTime, Date stopTime, boolean forceStart,
            boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    @Override
    public void asynchStartControlByScenarioName(String scenarioName,
            Date startTime, Date stopTime, boolean forceStart,
            boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus stopControlByProgramName(String programName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ScenarioStatus stopControlByScenarioName(String scenarioName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    @Override
    public void asynchStopControlByScenarioName(String scenarioName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramControlHistory> getAllControlHistory(Date fromTime, Date throughTime, LiteYukonUser user) {
    	throw new UnsupportedOperationException("Not Implemented");
    }
}
