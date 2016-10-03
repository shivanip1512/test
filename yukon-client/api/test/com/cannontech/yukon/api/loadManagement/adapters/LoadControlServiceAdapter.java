package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ProgramStatusEnum;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;

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
    public List<ProgramControlHistory> getAllControlHistory(Date fromTime, Date throughTime, LiteYukonUser user) {
    	throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramStatus> getAllProgramStatus(LiteYukonUser user, Set<ProgramStatusEnum> programStatusEnums) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
