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
    private int applianceCategoryId;
    
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
    
    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }
    
    public MessageSourceResolvable getDisplayName() {
        String code = MessageCodeGenerator.generateCode(PROGAM_PREFIX, programName);
        MessageSourceResolvable messageSourceResolvable = YukonMessageSourceResolvable.createDefault(code, programName);
        return messageSourceResolvable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + applianceCategoryId;
        result = prime * result + programId;
        result = prime * result + ((programName == null) ? 0
                : programName.hashCode());
        result = prime * result + programOrder;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Program other = (Program) obj;
        if (applianceCategoryId != other.applianceCategoryId)
            return false;
        if (programId != other.programId)
            return false;
        if (programName == null) {
            if (other.programName != null)
                return false;
        } else if (!programName.equals(other.programName))
            return false;
        if (programOrder != other.programOrder)
            return false;
        return true;
    }
    
}
