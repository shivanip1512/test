package com.cannontech.loadcontrol.service.data;

import org.springframework.core.style.ToStringCreator;

public class ProgramStartingGear {

	private int programId;
    private String programName;
    private String startingGearName;
    private int startingGearNumber;
    
    public ProgramStartingGear(int programId, String programName, String startingGearName, int startingGearNumber) {
    	this.programId = programId;
        this.programName = programName;
        this.startingGearName = startingGearName;
        this.startingGearNumber = startingGearNumber;
    }
    
    public int getProgramId() {
		return programId;
	}
    public String getProgramName() {
        return programName;
    }
    public String getStartingGearName() {
        return startingGearName;
    }
    public int getStartingGearNumber() {
        return startingGearNumber;
    }
    
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("programId", getProgramId());
        tsc.append("programName", getProgramName());
        tsc.append("startingGearName", getStartingGearName());
        tsc.append("startingGearNumber", getStartingGearNumber());
        return tsc.toString(); 
    }
}
