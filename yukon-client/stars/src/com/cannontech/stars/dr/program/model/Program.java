package com.cannontech.stars.dr.program.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class Program {
    private static final String PROGAM_PREFIX = "yukon.dr.program.displayname";
    private int programId;
    private String programName;
    private int programOrder;
    private String description;
    private int chanceOfControlId;
    private String logoLocation;
    
    public Program() {
        
    }
    
    public int getProgramId() {
        return programId;
    }
    
    public void setProgramId(int programId) {
        this.programId = programId;
    }
    
    public String getProgramName() {
        return programName;
    }
    
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    
    public int getChanceOfControlId() {
        return chanceOfControlId;
    }
    
    public void setChanceOfControlId(int chanceOfControlId) {
        this.chanceOfControlId = chanceOfControlId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProgramOrder() {
        return programOrder;
    }

    public void setProgramOrder(int programOrder) {
        this.programOrder = programOrder;
    }
    
    public String getLogoLocation() {
        return logoLocation;
    }

    public void setLogoLocation(String logoLocation) {
        this.logoLocation = logoLocation;
    }
    
    public MessageSourceResolvable getDisplayName() {
        String code = MessageCodeGenerator.generateCode(PROGAM_PREFIX, programName);
        MessageSourceResolvable messageSourceResolvable = 
            YukonMessageSourceResolvable.createDefault(programName);
        //TODO the YMSR should be for the simple situations
        //TODO A. variable args must be used carefully with overloaded methods
        //TODO B. this would be a nice use of multiple keys
        
        return messageSourceResolvable;
    }
    
}
