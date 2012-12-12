package com.cannontech.stars.dr.hardware.model;

import com.google.common.base.Function;

public class ProgramToAlternateProgram {
    
    private int assignedProgramId;
    private int alternateProgramId;
    
    public static final Function<ProgramToAlternateProgram, Integer> ALTERNATE_PROGRAM_IDS_FUNCTION = new Function<ProgramToAlternateProgram, Integer>() {
        @Override
        public Integer apply(ProgramToAlternateProgram programToAlternateProgram) {
            return programToAlternateProgram.getAlternateProgramId();
        }};
    
    public int getAssignedProgramId() {
        return assignedProgramId;
    }
    public void setAssignedProgramId(int assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }
    public int getAlternateProgramId() {
		return alternateProgramId;
	}
    public void setAlternateProgramId(int alternateProgramId) {
		this.alternateProgramId = alternateProgramId;
	}
    
    public static ProgramToAlternateProgram of(int assignedProgramId, int alternateProgramId) {
    	ProgramToAlternateProgram mapping = new ProgramToAlternateProgram();
    	mapping.setAssignedProgramId(assignedProgramId);
    	mapping.setAlternateProgramId(alternateProgramId);
    	return mapping;
    }
}