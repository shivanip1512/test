package com.cannontech.stars.dr.program.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class Program {
    
    // must match prefix in adminSetup/applianceCategory/editAssignedProgram.jsp
    public static final String PROGAM_PREFIX = "yukon.dr.program.displayname";
    private int programId;
    private String programName;
    private String programPaoName;
    private int programOrder;
    private String description;
    private String savingsDescriptionIcon;
    private String controlPercentDescriptionIcon;
    private String environmentDescriptionIcon;
    private String applianceCategoryLogo;
    private String chanceOfControl;
    private int applianceCategoryId;
    private PaoType paoType;
    private String descriptionUrl;
    private Integer relay;

    public Integer getRelay() {
        return relay;
    }

    public void setRelay(Integer relay) {
        this.relay = relay;
    }
    /**
     * Get the assigned (STARS) program id.
     * @return the assigned program id.
     */
    public int getProgramId() {
        return programId;
    }
    
    public void setProgramId(int programId) {
        this.programId = programId;
    }

    /**
     * The program's assigned (STARS) name ("AlternateDisplayName") if available, otherwise
     * PAO name.
     * @return the assigned program name.
     */
    public String getProgramName() {
        return programName;
    }
    
    /**
     * Set the program's assigned name ("AlternateDisplayName") if available,
     * otherwise PAO name.
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    
    public String getProgramPaoName() {
		return programPaoName;
	}
    
    public void setProgramPaoName(String programPaoName) {
		this.programPaoName = programPaoName;
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

    public String getSavingsDescriptionIcon() {
        return savingsDescriptionIcon;
    }

    public void setSavingsDescriptionIcon(String savingsDescriptionIcon) {
        this.savingsDescriptionIcon = savingsDescriptionIcon;
    }

    public String getControlPercentDescriptionIcon() {
        return controlPercentDescriptionIcon;
    }

    public void setControlPercentDescriptionIcon(String controlPercentDescriptionIcon) {
        this.controlPercentDescriptionIcon = controlPercentDescriptionIcon;
    }

    public String getEnvironmentDescriptionIcon() {
        return environmentDescriptionIcon;
    }

    public void setEnvironmentDescriptionIcon(String environmentDescriptionIcon) {
        this.environmentDescriptionIcon = environmentDescriptionIcon;
    }

    public String getApplianceCategoryLogo() {
        return applianceCategoryLogo;
    }

    public void setApplianceCategoryLogo(String applianceCategoryLogo) {
        this.applianceCategoryLogo = applianceCategoryLogo;
    }

    public String getChanceOfControl() {
        return chanceOfControl;
    }

    public void setChanceOfControl(String chanceOfControl) {
        this.chanceOfControl = chanceOfControl;
    }
    
    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }
    
    public String getDescriptionUrl() {
		return descriptionUrl;
	}
    
    public void setDescriptionUrl(String descriptionUrl) {
		this.descriptionUrl = descriptionUrl;
	}
    
    public PaoType getPaoType() {
        return paoType;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    public MessageSourceResolvable getDisplayName() {
        String code = MessageCodeGenerator.generateCode(PROGAM_PREFIX, programName);
        MessageSourceResolvable messageSourceResolvable = YukonMessageSourceResolvable.createDefault(code, programName);
        return messageSourceResolvable;
    }
    
    public boolean isLogoAssigned() {
        
        if (applianceCategoryLogo.equalsIgnoreCase(CtiUtilities.STRING_NONE)
                || applianceCategoryLogo.equalsIgnoreCase(",,") // just why
                || StringUtils.isBlank(applianceCategoryLogo)) {
            return false;
        }
        
        return true;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + applianceCategoryId;
		result = prime * result
				+ ((chanceOfControl == null) ? 0 : chanceOfControl.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((descriptionUrl == null) ? 0 : descriptionUrl.hashCode());
        result = prime * result + ((savingsDescriptionIcon == null) ? 0
                : savingsDescriptionIcon.hashCode());
        result = prime * result + ((controlPercentDescriptionIcon == null) ? 0
                : controlPercentDescriptionIcon.hashCode());
        result = prime * result + ((environmentDescriptionIcon == null) ? 0
                : environmentDescriptionIcon.hashCode());
        result = prime * result + ((applianceCategoryLogo == null) ? 0
                : applianceCategoryLogo.hashCode());
		result = prime * result + programId;
		result = prime * result
				+ ((programName == null) ? 0 : programName.hashCode());
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
		if (chanceOfControl == null) {
			if (other.chanceOfControl != null)
				return false;
		} else if (!chanceOfControl.equals(other.chanceOfControl))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (descriptionUrl == null) {
			if (other.descriptionUrl != null)
				return false;
		} else if (!descriptionUrl.equals(other.descriptionUrl))
			return false;
		if (savingsDescriptionIcon == null) {
			if (other.savingsDescriptionIcon != null)
				return false;
		} else if (!savingsDescriptionIcon.equals(other.savingsDescriptionIcon))
			return false;
        if (controlPercentDescriptionIcon == null) {
            if (other.controlPercentDescriptionIcon != null)
                return false;
        } else if (!controlPercentDescriptionIcon.equals(other.controlPercentDescriptionIcon))
            return false;
        if (environmentDescriptionIcon == null) {
            if (other.environmentDescriptionIcon != null)
                return false;
        } else if (!environmentDescriptionIcon.equals(other.environmentDescriptionIcon))
            return false;
        if (applianceCategoryLogo == null) {
            if (other.applianceCategoryLogo != null)
                return false;
        } else if (!applianceCategoryLogo.equals(other.applianceCategoryLogo))
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