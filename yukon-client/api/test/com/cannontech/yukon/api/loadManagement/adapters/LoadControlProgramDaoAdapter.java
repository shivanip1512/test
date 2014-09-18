package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Date;
import java.util.List;

import org.joda.time.ReadableInstant;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;

public class LoadControlProgramDaoAdapter implements LoadControlProgramDao {

    @Override
    public int getProgramIdByProgramName(String programName) throws NotFoundException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public int getScenarioIdForScenarioName(String scenarioName) throws NotFoundException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<Integer> getAllProgramIds() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<Integer> getProgramIdsByScenarioId(int scenarioId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramStartingGear> getProgramStartingGearsForScenarioId(int scenarioId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramControlHistory> getAllProgramControlHistory(Date startDateTime,
                                                                   Date stopDateTime) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramControlHistory> getProgramControlHistoryByProgramId(int programId,
                                                                           Date startDateTime,
                                                                           Date stopDateTime) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramControlHistory findHistoryForProgramAtTime(int programId, ReadableInstant when) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public int getGearNumberForGearName(int programId, String gearName) throws NotFoundException {
        throw new UnsupportedOperationException("Not Implemented");
    }

}
