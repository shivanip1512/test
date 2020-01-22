package com.cannontech.web.api.dr.setup.model;

import java.util.List;

import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.program.setup.model.ProgramConstraint;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.pao.PaoType;

public class LoadProgramFilteredResult {

    private LMDto program;
    private PaoType type;
    private List<LMDto> gears;
    private List<LMDto> loadGroups;
    private OperationalState operationalState;
    private ProgramConstraint constraint;

    public LMDto getProgram() {
        return program;
    }

    public void setProgram(LMDto program) {
        this.program = program;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public List<LMDto> getGears() {
        return gears;
    }

    public void setGears(List<LMDto> gears) {
        this.gears = gears;
    }

    public List<LMDto> getLoadGroups() {
        return loadGroups;
    }

    public void setLoadGroups(List<LMDto> loadGroups) {
        this.loadGroups = loadGroups;
    }

    public OperationalState getOperationalState() {
        return operationalState;
    }

    public void setOperationalState(OperationalState operationalState) {
        this.operationalState = operationalState;
    }

    public ProgramConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(ProgramConstraint constraint) {
        this.constraint = constraint;
    }

}
