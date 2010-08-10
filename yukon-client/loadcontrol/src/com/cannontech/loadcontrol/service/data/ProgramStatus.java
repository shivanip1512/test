package com.cannontech.loadcontrol.service.data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class ProgramStatus {

    private LMProgramBase program;
    private Set<ConstraintContainer> constraintViolations = new HashSet<ConstraintContainer>();
    
    public ProgramStatus(LMProgramBase program) {
        this.program = program;
    }
    
    public void addConstraintViolation(ConstraintContainer violation) {
        this.constraintViolations.add(violation);
    }
    
    public void setConstraintViolations(List<ConstraintContainer> violations) {
        this.constraintViolations.clear();
        this.constraintViolations.addAll(violations);
    }
    
    public void setProgram(LMProgramBase program) {
        this.program = program;
    }
    
    public int getProgramId() {
        return ProgramUtils.getProgramId(program);
    }
    
    public String getProgramName() {
        return ProgramUtils.getProgramName(program);
    }
    
    public boolean isActive() {
        return ProgramUtils.isActive(program);
    }
    
    public Date getStartTime() {
        return ProgramUtils.getStartTime(program);
    }
    
    public Date getStopTime() {
        return ProgramUtils.getStopTime(program);
    }
    
    public String getGearName() {
        return ProgramUtils.getCurrentGearName(program);
    }
    
    public Set<ConstraintContainer> getConstraintViolations() {
        return constraintViolations;
    }
    
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("programId", getProgramId());
        tsc.append("programName", getProgramName());
        tsc.append("active", isActive());
        tsc.append("startTime", getStartTime());
        tsc.append("stopTime", getStopTime());
        tsc.append("gearName", getGearName());
        tsc.append("constraintViolations", getConstraintViolations());
        return tsc.toString(); 
    }
}
