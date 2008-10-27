package com.cannontech.loadcontrol.service.data;

import org.springframework.core.style.ToStringCreator;

public class ProgramStartingGear {

    private String programName;
    private String startingGearName;
    private int startingGearNumber;
    
    public ProgramStartingGear(String programName, String startingGearName, int startingGearNumber) {
        this.programName = programName;
        this.startingGearName = startingGearName;
        this.startingGearNumber = startingGearNumber;
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
        tsc.append("programName", getProgramName());
        tsc.append("startingGearName", getStartingGearName());
        tsc.append("startingGearNumber", getStartingGearNumber());
        return tsc.toString(); 
    }
}
