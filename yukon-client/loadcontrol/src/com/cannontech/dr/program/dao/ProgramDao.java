package com.cannontech.dr.program.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;

public interface ProgramDao {
    public List<DisplayablePao> getProgramsForScenario(int scenarioId);
    public List<DisplayablePao> getProgramsForControlArea(int controlAreaId);
    public List<DisplayablePao> getPrograms();
    public DisplayablePao getProgram(int programId);
    public List<DisplayablePao> getProgramsForLoadGroup(int loadGroupId);
}
