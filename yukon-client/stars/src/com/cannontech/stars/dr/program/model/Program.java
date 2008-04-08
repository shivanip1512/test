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
    private String logoLocation;
    private ChanceOfControl chanceOfControl;
    
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
    
    public ChanceOfControl getChanceOfControl() {
        return chanceOfControl;
    }

    public void setChanceOfControl(ChanceOfControl chanceOfControl) {
        this.chanceOfControl = chanceOfControl;
    }
    
    public MessageSourceResolvable getDisplayName() {
        String code = MessageCodeGenerator.generateCode(PROGAM_PREFIX, programName);
        MessageSourceResolvable messageSourceResolvable = YukonMessageSourceResolvable.createDefault(code, programName);
        return messageSourceResolvable;
    }
    
}
