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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ProgramControlHistory> getControlHistoryByProgramName(
            String programName, Date fromTime, Date throughTime)
            throws NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProgramStatus getProgramStatusByProgramName(String programName)
            throws NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(
            String scenarioName) throws NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, int gearNumber, boolean forceStart,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProgramStatus startControlByProgramName(String programName,
            Date startTime, Date stopTime, boolean forceStart,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScenarioStatus startControlByScenarioName(String scenarioName,
            Date startTime, Date stopTime, boolean forceStart,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProgramStatus stopControlByProgramName(String programName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScenarioStatus stopControlByScenarioName(String scenarioName,
            Date stopTime, boolean forceStop,
            boolean observeConstraintsAndExecute) throws NotFoundException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

}
