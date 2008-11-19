package com.cannontech.yukon.api.loadManagement;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public class LoadControlServiceAdapter implements LoadControlService {

    @Override
    public List<ProgramStatus> getAllCurrentlyActivePrograms() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramControlHistory> getControlHistoryByProgramName(
            String programName, Date fromTime, Date throughTime)
            throws NotFoundException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus getProgramStatusByProgramName(String programName)
            throws NotFoundException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(
            String scenarioName) throws NotFoundException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, int gearNumber, boolean forceStart,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, boolean forceStart,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ScenarioStatus startControlByScenarioName(String scenarioName,
            Date startTime, Date stopTime, boolean forceStart,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus stopControlByProgramName(String programName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ScenarioStatus stopControlByScenarioName(String scenarioName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

}
