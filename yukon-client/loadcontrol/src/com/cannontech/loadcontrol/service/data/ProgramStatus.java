package com.cannontech.loadcontrol.service.data;

import java.util.Date;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.google.common.collect.Lists;

public class ProgramStatus {

    private LMProgramBase program;
    private List<ConstraintContainer> constraintViolations = Lists.newArrayList();
    
    public ProgramStatus(LMProgramBase program) {
        this.program = program;
    }
    
    public void addConstraintViolation(ConstraintContainer constraintContainer) {
    	this.constraintViolations.add(constraintContainer);
    }
    
    public void setConstraintViolations(List<ConstraintContainer> constraintViolations) {
        this.constraintViolations.clear();
        this.constraintViolations.addAll(constraintViolations);
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
    
    public boolean isScheduled() {
        return ProgramUtils.isScheduled(program);
    }
    
    public boolean isInactive() {
        return ProgramUtils.isInactive(program);
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
    
    public List<ConstraintContainer> getConstraintViolations() {
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
