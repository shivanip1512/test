package com.cannontech.dr.program.dao;

import java.util.List;

import com.cannontech.common.device.model.DisplayableDevice;

public interface ProgramDao {
    public List<DisplayableDevice> getProgramsForScenario(int scenarioId);
    public List<DisplayableDevice> getProgramsForControlArea(int controlAreaId);
    public List<DisplayableDevice> getPrograms();
    public DisplayableDevice getProgram(int programId);
    public List<DisplayableDevice> getProgramsForLoadGroup(int loadGroupId);
}
